package org.dynamicschema.context;

import java.util.ArrayList;
import java.util.List;

import org.dynamicschema.reification.Relation;
import org.dynamicschema.reification.Table;
import org.dynamicschema.reification.TableRelation;

public class RelationNode extends RelationTree {

	public static final String RELATION_PREFIX = "Relation";
	
	private TableRelation tableRelation;
	private int relationIndex; //index of the table relation
	
	public RelationNode(TableNode parent, int relationIndex) {
		super(parent, parent.getId() + TABLE_RELATION_SEP + RELATION_PREFIX + relationIndex);
		this.relationIndex = relationIndex;
		this.tableRelation = parent.getTable().getTableRelations().get(relationIndex);
	}

	@Override
	public List<TableNode> getChildren() {
		List<TableNode> children = new ArrayList<TableNode>();
		for(int i=0; i<getRelation().getRelationMembers().size(); i++) {
			if(i == tableRelation.getIndexTableInRelation())
				continue;
			TableNode tableNode = new TableNode(this, i);
			children.add(tableNode);
		}
		return children;
	}
	
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
		for(int i=0; i<getRelation().getRelationMembers().size(); i++) {
			if(i == tableRelation.getIndexTableInRelation()) {
				relationArgs.add(parentTableNode.getContextedTable(ctx));
			} else {
				TableNode tableNode = new TableNode(this, i);
				relationArgs.add(tableNode.getContextedTable(ctx));
			}
		}
		return relationArgs;
	}

	
}
