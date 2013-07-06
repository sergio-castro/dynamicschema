/**
 * Represent a specific join condition used as a trick 
 */
package org.dynamicschema.sql;

import java.util.List;

import org.dynamicschema.reification.ContextedTable;
import org.dynamicschema.reification.Table;

/**
 * @author esp
 *
 * Builds a relation for joining the base table of a query with itself and serve as transitive "condition"
 * Why?: When traversing the relational tree in a Eager way, we come to the point where a table is linked 
 * to the base table (initial table used for the traversal). 
 * But the base table is visited twice which means that we have the same (base) table having appearing
 * twice in the query with 2 different aliases.
 * 
 *  This is used as a trick in order to generate a SQL routine without ambiguities 
 * 
 */
public class SyntaxicTrickRelationCondition extends RelationCondition {

	/**
	 * 
	 */
	public SyntaxicTrickRelationCondition() {
		// TODO Auto-generated constructor stub
	}
	
	public SqlCondition eval(Table table1, Table table2){
		
		ContextedTable tab1 = (ContextedTable) table1;
		ContextedTable tab2 = (ContextedTable) table2;
		
		if(!tab1.isFromSameTableThan(tab2))
				throw new RuntimeException("Unexpected parameters: " + table1.getName() + " and " + 
						table2.getName()+ " should be of the same type");
		
		
	
		List<String> tab1Ids  = table1.getIDColumnNames();
		List<String> tab2Ids = table2.getIDColumnNames();
		
		if(tab1Ids.size() != tab2Ids.size())
				throw new RuntimeException("Unexpected Error");
		
		if(tab1Ids.size() == 1 && tab2Ids.size() == 1){
			return new SqlCondition().eq(table1.col(tab1Ids.get(0)), 
					table2.col(tab2Ids.get(0)));
		}
		
		SqlCondition tab1Cond = new SqlCondition().eq(tab1.col(tab1Ids.get(0)), tab2.col(tab2Ids.get(0)));
		SqlCondition tab2Cond = new SqlCondition().eq(tab1.col(tab1Ids.get(1)), tab2.col(tab2Ids.get(1)));
		return tab2Cond.and(tab2Cond.toString());
	}
}
