package org.dynamicschema.visitor.context;

import java.util.List;

import org.dynamicschema.context.RelationNode;
import org.dynamicschema.context.RelationTree;
import org.dynamicschema.context.RelationalContextManager;
import org.dynamicschema.context.TableNode;
import org.dynamicschema.reification.Fetching;
import org.dynamicschema.reification.Relation;
import org.dynamicschema.reification.Table;

public class ContextedTableRelationsVisitor {

	protected RelationalContextManager ctx;
	private Fetching fetching; //only relations with this kind of fetching will be visited
	private TableNode root;
	
	//Needed for specific relations visit
	private List<Relation> relations2Visit;

	
	public ContextedTableRelationsVisitor(Table table) {
		this(table, Fetching.EAGER);
	}


	public ContextedTableRelationsVisitor(Table table, Fetching fetching) {
		this.ctx = new RelationalContextManager();
		this.root = new TableNode(table);
		this.fetching = fetching;
	}

	public TableNode getRoot() {
		return root;
	}

	public RelationalContextManager getRelationalContext() {
		return ctx;
	}

	public void visit() {
		visit(root, true);
	}

	private boolean relationExists(RelationNode relationNode, List<RelationTree> fullPath) {
		for(RelationTree relationTree : fullPath) {
			if(relationTree instanceof RelationNode) {
				RelationNode existingRelationNode = (RelationNode) relationTree;
				if(existingRelationNode.getRelation().equals(relationNode.getRelation()))
					return true;
			}
		}
		return false;
	}



	private boolean shouldStopVisiting(TableNode tableNode){

		if(inRecursiveRelation(tableNode)){
			if(recursivelyVisited(tableNode))
				return true;

		}else{
			if(ctx.tableAlreadyVisited(tableNode.getTable()))
				return true;
		}


		return false;
	}
/*
 * Implements the relational tree traversal  
 */
	public void visit(TableNode tableNode, boolean baseTable) {

		ctx.createTableContext(tableNode);
		
		if(baseTable)
			ctx.setBaseTable(tableNode.getTable());

		if(baseTable)
			onVisitBaseTable(tableNode);
		else {
			onVisitRelatedTable(tableNode);
	
		}

		if(inRecursiveRelation(tableNode)){
			ctx.setVisitedRecursion(tableNode.getTable());
			if(ctx.isInLazyQuery())
				ctx.setVisitedTable(tableNode.getTable(), tableNode);
		}

		//Add
		if(shouldStopVisiting(tableNode))
			return;	
		ctx.setVisitedTable(tableNode.getTable(), tableNode);
		

		for(RelationNode relationNode : tableNode.getChildren()) { 
			if(eligibleForVisitation(relationNode, tableNode)) { 
//				relationNode.setRelContextManager(ctx); //pass the context to the node 	
				Fetching relationFecthing = getFetchingForRelationNode(relationNode);
				if(fetching==null || fetching.equals(relationFecthing) || allowedForVisitation(relationNode)) {
					if(traversedLazyRelation(relationFecthing, relationNode)) //stop if we reached the tableNode by traversing a relation with lazy fetching 
						continue;
					for(TableNode childTableNode : relationNode.getChildren()) {
						visit(childTableNode, false);
					}
					onVisitedTableRelation(relationNode);
				}
			}
		}
	}
   

/*
 * Determines whether we reached the current relation node through a lazy relation in the past
 */
	private boolean traversedLazyRelation(Fetching currFetching, RelationNode relationNode) {
		TableNode parentTableNode = relationNode.getParentTable();
		RelationNode parentRelNode = (RelationNode) parentTableNode.getParent();
		if(parentRelNode == null) //in case the parent table was the base table
			return false;
		
		boolean  parentRelIsLazy = parentRelNode.getTableRelation().getFetching().equals(Fetching.LAZY);
		
		if(startedALazySelect(parentRelNode))
			return false;
		
		boolean traversedLazyRel = parentRelIsLazy || parentRelNode.isMarkedAsLazy();
		//Can not traverse an eager relation after traversing a Lazy one except in a lazy Select. 
		
		return currFetching != null && currFetching.equals(Fetching.EAGER) && traversedLazyRel;
	}

	/*
	 * In need some cases, we need to transform the fecthing of a relation for overcoming the problem of traversing lazy relations
	 * If we encounter an eager relation after traversing lazy relations, we transform the eager relation pretends to be a lazy one
	 * so its columns are not traversed
	 */
	private Fetching getFetchingForRelationNode(RelationNode relationNode){
		
		Fetching fetch =  relationNode.getTableRelation().getFetching();
		
		if(fetch.equals(Fetching.EAGER)){
			TableNode parentTableNode = relationNode.getParentTable();
			RelationNode parentRelNode = (RelationNode) parentTableNode.getParent();
			if(parentRelNode == null)
				return fetch;
			
			if(startedALazySelect(parentRelNode))
				return fetch;
			
			if(parentRelNode.getTableRelation().getFetching().equals(Fetching.LAZY)) {
				relationNode.setMarkedAsLazy(true);
				return Fetching.LAZY;
			}
		}
		
		return fetch;
	}
	
	
	/*
	 * Determines whether we just initated a lazy select by checking if there is no 
	 * other relation at the upper level of the current one
	 */
	private boolean startedALazySelect(RelationNode relNode){
		TableNode parentTable = relNode.getParentTable();
		boolean res = parentTable.getParent() == null && ctx.isInLazyQuery();
		return res;
	}
	


	

	private boolean eligibleForVisitation(RelationNode relationNode, TableNode tableNode){
		return !relationExists(relationNode, tableNode.getFullPath()) ; //avoid cycles (for ex. recursive relations)
				
	}
	
	private boolean allowedForVisitation(RelationNode relationNode){
		Relation rel = relationNode.getRelation();
		if(getRelations2Visit().contains(rel))
			return true;
		
		return false;
	}

	protected boolean inRecursiveRelation(TableNode tableNode){

		RelationNode relNode = (RelationNode) tableNode.getParent();

		if(relNode == null)
			return false;

		return relNode.getRelation().isBinaryRecursive();
	}


	private boolean recursivelyVisited(TableNode tableNode ){
		return  ctx.recursionAlreadyVisited(tableNode.getTable());
	}

	/**
	 * Visiting the base table of the relation
	 * @param tableNode
	 */
	protected void onVisitBaseTable(TableNode tableNode) {
	}

	/**
	 * Visiting any table in the relation (including the base table)
	 * @param tableNode
	 */
	protected void onVisitRelatedTable(TableNode tableNode) {
	}

	/**
	 * Visiting a relation of the table
	 * @param relationNode
	 */
	protected void onVisitedTableRelation(RelationNode relationNode) {
	}


	/**
	 * @param relationsToVisit the set of relations to visit 
	 */
	protected void setRelations2Visit(List<Relation> relationsToVisit) {
		this.relations2Visit = relationsToVisit;
	}



	/**
	 * @return the relations2Visit
	 */
	protected List<Relation> getRelations2Visit() {
		return relations2Visit;
	}

}
