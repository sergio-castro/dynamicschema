package org.dynamicschema.reification;

import static org.dynamicschema.sql.Sql.CREATE_TABLE;
import static org.dynamicschema.sql.Sql.DROP_TABLE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dynamicschema.context.ContextedQueryBuilder;
import org.dynamicschema.context.RelationalContextManager;
import org.dynamicschema.reification.columnconstraint.ColumnConstraint;
import org.dynamicschema.reification.columnconstraint.PrimaryKey;
import org.dynamicschema.sql.RelationBuilder;
import org.dynamicschema.sql.RelationCondition;
import org.dynamicschema.sql.Sql;
import org.dynamicschema.sql.SqlCondition;
import org.dynamicschema.visitor.SchemaVisitor;
import org.dynamicschema.visitor.context.QueryFilteringSpecifier;
import org.dynamicschema.visitor.context.SelectBuilderEagerRelationsVisitor;
import org.dynamicschema.visitor.context.SelectBuilderSpecificRelationVisitor;

import com.google.common.base.Joiner;

public class DBTable extends AbstractTable {
	private String name;
	private ColumnModel columnModel;
	private Schema schema;
	private RelationCondition filtering;
	private List<RelationCondition> moreFilterings; //other filterings that could be added 
	
	public DBTable(String tableName, ColumnModel columns) {
		setName(tableName);
		setColumnModel(columns);
		moreFilterings = new ArrayList<RelationCondition>();
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
	
	public String col(String columnName) {
		Column col  = getColumnModel().getColumn(columnName);
		if(col != null)
				return col.toString();
		return null;
	}
	
	public List<TableRelation> getTableRelations() {
		return getSchemaOrThrow().getRelationModel().getTableRelations(this);
	}
	
	public TableRelation getTabRelation(String relName, String role){
		
		List<TableRelation> tabRelations = getTableRelations();
		for (TableRelation tableRelation : tabRelations) {
			 Relation relation = tableRelation.getRelation();
			 
			if(relation.getName().equals(relName)){
				if(!relation.isBinaryRecursive())
					return tableRelation;
				
				if(role == null)
						throw new RuntimeException("Expecting Role name to differentiate tables in recursive relation");
				TableRelation tabRelRecursive = relation.getTableRelationWithRole(role);
				return tabRelRecursive;
			}	
		}
		return null;
	}
	
	public RelationCondition getFiltering() {
		
		return filtering;
	}

	public SqlCondition evalFiltering() {
		
		SqlCondition defFilteringCond = null;	
		RelationCondition filtering = getFiltering();
		
		if(filtering != null)
			defFilteringCond = filtering.eval(this);
		else
			defFilteringCond = new SqlCondition();
	
		List<RelationCondition> otherFilterings= getTableFilterings();
		for (RelationCondition cond : otherFilterings) {
			defFilteringCond.and(cond.eval(this).toString());
		}
		
		return defFilteringCond;
	}
	
	public void setFiltering(RelationCondition filtering) {
		
//		if(this.name.equals("Language")){
//			System.out.println("BEWARE SETTING FILTERING IN Language");
//		}
		
		this.filtering = filtering;
	}
	
	public void resetFilterings(RelationCondition emptyFiltering){
		setFiltering(emptyFiltering);
		int sizeOtherFilterings = moreFilterings.size();
		for (int i = 0; i < sizeOtherFilterings; i++) {
			moreFilterings.remove(i);
		}
		
	}
	
	public void addMoreFiltering(RelationCondition newFilterFiltering){
		moreFilterings.add(newFilterFiltering);
	}
	
	public List<RelationCondition> getTableFilterings(){
		return this.moreFilterings;
	}

	
//	public String filteringCondition(RelationalContextManager ctx) {
//		if (getFiltering() == null)
//				return "";
//		else
//			return getFiltering().eval(this).toString();
//	}
	

	public String getColumnName(Column column) {
		return getName()+"."+column.getSimpleName();
	}
	
	public ContextedQueryBuilder select() {
		return select(new ArrayList<Relation>());
	}
	
	public ContextedQueryBuilder select(List<Relation> relations2Visit){
		return select(relations2Visit,null);
	}
	
	public ContextedQueryBuilder select(QueryFilteringSpecifier specifier){
		return select(new ArrayList<Relation>(), specifier);
	}
	
	public ContextedQueryBuilder select(List<Relation> relations2Visit, QueryFilteringSpecifier specifier){
		SelectBuilderEagerRelationsVisitor visitor = new SelectBuilderEagerRelationsVisitor(this, relations2Visit);
		visitor.setSpecifier(specifier);
		visitor.visit();
		return visitor.getQueryBuilder();
	}
//Added 	

	public ContextedQueryBuilder lazyRelationSelect(TableRelation tableRelation, Map<String, Object> columnBindings, QueryFilteringSpecifier specifier) {
		return lazyRelationSelect(tableRelation, columnBindings, new ArrayList<Relation>(), specifier);
	}
	
	public ContextedQueryBuilder lazyRelationSelect(TableRelation tableRelation, Map<String, Object> columnBindings) {
		return lazyRelationSelect(tableRelation, columnBindings, new ArrayList<Relation>(), null);
	}
	
	public ContextedQueryBuilder lazyRelationSelect(TableRelation tableRelation, Map<String, Object> columnBindings, List<Relation> relToTraverse) {
		return lazyRelationSelect(tableRelation, columnBindings, relToTraverse, null);
	}

		
	public ContextedQueryBuilder lazyRelationSelect(TableRelation tableRelation, Map<String, Object> columnBindings, List<Relation> relations2visit, QueryFilteringSpecifier specifier){
		SelectBuilderSpecificRelationVisitor visitor = new SelectBuilderSpecificRelationVisitor(tableRelation, columnBindings);
		visitor.setRelationsToTraverse(relations2visit);
		visitor.setQueryFilteringSpecifier(specifier);
		visitor.visit();
		return visitor.getQueryBuilder();	
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
	
	public List<String> createIndexStatements(){
		return getColumnModel().toColumnModelIndicesDefString();
	}
	
	public String dropTableStatement() {
		return DROP_TABLE + " "+ Sql.IF_EXISTS + " " + name + ";";
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

	public List<String> getIDColumnNames() {
		
		List<String> idNamesList = new ArrayList<String>();
		List<ColumnConstraint> colConstr = getColumnModel().getColumnsConstraints();
		PrimaryKey pkConstr = null;
		for (ColumnConstraint constr : colConstr) {
			if( constr instanceof PrimaryKey)
					pkConstr = (PrimaryKey) constr;
		}
		
		if(pkConstr == null)
			throw new RuntimeException("No Primary Key was found!!!");

		List<String> colNames = pkConstr.getColumnsNames();
	
		for (String colID : colNames) {
			idNamesList.add(colID);
		}
		return idNamesList;
	}

	



	


	

	
	
}


