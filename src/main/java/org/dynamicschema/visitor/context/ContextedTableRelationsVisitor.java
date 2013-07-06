package org.dynamicschema.visitor.context;

import java.util.ArrayList;
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
	private List<Relation> visited;
	
	public ContextedTableRelationsVisitor(Table table) {
		this(table, null);
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

	public void visit(TableNode tableNode, boolean baseTable) {

		ctx.createTableContext(tableNode);
		

		
		//Add
		if(shouldRegisterVisitation(tableNode)){
			if(shouldStopVisiting(tableNode))
				return;	
			ctx.setVisitedTable(tableNode.getTable(), tableNode);
		}
		
		if(baseTable)
			ctx.setBaseTable(tableNode.getTable());
	
		if(inRecursiveRelation(tableNode)){
			ctx.setVisitedRecursion(tableNode.getTable());
		}

		if(baseTable)
			onVisitBaseTable(tableNode);
		else {
			onVisitRelatedTable(tableNode);
			if(tableNode.holdsBaseTable(ctx)) 
				return;
		}
		for(RelationNode relationNode : tableNode.getChildren()) { 
			if(eligibleForVisitation(relationNode, tableNode)) { 
				relationNode.setRelContextManager(ctx); //pass the context to the node 	
				
				Fetching relationFecthing = relationNode.getTableRelation().getFetching();
				if(fetching==null || fetching.equals(relationFecthing)) {
					for(TableNode childTableNode : relationNode.getChildren()) {
						visit(childTableNode, false);
					}
					onVisitedTableRelation(relationNode);
				}
			}
		}
	}



	private boolean eligibleForVisitation(RelationNode relationNode, TableNode tableNode){
		return !relationExists(relationNode, tableNode.getFullPath()) //avoid cycles (for ex. recursive relations)
					&& 
						mayBeVisitedInRestrictedMode(relationNode);
	}
	
	private boolean inRecursiveRelation(TableNode tableNode){

		if(tableNode.holdsBaseTable(ctx))
			return false;

		RelationNode relNode = (RelationNode) tableNode.getParent();
		
		if(relNode == null)
				return false;
		
		return relNode.getRelation().isBinaryRecursive();
	}

	private boolean shouldRegisterVisitation(TableNode tableNode){
		return !tableNode.holdsBaseTable(ctx);
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
	public void setRelations2Visit(List<Relation> relationsToVisit) {
		this.relations2Visit = relationsToVisit;
		this.visited = new ArrayList<Relation>();
	}
	
	
	/*
	 *  In case we are in a restricted selection, this determines whether a relation is allowed to 
	 *  be traversed
	 */
	private boolean mayBeVisitedInRestrictedMode(RelationNode relNode){
		
		if(this.relations2Visit == null) 
				return true;
		
		Relation rel = relNode.getRelation();
		if(this.visited.contains(rel)) 
			return false;
		
		if(this.relations2Visit.contains(rel)){
			this.visited.add(rel);
			this.relations2Visit.remove(rel);
			return true;
		}
		return false;
	}

}
