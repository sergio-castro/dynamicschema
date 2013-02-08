package org.dynamicschema.visitor;

import java.util.ArrayList;
import java.util.List;

import org.dynamicschema.Column;
import org.dynamicschema.Fetching;
import org.dynamicschema.Relation;
import org.dynamicschema.Table;
import org.dynamicschema.context.IRelationalContextManager;
import org.dynamicschema.context.TableContext;
import org.dynamicschema.sql.ContextedQueryBuilder;
import org.dynamicschema.sql.Join.LeftJoin;


public class SelectBuilderEagerRelationsVisitor extends ContextedTableRelationsVisitor {
	
	private ContextedQueryBuilder queryBuilder;
	private Relation baseRelation;
	
	public SelectBuilderEagerRelationsVisitor(IRelationalContextManager ctx) {
		super(ctx, Fetching.EAGER);
		queryBuilder = new ContextedQueryBuilder();
	}

	public Relation getBaseRelation() {
		return baseRelation;
	}

	public void setBaseRelation(Relation baseRelation) {
		this.baseRelation = baseRelation;
	}

	public ContextedQueryBuilder getQueryBuilder() {
		return queryBuilder;
	}

	public void setQueryBuilder(ContextedQueryBuilder queryBuilder) {
		this.queryBuilder = queryBuilder;
	}

	private TableContext getTableContext(Relation relation, Table table) {
		return ctx.getRelationContext(relation).getTableContext(table);
	}
	
	private List<String> getTableAliases(Relation relation) {
		List<String> aliases = new ArrayList<String>();
		for(Table table : relation.getTables()) {
			TableContext tableContext = getTableContext(relation, table);
			aliases.add(tableContext.aliasedTable(table));
		}
		return aliases;
	}
	
	protected void processBaseRelation(Relation relation) {
		for(Table table : relation.getTables()) {
			TableContext tableContext = getTableContext(relation, table);
			queryBuilder.addTable(tableContext.aliasedTable(table));
		}
		queryBuilder.addWhere(relation.joinCondition(ctx));
	}
	
	protected void processJoinRelation(Relation relation) {
		LeftJoin join = new LeftJoin(relation.joinCondition(ctx), getTableAliases(relation));
		queryBuilder.addJoin(join);
	}
	
	@Override
	public boolean doVisit(Relation relation) {
		if(relation.isBaseRelation()) {
			if(baseRelation == null) {
				baseRelation = relation;
				processBaseRelation(relation);
			} else {
				return false;
			}
		} else {
			processJoinRelation(relation);
		}
		return true;
	}
	
	@Override
	public boolean doVisit(Relation relation, Table table) {
		for(Column column : table.getColumnModel()) {
			queryBuilder.addColumn(ctx.getColumnValue(relation, column).toString());
		}
		if(!relation.isBaseRelation())
			addBaseRelationConditions(table);
		return true;
	}
	
	protected void addBaseRelationConditions(Table table) {
		queryBuilder.addWhere(table.getBaseRelation().joinCondition(ctx));
	}
	
	@Override
	public String toString() {
		return queryBuilder.toString();
	}

}
