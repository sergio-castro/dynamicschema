package org.dynamicschema.sql;

import org.dynamicschema.context.IRelationalContextManager;

public class ContextedQueryBuilder extends QueryBuilder {

	private IRelationalContextManager relationalContext;
	
	
	
	public IRelationalContextManager getRelationalContext() {
		return relationalContext;
	}



	public void setRelationalContext(IRelationalContextManager relationalContext) {
		this.relationalContext = relationalContext;
	}

/*
    public ContextedQueryBuilder() {}

	public ContextedQueryBuilder(IRelationalContextManager ctx) {
		setRelationalContext(ctx);
	}
*/
}
