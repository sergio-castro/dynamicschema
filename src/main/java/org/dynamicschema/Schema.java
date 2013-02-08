package org.dynamicschema;

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
	
}
