package org.dynamicschema.visitor.context;

import java.util.List;
import java.util.Map;

import org.dynamicschema.context.RelationNode;
import org.dynamicschema.context.TableNode;
import org.dynamicschema.reification.TableRelation;

public class SelectBuilderSpecificRelationVisitor extends SelectBuilderEagerRelationsVisitor {

	private TableRelation tableRelation;
	private int indexTableRelation; //the index of the TableRelation object in the list of TableRelations of the table
	Map<String, Object> columnBindings;
	
	public SelectBuilderSpecificRelationVisitor(TableRelation tableRelation, Map<String, Object> columnBindings) {
		super(tableRelation.getBaseTable());
		this.tableRelation = tableRelation;
		this.columnBindings = columnBindings;
		this.indexTableRelation = tableRelation.getBaseTable().getTableRelations().indexOf(tableRelation);
	}

	public TableRelation getTableRelation() {
		return tableRelation;
	}

	public void visit() {
		ctx.createTableContext(getRoot(), columnBindings);
		RelationNode relationNode = new RelationNode(getRoot(), indexTableRelation);
		List<TableNode> tablesRelation = relationNode.getChildren();
		for(TableNode tableNode : tablesRelation) {
			visit(tableNode, true);
		}
		onVisitedTableRelation(relationNode);
	}
	
}
