package org.dynamicschema.visitor.context;

import java.util.List;

import org.dynamicschema.context.RelationNode;
import org.dynamicschema.context.RelationTree;
import org.dynamicschema.context.RelationalContextManager;
import org.dynamicschema.context.TableNode;
import org.dynamicschema.reification.Fetching;
import org.dynamicschema.reification.Table;

public class ContextedTableRelationsVisitor {

	protected RelationalContextManager ctx;
	private Fetching fetching; //only relations with this kind of fetching will be visited
	private TableNode root;
	
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
	
	public void visit(TableNode tableNode, boolean baseTable) {
		ctx.createTableContext(tableNode);
		if(baseTable)
			onVisitBaseTable(tableNode);
		else
			onVisitRelatedTable(tableNode);
		
		for(RelationNode relationNode : tableNode.getChildren()) {
			if(!relationExists(relationNode, tableNode.getFullPath())) {
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
	
}
