package org.dynamicschema.reification;

import static org.dynamicschema.sql.Sql.CREATE_TABLE;
import static org.dynamicschema.sql.Sql.DROP_TABLE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dynamicschema.context.ContextedQueryBuilder;
import org.dynamicschema.context.RelationalContextManager;
import org.dynamicschema.sql.RelationBuilder;
import org.dynamicschema.sql.RelationCondition;
import org.dynamicschema.sql.SqlCondition;
import org.dynamicschema.visitor.SchemaVisitor;
import org.dynamicschema.visitor.context.SelectBuilderEagerRelationsVisitor;
import org.dynamicschema.visitor.context.SelectBuilderSpecificRelationVisitor;

import com.google.common.base.Joiner;

public class DBTable extends AbstractTable {
	private String name;
	private ColumnModel columnModel;
	private Schema schema;
	private RelationCondition filtering;
	
	public DBTable(String tableName, ColumnModel columns) {
		setName(tableName);
		setColumnModel(columns);
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setColumnModel(ColumnModel columnModel) {
		this.columnModel = columnModel;
		columnModel.attach(this);
	}

	public ColumnModel getColumnModel() {
		return columnModel;
	}
	
	public String getColumnValue(String columnName) {
		return getColumnModel().getColumn(columnName).toString();
	}
	
	public List<TableRelation> getTableRelations() {
		return getSchemaOrThrow().getRelationModel().getTableRelations(this);
	}
	
	public RelationCondition getFiltering() {
		return filtering;
	}

	public SqlCondition evalFiltering() {
		return getFiltering().eval(this);
	}
	
	public void setFiltering(RelationCondition filtering) {
		this.filtering = filtering;
	}
	
	public String filteringCondition(RelationalContextManager ctx) {
		if (getFiltering() == null)
				return "";
		else
			return getFiltering().eval(this).toString();//TODO fix
	}
	

	public String getColumnName(Column column) {
		return getName()+"."+column.getSimpleName();
	}

	public TableRelation getTableRelation(String relationName, String tableRole) {
		Relation relation = getSchemaOrThrow().getRelationModel().getRelation(relationName);
		return relation.getTableRelation(tableRole);
	}
	
	public ContextedQueryBuilder lazyRelationSelect(TableRelation tableRelation, Map<String, Object> columnBindings) {
		SelectBuilderSpecificRelationVisitor selectBuilderVisitor = new SelectBuilderSpecificRelationVisitor(tableRelation, columnBindings);
		selectBuilderVisitor.visit();
		return selectBuilderVisitor.getQueryBuilder();
	}
	
	public ContextedQueryBuilder select() {
		SelectBuilderEagerRelationsVisitor selectBuilderVisitor = new SelectBuilderEagerRelationsVisitor(this);
		selectBuilderVisitor.visit();
		return selectBuilderVisitor.getQueryBuilder();
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
	
	public Schema getSchemaOrThrow() {
		if(schema == null)
			throw new RuntimeException("Table not attached to a Schema");
		return getSchema();
	}
	
	public Schema getSchema() {
		return schema;
	}
	
	public void attach(Schema schema) {
		this.schema = schema;
	}
	
	public String fromClauseName() {
		return name;
	}
	
	public void accept(SchemaVisitor visitor) {
		if(visitor.doVisit(this)) {
			getColumnModel().accept(visitor);
		}
	}
	
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof DBTable))
			return false;
		DBTable table = (DBTable)o;
		return this.getName().equals(table.getName());
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}
	

	
	
}


