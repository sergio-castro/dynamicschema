package org.dynamicschema.visitor;

import org.dynamicschema.context.IRelationalContextManager;
import org.dynamicschema.context.RelationContext;
import org.dynamicschema.context.TableContext;
import org.dynamicschema.reification.Fetching;
import org.dynamicschema.reification.Relation;
import org.dynamicschema.reification.Table;

public class RelationalContextInitializerVisitor extends ContextedTableRelationsVisitor {

	public RelationalContextInitializerVisitor(IRelationalContextManager ctx) {
		super(ctx, Fetching.EAGER);
	}

	@Override
	public boolean doVisit(Relation relation, Table table) {
		RelationContext relationContext = ctx.getOrCreateRelationContext(relation);
		TableContext tableContext = relationContext.getTableContext(table);
		if(tableContext != null)
			return false;
			
		tableContext = ctx.createTableContext(relation, table);
		relationContext.addTableContext(table, tableContext);
		
		if(!relation.isBaseRelation() && table.getRelationModel().size()>0) {
			ctx.getOrCreateRelationContext(table.getBaseRelation()).addTableContext(table, tableContext);
			return true;
		} else
			return false;
	}



}
