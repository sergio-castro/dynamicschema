package org.dynamicschema.context;

import org.dynamicschema.sql.QueryBuilder;

/**
 * @author sergioc
 *
 */
public class ContextedQueryBuilder extends QueryBuilder {

	private RelationalContextManager relationalContext;

	
	public ContextedQueryBuilder(RelationalContextManager relationalContext) {
		this.relationalContext = relationalContext;
	}

	public RelationalContextManager getRelationalContext() {
		return relationalContext;
	}

}
