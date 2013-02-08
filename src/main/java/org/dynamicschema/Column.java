package org.dynamicschema;


public class Column {
	private int index;
	private Table table;
	private String simpleName;

	public Column(String simpleName) {
		setSimpleName(simpleName);
	}

	public String getSimpleName() {
		return simpleName;
	}

	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
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
	
	@Override
	public String toString() {
		return getSimpleName();
		//return getName();
	}

}
