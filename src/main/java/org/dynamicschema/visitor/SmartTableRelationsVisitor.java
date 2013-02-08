package org.dynamicschema.visitor;

import java.util.ArrayList;
import java.util.List;

import org.dynamicschema.Relation;


/*
 * Detects cycles.
 * If a relation has already been visited, it will ignore it
 */
public class SmartTableRelationsVisitor extends TableRelationsVisitor {
	private List<Relation> visitedRelations;
	
	public SmartTableRelationsVisitor() {
		visitedRelations = new ArrayList<Relation>();
	}
	
	@Override
	public void visit(Relation relation) {
		if(!visitedRelations.contains(relation)) {
			visitedRelations.add(relation);
			super.visit(relation);
		}
	}
}
