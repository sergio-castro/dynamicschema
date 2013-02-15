package org.dynamicschema.visitor.context;

import java.util.List;

import org.dynamicschema.context.RelationNode;
import org.dynamicschema.context.TableNode;
import org.dynamicschema.reification.Table;
import org.dynamicschema.sql.ContextedQueryBuilder;
import org.dynamicschema.sql.Join.LeftJoin;
import org.dynamicschema.sql.util.SqlCondition;


public class SelectBuilderEagerRelationsVisitor extends ContextedTableRelationsVisitor {
	
	private ContextedQueryBuilder queryBuilder;

	public SelectBuilderEagerRelationsVisitor(Table table) {
		super(table);
		queryBuilder = new ContextedQueryBuilder(ctx);
	}

	public ContextedQueryBuilder getQueryBuilder() {
		return queryBuilder;
	}

	@Override
	protected void onVisitBaseTable(TableNode tableNode) {
		Table contextedTable = tableNode.getContextedTable(ctx);
		queryBuilder.addTable(contextedTable.fromClauseName());
		queryBuilder.addColumns(contextedTable.getColumnValues());
		queryBuilder.addWhere(contextedTable.evalFiltering());
	}
	
	@Override
	protected void onVisitRelatedTable(TableNode tableNode) {
		Table contextedTable = tableNode.getContextedTable(ctx);
		queryBuilder.addColumns(contextedTable.getColumnValues());
		queryBuilder.addWhere(contextedTable.evalFiltering());
	}

	@Override
	protected void onVisitedTableRelation(RelationNode relationNode) {
		List<Table> joinedTables = relationNode.getJoinedTables(ctx);
		SqlCondition sqlCondition = relationNode.getRelation().getCondition().eval(relationNode.getRelationArgs(ctx));
		if(relationNode.getRelation().isBinary()) {
			Table joinedTable = joinedTables.get(0);
			LeftJoin leftJoin = new LeftJoin(joinedTable, sqlCondition);
			queryBuilder.addJoin(leftJoin);
		} else {
			for(Table table : joinedTables) {
				queryBuilder.addTable(table.fromClauseName());
			}
			queryBuilder.addWhere(sqlCondition);
		}
		
	}

	

}
