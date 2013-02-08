package org.dynamicschema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ColumnModel implements Iterable<Column> {

	protected List<Column> columns;

	public ColumnModel(Column ...columns) {
		setColumns(columns);
	}

	private void setColumns(List<Column> columns) {
		this.columns = columns;
		for(int i=0; i<columns.size(); i++)
			columns.get(i).setIndex(i);
	}
	public void setColumns(Column ...columnsArray) {
		setColumns(Arrays.<Column>asList(columnsArray));
	}
	public List<Column> getColumns() {
		return Collections.unmodifiableList(columns);
	}
	
	/*
	public List<Column> getColumns() {
		return Collections.unmodifiableList(columns);
	}
	*/
	public List<String> getColumnsNames() {
		List<String> columnsNames = new ArrayList<String>();
		for(Column column : columns) {
			columnsNames.add(column.getName());
		}
		return columnsNames;
	}
	
	public void setTable(Table table) {
		for(Column column : columns) {
			column.setTable(table);
		}
	}
	
	public int size() {
		return columns.size();
	}

	@Override
	public Iterator<Column> iterator() {
		return columns.iterator();
	}

}
