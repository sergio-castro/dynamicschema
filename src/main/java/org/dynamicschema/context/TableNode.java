package org.dynamicschema.context;

import java.util.ArrayList;
import java.util.List;

import org.dynamicschema.reification.ContextedTable;
import org.dynamicschema.reification.DBTable;
import org.dynamicschema.reification.Table;
import org.dynamicschema.reification.TableRelation;

/**
 * Instances of this class identify uniquely a table in the context of a query
 * If the table appears more than once, they will have different identifiers
 * @author sergioc
 *
 */
public class TableNode extends RelationTree {

	public static final String TABLE_PREFIX = "Table";

	private Table table;
	private int tableIndex; //index of table in the parent relation
	
	public TableNode(Table table) {
		super(null, TABLE_PREFIX + "0");
		this.table = table;
		this.tableIndex = 0;
	}
	
	public TableNode(RelationNode parent, int tableIndex) {
		super(parent, parent.getId() + RELATION_TABLE_SEP + TABLE_PREFIX + tableIndex);
		this.table = parent.getRelation().getTable(tableIndex);
		this.tableIndex = tableIndex;
	}

	@Override
	public List<RelationNode> getChildren() {
		List<RelationNode> children = new ArrayList<RelationNode>();
		List<TableRelation> tableRelations = getTable().getTableRelations();
		for(int i=0; i<tableRelations.size();i++) {
			RelationNode relationNode = new RelationNode(this, i);
			children.add(relationNode);
		}
		return children;
	}
	
	public Table getTable() {
		return table;
	}

	public int getTableIndex() {
		return tableIndex;
	}

	
	@Override
	public List<Table> getTablePath() {
		List<Table> tablePath = !isRoot()?getParent().getTablePath():new ArrayList<Table>();
		tablePath.add(table);
		return tablePath;
	}
	
	public ContextedTable getContextedTable(RelationalContextManager ctx) {
		TableContext tableContext = ctx.getRelationContext().getTableContext(this);
		return new ContextedTable((DBTable)this.getTable(), tableContext.getAlias(), tableContext.getBindings());
	}
	
}
