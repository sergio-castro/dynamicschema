package org.dynamicschema;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.dynamicschema.context.IRelationalContextManager;
import org.dynamicschema.sql.util.RelationCondition;


public class Relation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<Table> tables;
	private Fetching fetching;
	private RelationModel relationModel;
	private String name;
	private RelationCondition relationCondition;
	//private RelationalContext relationalContext;
	//private SqlCondition joinCondition;
	
	
	public Relation(RelationModel relationModel, String name, Table ...tables) {
		this(relationModel, name, Fetching.EAGER, tables);
	}
	
	public Relation(RelationModel relationModel, String name, Fetching fetching, Table ...tablesArray) {
		setRelationModel(relationModel);
		setName(name);
		setFetching(fetching);
		setTables(tablesArray);
	}
	
	public List<Table> getTables() {
		return tables;
	}
	
	public void setTables(List<Table> tables) {
		this.tables = tables;
	}
	
	public void setTables(Table ...tablesArray) {
		setTables(Arrays.<Table>asList(tablesArray));
	}
	

	public Fetching getFetching() {
		return fetching;
	}

	public void setFetching(Fetching fetching) {
		this.fetching = fetching;
	}

	public void setRelationModel(RelationModel relationModel) {
		this.relationModel = relationModel;
	}

	public RelationModel getRelationModel() {
		return relationModel;
	}

	public Relation baseRelation() {
		return relationModel.getBaseRelation();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if(name==null || name.isEmpty())
			throw new RuntimeException(name+" is not a valid name for a relation");
		this.name = name;
	}

	public String joinCondition(IRelationalContextManager ctx) {
		if (getCondition() == null)
				return "";
		else
			return getCondition().eval(ctx).toString();
	}

	public RelationCondition getCondition() {
		return relationCondition;
	}

	public void setCondition(RelationCondition relationCondition) {
		this.relationCondition = relationCondition;
	}
	
	
	/*
	public String orderBy(IRelationalContextManager ctx) {
		return "";
	}
	
	public String groupBy(IRelationalContextManager ctx) {
		return "";
	}
	
	public String having(IRelationalContextManager ctx) {
		return "";
	}
*/
	
	/*
	public String getJoinCondition() {
		return joinCondition.toString();
	}

	public void addJoinCondition(String clause) {
		joinCondition.and(clause);
	}
	*/
/*
	public void addAliases(GlobalRelationalContext ctx) {
		
	}
*/

	public boolean isBaseRelation() {
		return getTables().get(0).getBaseRelation().equals(this);
	}
	
	public Object col(IRelationalContextManager ctx, Column column) {
		return ctx.getColumnValue(this, column);
	}
	
	@Override
	public String toString() {
		return getName();
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


