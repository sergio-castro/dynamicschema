package org.dynamicschema.visitor.context;

import java.util.List;
import java.util.Map;

import org.dynamicschema.context.RelationNode;
import org.dynamicschema.context.TableNode;
import org.dynamicschema.reification.Relation;
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
	
	
	public void setRelationsToTraverse(List<Relation> relations2Traverse){
		setRelations2Visit(relations2Traverse);
	}
	
	public void setQueryFilteringSpecifier(QueryFilteringSpecifier specifier){
		setSpecifier(specifier);
	}
	

	public TableRelation getTableRelation() {
		return tableRelation;
	}

	public void visit() {
		TableNode root = getRoot();
		ctx.createTableContext(root, columnBindings, root.getTable());
		RelationNode relationNode = new RelationNode(getRoot(), indexTableRelation);
		List<TableNode> tablesRelation = relationNode.getChildren();
		for(TableNode tableNode : tablesRelation) {
			visit(tableNode, true);
		}
		onVisitedTableRelation(relationNode);
	}
	
}
