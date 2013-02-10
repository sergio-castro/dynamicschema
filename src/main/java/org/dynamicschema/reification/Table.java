package org.dynamicschema.reification;

import static org.dynamicschema.sql.Sql.CREATE_TABLE;
import static org.dynamicschema.sql.Sql.DROP_TABLE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dynamicschema.context.IRelationalContextManager;
import org.dynamicschema.context.ProxyRelationalContextManager;
import org.dynamicschema.context.RelationalContextManager;
import org.dynamicschema.sql.ContextedQueryBuilder;
import org.dynamicschema.sql.util.RelationBuilder;
import org.dynamicschema.visitor.RelationalContextInitializerVisitor;
import org.dynamicschema.visitor.SelectBuilderEagerRelationsVisitor;
import org.dynamicschema.visitor.SelectBuilderLazyRelationVisitor;

import com.google.common.base.Joiner;



public abstract class Table<ColumnModelType extends ColumnModel, RelationModelType extends RelationModel> {
	//table name
	private String name;
	//table columns
	private ColumnModelType columnModel;
	//table relations
	private RelationModelType relationModel;
	private IRelationalContextManager ctx;

	
	public Table(String tableName, ColumnModelType columns) {
		setName(tableName);
		setColumnModel(columns);
	}


	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setColumnModel(ColumnModelType columnModel) {
		this.columnModel = columnModel;
		columnModel.attach(this);
	}

	public ColumnModelType getColumnModel() {
		return columnModel;
	}
	
	public RelationModelType getRelationModel() {
		if(relationModel == null) { //lazy initialization of the relationModel
			setRelationModel(createRelationModel());
		}
		return relationModel;
	}
	
	/*
	 * By default this method just creates an empty relation model
	 * This class needs to be overridden when relations are present
	 */
	protected RelationModelType createRelationModel() {
		return (RelationModelType) new RelationModel(); 
	}
	
	public void setRelationModel(RelationModelType relationModel) {
		relationModel.setBaseTable(this);
		this.relationModel = relationModel;
	}
	
	public Relation getBaseRelation() {
		return getRelationModel().getBaseRelation();
	}
	
	public IRelationalContextManager getRelationalContext() {
		if(ctx == null) {
			ctx = createRelationalContext();
		}
		return ctx;
	}
	
	private IRelationalContextManager createRelationalContext() {
		IRelationalContextManager ctx = new RelationalContextManager();
		configureRelationalContext(ctx);
		return ctx;
	}

	public void configureRelationalContext(IRelationalContextManager ctx) {
		new RelationalContextInitializerVisitor(ctx).visit(this);
	}

	public String getColumnName(Column column) {
		return getName()+"."+column.getSimpleName();
	}

	public ContextedQueryBuilder lazyRelationSelect(Map<Column, Object> columnBindings, Relation relation) {
		Map<Relation, Map<Column, Object>> bindings = new HashMap<Relation, Map<Column, Object>>();
		bindings.put(getBaseRelation(), columnBindings);

		IRelationalContextManager relationalContext = new RelationalContextManager();
		RelationalContextInitializerVisitor contextInitializerVisitor = new RelationalContextInitializerVisitor(relationalContext);
		//contextInitializerVisitor.doVisit(getRelationModel().getBaseRelation(), this);
		contextInitializerVisitor.visit(relation);

		IRelationalContextManager bindingContext = new ProxyRelationalContextManager(bindings, relationalContext);
		SelectBuilderLazyRelationVisitor selectBuilderVisitor = new SelectBuilderLazyRelationVisitor(bindingContext);
		selectBuilderVisitor.visit(relation);
		ContextedQueryBuilder queryBuilder = selectBuilderVisitor.getQueryBuilder();
		queryBuilder.setRelationalContext(relationalContext);
		return queryBuilder;
	}
	
	public ContextedQueryBuilder select() {
		IRelationalContextManager relationalContext = getRelationalContext();
		SelectBuilderEagerRelationsVisitor selectBuilderVisitor = new SelectBuilderEagerRelationsVisitor(relationalContext);
		selectBuilderVisitor.visit(this);
		ContextedQueryBuilder queryBuilder = selectBuilderVisitor.getQueryBuilder();
		queryBuilder.setRelationalContext(relationalContext);
		return queryBuilder;
	}

	
	public String insertStatement(Map<Column, Object> bindings) {
		List<Column> columns = new ArrayList<Column>();
		List<Object> values = new ArrayList<Object>();
		
		for(Entry<Column, Object> binding: bindings.entrySet()) {
			columns.add(binding.getKey());
			values.add(binding.getValue());
		}
		
		String insertStatement = "INSERT INTO "+getName()+" ("+ Joiner.on(", ").join(columns) +") VALUES ("+ Joiner.on(", ").join(values)+")";
		return insertStatement;
	}
	
	public String updateStatement(Map<Column, Object> updateBindings, Map<Column, Object> condition) {
		return updateStatement(updateBindings, new RelationBuilder(condition, "=", ", ").toString());
	}
	
	public String updateStatement(Map<Column, Object> updateBindings, String whereCondition) {
		return "UPDATE "+getName()+" SET "+new RelationBuilder(updateBindings, "=", ", ")+" WHERE "+whereCondition;
	}
	
	public String deleteStatement(Map<Column, Object> condition) {
		return deleteStatement(new RelationBuilder(condition, "=", ", ").toString());
	}
	
	public String deleteStatement(String whereCondition) {
		return "DELETE FROM "+getName()+" WHERE "+whereCondition;
	}
	
	
	@Override
	public String toString() {
		return getName();
	}
	
	public String toTableDefString() {
		StringBuilder sb = new StringBuilder(name + "(");
		sb.append(getColumnModel().toColumnModelDefString());
		sb.append(")");
		return sb.toString();
	}
	
	public String createTableStatement() {
		return CREATE_TABLE + " " + toTableDefString() + ";";
	}
	
	public String dropTableStatement() {
		return DROP_TABLE + " " + name + ";";
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Table))
			return false;
		Table table = (Table)o;
		return this.getName().equals(table.getName());
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}
	
	
}


