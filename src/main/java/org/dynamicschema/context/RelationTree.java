package org.dynamicschema.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dynamicschema.reification.Table;


public abstract class RelationTree {

	public static final String TABLE_RELATION_SEP = "->";
	public static final String RELATION_TABLE_SEP = TABLE_RELATION_SEP; //just use the TABLE_RELATION_SEP (for the moment)

	private String id;
	private RelationTree parent;
	
	public RelationTree(RelationTree parent, String id) {
		this.id = id;
		this.parent = parent;
	}
	
	public abstract List<Table> getTablePath();
	
	public List<RelationTree> getFullPath() {
		if(parent == null){
//			return Arrays.asList(this);
			List<RelationTree> list = new ArrayList<RelationTree>();
			list.add(this);
			return list;
		}
		else {
			List<RelationTree> fullPath = getParent().getFullPath();
			fullPath.add(this);
			return fullPath;
		}
	}
	public abstract <T extends RelationTree> List<T> getChildren();
	
	
	public String getId() {
		return id;
	}

	public RelationTree getParent() {
		return parent;
	}

	public boolean isRoot() {
		return parent == null;
	}

	@Override
	public boolean equals(Object other) {
		if(!(other instanceof RelationTree))
			return false;
		else
			return ((RelationTree)other).getId().equals(getId());
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
