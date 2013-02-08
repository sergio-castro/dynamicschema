package org.dynamicschema.sql;

import java.util.Arrays;
import java.util.List;

import org.dynamicschema.sql.util.EnumerationBuilder;


public class Join {

	private List tables;
	private String on;

	public Join(List tables) {
		this(null, tables);
	}
	
	public Join(String on, List tables) {
		setOn(on);
		setTables(tables);
	}
	
	public Join(Object ...tables) {
		this(null, tables);
	}
	
	public Join(String on, Object ...tables) {
		this(on, Arrays.asList(tables));
	}

	public List getTables() {
		return tables;
	}
	public void setTables(List tables) {
		this.tables = tables;
	}
	public void setTables(Object ...tables) {
		setTables(Arrays.asList(tables));
	}
	
	public String getOn() {
		return on;
	}
	public void setOn(String on) {
		this.on = on;
	}
	
	public String joinKeyword() {
		return SqlConstants.JOIN;
	}
	
	@Override
	public String toString() {
		String join = joinKeyword() + " " + new EnumerationBuilder(tables, " ");
		if(getOn() != null)
			join += " " + SqlConstants.ON + " " + getOn();
		return join;
	}

	public static class InnerJoin extends Join {
		public InnerJoin(String on, List tables) {
			super(on, tables);
		}

		public InnerJoin(String on, Object ...tables) {
			super(on, tables);
		}

		@Override
		public String joinKeyword() {
			return SqlConstants.INNER_JOIN;
		}
	}

	public static class LeftJoin extends Join {
		public LeftJoin(String on, List tables) {
			super(on, tables);
		}

		public LeftJoin(String on, Object ...tables) {
			super(on, tables);
		}

		@Override
		public String joinKeyword() {
			return SqlConstants.LEFT_JOIN;
		}
	}

}

