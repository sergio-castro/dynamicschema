package org.dynamicschema.visitor;

import org.dynamicschema.reification.DBTable;

public class DropSchemaScriptVisitor extends ScriptBuilderVisitor {

	@Override
	public boolean doVisit(DBTable table) {
		sb.append(table.dropTableStatement());
		sb.append("\n");
		return false; //do not visit the children of the table
	}
	
}
