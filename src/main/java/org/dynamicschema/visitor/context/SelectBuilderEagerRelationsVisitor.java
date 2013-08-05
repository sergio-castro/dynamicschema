package org.dynamicschema.visitor.context;

import java.util.ArrayList;
import java.util.List;

import org.dynamicschema.context.ContextedQueryBuilder;
import org.dynamicschema.context.RelationNode;
import org.dynamicschema.context.TableContext;
import org.dynamicschema.context.TableNode;
import org.dynamicschema.reification.ContextedTable;
import org.dynamicschema.reification.Fetching;
import org.dynamicschema.reification.Relation;
import org.dynamicschema.reification.Table;
import org.dynamicschema.reification.TableRelation;
import org.dynamicschema.sql.Join;
import org.dynamicschema.sql.Join.InnerJoin;
import org.dynamicschema.sql.Join.LeftJoin;
import org.dynamicschema.sql.RelationCondition;
import org.dynamicschema.sql.SqlCondition;


public class SelectBuilderEagerRelationsVisitor extends ContextedTableRelationsVisitor {

	private ContextedQueryBuilder queryBuilder;

	
	private boolean nonNulljoining; //determines the type of joining to be used for relations
	private QueryFilteringSpecifier specifier;
	
	public SelectBuilderEagerRelationsVisitor(Table table) {
		super(table, Fetching.EAGER);
		queryBuilder = new ContextedQueryBuilder(ctx);
	}
	


	public SelectBuilderEagerRelationsVisitor(Table table, List<Relation> relationToTraverse) {
		this(table);
		setRelations2Visit(relationToTraverse);
	}

	public ContextedQueryBuilder getQueryBuilder() {
		return queryBuilder;
	}
	
	

	

	/**
	 * @param specifier the specifier to set
	 */
	public void setSpecifier(QueryFilteringSpecifier specifier) {
		this.specifier = specifier;
	}



	@Override
	protected void onVisitBaseTable(TableNode tableNode) {

		Table contextedTable = tableNode.getContextedTable(ctx);
		queryBuilder.addTable(contextedTable.fromClauseName());
		List<String> colValues = contextedTable.getColumnValues(); 
		queryBuilder.addColumns(colValues);
		queryBuilder.addGroupBy(colValues);
		queryBuilder.addOrderBy(colValues.get(0));
		queryBuilder.addWhere(contextedTable.evalFiltering()); //will be global filtering for all generated queries (=> depend on context)
		ctx.setSelectedCtxTable((ContextedTable)contextedTable);
	}

	@Override
	protected void onVisitRelatedTable(TableNode tableNode) {
		Table contextedTable = tableNode.getContextedTable(ctx);
		queryBuilder.addWhere(contextedTable.evalFiltering());
		queryBuilder.addColumns(contextedTable.getColumnValues());
		ctx.setSelectedCtxTable((ContextedTable)contextedTable);
		
		if(this.specifier != null)
			setSpecificFilterings(tableNode);
	}




	@Override
	protected void onVisitedTableRelation(RelationNode relationNode) {

		if(shouldStopVisitingRelation(relationNode))
				return;
		registerSelectDependency(relationNode);
	
		List<Table> joinedTables = relationNode.getJoinedTables(ctx);
		List<Table> relArgs = relationNode.getRelationArgs(ctx);
		Table [] evalArgs = relArgs.toArray(new Table[relArgs.size()]);	

		SqlCondition sqlCondition = null;
		if(relationNode.getRelation().isBinary()){
			sqlCondition = relationNode.getRelation().getCondition().eval(evalArgs[0], evalArgs[1]);

		}else
			sqlCondition = relationNode.getRelation().getCondition().eval(evalArgs);

		handleJoining(relationNode, joinedTables, sqlCondition);
	}
	
	
	private void registerSelectDependency(RelationNode relNode){
		TableNode parentTabNode = relNode.getParentTable();
		ContextedTable parentCtxTab = parentTabNode.getContextedTable(ctx);
		
		if(ctx.isSelectedInQuery(parentCtxTab)){
			for (TableNode child : relNode.getChildren()) {
				ContextedTable childCtxTab = child.getContextedTable(ctx);
				if(ctx.isSelectedInQuery(childCtxTab))
					ctx.registerSelectDependency(parentCtxTab, childCtxTab);
			}
		}
	}
	
	
	/*
	 * If there are query filterings specified by the user of the framework, then these will be appended to the current
	 * query plus, the global filtering
	 * 
	 */
	private void setSpecificFilterings(TableNode currTabNode){
		
		RelationNode parentRel = (RelationNode) currTabNode.getParent();
		TableNode parentTab = parentRel.getParentTable();
		Relation currRel = parentRel.getRelation();
		Table currTable = currTabNode.getTable();
		Table parentTable = parentTab.getTable();
		
		if(!inRecursiveRelation(currTabNode)){
			RelationCondition filteringParent = this.specifier.getFilteringFor(currRel, parentTable);
			RelationCondition filteringCurr = this.specifier.getFilteringFor(currRel, currTable);
			if(filteringParent != null){
				ContextedTable tableParent = parentTab.getContextedTable(ctx);
				queryBuilder.addWhere(filteringParent.eval(tableParent));
			}
			
			if(filteringCurr != null){
				ContextedTable tableCurr = currTabNode.getContextedTable(ctx);
				queryBuilder.addWhere(filteringCurr.eval(tableCurr));
			}
					
		}else{ //dealing with recursive rel -> use role
			List<String> roles = currRel.getRoles();
			
			for (String role : roles) {
				TableRelation tabRel = currRel.getTableRelationWithRole(role);
				RelationCondition filtering = this.specifier.getFilteringFor(currRel, currTable, role);
			
				if(filtering != null){
					ContextedTable tab =  null;
					
					if(tabRel.getIndexTableInRelation() == parentRel.getTableRelation().getIndexTableInRelation()) //role is for parent table
						tab = parentTab.getContextedTable(ctx);
					else
						tab = currTabNode.getContextedTable(ctx);
					queryBuilder.addWhere(filtering.eval(tab));
					
				}
				
			}
		}
		
	}
		
	
	/*
	 * determine whether a relation should be visited 
	 */
	private boolean shouldStopVisitingRelation(RelationNode relationNode){
		
		Relation currRel = relationNode.getRelation();

		if(ctx.hasAlreadyOccured(currRel)){// Don't put a join condition of same relation twice
			// Remove columns values that would which were potentially added before 
			removeColumnsAndUpdateOffSet(relationNode);
			return true;
		}
		//This was only visited to apply specific filter conditions on its participating tables. 
		//We shoud unselect columns of these tables which were selected by default
		if(super.getRelations2Visit().contains(currRel)) 
			removeColumnsAndUpdateOffSet(relationNode);
		
		ctx.setOccuredRelation(currRel);
		return false;
	}
	
	
	private void removeColumnsAndUpdateOffSet(RelationNode relationNode){
		List<TableNode> children = relationNode.getChildren();
		for (TableNode tableNode : children) {
			ContextedTable ctxTable = tableNode.getContextedTable(ctx);
			List<String> colValues = ctxTable.getColumnValues();
			queryBuilder.removeColumns(colValues);
			ctx.notifyColumnsRemoval(colValues.size());
			ctx.setUnSelectedCtxTable(ctxTable);
		}
	}
	
