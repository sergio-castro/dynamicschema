package org.dynamicschema.reification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.dynamicschema.reification.columnconstraint.ColumnConstraint;

import com.google.common.base.Joiner;

public class ColumnModel implements Iterable<Column> {

	private List<Column> columns;
	private List<ColumnConstraint> columnsConstraints;
	private Table table;
	
	public ColumnModel() {
		this(new ArrayList<Column>());
	}
	
	public ColumnModel(List<Column> columns) {
		this(columns, new ArrayList<ColumnConstraint>());
	}
	
	public ColumnModel(List<Column> columns, List<ColumnConstraint> columnsConstraints) {
		setColumns(columns);
		setColumnsConstraints(columnsConstraints);
	}

	public void setColumnsConstraints(List<ColumnConstraint> columnsConstraints) {
		this.columnsConstraints = columnsConstraints;
	}
	
	public void setColumns(List<Column> columns) {
		this.columns = new ArrayList<Column>();
		for(Column column : columns) {
			addColumn(column);
		}
	}
	
	public List<Column> getColumns() {
		return Collections.unmodifiableList(columns);
	}
	
	public void addColumn(Column column) {
		columns.add(column);
		column.attach(this);
	}
	
	public int getIndex(Column column) {
		return columns.indexOf(column);
	}

	public Table getTableOrThrow() {
		if(table == null)
			throw new RuntimeException("ColumnModel not attached to a Table");
		return getTable();
	}
	
	public Table getTable() {
		return table;
	}
	
	public void attach(Table table) {
		this.table = table;
	}
	
	public List<String> getColumnsNames() {
		List<String> columnsNames = new ArrayList<String>();
		for(Column column : columns) {
			columnsNames.add(column.getName());
		}
		return columnsNames;
	}
	
	public int size() {
		return columns.size();
	}

	@Override
	public Iterator<Column> iterator() {
		return columns.iterator();
	}

	public String toColumnModelDefString() {
		List<String> columnDefs = new ArrayList<String>();
		for(Column column : columns) {
			columnDefs.add(column.toColumnDefString());
		}
		StringBuilder sb = new StringBuilder(Joiner.on(", ").join(columnDefs));
		
		String constraints = Joiner.on(", ").join(columnsConstraints);
		if(!constraints.isEmpty())
			sb.append(", " + constraints);
		return sb.toString();
	}

}
