package org.dynamicschema.sql;

import java.util.ArrayList;
import java.util.List;

import org.dynamicschema.sql.util.SqlCondition;

import com.google.common.base.Joiner;


public class QueryBuilder {

	private List<String> columns = new ArrayList<String>();
	private List<String> tables = new ArrayList<String>();
	private List<Join> joins = new ArrayList<Join>();
	private SqlCondition where = new SqlCondition();
	private List<String> orderBy = new ArrayList<String>();
	private List<String> groupBy = new ArrayList<String>();
	private SqlCondition having = new SqlCondition();
	private Integer limit;

	
	public QueryBuilder addColumns(List<String> columnsNames) {
		columns.addAll(columnsNames);
		return this;
	}
	
	public QueryBuilder addColumn(String columnName) {
		columns.add(columnName);
		return this;
	}
	
	public QueryBuilder addTable(String tableName) {
		tables.add(tableName);
		return this;
	}

	public QueryBuilder addWhere(SqlCondition whereCondition) {
		if(whereCondition != null)
			return addWhere(whereCondition.toString());
		else
			return this;
	}
	
	public QueryBuilder addWhere(String whereString) {
		where.and(whereString);
		return this;
	}

	 public QueryBuilder addJoin(Join joinCondition) {
		joins.add(joinCondition);
		return this;
	}

	public QueryBuilder addOrderBy(String orderByString) {
		orderBy.add(orderByString);
		return this;
	}
	
	public QueryBuilder addGroupBy(String groupByString) {
		groupBy.add(groupByString);
		return this;
	}
	
	public QueryBuilder addHaving(SqlCondition havingCondition) {
		if(havingCondition != null)
			return addHaving(havingCondition.toString());
		else
			return this;
	}
	
	public QueryBuilder addHaving(String havingString) {
		having.and(havingString);
		return this;
	}
	
	public QueryBuilder setLimit(Integer limit) {
		this.limit = limit;
		return this;
	}

	public String getColumns() {
		return Joiner.on(", ").join(columns);
	}
	
	public SqlCondition where() {
		return where;
	}
	
	public List<Join> joins() {
		return joins;
	}
	
	public String getTablesString() {
		return Joiner.on(", ").join(tables);
	}
	
	public String getWhereString() {
		return where.toString();
	}
	
	public String getOrderByString() {
		return Joiner.on(", ").join(orderBy);
	}
	
	public String getGroupByString() {
		return Joiner.on(", ").join(groupBy);
	}
	
	public String getHavingString() {
		return having.toString();
	}
	
	public Query build() {
		Query query = new Query(
				getColumns(), 
				getTablesString(), 
				joins(),
				getWhereString(),
				getOrderByString(), 
				getGroupByString(), 
				getHavingString()
		);
		query.setLimit(limit);
		return query;
	}
	
	@Override
	public String toString() {
		return build().toString();
	}
	
}
