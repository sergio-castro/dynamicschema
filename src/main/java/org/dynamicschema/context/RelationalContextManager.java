package org.dynamicschema.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dynamicschema.reification.ContextedTable;
import org.dynamicschema.reification.DBTable;
import org.dynamicschema.reification.Relation;
import org.dynamicschema.reification.Table;

/*
 * Keep track of used alias names
 * Provides a method answering a new alias for a table that warranties the alias has not been used in any possible occurrence of the table in a SQL expression
 */
public class RelationalContextManager {

	private Map<String, Integer> aliasesCounter;
	private int offset;
	private RelationContext relationContext;

	//Addendum 
	private Map<Table, TableNode> visitedTableNodes; //stores references to tablesNode visited at least 1   
	private Map<Relation, Integer> occuredRelations; // holds relations already occured in the query
	private Table baseTable ; //holds the base table of query : tables which called select()
	private Map<Table, Integer> recursionVisited; // determines whether a table has appeared in a recursive relation
	private boolean inLazyQuery;
	private Map<String, Integer> selectedTab; // did a given contexted table appeared in the query selection ?
	private Map<ContextedTable,ContextedTable> tablesDependencies; //holds dependecies between selected tables
	//end add

	public RelationalContextManager() {
		aliasesCounter = new HashMap<String, Integer>();
		relationContext = new RelationContext();
		visitedTableNodes = new HashMap<Table, TableNode>();
		occuredRelations = new HashMap<Relation, Integer>();
		recursionVisited = new HashMap<Table, Integer>();
		inLazyQuery = false;
		selectedTab = new HashMap<String, Integer>();
		tablesDependencies = new HashMap<ContextedTable, ContextedTable>();
	}

	

	private int getOffset() {
		return offset;
	}

	
	
	/**
	 * @return the inLazyQuery
	 */
	public boolean isInLazyQuery() {
		return inLazyQuery;
	}

	


	/**
	 * @param inLazyQuery the inLazyQuery to set
	 * 
	 * Will be set only once
	 */
	public void notifyInLazySelect() {
		this.inLazyQuery = true;
	}

	/**
	 * @return the tablesDependencies
	 */
	public Map<ContextedTable, ContextedTable> getTablesDependencies() {
		return tablesDependencies;
	}
	
	/**
	 * register dependency between tables. There is a dependency where there exist a relation between 
	 * table parent and child in the relational model
	 */
	public void registerSelectDependency(ContextedTable parent, ContextedTable child){
			this.tablesDependencies.put(parent, child);
	}
	
	
	/**
	 * Register selection for this contexted table
	 */
	public void setSelectedCtxTable(ContextedTable table){
		Integer flag = this.selectedTab.get(table.getAlias());
		if(flag != null) 
			throw new RuntimeException("Unpexcted: Attempt to flag a contexted table twice: "+ table.getAlias());
		this.selectedTab.put(table.getAlias(), new Integer(1));
		
		flag = this.selectedTab.get(table);
	}
	
	public void setUnSelectedCtxTable(ContextedTable table){
		Integer flag = (Integer) this.selectedTab.get(table.getAlias());
		if(flag == null) 
			throw new RuntimeException("Unpexcted: Attempt to unflag an non selected table: "+ table.getAlias());
		this.selectedTab.put(table.getAlias(), null);
	}
	
	
	/**
	 * 
	 * @param table
	 * @return
	 */
	public boolean isSelectedInQuery(ContextedTable table){
		Integer flag = this.selectedTab.get(table.getAlias());
		if( flag == null)
				return false;
		return flag.intValue() == 1;
	}


	public void setVisitedRecursion(Table table){
		Integer i = recursionVisited.get(table);
		if( i == null){
			i = new Integer(1);
			recursionVisited.put(table, i);
		}else{
			throw new RuntimeException("Unexpected behavior: Table "+ table + " Should have at most 1 recursion ");
		}
	}

