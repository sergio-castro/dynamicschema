package org.dynamicschema.context;

import org.dynamicschema.reification.Column;
import org.dynamicschema.reification.Table;

public class TableContext {

	private String alias;
	private int offset;
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	/*
	 * Table name as it should look in the FROM clause of a SELECT statement
	 */
	public String aliasedTable(Table table) {
		return table+" "+alias;
	}
	
	/*
	 * Column name as it should be referenced in a SELECT statement
	 */
	public String columnName(Column column) {
		return alias+"."+column.getSimpleName();
	}
	
	@Override
	public String toString() {
		return "Table context with alias: " + getAlias();
	}
	
}
