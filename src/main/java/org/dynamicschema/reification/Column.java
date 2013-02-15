package org.dynamicschema.reification;

import org.dynamicschema.visitor.SchemaVisitor;

public class Column {
	private String simpleName;
	private String type;
	private ColumnModel columnModel;
	
	public Column(String simpleName) {
		setSimpleName(simpleName);
	}
	
	public Column(String simpleName, String type) {
		setSimpleName(simpleName);
		setType(type);
	}

	public String getSimpleName() {
		return simpleName;
	}

	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		if(getTable() == null)
			return getSimpleName();
		return getTable().getColumnName(this);
	}

	/*
	public Object value(IRelationalContextManager ctx, Relation relation) {
		if(ctx == null)
			return getName();
			
		else
			return ctx.getColumnValue(relation, this);	
	}
*/

	public DBTable getTable() {
		return getColumnModel().getTable();
	}

	public int getIndex() {
		return getColumnModelOrThrow().getIndex(this);
	}


	@Override
	public String toString() {
		return getSimpleName();
		//return getName();
	}

	public String toColumnDefString() {
		StringBuilder sb = new StringBuilder(getSimpleName());
		if(type != null)
			sb.append(" " + type);
		return sb.toString();
	}
	
	public ColumnModel getColumnModelOrThrow() {
		if(columnModel == null)
			throw new RuntimeException("Column not attached to a ColumnModel");
		return getColumnModel();
	}
	
	public ColumnModel getColumnModel() {
		return columnModel;
	}
	
	void attach(ColumnModel columnModel) {
		this.columnModel = columnModel;
	}

	public void accept(SchemaVisitor visitor) {
		visitor.doVisit(this);
	}
	
}
