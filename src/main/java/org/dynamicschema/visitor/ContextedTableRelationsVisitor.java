package org.dynamicschema.visitor;

import org.dynamicschema.context.IRelationalContextManager;
import org.dynamicschema.reification.Fetching;

public class ContextedTableRelationsVisitor extends TableRelationsVisitor {

	protected IRelationalContextManager ctx;
	
	public ContextedTableRelationsVisitor(IRelationalContextManager ctx) {
		this(ctx, null);
	}
	
	public ContextedTableRelationsVisitor(IRelationalContextManager ctx, Fetching fetching) {
		super(fetching);
		this.ctx=ctx;
	}
	
	public IRelationalContextManager getRelationalContext() {
		return ctx;
	}

}
