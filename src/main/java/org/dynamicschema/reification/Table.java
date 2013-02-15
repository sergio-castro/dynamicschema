package org.dynamicschema.reification;

import java.util.List;

import org.dynamicschema.sql.RelationCondition;
import org.dynamicschema.sql.SqlCondition;

public interface Table {

	public String getName();
	
	public ColumnModel getColumnModel();
	
	public List<TableRelation> getTableRelations();
	
	public RelationCondition getFiltering();
	
	public SqlCondition evalFiltering();
	
	public List<String> getColumnValues();
	
	public String getColumnValue(String columnName);

	public String fromClauseName();
	
}
