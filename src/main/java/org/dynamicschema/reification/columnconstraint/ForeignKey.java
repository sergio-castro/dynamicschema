package org.dynamicschema.reification.columnconstraint;

import java.util.List;

import com.google.common.base.Joiner;

public class ForeignKey extends ColumnConstraint {

	public static String FOREIGN_KEY = "FOREIGN KEY";
	public static String REFERENCES = "REFERENCES";
	
	private List<String> localColumnsNames;
	private String foreignTableName;
	private List<String> foreignColumnsNames;
	
	public ForeignKey(List<String> localColumnsNames, String foreignTableName, List<String> foreignColumnsNames) {
		this.localColumnsNames = localColumnsNames;
		this.foreignTableName = foreignTableName;
		this.foreignColumnsNames = foreignColumnsNames;
	}
	
	@Override
	public String toString() {
		return FOREIGN_KEY + "("+Joiner.on(", ").join(localColumnsNames)+")" + " " + REFERENCES + " " + foreignTableName + "("+ Joiner.on(", ").join(foreignColumnsNames) + ")";
	}
	
}
