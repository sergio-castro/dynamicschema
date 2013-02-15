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
	
	public ContextedTable(DBTable table, String alias) {
		this(table, alias, new HashMap<String, Object>());
	}
	
	public ContextedTable(DBTable table, String alias, Map<String, Object> bindings) {
		this.table = table;
		this.alias = alias;
		this.bindings = bindings;
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
	public String getColumnValue(String columnName) {
		Object binding = getColumnBinding(columnName);
		if(binding != null)
			return binding.toString();
		else
			return alias + "." + table.getColumnValue(columnName);
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
	
}
