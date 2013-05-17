package org.dynamicschema.reification.columnconstraint;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Joiner;

public class PrimaryKey extends ColumnConstraint {
	
	public static String PRIMARY_KEY = "PRIMARY KEY";
	
	private List<String> columnsNames;

	public PrimaryKey(List<String> columnsNames) {
		this.columnsNames = columnsNames;
	}
	
	public PrimaryKey(String ...columnsNames) {
		this(Arrays.asList(columnsNames));
	}

	/**
	 * @return the columnsNames
	 */
	public List<String> getColumnsNames() {
		return columnsNames;
	}

	@Override
	public String toString() {
		return PRIMARY_KEY + "("+Joiner.on(", ").join(columnsNames)+")";
	}

}
