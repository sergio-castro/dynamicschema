package org.dynamicschema.context;

import java.util.ArrayList;
import java.util.List;

import org.dynamicschema.reification.Relation;
import org.dynamicschema.reification.RelationMember;
import org.dynamicschema.reification.Table;
import org.dynamicschema.reification.TableRelation;

public class RelationNode extends RelationTree {

	public static final String RELATION_PREFIX = "Relation";

	private TableRelation tableRelation;
	private int relationIndex; //index of the table relation in the List of TableRelations of the parent table
	
	private boolean markedAsLazy; //determine whether relation held by current node should be considered as lazy

	//Addendum
	private RelationalContextManager relContextManager;

	public RelationNode(TableNode parent, int relationIndex) {
		super(parent, parent.getId() + TABLE_RELATION_SEP + RELATION_PREFIX + relationIndex);
		this.relationIndex = relationIndex;
		this.tableRelation = parent.getTable().getTableRelations().get(relationIndex);
	}

	@Override
	public List<TableNode> getChildren() {
		List<TableNode> children = new ArrayList<TableNode>();
		List<RelationMember> relMembers = getRelation().getRelationMembers(); 
		for(int i=0; i<	relMembers.size(); i++) {
			if(i == tableRelation.getIndexTableInRelation()) // for avoiding cycles !!!
				continue;

			//Addendum 
			TableNode  tableNode = null;
			Table childTable = relMembers.get(i).getTable();
			if(tableAlreadyVisited(childTable)){
//				tableNode = getRelContextManager().getVisitedTableNode(childTable);
			}else{
			//End Addendum
				tableNode = new TableNode(this, childTable, i);
			}
			children.add(tableNode);
		}
		return children;
	}

	

	/*
	 * Determines whether this table correspond to one that has been visited before 
	 */
	private boolean tableAlreadyVisited(Table table){
		return false;
//		return  !getRelation().isBinaryRecursive() 
//				&&
//				getRelContextManager().tableAlreadyVisited(table)
//				&&	
//				!getRelContextManager().isBaseTable(table);
	
	}
	

	/**
	 * @param relContextManager the relContextManager to set
	 */
//	public void setRelContextManager(RelationalContextManager relContextManager) {
//		this.relContextManager = relContextManager;
//	}
//
//	public RelationalContextManager getRelContextManager(){
//		return this.relContextManager ;
//	}

	public int getRelationIndex() {
		return relationIndex;
	}

	public TableRelation getTableRelation() {
		return tableRelation;
	}

	public Relation getRelation() {
		return tableRelation.getRelation();
	}

	public TableNode getParentTable() {
		return (TableNode) getParent();
	}

	@Override
	public List<Table> getTablePath() {
		return getParent().getTablePath();
	}

	public List<Table> getJoinedTables(RelationalContextManager ctx) {
		List<Table> joinedTables = new ArrayList<Table>();
		for(TableNode child : getChildren()) {
			joinedTables.add(child.getContextedTable(ctx));
		}
		return joinedTables;
	}

	public List<Table> getRelationArgs(RelationalContextManager ctx) {
		TableNode parentTableNode = getParentTable();
		List<Table> relationArgs = new ArrayList<Table>();
		List<RelationMember> relMembers = getRelation().getRelationMembers();
		
		for(int i=0; i< relMembers.size(); i++) {
			if(i == tableRelation.getIndexTableInRelation()) {
				relationArgs.add(parentTableNode.getContextedTable(ctx));
			} else {
				TableNode tableNode = new TableNode(this, i);
					//getRelContextManager().getVisitedTableNode(relMembers.get(i).getTable());
				relationArgs.add(tableNode.getContextedTable(ctx));
			}
		}
		return relationArgs;
	}

	/**
	 *  A an eager relation can be faken to be seen as a lazy.
	 *  When we are doing a traversal of lazy relations only, we do to want to fetch  entities eagerly if we do not want them 
	 * @return the markedAsLazy
	 */
	public boolean isMarkedAsLazy() {
		return markedAsLazy;
	}

	/**
	 * @param markedAsLazy the markedAsLazy to set
	 */
	public void setMarkedAsLazy(boolean markedAsLazy) {
		this.markedAsLazy = markedAsLazy;
	}



}
