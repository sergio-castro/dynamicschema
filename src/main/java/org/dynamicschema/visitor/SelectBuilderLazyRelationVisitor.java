package org.dynamicschema.visitor;

import org.dynamicschema.context.IRelationalContextManager;
import org.dynamicschema.context.ProxyRelationalContextManager;
import org.dynamicschema.reification.Relation;
import org.dynamicschema.reification.Table;

public class SelectBuilderLazyRelationVisitor extends SelectBuilderEagerRelationsVisitor {

	public SelectBuilderLazyRelationVisitor(IRelationalContextManager ctx) {
		super(ctx);
	}

	@Override
	public boolean doVisit(Relation relation) {
		processBaseRelation(relation);
		return true;
	}
	
	@Override
	public void visit(Table table) {
		SelectBuilderEagerRelationsVisitor selectBuilderEagerRelationsVisitor = new SelectBuilderEagerRelationsVisitor(getProxiedContext()); 
		selectBuilderEagerRelationsVisitor.setQueryBuilder(getQueryBuilder());
		selectBuilderEagerRelationsVisitor.setBaseRelation(table.getBaseRelation());
		
		selectBuilderEagerRelationsVisitor.visit(table);
	}
	
	private IRelationalContextManager getProxiedContext() {
		return ProxyRelationalContextManager.class.cast(ctx).getProxiedContext(); //context without the column bindings corresponding to the base table with the lazy relationship
	}
	
	@Override
	protected void addBaseRelationConditions(Table table) {
		getQueryBuilder().addWhere(table.getBaseRelation().joinCondition(getProxiedContext()));
	}
}
