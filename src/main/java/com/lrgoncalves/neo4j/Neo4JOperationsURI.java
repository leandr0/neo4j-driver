/**
 * 
 */
package com.lrgoncalves.neo4j; 

/**
 * @author lrgoncalves
 *
 */
public enum Neo4JOperationsURI {
	
	CYPHER("transaction/commit"),
	NODE("node"),
	PROPERTIES("properties/"),
	NODE_BY_LABEL("label/$1/nodes"),
	LABELS("labels"),
	RELATIONSHIP("relationships");
	
	private Neo4JOperationsURI(final String value){
		this.value = value;
	}
	
	private final String value; 

	public String getValue(){
		return this.value;
	}
	
	@Override
	public String toString(){
		return this.value;
	}
}
