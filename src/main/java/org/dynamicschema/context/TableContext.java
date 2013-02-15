package org.dynamicschema.context;

import java.util.Map;


public class TableContext {

	private String alias;
	private int offset;
	private Map<String, Object> bindings;
	
	public TableContext() {
	}
	
	public TableContext(String alias, int offset) {
		this.alias = alias;
		this.offset = offset;
	}
	
	public TableContext(String alias, int offset, Map<String, Object> bindings) {
		this.alias = alias;
		this.offset = offset;
		this.bindings = bindings;
	}
	
	public String getAlias() {
		return alias;
	}
	
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public void setOffset(int offset) {
		this.offset = offset;
	}

	public Map<String, Object> getBindings() {
		return bindings;
	}

	public void setBindings(Map<String, Object> bindings) {
		this.bindings = bindings;
	}

}
