package org.dynamicschema.reification;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dynamicschema.annotation.Role;
import org.dynamicschema.sql.RelationCondition;
import org.dynamicschema.visitor.SchemaVisitor;

public class Relation {

	private RelationModel relationModel;
	private String name;
	private RelationCondition condition;
	private List<TableOccurrence> cardinality;
	private List<Fetching> fetchings;
	
	public static Fetching defaultFetching(List<TableOccurrence> cardinality, int pos) {
		if(cardinality.size() > 2) //terciary or bigger relationship
			return Fetching.LAZY;
		DBTable targetTable = cardinality.get(pos).getTable();
		for(int i = 0; i < cardinality.size(); i++) {
			if(i == pos)
				continue;
			if(cardinality.get(i).getTable().equals(targetTable)) //recursive relationship
				return Fetching.LAZY;
			if(!cardinality.get(i).getOccurrence().equals(Occurrence.ONE)) //if there is at least table with a cardinality different than one, then the fetching is lazy
				return Fetching.LAZY;
		}
		return Fetching.EAGER;
	}
	
	public static List<Fetching> defaultFetchings(List<TableOccurrence> cardinality) {
		List<Fetching> fetchings = new ArrayList<Fetching>();
		for(int i = 0; i<cardinality.size(); i++) {
			fetchings.add(defaultFetching(cardinality, i));
		}
		return fetchings;
	}

	
	public Relation(String name, List<TableOccurrence> cardinality, RelationCondition condition) {
		this(name, cardinality, condition, defaultFetchings(cardinality));
	}
	
	public Relation(String name, List<TableOccurrence> cardinality, RelationCondition condition, List<Fetching> fetchings) {
		setName(name);
		setCardinality(cardinality);
		setCondition(condition);
		setFetchings(fetchings);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if(name==null || name.isEmpty())
			throw new RuntimeException(name+" is not a valid name for a relation");
		this.name = name;
	}
	
	public List<DBTable> getTables() {
		List<DBTable> tables = new ArrayList<DBTable>();
		for(TableOccurrence tableOccurrence : cardinality) {
			tables.add(tableOccurrence.getTable());
		}
		return tables;
	}

	public RelationCondition getCondition() {
		return condition;
	}

	public void setCondition(RelationCondition condition) {
		this.condition = condition;
	}

	public List<TableOccurrence> getCardinality() {
		return cardinality;
	}

//	public boolean isUnary() {
//		return cardinality.size() == 1;
//	}

	public boolean isBinary() {
		return cardinality.size() == 2;
	}

	public boolean isTerciary() {
		return cardinality.size() == 3;
	}

	public void setCardinality(List<TableOccurrence> cardinality) {
		this.cardinality = cardinality;
	}

	public List<Fetching> getFetchings() {
		return fetchings;
	}

	public void setFetchings(List<Fetching> fetchings) {
		this.fetchings = fetchings;
	}
	
	public void attach(RelationModel relationModel) {
		this.relationModel = relationModel;
	}
	
	public List<TableRelation> getTableRelations(DBTable table) {
		List<TableRelation> tableRelations = new ArrayList<TableRelation>();
		for(int i=0; i<cardinality.size(); i++) {
			if(cardinality.get(i).getTable().equals(table)) {
				tableRelations.add(getTableRelation(i));
			}
		}
		return tableRelations;
	}



//	public boolean includes(Table table) {
//		for(Table auxTable : getTables()) {
//			if(auxTable.equals(table))
//				return true;
//		}
//		return false;
//	}
	
	public RelationModel getRelationModelOrThrow() {
		if(relationModel == null)
			throw new RuntimeException("Relation not attached to a RelationModel");
		return getRelationModel();
	}
	
	public RelationModel getRelationModel() {
		return relationModel;
	}
	
	public Schema getSchemaOrThrow() {
		return getRelationModelOrThrow().getSchemaOrThrow();
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public void accept(SchemaVisitor visitor) {
		visitor.doVisit(this);
	}
	
	public TableRelation getTableRelation(Table table) {
		TableRelation tableRelation = null;
		for(int i=0; i<cardinality.size(); i++) {
			if(cardinality.get(i).getTable().getName().equals(table.getName())) {
				tableRelation = new TableRelation(this, i);
			}
		}
		return tableRelation;
	}
	
	public int getRoleIndex(String role) {
		List<String> roles = getRoles();
		for(int i=0; i<roles.size(); i++) {
			if(role.equals(roles.get(i))) {
				return i;
			}
		}
		throw new RuntimeException("Unrecognized role: " + role + " in relation: " + name);
	}
	
	public Table getTable(String role) {
		return cardinality.get(getRoleIndex(role)).getTable();
	}
	
	public Table getTable(int index) {
		return cardinality.get(index).getTable();
	}
	
	public Occurrence getOccurrence(String role) {
		return cardinality.get(getRoleIndex(role)).getOccurrence();
	}
	
	public Occurrence getOccurrence(int index) {
		return cardinality.get(index).getOccurrence();
	}
	
	public Fetching getFetching(String role) {
		return fetchings.get(getRoleIndex(role));
	}
	
	public Fetching getFetching(int index) {
		return fetchings.get(index);
	}
	
	public TableRelation getTableRelation(String role) {
		return new TableRelation(this, getRoleIndex(role));
	}
	
	private TableRelation getTableRelation(int index) {
		return new TableRelation(this, index);
	}
	
	public List<String> getRoles() {
		String[] roles = new String[cardinality.size()];
		Method evalMethod = condition.getCustomEvalMethod();
		if(evalMethod == null) {
			throw new RuntimeException("The relation should define a join condition by means of an eval method in its RelationCondition class");
		}
		Annotation[][] paramAnnotations = evalMethod.getParameterAnnotations();
		for(int i = 0; i<paramAnnotations.length; i++) {
			for(Annotation annotation : paramAnnotations[i]) {
				if(annotation.annotationType().equals(Role.class)) {
					Role roleAnnotation = (Role)annotation;
					roles[i] = roleAnnotation.value();
					break;
				}
			}
		}
		return Arrays.asList(roles);
	}

	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Relation))
			return false;
		Relation relation = (Relation)o;
		return this.getName().equals(relation.getName());
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}
	
}
