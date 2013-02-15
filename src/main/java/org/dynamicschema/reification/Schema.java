package org.dynamicschema.reification;

import java.util.ArrayList;
import java.util.List;

import org.dynamicschema.visitor.CreateSchemaScriptVisitor;
import org.dynamicschema.visitor.DropSchemaScriptVisitor;
import org.dynamicschema.visitor.SchemaVisitor;

public class Schema {

	private List<DBTable> tables;
	private RelationModel relationModel;
	
	
	public DBTable getTable(String tableName) {
		for(DBTable table : getTables()) {
			if(table.getName().equals(tableName))
				return table;
		}
		return null;
	}
	
	public List<DBTable> getTables() {
		return tables;
	}

	public void setTables(List<DBTable> tables) {
		this.tables = new ArrayList<DBTable>();
		for(DBTable table : tables)
			addTable(table);
	}

	public void addTable(DBTable table) {
		tables.add(table);
		table.attach(this);
	}

	public RelationModel getRelationModel() {
		return relationModel;
	}

	public void setRelationModel(RelationModel relationModel) {
		this.relationModel = relationModel;
		relationModel.attach(this);
	}
	
	
	public void accept(SchemaVisitor visitor) {
		if(visitor.doVisit(this)) {
			for(DBTable table : getTables()) {
				table.accept(visitor);
			}
			//TODO visit the relations also
		}
		
	}
	
	public String createSchemaScript() {
		CreateSchemaScriptVisitor visitor = new CreateSchemaScriptVisitor();
		accept(visitor);
		return visitor.getScript();
	}
	
	public String dropSchemaScript() {
		DropSchemaScriptVisitor visitor = new DropSchemaScriptVisitor();
		accept(visitor);
		return visitor.getScript();
	}
	
}