	/*
	 * 
	 */
	private void  handleJoining(RelationNode relationNode, List<Table> joinedTables, SqlCondition sqlCondition){

		if(relationNode.getRelation().isBinary()) {		
			Table joinedTable = joinedTables.get(0);
			
			if(inLazySelection((ContextedTable)joinedTable)) 
				queryBuilder.addWhere(sqlCondition);
			else
				applyJoinCondition(joinedTable, sqlCondition);
			
		} else {
			for(Table table : joinedTables) {
				queryBuilder.addTable(table.fromClauseName());
			}
			queryBuilder.addWhere(sqlCondition);
		}
	}

	
	/*
	 * Determines whether we are in lazySelect 
	 */
	private boolean inLazySelection(ContextedTable joinedTable){
		Table refTable = joinedTable.getTable();
		boolean isBase = ctx.isBaseTable(refTable);
		boolean isLazy = ctx.isInLazyQuery();
		TableNode firstNodeOccurence = ctx.getVisitedTableNode(refTable);
		TableContext tabFirst = ctx.getRelationContext().getTableContext(firstNodeOccurence);
		boolean hasSameAliases = joinedTable.getAlias().equals(tabFirst.getAlias());
		return isBase && isLazy && hasSameAliases ; 
			
	}

	private void applyJoinCondition(Table joinedTable, SqlCondition sqlCondition){
		Join joinCondition = null;
		if(isNonNulljoining())
			joinCondition = new InnerJoin(joinedTable, sqlCondition);
		else
			joinCondition= new LeftJoin(joinedTable, sqlCondition);
		queryBuilder.addJoin(joinCondition);		
	}
	


	
	private Table getOriginalBaseTableContextedTable(){
		return ctx.getBaseTableContextedTable();
	}

	private Table getOriginalContextedTable(Table table){
		return ctx.getVisitedTableNode(table).getContextedTable(ctx);
	}

	private List<Table> getTablesAppearedTwiceInTree(List<Table> joinedTables){

		List<Table> finalTables = new ArrayList<Table>();
		for (Table table : joinedTables) {
			ContextedTable tab = (ContextedTable) table;
			ContextedTable visitedTab = ctx.getVisitedTableNode(tab.getTable()).getContextedTable(ctx);
			if(!tab.getAlias().equals(visitedTab.getAlias()))
				finalTables.add(table);
		}
		return finalTables;
	}


	/* 
	 * Detects if the base table (the one that initiated the query) is about to used as joining table 
	 */
	private Table getEventualBaseTable(List<Table> joinedTables){

		for (Table table : joinedTables) {
			ContextedTable tab = (ContextedTable) table;
			if(ctx.isBaseTable(tab.getTable()))
				return table;
		}

		return null;
	}


	/**
	 * @return the nonNulljoining
	 */
	public boolean isNonNulljoining() {
		return nonNulljoining;
	}

	/**
	 * @param nonNulljoining the nonNulljoining to set
	 */
	public void setNonNulljoining(boolean nonNulljoining) {
		this.nonNulljoining = nonNulljoining;
	}
	
}
