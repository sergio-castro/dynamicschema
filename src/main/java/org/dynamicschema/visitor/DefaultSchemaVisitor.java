package org.dynamicschema.visitor;

import org.dynamicschema.reification.Column;
import org.dynamicschema.reification.ColumnModel;
import org.dynamicschema.reification.Relation;
import org.dynamicschema.reification.RelationModel;
import org.dynamicschema.reification.Schema;
import org.dynamicschema.reification.DBTable;

public class DefaultSchemaVisitor implements SchemaVisitor {

	@Override
	public boolean doVisit(Schema schema) {
		return true;
	}

	@Override
	public boolean doVisit(DBTable table) {
		return true;
	}

	@Override
	public boolean doVisit(ColumnModel columnModel) {
		return true;
	}

	@Override
	public void doVisit(Column column) {
	}

	@Override
	public boolean doVisit(RelationModel relationModel) {
		return true;
	}

	@Override
	public void doVisit(Relation relation) {
	}

}
