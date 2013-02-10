package org.dynamicschema.sql;

/**
 * SQL constants
 * @author sergioc
 *
 */
public class Sql {

	public  static final String SELECT = "SELECT";
	public  static final String AS = "AS";
	public  static final String DISTINCT = "DISTINCT";
	public  static final String TOP = "TOP";
	public  static final String INTO = "INTO";
	public  static final String FROM = "FROM";
	public  static final String WHERE = "WHERE";
	public  static final String LIKE = "LIKE";
	public  static final String IN = "IN";
	public  static final String BETWEEN = "BETWEEN";
	public  static final String ORDER_BY = "ORDER BY";
	public  static final String ASC = "ASC";
	public  static final String DESC = "DESC";
	public  static final String GROUP_BY = "GROUP BY";
	public  static final String HAVING = "HAVING";
	public  static final String LIMIT = "LIMIT";
	public  static final String JOIN = "JOIN";
	public  static final String INNER_JOIN = "INNER JOIN";
	public  static final String LEFT_JOIN = "LEFT JOIN";
	public  static final String RIGHT_JOIN = "RIGHT JOIN"; //warning: some DBMS do not support right joins (e.g., SQLite)
	public  static final String FULL_JOIN = "FULL JOIN";
	public  static final String ON = "ON";
	
	public static final String AND = "AND";
	public static final String OR = "OR";
	public static final String NOT = "NOT";

	public static final String INSERT = "INSERT";
	public static final String VALUES = "VALUES";
	public static final String DELETE = "DELETE";
	public static final String UPDATE = "UPDATE";
	public static final String SET = "SET";
	
	
	//DDL
	public static final String CREATE_TABLE = "CREATE TABLE";
	public static final String DROP_TABLE = "DROP TABLE";
}
