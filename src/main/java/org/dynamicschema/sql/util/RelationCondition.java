package org.dynamicschema.sql.util;

import org.dynamicschema.context.IRelationalContextManager;

public abstract class RelationCondition {

	public abstract SqlCondition eval(IRelationalContextManager ctx);
	
}
