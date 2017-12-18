package com.lrgoncalves.neo4j;

import java.io.File;
import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.impl.util.StringLogger;
import org.neo4j.rest.graphdb.RestGraphDatabase;

import static com.lrgoncalves.neo4j.Neo4JOperationsURI.*;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Hello world!
 *
 */
public class App{


	private static final String SERVER_ROOT_URI = "http://localhost:7474/db/data/";

	public static void main( String[] args ){

	
		//startServer();
		//sendingCypher();
		//fineGrainedRestApiCallsCreatingNode();
		//fineGrainedRestApiCallsAddingProperties();
		/**
		GraphDatabaseService database =  new RestGraphDatabase(SERVER_ROOT_URI);
		
		Transaction tx = database.beginTx();
		
		ExecutionEngine cypher = new ExecutionEngine(database,StringLogger.logger(new File ("file.log")));
		**/
		
		System.out.println(CYPHER);
	}


	private static void startServer(){
		WebResource resource = Client.create()
				.resource( SERVER_ROOT_URI );
		ClientResponse response = resource.get( ClientResponse.class );

		System.out.println( String.format( "GET on [%s], status code [%d]",
				SERVER_ROOT_URI, response.getStatus() ) );
		response.close();
	}

	private static void sendingCypher(){

		String query = "MATCH( person : Resource ) WHERE person.name = 'person.xsd'  RETURN person";
		//"match n return n limit 1";
		final String txUri = SERVER_ROOT_URI +CYPHER;
		WebResource resource = Client.create().resource( txUri );

		String payload = "{\"statements\" : [ {\"statement\" : \"" +query + "\"} ]}";
		ClientResponse response = resource
				.accept( MediaType.APPLICATION_JSON )
				.type( MediaType.APPLICATION_JSON )
				.entity( payload )
				.post( ClientResponse.class );

		System.out.println( String.format(
				"POST [%s] to [%s], status code [%d], returned data: "
						+ System.getProperty( "line.separator" ) + "%s",
						payload, txUri, response.getStatus(),
						response.getEntity( String.class ) ) );

		response.close();
	}

	private static URI fineGrainedRestApiCallsCreatingNode(){

		final String nodeEntryPointUri = SERVER_ROOT_URI + NODE;
		// http://localhost:7474/db/data/node

		WebResource resource = Client.create()
				.resource( nodeEntryPointUri );
		// POST {} to the node entry point URI
		ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
				.type( MediaType.APPLICATION_JSON )
				.entity( "{}" )
				.post( ClientResponse.class );

		final URI location = response.getLocation();
		System.out.println( String.format(
				"POST to [%s], status code [%d], location header [%s]",
				nodeEntryPointUri, response.getStatus(), location.toString() ) );
		response.close();

		return location;
	}

	private static void fineGrainedRestApiCallsAddingProperties(){
		URI firstNode = fineGrainedRestApiCallsCreatingNode();
		addProperty( firstNode, "name", "Joe Strummer" );
		URI secondNode = fineGrainedRestApiCallsCreatingNode();
		addProperty( secondNode, "band", "The Clash" );
	}

	private static ClientResponse addProperty(URI nodeUri, String propertyName, String propertyValue){
		String propertyUri = nodeUri.toString() + "/"+PROPERTIES + propertyName;
		// http://localhost:7474/db/data/node/{node_id}/properties/{property_name}

		WebResource resource = Client.create()
				.resource( propertyUri );
		ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
				.type( MediaType.APPLICATION_JSON )
				.entity( "\"" + propertyValue + "\"" )
				.put( ClientResponse.class );

		System.out.println( String.format( "PUT to [%s], status code [%d]",
				propertyUri, response.getStatus() ) );
		response.close();

		return response;
	}

	/*private static URI fineGrainedRestApiCallsAddingRelationship( URI startNode, URI endNode,String relationshipType, String jsonAttributes )throws URISyntaxException{
		URI fromUri = new URI( startNode.toString() + "/relationships" );
		String relationshipJson = generateJsonRelationship( endNode,
				relationshipType, jsonAttributes );

		WebResource resource = Client.create()
				.resource( fromUri );
		// POST JSON to the relationships URI
		ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
				.type( MediaType.APPLICATION_JSON )
				.entity( relationshipJson )
				.post( ClientResponse.class );

		final URI location = response.getLocation();
		System.out.println( String.format(
				"POST to [%s], status code [%d], location header [%s]",
				fromUri, response.getStatus(), location.toString() ) );

		response.close();
		return location;
	}

	private static void addMetadataToProperty( URI relationshipUri,String name, String value ) throws URISyntaxException{
		URI propertyUri = new URI( relationshipUri.toString() + "/properties" );
		String entity = toJsonNameValuePairCollection( name, value );
		WebResource resource = Client.create()
				.resource( propertyUri );
		ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
				.type( MediaType.APPLICATION_JSON )
				.entity( entity )
				.put( ClientResponse.class );

		System.out.println( String.format(
				"PUT [%s] to [%s], status code [%d]", entity, propertyUri,
				response.getStatus() ) );
		response.close();
	}*/
	
	/*	
	//GraphDatabaseService database = new GraphDatabaseFactory().newEmbeddedDatabase("");

			//GraphDatabaseSettings settings = new GraphDatabaseSettings() {};

			DatabaseCreator creator = new DatabaseCreator() {

				@Override
				public GraphDatabaseService newDatabase(Map<String, String> arg0) {
					// TODO Auto-generated method stub
					return null;
				}
			};


			//GraphDatabaseBuilder builder = new GraphDatabaseBuilder(creator);

			RestAPI restapi = new RestAPIFacade("http://localhost:7474/db/data")
			RestCypherQueryEngine rcqer=new RestCypherQueryEngine(restapi)	



			GraphDatabaseService database =  new RestGraphDatabase("http://localhost:7474/db/data/cypher");
			//GraphDatabaseService database = new RestGraphDatabase("http://localhost:7474/db/data");

			//RestAPI restAPI = database

			//new RestCypherQueryEngine(restAPI);


			System.out.println(StringLogger.SYSTEM_DEBUG);
			System.out.println(StringLogger.SYSTEM_ERR);
			System.out.println(StringLogger.SYSTEM_ERR_DEBUG);

			ExecutionEngine cypher = new ExecutionEngine(database,StringLogger.logger(new File ("file.log")));


			//engine = new RestCypherQueryEngine(new RestAPIFacade(restUrl));
			//results = engine.query(cypher, null)


			//System.out.println(cypher.execute("MATCH(person:Resource) 	where person.name 	= \"person.xsd\""));
	 */

}
