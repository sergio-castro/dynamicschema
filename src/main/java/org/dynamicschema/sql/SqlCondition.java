package org.dynamicschema.sql;

import static org.dynamicschema.sql.Sql.AND;
import static org.dynamicschema.sql.Sql.NOT;
import static org.dynamicschema.sql.Sql.OR;

public class SqlCondition {

	private StringBuilder builder;
	
	public SqlCondition(String ...conditions) {
		builder = new StringBuilder();
		for(String condition : conditions)
			and(condition);
	}
	
	public boolean isEmpty() {
		return builder.length() == 0;
	}
	
	public SqlCondition and(String condition) {
		if(condition == null || condition.isEmpty())
			return this;
		if(isEmpty()) 
			builder.append(condition);
		else {
			and();
			builder.append(condition);
		}
		return this;
	}
	
	public SqlCondition or(String condition) {
		if(condition == null || condition.isEmpty())
			return this;
		if(isEmpty()) 
			builder.append(condition);
		else {
			or();
			builder.append(condition);
		}
		return this;
	}
	
	public SqlCondition and() {
		builder.append(" "+AND+" ");
		return this;
	}
	
	public SqlCondition or() {
		builder.append(" "+OR+" ");
		return this;
	}
	
	public SqlCondition not() {
		builder.append(" "+NOT+" ");
		return this;
	}
	
	public SqlCondition isNull(Object object) {
		builder.append(object);
		builder.append(" IS NULL ");
		return this;
	}
	
	public SqlCondition isNotNull(Object object) {
		builder.append(object);
		builder.append(" IS NOT NULL ");
		return this;
	}
	
	public SqlCondition eq(Object leftEquals, Object rightEquals) {
		builder.append(leftEquals);
		builder.append(" = ");
		builder.append(rightEquals);
		return this;
	}
	
	public SqlCondition in(Object variable, String subquery) {
		builder.append(variable);
		builder.append(" IN (");
		builder.append(subquery);
		builder.append(") ");
		return this;
	}
	
	public SqlCondition group(String conditions) {
		builder.append("(");
		builder.append(conditions);
		builder.append(")");
		return this;
	}
	
	public SqlCondition group(SqlCondition conditions) {
		return group(conditions.toString());
	}
	
	public SqlCondition append(String s) {
		builder.append(s);
		return this;
	}
	
	@Override
	public String toString() {
		return builder.toString();
	}
}
