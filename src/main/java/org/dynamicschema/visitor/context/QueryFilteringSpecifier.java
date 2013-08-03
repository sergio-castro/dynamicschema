package org.dynamicschema.visitor.context;

import java.util.HashMap;
import java.util.Map;

import org.dynamicschema.reification.Relation;
import org.dynamicschema.reification.Table;
import org.dynamicschema.sql.RelationCondition;

/**
 *  Encapsulate table filterings for a given relation, for a specific query
 * @author esp
 *
 */
public class QueryFilteringSpecifier {
	
	private Map<String, RelationCondition> specificQueryFilterings;
	

	public QueryFilteringSpecifier() {
		this.specificQueryFilterings =new HashMap<String, RelationCondition>();
	}

	
	
	public void addQuerFiltering(Relation relation, Table table, RelationCondition filtering){
		addQueryFiltering(relation, table, filtering, null);
		
	}
	public void addQueryFiltering(Relation relation, Table table, RelationCondition filtering, String role){
		
		String key = getKey(relation, table, role);
		RelationCondition val = this.specificQueryFilterings.get(key);
		if(val != null)
			throw new RuntimeException("Unexpected: Specific filtering already exists for following key: "+ key);
		this.specificQueryFilterings.put(key,filtering);
	}
	
	/**
	 * @return the specificFilterings
	 */
	public Map<String, RelationCondition> getSpecificFilterings() {
		return specificQueryFilterings;
	}
	
	
	public RelationCondition getFilteringFor(Relation rel, Table table, String role){
		String key = getKey(rel, table, role);
		RelationCondition cond = this.specificQueryFilterings.get(key);
		return cond;
	}
	
	public RelationCondition getFilteringFor(Relation rel, Table table){
			return getFilteringFor(rel, table, null);
	}
	
	private String getKey(Relation rel, Table table, String role){
		String key;
		if(role == null){
			key = rel.getName()+  "_" + table.getName();
		}else{ //Used in recursive relations
			key = rel.getName()+ "_" + table.getName()+ "_" + role;
		}
		return key;
	}
}
