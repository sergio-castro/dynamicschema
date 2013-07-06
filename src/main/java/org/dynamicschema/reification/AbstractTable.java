package org.dynamicschema.reification;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTable implements Table {

	public List<String> getColumnValues() {
		List<String> columnValues = new ArrayList<String>();
		for(Column column : getColumnModel().getColumns()) {
			columnValues.add(col(column.getSimpleName())); //before: colum.getName()
		}
		return columnValues;
	}
	
}
