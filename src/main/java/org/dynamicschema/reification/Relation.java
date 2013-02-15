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

	public Relation(String name, List<RelationMember> relationMembers, RelationCondition condition) {
		this(name, relationMembers, condition, defaultFetchings(relationMembers));
	}
	
	public Relation(String name, List<RelationMember> relationMembers, RelationCondition condition, List<Fetching> fetchings) {
		setName(name);
		setRelationMembers(relationMembers);
		setCondition(condition);
		setFetchings(fetchings);
	}

	private static Fetching defaultFetching(List<RelationMember> relationMembers, int pos) {
		if(relationMembers.size() > 2) //terciary or bigger relationship
			return Fetching.LAZY;
		String targetTableName = relationMembers.get(pos).getTableName();
		for(int i = 0; i < relationMembers.size(); i++) {
			if(i == pos)
				continue;
			if(relationMembers.get(i).getTableName().equals(targetTableName)) //recursive relationship
				return Fetching.LAZY;
			if(!relationMembers.get(i).getOccurrence().equals(Occurrence.ONE)) //if there is at least table with a cardinality different than one, then the fetching is lazy
				return Fetching.LAZY;
		}
		return Fetching.EAGER;
	}
	
	private static List<Fetching> defaultFetchings(List<RelationMember> relationMembers) {
		List<Fetching> fetchings = new ArrayList<Fetching>();
		for(int i = 0; i<relationMembers.size(); i++) {
			fetchings.add(defaultFetching(relationMembers, i));
		}
		return fetchings;
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
			tables.add(getSchemaOrThrow().getTable(relationMember.getTableName()));
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
			if(relationMembers.get(i).getTableName().equals(table)) {
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
		for(int i=0; i<relationMembers.size(); i++) {
			if(relationMembers.get(i).getTableName().equals(table.getName())) {
				tableRelation = new TableRelation(this, i);
			}
		}
		return tableRelation;
	}
	
	public int getRoleIndex(DBTable role) {
		List<String> roles = getRoles();
		for(int i=0; i<roles.size(); i++) {
			if(role.equals(roles.get(i))) {
				return i;
			}
		}
		throw new RuntimeException("Unrecognized role: " + role + " in relation: " + name);
	}
	
	public Table getTable(DBTable role) {
		String tableName = relationMembers.get(getRoleIndex(role)).getTableName();
		return getSchemaOrThrow().getTable(tableName);
	}
	
	public Table getTable(int index) {
		String tableName = relationMembers.get(index).getTableName();
		return getSchemaOrThrow().getTable(tableName);
	}
	
	public Occurrence getOccurrence(DBTable role) {
		return relationMembers.get(getRoleIndex(role)).getOccurrence();
	}
	
	public Occurrence getOccurrence(int index) {
		return relationMembers.get(index).getOccurrence();
	}
	
	public Fetching getFetching(DBTable role) {
		return fetchings.get(getRoleIndex(role));
	}
	
	public Fetching getFetching(int index) {
		return fetchings.get(index);
	}
	
	public TableRelation getTableRelation(DBTable role) {
		return new TableRelation(this, getRoleIndex(role));
	}
	
	private TableRelation getTableRelation(int index) {
		return new TableRelation(this, index);
	}
	
	public List<String> getRoles() {
		String[] roles = new String[relationMembers.size()];
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
