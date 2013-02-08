package org.dynamicschema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class RelationModel implements Iterable<Relation> {

	protected Table baseTable;
	protected Relation baseRelation; //value of the node
	private List<Relation> joinRelations; //children of the node
	

	
	public RelationModel() {
		setJoinRelations(new ArrayList<Relation>());
	}	

	/*
	public RelationModel(Relation baseRelation) {
		this(baseRelation, new ArrayList<Relation>());
	}

	public RelationModel(Relation baseRelation, List<Relation> joinRelations) {
		setBaseRelation(baseRelation);
		setJoinRelations(joinRelations);
	}
*/
	
/*
	public RelationalContext getContext() {
		return context;
	}

	public void setContext(RelationalContext context) {
		this.context = context;
	}
*/

	public List<Relation> getEagerRelations() {
		List<Relation> eagerRelations = new ArrayList<Relation>();
		for(Relation relation : getJoinRelations()) {
			if(relation.getFetching().equals(Fetching.EAGER))
				eagerRelations.add(relation);
		}
		return eagerRelations;
	}
	/*
	public List<Relation> getJoinRelations() {
		return new ArrayList<Relation>();
	}
	*/
	public List<Relation> getJoinRelations() {
		return Collections.unmodifiableList(joinRelations);
	}
	
	private void setJoinRelations(List<Relation> joinRelations) {
		this.joinRelations = joinRelations;
	}
	
	public void setJoinRelations(Relation ...joinRelations) {
		setJoinRelations(Arrays.<Relation>asList(joinRelations));
	}
	
	public int size() {
		return getJoinRelations().size();
	}
	
	public Table getBaseTable() {
		return baseTable;
	}

	public final String BASE_RELATION_NAME = "BASE RELATION";
	public void setBaseTable(Table baseTable) {
		this.baseTable = baseTable;
		String baseRelationName = BASE_RELATION_NAME+"_"+baseTable.getName();
		setBaseRelation(createBaseRelation(baseRelationName, baseTable));
	}

	protected Relation createBaseRelation(String baseRelationName, Table baseTable) {
		return new Relation(this, baseRelationName, baseTable);
	}

	private void setBaseRelation(Relation baseRelation) {
		this.baseRelation = baseRelation;
	}

	public Relation getBaseRelation() {
		return baseRelation;
	}

	@Override
	public Iterator<Relation> iterator() {
		return getJoinRelations().iterator();
	}


}
