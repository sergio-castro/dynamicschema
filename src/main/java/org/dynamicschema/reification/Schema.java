package org.dynamicschema.reification;

import java.util.Arrays;
import java.util.List;

public class Schema {

	private List<Table> tables;

	public List<Table> getTables() {
		return tables;
	}

	public void setTables(List<Table> tables) {
		this.tables = tables;
	}
	
	public void setTables(Table ...tables) {
		setTables(Arrays.<Table>asList(tables));
	}
	
	public String createSchemaScript() {
		StringBuilder sb = new StringBuilder();
		for(Table table : tables) {
			sb.append(table.createTableStatement());
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public String dropSchemaScript() {
		StringBuilder sb = new StringBuilder();
		for(Table table : tables) {
			sb.append(table.dropTableStatement());
			sb.append("\n");
		}
		return sb.toString();
	}
	
}
