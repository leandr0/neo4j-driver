/**
 * 
 */
package com.lrgoncalves.neo4j;

import java.io.Serializable;
import java.util.Map;

/**
 * @author lrgoncalves
 *
 */
public interface CypherQuery extends Serializable{
	
	/**
	 * 
	 * @param parameters
	 */
	public void setParameters(Map<QueryCypherParamType, Object> parameters);
	
	/**
	 * 
	 * @return
	 */
	public Map<QueryCypherParamType,Object> getParameters();
	
	/**
	 * 
	 * @param queryCypherParamType
	 * @param value
	 */
	public void setParameter(QueryCypherParamType queryCypherParamType, final Object value);
}
