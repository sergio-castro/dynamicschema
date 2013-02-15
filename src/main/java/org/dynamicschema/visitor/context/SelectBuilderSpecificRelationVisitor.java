package org.dynamicschema.visitor.context;

import java.util.List;
import java.util.Map;

import org.dynamicschema.context.RelationNode;
import org.dynamicschema.context.TableNode;
import org.dynamicschema.reification.Table;
import org.dynamicschema.reification.TableRelation;

public class SelectBuilderSpecificRelationVisitor extends SelectBuilderEagerRelationsVisitor {

	private TableRelation tableRelation;
	private int indexTableRelation;
	Map<String, Object> columnBindings;
	
	public SelectBuilderSpecificRelationVisitor(Table table, int indexTableRelation, Map<String, Object> columnBindings) {
		super(table);
		this.indexTableRelation = indexTableRelation;
		//this.tableRelation = tableRelation;
		this.columnBindings = columnBindings;
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
