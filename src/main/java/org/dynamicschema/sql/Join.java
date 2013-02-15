package org.dynamicschema.sql;

import static org.dynamicschema.sql.Sql.FULL_JOIN;
import static org.dynamicschema.sql.Sql.INNER_JOIN;
import static org.dynamicschema.sql.Sql.LEFT_JOIN;
import static org.dynamicschema.sql.Sql.RIGHT_JOIN;

import org.dynamicschema.reification.Table;


public class Join {

	private Table table;
	private String on;

	public Join(Table table) {
		this(table, (String)null);
	}
	
	public Join(Table table, SqlCondition on) {
		setTable(table);
		if(on != null)
			setOn(on.toString());
	}
	
	public Join(Table table, String on) {
		setTable(table);
		setOn(on);
	}

	public Table getTable() {
		return table;
	}
	public void setTable(Table table) {
		this.table = table;
	}
	
	public String getOn() {
		return on;
	}
	public void setOn(String on) {
		this.on = on;
	}
	
	public String joinKeyword() {
		return Sql.JOIN;
	}
	
	@Override
	public String toString() {
		String join = joinKeyword() + " " + table.fromClauseName();
		if(getOn() != null)
			join += " " + Sql.ON + " " + getOn();
		return join;
	}

	public static class InnerJoin extends Join {
		public InnerJoin(Table table) {
			super(table);
		}

		public InnerJoin(Table table, SqlCondition on) {
			super(table, on);
		}
		
		public InnerJoin(Table table, String on) {
			super(table, on);
		}

		@Override
		public String joinKeyword() {
			return INNER_JOIN;
		}
	}

	public static class LeftJoin extends Join {
		public LeftJoin(Table table) {
			super(table);
		}
		
		public LeftJoin(Table table, SqlCondition on) {
			super(table, on);
		}
		
		public LeftJoin(Table table, String on) {
			super(table, on);
		}

		@Override
		public String joinKeyword() {
			return LEFT_JOIN;
		}
	}
	
	public static class RightJoin extends Join {
		public RightJoin(Table table) {
			super(table);
		}
		
		public RightJoin(Table table, SqlCondition on) {
			super(table, on);
		}
		
		public RightJoin(Table table, String on) {
			super(table, on);
		}

		@Override
		public String joinKeyword() {
			return RIGHT_JOIN;
		}
	}

	public static class FullJoin extends Join {
		public FullJoin(Table table) {
			super(table);
		}
		
		public FullJoin(Table table, SqlCondition on) {
			super(table, on);
		}
		
		public FullJoin(Table table, String on) {
			super(table, on);
		}

		@Override
		public String joinKeyword() {
			return FULL_JOIN;
		}
	}

}

