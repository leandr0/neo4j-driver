/**
 * 
 */
package com.lrgoncalves.neo4j;

/**
 * @author lrgoncalves
 *
 */
public enum QueryCypherParamType {
	
	IDENTIFIER("$identifier");
	
	private final String keyParam;
	
	private QueryCypherParamType(final String keyParam){
		this.keyParam = keyParam;
	}

	public String getKeyParam(){
		return this.keyParam;
	}
}
