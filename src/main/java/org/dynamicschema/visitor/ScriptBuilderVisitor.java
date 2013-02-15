package org.dynamicschema.visitor;

public class ScriptBuilderVisitor extends DefaultSchemaVisitor {

	protected StringBuilder sb;
	
	public ScriptBuilderVisitor() {
		sb = new StringBuilder();
	}
	
	public String getScript() {
		return sb.toString();
	}
	
}
