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
	private List<RelationMember> relationMembers;
	private List<Fetching> fetchings;

	
	private static List<Fetching> defaultFetchings(List<RelationMember> relationMembers) {
		List<Fetching> fetchings = new ArrayList<Fetching>();
		for(int i = 0; i<relationMembers.size(); i++) {
			fetchings.add(defaultFetching(relationMembers, i));
		}
		return fetchings;
	}
	
	private static Fetching defaultFetching(List<RelationMember> relationMembers, int pos) {
		if(relationMembers.size() > 2) //terciary or bigger relationship
			return Fetching.LAZY;
		String targetTableName = relationMembers.get(pos).getTable().getName();
		for(int i = 0; i < relationMembers.size(); i++) { //there are only two tables in the relation (binary relation)
			if(i == pos)
				continue;
			if(relationMembers.get(i).getTable().getName().equals(targetTableName)) //recursive relationship
				return Fetching.LAZY;
			if(!relationMembers.get(i).getOccurrence().equals(Occurrence.ONE)) //if there is at least one table with a cardinality different than ONE, then the fetching is lazy
				return Fetching.LAZY;
		}
		return Fetching.EAGER;
	}
	
	
	public Relation(String name, List<RelationMember> relationMembers, RelationCondition condition) {
		this(name, relationMembers, condition, defaultFetchings(relationMembers));
	}
	
	public Relation(String name, List<RelationMember> relationMembers, RelationCondition condition, List<Fetching> fetchings) {
		setName(name);
		setRelationMembers(relationMembers);
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
		for(RelationMember relationMember : relationMembers) {
			tables.add(relationMember.getTable());
		}
		return tables;
	}

	public RelationCondition getCondition() {
		return condition;
	}

	public void setCondition(RelationCondition condition) {
		this.condition = condition;
	}

	public List<RelationMember> getRelationMembers() {
		return relationMembers;
	}

//	public boolean isUnary() {
//		return relationMembers.size() == 1;
//	}

	public boolean isBinary() {
		return relationMembers.size() == 2;
	}

	public boolean isTerciary() {
		return relationMembers.size() == 3;
	}

	public void setRelationMembers(List<RelationMember> relationMembers) {
		this.relationMembers = relationMembers;
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
		for(int i=0; i<relationMembers.size(); i++) {
			RelationMember relationMember = relationMembers.get(i);
			if(relationMember.getTable().getName().equals(table.getName())) {
				tableRelations.add(getTableRelation(i));
			}
		}
		return tableRelations;
	}

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
	
	public Table getTable(int index) {
		return relationMembers.get(index).getTable();
	}
	
	public Table getTableWithRole(String role) {
		return relationMembers.get(getRoleIndex(role)).getTable();
	}

	private TableRelation getTableRelation(int index) {
		return new TableRelation(this, index);
	}
	
	public TableRelation getTableRelationWithRole(String role) {
		return getTableRelation(getRoleIndex(role));
	}
	
	public Occurrence getOccurrence(int index) {
		return relationMembers.get(index).getOccurrence();
	}
	
	public Occurrence getOccurrenceForRole(String role) {
		return relationMembers.get(getRoleIndex(role)).getOccurrence();
	}
	
	public Fetching getFetching(int index) {
		return fetchings.get(index);
	}

	public Fetching getFetchingForRole(String role) {
		return fetchings.get(getRoleIndex(role));
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
	
	/**
	 * Answers a list of the roles in the relation according to the Role annotation in the eval method.
	 * In most cases these annotations are optional. 
	 * However, if the same table appears more than once in a relation (e.g., a recursive relation) this annotation should be present in all the occurrences to solve ambiguities.
	 * @return
	 */
	public List<String> getRoles() {
		String[] roles = new String[relationMembers.size()];
		Method evalMethod = condition.getCustomEvalMethod();
		if(evalMethod == null) {
			throw new RuntimeException("The relation should define a join condition by means of an eval method in its RelationCondition class");
		}
		Annotation[][] paramAnnotations = evalMethod.getParameterAnnotations();
		for(int i = 0; i<paramAnnotations.length; i++) {
			for(Annotation annotation : paramAnnotations[i]) {
				if(annotation.annotationType().equals(Role.class)) { //we have just found a Role annotation
					Role roleAnnotation = (Role)annotation;
					roles[i] = roleAnnotation.value();
					break; //ignore other any annotation in the current parameter
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