	public boolean recursionAlreadyVisited(Table table){
		return recursionVisited.get(table) != null ; 
	}

	/*
	 * Record that a given table is a table from which the query has been initiated 
	 */
	public void setBaseTable(Table table){
		this.baseTable = table;
	}

	public Table getBaseTable(){
		return this.baseTable;
	}

	/*
	 * Answers whether a given table is a base table or not
	 */
	public boolean isBaseTable(Table table){
		return this.baseTable != null && this.baseTable.equals(table);
	}
	/**
	 * @return the visitedTables
	 */
	public Map<Table, TableNode> getVisitedTables() {
		return visitedTableNodes;
	}

	/*
	 * Record that a table node of a given table has been visited at least once
	 */
	public void setVisitedTable(Table table, TableNode tableNode){
		if(visitedTableNodes.get(table) == null)
			visitedTableNodes.put(table, tableNode);
	}

	/*
	 * Determine whether , in the Relational tree, there exist at least one table node corresponding to this table
	 */
	public boolean tableAlreadyVisited(Table table){
		return visitedTableNodes.containsKey(table); 
	}

	/*
	 * Answers the table node object in case the table has already been visited
	 */
	public TableNode getVisitedTableNode(Table table){
		return visitedTableNodes.get(table);
	}


	/*
	 * Answers the first contexted table that appeared in the query
	 */
	public Table getFirstContextedTable(Table table){
		TableNode node = getVisitedTableNode(table);
		if(node == null)
				throw new RuntimeException("Unexpected runtime error: Should have visited table: "+ table.getName()+  " at least Once!");
		
		return node.getContextedTable(this);
	}
	

	/*
	 * Record that a given relation has occured in the query
	 */
	public void setOccuredRelation(Relation relation){
		Integer occurence = occuredRelations.get(relation);
		if(occurence != null)
			new RuntimeException("2x Registration of Occurrence of relation: " + relation.toString());

		occuredRelations.put(relation, new Integer(1));
	}

	/*
	 * Answers whether a relation has already occurred in the query
	 */

	public boolean hasAlreadyOccured(Relation relation){ 
		return occuredRelations.get(relation) != null;
	}



	private int addOffset(int delta) {
		return offset+=delta;
	}
	
	private int decreaseOffset(int delta){
		return offset-= delta;
	}

	public String newAliasName(String tableName) {
		Integer occurences = aliasesCounter.get(tableName);
		if(occurences == null)
			occurences = 1;
		else
			occurences++;
		aliasesCounter.put(tableName, occurences);
		return tableName + occurences;
	}

	public RelationContext getRelationContext() {
		return relationContext;
	}

	public TableContext createTableContext(TableNode tableNode) {
		return createTableContext(tableNode, null, null);
	}

	
	/**
	 * 
	 * @param tableNode
	 * @param bindings
	 * @param initialTableLazySelect The table on which the bindings are applicable
	 * @return
	 */
	public TableContext createTableContext(TableNode tableNode, Map<String, Object> bindings, Table initialTableLazySelect) {
		TableContext tableContext = relationContext.getOrCreateTableContext(tableNode);
		Table table = tableNode.getTable();
		String alias = newAliasName(table.getName());
		tableContext.setAlias(alias);
		tableContext.setOffset(getOffset());
		if(bindings != null)
			notifyInLazySelect();	

		if(bindings == null && initialTableLazySelect == null)
			addOffset(table.getColumnModel().size());

		tableContext.setBindings(bindings);
		return tableContext;
	}

	
	public void notifyColumnsRemoval(int nbColumns){
		decreaseOffset(nbColumns);
	}
	

	
	public Table getBaseTableContextedTable() {
		TableContext baseTableContext = getRelationContext().getTableContext(getVisitedTableNode(getBaseTable()));
		return new ContextedTable((DBTable)getBaseTable(), baseTableContext.getAlias(), baseTableContext.getOffset(), baseTableContext.getBindings());
	}


	public int getQueryOffset(){
		return offset;
	}


	
	
}
