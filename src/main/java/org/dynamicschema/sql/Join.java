package org.dynamicschema.sql;

import static org.dynamicschema.sql.Sql.FULL_JOIN;
import static org.dynamicschema.sql.Sql.INNER_JOIN;
import static org.dynamicschema.sql.Sql.LEFT_JOIN;
import static org.dynamicschema.sql.Sql.RIGHT_JOIN;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Joiner;


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
		return Sql.JOIN;
	}
	
	@Override
	public String toString() {
		String join = joinKeyword() + " " + Joiner.on(" ").join(tables);
		if(getOn() != null)
			join += " " + Sql.ON + " " + getOn();
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
			return INNER_JOIN;
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
			return LEFT_JOIN;
		}
	}
	
	public static class RightJoin extends Join {
		public RightJoin(String on, List tables) {
			super(on, tables);
		}

		public RightJoin(String on, Object ...tables) {
			super(on, tables);
		}

		@Override
		public String joinKeyword() {
			return RIGHT_JOIN;
		}
	}

	public static class FullJoin extends Join {
		public FullJoin(String on, List tables) {
			super(on, tables);
		}

		public FullJoin(String on, Object ...tables) {
			super(on, tables);
		}

		@Override
		public String joinKeyword() {
			return FULL_JOIN;
		}
	}

}

