package org.dynamicschema.reification;

import java.util.List;

import org.dynamicschema.sql.RelationCondition;
import org.dynamicschema.sql.SqlCondition;

public interface Table {

	public String getName();
	
	public ColumnModel getColumnModel();
	
	/**
	 * The relations in which this table participate
	 * @return
	 */
	public List<TableRelation> getTableRelations();
	
	/**
	 * A filtering condition that affects all the relations where this table participates
	 * @return
	 */
	public RelationCondition getFiltering();
	

	public SqlCondition evalFiltering();
	
	/**
	 * The bound column values (column names if there are no any bindings)
	 * @return
	 */
	public List<String> getColumnValues();
	
	/**
	 * The value of a column (the column name if it is not bound to a value)
	 * @param columnName
	 * @return
	 */
	public String col(String columnName);

	public String fromClauseName();
	
}
