package org.dynamicschema.sql;

import java.util.ArrayList;
import java.util.List;

import org.dynamicschema.sql.util.EnumerationBuilder;
import org.dynamicschema.sql.util.SqlCondition;


public class QueryBuilder {

	private EnumerationBuilder columns = new EnumerationBuilder();
	private EnumerationBuilder tables = new EnumerationBuilder();
	private List<Join> joins = new ArrayList<Join>();
	private SqlCondition where = new SqlCondition();
	private EnumerationBuilder orderBy = new EnumerationBuilder();
	private EnumerationBuilder groupBy = new EnumerationBuilder();
	private SqlCondition having = new SqlCondition();
	private Integer limit;

	public QueryBuilder addColumn(String columnName) {
		columns.add(columnName);
		return this;
	}
	
	public QueryBuilder addTable(String tableName) {
		tables.add(tableName);
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
	
	public QueryBuilder addHaving(String havingString) {
		having.and(havingString);
		return this;
	}
	
	public QueryBuilder setLimit(Integer limit) {
		this.limit = limit;
		return this;
	}

	public String getColumns() {
		return columns.toString();
	}
	
	public SqlCondition where() {
		return where;
	}
	
	public List<Join> joins() {
		return joins;
	}
	
	public String getTablesString() {
		return tables.toString();
	}
	
	public String getWhereString() {
		return where.toString();
	}
	
	public String getOrderByString() {
		return orderBy.toString();
	}
	
	public String getGroupByString() {
		return groupBy.toString();
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
