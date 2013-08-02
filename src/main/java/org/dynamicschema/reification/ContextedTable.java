package org.dynamicschema.reification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dynamicschema.sql.RelationCondition;
import org.dynamicschema.sql.Sql;
import org.dynamicschema.sql.SqlCondition;

public class ContextedTable extends AbstractTable {

	private DBTable table;
	private String alias;
	private Map<String, Object> bindings;
	private int offset;
	private String test;
	
	public ContextedTable(DBTable table, String alias) {
		this(table, alias, 0, new HashMap<String, Object>());
	}
	
	public ContextedTable(DBTable table, String alias, int offset, Map<String, Object> bindings) {
		this.table = table;
		this.alias = alias;
		this.bindings = bindings;
		this.offset = offset;
	}

	public DBTable getTable() {
		return table;
	}

	public String getAlias() {
		return alias;
	}

	public Map<String, Object> getBindings() {
		return bindings;
	}

	@Override
	public String getName() {
		return alias;
	}


	public void setTest(String val){
		this.test = val;
	}
	public String getTest(){
		return test;
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	@Override
	public ColumnModel getColumnModel() {
		return table.getColumnModel();
	}

	@Override
	public List<TableRelation> getTableRelations() {
		return table.getTableRelations();
	}
	
	
	private Object getColumnBinding(String columnName) {
		Object binding = null;
		if(bindings != null)
			binding = bindings.get(columnName);
		return binding;
	}
	
	@Override
	public String col(String columnName) {
		Object binding = getColumnBinding(columnName);
		if(binding != null)
			return binding.toString();
		else
			return alias + "." + table.col(columnName);
	}
	
	public String fromClauseName() {
		return table.getName() + " " + Sql.AS + " "+ alias;
	}

	public RelationCondition getFiltering() {
		return table.getFiltering();
	}
	
	public SqlCondition evalFiltering() {
		return getFiltering().eval(this);
	}
	
	
	//Add
	public List<String> getIDColumnNames(){
		return table.getIDColumnNames();
	}

	public boolean isFromSameTableThan(ContextedTable table){
		return table.getTable().equals(this.getTable());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.table.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return this.table.equals(obj);
	}
	
	
	
	
}
