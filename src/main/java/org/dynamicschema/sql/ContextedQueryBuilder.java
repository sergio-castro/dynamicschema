package org.dynamicschema.sql;

import org.dynamicschema.context.RelationalContextManager;

/**
 * @author sergioc
 *
 */
public class ContextedQueryBuilder extends QueryBuilder {

	private RelationalContextManager relationalContext;

	/*
    public ContextedQueryBuilder() {}

*/
	
	public ContextedQueryBuilder(RelationalContextManager relationalContext) {
		this.relationalContext = relationalContext;
	}

	public RelationalContextManager getRelationalContext() {
		return relationalContext;
	}

//	public void setRelationalContext(RelationalContextManager relationalContext) {
//		this.relationalContext = relationalContext;
//	}



}
