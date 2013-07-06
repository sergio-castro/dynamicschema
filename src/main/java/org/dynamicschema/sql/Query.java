package org.dynamicschema.sql;

import static org.dynamicschema.sql.Sql.FROM;
import static org.dynamicschema.sql.Sql.GROUP_BY;
import static org.dynamicschema.sql.Sql.HAVING;
import static org.dynamicschema.sql.Sql.LIMIT;
import static org.dynamicschema.sql.Sql.ORDER_BY;
import static org.dynamicschema.sql.Sql.SELECT;
import static org.dynamicschema.sql.Sql.WHERE;
import static org.dynamicschema.sql.Sql.DISTINCT;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;


public class Query {

	private final String SEPARATOR = "\n";
	
	private String columns;
	private String tables;
	private List<Join> joinConditions;
	private String where;
	private String orderBy;
	private String groupBy;
	private String having;
	private Integer limit;
	
	
	
	public Query() {
		
	}
	
	public Query(String columns, String tables) {
		this(columns, tables, null, null, null, null, null);
	}
	
	public Query(String columns, String tables, List<Join> joinConditions, String where) {
		this(columns, tables, joinConditions, where, null, null, null);
	}
	
	public Query(String columns, String tables, List<Join> joinConditions, String where, String orderBy) {
		this(columns, tables, joinConditions, where, orderBy, null, null);
	}
	
	public Query(String columns, String tables, List<Join> joinConditions, String where, String orderBy, String groupBy) {
		this(columns, tables, joinConditions, where, orderBy, groupBy, null);
	}
	
	public Query(String columns, String tables, List<Join> joinConditions, String where, String orderBy, String groupBy, String having) {
		setColumns(columns);
		setTables(tables);
		setJoinConditions(joinConditions);
		setWhere(where);
		setOrderBy(orderBy);
		setGroupBy(groupBy);
		setHaving(having);
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getTables() {
		return tables;
	}

	public void setTables(String tables) {
		this.tables = tables;
	}

	public List<Join> getJoinConditions() {
		return joinConditions;
	}

	public void setJoinConditions(List<Join> joinConditions) {
		this.joinConditions = joinConditions;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}

	public String getHaving() {
		return having;
	}

	public void setHaving(String having) {
		this.having = having;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public String select() {
		return SELECT + " " + DISTINCT + " " + columns;
	}
	
	public String from() {
		return FROM + " " + tables;
	}
	
	public String joinConditions() {
		//Add
		List<Join> jConditions = new ArrayList<Join>();
		for (int i = joinConditions.size(); i > 0 ; i--) {
			jConditions.add(joinConditions.get(i-1));
		}
		//End
		String joinConditionsString = "";
		if(joinConditions != null) {
			joinConditionsString = Joiner.on(SEPARATOR).join(jConditions); // before: joinConditions
		}
		return joinConditionsString;
	}
	
	public String where() {
		if(nullOrEmpty(where))
			return "";
		return WHERE + " " + where;
	}
	
	public String orderBy() {
		if(nullOrEmpty(orderBy))
			return "";
		return ORDER_BY + " " + orderBy;
	}
	
	public String groupBy() {
		if(nullOrEmpty(groupBy))
			return "";
		return GROUP_BY + " " + groupBy;
	}
	
	public String having() {
		if(nullOrEmpty(having))
			return "";
		return HAVING + " " + having;
	}
	
	public String limit() {
		if(limit==null)
			return "";
		return LIMIT + " " + limit;
	}
	
	@Override
	public String toString() {
		return select() + 
			SEPARATOR + from() + 
			SEPARATOR + joinConditions() +
			SEPARATOR + where() +
			SEPARATOR + orderBy() + 
			SEPARATOR + groupBy() +
			SEPARATOR + having() + 
			SEPARATOR + limit();
	}
	
	private boolean nullOrEmpty(String s) {
		return s==null || s.isEmpty();
	}
	
}
