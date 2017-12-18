/**
 * 
 */
package com.lrgoncalves.neo4j;

import org.neo4j.graphdb.RelationshipType;

/**
 * @author lrgoncalves
 *
 */
public enum RelTypes implements RelationshipType{

	KNOWS,
	HAVE,
	IS,
	USE;
}
