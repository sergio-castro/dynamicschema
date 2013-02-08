package org.dynamicschema.sql.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Joiner;


public class RelationBuilder {

	private Map map;
	private String relation;
	private String pairSeparator;
	List<String> relations;
	
	public RelationBuilder(Map map, String relation, String pairSeparator) {
		this.map = map;
		this.relation = relation;
		this.pairSeparator = pairSeparator;
		build();
	}

	private void build() {
		relations = new ArrayList<String>();
		Set<Entry> entries = map.entrySet();
		for(Entry entry : entries) {
			relations.add(entry.getKey() + relation + entry.getValue());
		}
	}
	
	@Override
	public String toString() {
		return Joiner.on(pairSeparator).join(relations);
	}
}
