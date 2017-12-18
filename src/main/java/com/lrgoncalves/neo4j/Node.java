/**
 * 
 */
package com.lrgoncalves.neo4j;

import static com.lrgoncalves.neo4j.Neo4JOperationsURI.LABELS;
import static com.lrgoncalves.neo4j.Neo4JOperationsURI.PROPERTIES;
import static com.lrgoncalves.neo4j.Neo4JOperationsURI.RELATIONSHIP;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * @author lrgoncalves
 *
 */
public class Node implements GraphEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6312661959370156614L;

	private URI nodeUri;
	
	private Client client;
	
	private String authenticationRequest;
	
	public Node() {}

	public Node(final URI nodeUri,Client client) {
		this();
		this.nodeUri = nodeUri;
		this.client = client;
	}
	
	public Node setClient(Client client){
		this.client = client;
		return this;
	}

	/**
	 * 
	 * @param labels
	 * @return {@link Node}
	 * @throws IllegalArgumentException - If the 
	 */
	public Node addLabel(String ...labels ){

		if(nodeUri == null){
			throw new IllegalArgumentException("The Node is not a linked representation.");
		}

		String propertyUri = nodeUri.toString()+"/"+ LABELS;

		WebResource resource = client.resource( propertyUri );

		WebResource.Builder builder = resource.getRequestBuilder();
		
		if(!Objects.isNull(authenticationRequest))
			builder.header("Authorization",authenticationRequest);
		
		final String labelValues = prepareLabels(labels);
				
		builder.accept( MediaType.APPLICATION_JSON)
		.type( MediaType.APPLICATION_JSON_TYPE )
		.entity(labelValues);

		ClientResponse response = builder.post( ClientResponse.class ,labelValues);
		response.close();

		return this;
	}
	
	private final String prepareLabels(String ...labels){

		Collection<String> labelsPropery = new LinkedList<String>();

		for (String label : labels) {
			labelsPropery.add("\""+label+"\"");
		}

		final String labelValues = labelsPropery.toString();

		return labelValues;
	}
	
	public Node addRelationship(Node endNode,String relationshipType, String jsonAttributes )throws URISyntaxException{
		
		URI fromUri = new URI( nodeUri + "/"+RELATIONSHIP );
		
		String relationshipJson = generateJsonRelationship( endNode.nodeUri,
				relationshipType, jsonAttributes );

		WebResource resource = client.resource( fromUri );
		
		WebResource.Builder builder = resource.getRequestBuilder();
		
		if(!Objects.isNull(authenticationRequest))
			builder.header("Authorization",authenticationRequest);

		builder.accept( MediaType.APPLICATION_JSON )
				.type( MediaType.APPLICATION_JSON )
				.entity( relationshipJson )
				.post( ClientResponse.class );

		return this;
	}
	
	public Node addProperty( String propertyName,String propertyValue ){

		String propertyUri = nodeUri+ "/"+PROPERTIES+ propertyName;

		WebResource resource = client.resource( propertyUri );
		
		WebResource.Builder builder = resource.getRequestBuilder();
		
		if(!Objects.isNull(authenticationRequest))
			builder.header("Authorization",authenticationRequest);
		
		builder.accept( MediaType.APPLICATION_JSON )
				.type( MediaType.APPLICATION_JSON )
				.entity( "\"" + propertyValue + "\"" )
				.put( ClientResponse.class );

		return this;
	}
	

	private static String generateJsonRelationship( URI endNode,
			String relationshipType, String... jsonAttributes )
	{
		StringBuilder sb = new StringBuilder();
		sb.append( "{ \"to\" : \"" );
		sb.append( endNode.toString() );
		sb.append( "\", " );

		sb.append( "\"type\" : \"" );
		sb.append( relationshipType );
		if ( jsonAttributes == null || jsonAttributes.length < 1 )
		{
			sb.append( "\"" );
		}
		else
		{
			sb.append( "\", \"data\" : " );
			for ( int i = 0; i < jsonAttributes.length; i++ )
			{
				sb.append( jsonAttributes[i] );
				if ( i < jsonAttributes.length - 1 )
				{ // Miss off the final comma
					sb.append( ", " );
				}
			}
		}
		sb.append( " }" );
		return sb.toString();
	}	
	
	public Node addAuthenticationRequest(final String authentication){
		
		this.authenticationRequest = authentication;
		
		return this;
	}
}