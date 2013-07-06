package org.dynamicschema.reification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.dynamicschema.reification.columnconstraint.ColumnConstraint;
import org.dynamicschema.reification.columnconstraint.ForeignKey;
import org.dynamicschema.reification.columnconstraint.PrimaryKey;
import org.dynamicschema.sql.Sql;
import org.dynamicschema.sql.SqlCondition;
import org.dynamicschema.visitor.SchemaVisitor;


import com.google.common.base.Joiner;

public class ColumnModel implements Iterable<Column> {

	private List<Column> columns;
	private List<ColumnConstraint> columnsConstraints;
	private DBTable table;

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

	public void setColumnsNames(List<String> columnsNames) {
		List<Column> columns = new ArrayList<Column>();
		for(String columnName : columnsNames) {
			columns.add(new Column(columnName));
		}
		setColumns(columns);
	}

	public Column getColumn(String columnName) {
		for(Column column : getColumns()) {
			if(column.getSimpleName().equals(columnName))
				return column;
		}
		return null;
	}

	public List<Column> getColumns() {
		return Collections.unmodifiableList(columns);
	}

	/**
	 * @return the columnsConstraints
	 */
	public List<ColumnConstraint> getColumnsConstraints() {
		return columnsConstraints;
	}


	public void addColumn(Column column) {
		columns.add(column);
		column.attach(this);
	}

	public int getIndex(Column column) {
		return columns.indexOf(column);
	}

	public DBTable getTableOrThrow() {
		if(table == null)
			throw new RuntimeException("ColumnModel not attached to a Table");
		return getTable();
	}

	public DBTable getTable() {
		return table;
	}

	public void attach(DBTable table) {
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

		//Primary Key are already handled by the primary key itself
		//Only add foreign keys
		//		List<ColumnConstraint> forKeyConstr = getForeignKeyOnly();

		String constraints = Joiner.on(", ").join(getColumnsConstraints());
		if(!constraints.isEmpty())
			sb.append(", " + constraints);
		return sb.toString();
	}


	/*
	 * generate SQL code for creating indices for speeding up the queries 
	 */
	public List<String> toColumnModelIndicesDefString(){

		List<String> indices = new ArrayList<String>();
		List<ColumnConstraint> constrList = getColumnsConstraints();
		int fkCounter= 1;
		for (ColumnConstraint colConstr : constrList) {

			if(colConstr instanceof PrimaryKey){
				String index = createIndex(colConstr, true, 0);
				indices.add(index);
			}else if (colConstr instanceof ForeignKey){
				String index = createIndex(colConstr, false, fkCounter);
				fkCounter++;
				indices.add(index);
			}
		}
		return indices;
	}


	private String createIndex(ColumnConstraint colConstr, boolean primaryKey, int fkCounter){

		StringBuilder sb = new StringBuilder();
		String tabName = getTableOrThrow().getName();

		if(primaryKey){
			sb.append(Sql.CREATE_UNIQUE_INDEX+ " "+ PrimaryKey.PK_INDEX+"_"+ tabName+ "\n");
			sb.append(Sql.ON+ " " + tabName + " " + ((PrimaryKey)colConstr).toStringIndex());
		}else {
				sb.append(Sql.CREATE_INDEX+ " " + ForeignKey.FK_INDEX+ "_"+tabName+fkCounter+"\n");
				sb.append(Sql.ON+ " "+ tabName+ " " +((ForeignKey)colConstr).toStringIndex());
			}
	
		return sb.toString();
	}

	public void accept(SchemaVisitor visitor) {
		if(visitor.doVisit(this)) {
			for(Column column : getColumns()) {
				column.accept(visitor);
			}
		}
	}

}
