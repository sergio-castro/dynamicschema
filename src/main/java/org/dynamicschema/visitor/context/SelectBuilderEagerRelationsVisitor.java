package org.dynamicschema.visitor.context;

import java.util.ArrayList;
import java.util.List;

import org.dynamicschema.context.ContextedQueryBuilder;
import org.dynamicschema.context.RelationNode;
import org.dynamicschema.context.TableContext;
import org.dynamicschema.context.TableNode;
import org.dynamicschema.reification.ContextedTable;
import org.dynamicschema.reification.Relation;
import org.dynamicschema.reification.Table;
import org.dynamicschema.sql.Join.LeftJoin;
import org.dynamicschema.sql.SqlCondition;
import org.dynamicschema.sql.SyntaxicTrickRelationCondition;


public class SelectBuilderEagerRelationsVisitor extends ContextedTableRelationsVisitor {

	private ContextedQueryBuilder queryBuilder;
	


	public SelectBuilderEagerRelationsVisitor(Table table, List<Table> tables) {
		super(table);
		queryBuilder = new ContextedQueryBuilder(ctx);
		ctx.setTables2Select(tables);  // additional tables whose columns have to be selected in the query
	}
	
	public SelectBuilderEagerRelationsVisitor(Table table, List<Table> tableWhoseColToSelect, List<Relation> relationToVisit) {
		this(table, tableWhoseColToSelect);
		super.setRelations2Visit(relationToVisit);
	}

	public ContextedQueryBuilder getQueryBuilder() {
		return queryBuilder;
	}

	@Override
	protected void onVisitBaseTable(TableNode tableNode) {

		Table contextedTable = tableNode.getContextedTable(ctx);
		queryBuilder.addTable(contextedTable.fromClauseName());
		queryBuilder.addColumns(contextedTable.getColumnValues());
		queryBuilder.addWhere(contextedTable.evalFiltering());
	}

	@Override
	protected void onVisitRelatedTable(TableNode tableNode) {
		Table contextedTable = tableNode.getContextedTable(ctx);

		if(!tableNode.holdsBaseTable(ctx)){
			queryBuilder.addWhere(contextedTable.evalFiltering());
		}

		if(canAddColumns(tableNode, (ContextedTable) contextedTable))
			queryBuilder.addColumns(contextedTable.getColumnValues());

	}




	@Override
	protected void onVisitedTableRelation(RelationNode relationNode) {

		//Add 
		if(shouldStopVisitingRelation(relationNode))
				return;
		//End
	
		List<Table> joinedTables = relationNode.getJoinedTables(ctx);
		List<Table> relArgs = relationNode.getRelationArgs(ctx);
		Table [] evalArgs = relArgs.toArray(new Table[relArgs.size()]);	
		List<Table> dupTables = new ArrayList<Table>();
		List<SqlCondition> trickCondList = new ArrayList<SqlCondition>();

		//Added
		Table possibleBaseTable = getEventualBaseTable(joinedTables);
		SqlCondition baseTableTrickCond = null;
		
		if(possibleBaseTable != null && !inLazySelection((ContextedTable)possibleBaseTable)){
				
			Table origBaseTable = getOriginalBaseTableContextedTable();
			baseTableTrickCond = new SyntaxicTrickRelationCondition().eval(origBaseTable, possibleBaseTable);
			trickCondList.add(baseTableTrickCond);
		}else
			if(!relationNode.getRelation().isBinaryRecursive())
				dupTables = getTablesAppearedTwiceInTree(joinedTables);

		for (Table table : dupTables) {
			SqlCondition additionalTrickCond = null;
			Table origTable = null;
			origTable = getOriginalContextedTable(table);
			additionalTrickCond = new SyntaxicTrickRelationCondition().eval(origTable, table);
			trickCondList.add(additionalTrickCond);
		}

		SqlCondition sqlCondition = null;
		if(relationNode.getRelation().isBinary()){
			sqlCondition = relationNode.getRelation().getCondition().eval(evalArgs[0], evalArgs[1]);

		}else
			sqlCondition = relationNode.getRelation().getCondition().eval(evalArgs);

		for (SqlCondition additionalSqlCond : trickCondList) {
			sqlCondition.and(additionalSqlCond.toString());
		}

		handleJoining(relationNode, joinedTables, sqlCondition);
	}
	
	
	private boolean shouldStopVisitingRelation(RelationNode relationNode){
		
		if(ctx.hasAlreadyOccured(relationNode.getRelation())) 
			// Don't put a join condition of same relation twice
			return true;
		ctx.setOccuredRelation(relationNode.getRelation());
		return false;
	}
	
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
	 * 
	 */
	private boolean inLazySelection(ContextedTable joinedTable){
		Table refTable = joinedTable.getTable();
		boolean isBase = ctx.isBaseTable(refTable);
		boolean isLazy = ctx.isInLazyQuery();
		TableNode firstNodeOccurence = ctx.getVisitedTableNode(refTable);
		boolean hasSameAliases = joinedTable.getAlias().equals(ctx.getRelationContext().getTableContext(firstNodeOccurence).getAlias());
		return isBase && isLazy && hasSameAliases ; 
			
	}

	private void applyJoinCondition(Table joinedTable, SqlCondition sqlCondition){
		LeftJoin leftJoin = new LeftJoin(joinedTable, sqlCondition);
		queryBuilder.addJoin(leftJoin);		
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


	private boolean canAddColumns(TableNode currNode, ContextedTable currCtxTable){


		Table currTable = currNode.getTable();

		if(!ctx.getTables2Select().contains(currTable))
			return false;	
		TableNode origNode = ctx.getVisitedTableNode(currTable);
		if(origNode == null)
			throw new RuntimeException("Unexpected Error: Should already visit table "+ currNode.getTable().toString());

		TableContext origTableContext = ctx.getRelationContext().getTableContext(origNode);

		if(origTableContext.getAlias().equals(currCtxTable.getAlias()))
			return true;
		return false;	
	}


}
