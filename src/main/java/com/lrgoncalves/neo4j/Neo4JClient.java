/**
 * 
 */
package com.lrgoncalves.neo4j;

import static com.lrgoncalves.neo4j.Neo4JOperationsURI.NODE;
import static com.lrgoncalves.neo4j.Neo4JOperationsURI.NODE_BY_LABEL;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.IOException;
import java.net.URI;
import java.util.*;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.resource.ResourceException;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RestAPIFacade;
import org.neo4j.rest.graphdb.util.QueryResult;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
/**
 * @author lrgoncalves
 *
 */
@Named
public class Neo4JClient implements GraphClient {

	private static String SERVER_ROOT_URI;

	@SuppressWarnings("unused")
	private String TRANSACTION_URI;

	private RestAPI graphDb;

	private static final String QUERY_PAYLOAD = "{\"statements\" : [ {\"statement\" : \" $query \"} ]}";

	@Resource(lookup = "java:global/neo4jDatabase")
	private Properties  neo4jURLConnection;

	private static Client	jerseyClient;

	private String AUTHORIZATION;

	private void prepare(){

		if(Objects.isNull(jerseyClient)){
			Neo4JClient.jerseyClient 	= Client.create();
		}
		
		AUTHORIZATION 		= neo4jURLConnection.getProperty("Authorization");
		SERVER_ROOT_URI 	= neo4jURLConnection.getProperty("Host");
		TRANSACTION_URI 	= SERVER_ROOT_URI + "transaction/commit";
		graphDb				= new RestAPIFacade(SERVER_ROOT_URI);

	}

	/**
	 * For tests
	 * @param urlConnection
	 * @throws IOException 
	 */
	public Neo4JClient setClient(final Properties urlConnection) throws IOException{
		
		neo4jURLConnection = urlConnection;
		SERVER_ROOT_URI 	= neo4jURLConnection.getProperty("Host");
		//graphDb 	= new RestAPIFacade(SERVER_ROOT_URI);

		return this;
	}

	/**
	 * 
	 * @param label
	 * @return
	 * @throws ParseException
	 * @throws ResourceException
	 */
	public Collection<Metadata> findByLabel(String label) throws ParseException, ResourceException{

		prepare();
		
		String propertyUri = SERVER_ROOT_URI+NODE_BY_LABEL;

		propertyUri = propertyUri.replace("$1", label);

		Builder webBuilder = creatWebBuilder(propertyUri);

		String response = webBuilder
				.accept( APPLICATION_JSON )
				.type( APPLICATION_JSON )
				.get(String.class );

		JSONParser	parser 		= new JSONParser();

		return Metadata.JsonArrayToMetadataCollection((JSONArray) parser.parse(response));	
	}

	public Node createNode(String ... labels) throws ResourceException{
		
		prepare();
		
		final String nodeEntryPointUri = SERVER_ROOT_URI +NODE;		

		WebResource.Builder builder = creatWebBuilder(nodeEntryPointUri);

		builder.accept(APPLICATION_JSON ).type( APPLICATION_JSON ).entity( "{}" );

		ClientResponse response =  builder.post( ClientResponse.class );

		final URI location = response.getLocation();

		response.close();

		Node node = getNode(location);

		return node.addAuthenticationRequest(AUTHORIZATION).addLabel(labels);
	}

	public  Collection<Map<String, Map<String,String>>> sendTransactionalCypherQuery(CypherQueryType query, CypherQuery queryParams) throws ResourceException, ClientHandlerException, UniformInterfaceException, ParseException {

		prepare();
		
		final String queryAndParameters = fillParameters(query, queryParams);
		
		Builder resource = creatWebBuilder(TRANSACTION_URI);
		
		ClientResponse response = resource
		        .accept( MediaType.APPLICATION_JSON )
		        .type( MediaType.APPLICATION_JSON )
		        .entity( QUERY_PAYLOAD.replace("$query", queryAndParameters) )
		        .post( ClientResponse.class );
		
		final String result = response.getEntity(String.class);
		
		response.close();

		return processQueryResult(getJsonData(result));
	}

	/**
	 *
	 * @param resultQueryData
	 * @return
	 */
	private Collection<Map<String, Map<String,String>>> processQueryResult(final Object[] resultQueryData){

		Collection<Map<String, Map<String,String>>> collectionResultMap = new LinkedList<Map<String, Map<String,String>>>();

		for (Object resultQueryObject : resultQueryData) {
			collectionResultMap.add(populateKeyValueMap(resultQueryObject));
		}

		return collectionResultMap;
	}

	/**
	 *
	 * @param result
	 * @return
	 * @throws ParseException
	 */
	private Object[] getJsonData(final String result) throws ParseException {

		JSONParser  jsonParser  = new JSONParser();
		/** Transform String query result to JSON object **/
		JSONObject jsonObject  = (JSONObject) jsonParser.parse(result);
		/** Collect result key inside JSON text **/
		JSONArray   results     = (JSONArray) jsonObject.get(RESULTS);
		/**  Recover the first array position **/
		JSONObject  data        = (JSONObject)results.get(FIRST_INDEX);

		//TODO : Analyze to evolve this methos to use a dynamic iteration
        /*
        Iterator<JSONObject> iterator = results.iterator();

        while(iterator.hasNext()){

            JSONObject  item = iterator.next();
            System.out.println(item);

        }
        */

		/** Get result rows value **/
		JSONArray   rowValues   = (JSONArray) data.get(DATA);

		return rowValues.toArray();
	}

	private Object[] getResultQueryRows(Object resultQueryObject){
		return ((JSONArray)((JSONObject)resultQueryObject).get(ROW)).toArray();
	}

	private Map<String, Map<String,String>> populateKeyValueMap(Object resultQueryObject){

		Map<String, Map<String,String>> mapRow      = new HashMap<String, Map<String,String>>();
		Map<String,String>              mapValues   = new HashMap<String,String>();

		String entityLabel = null;
        //TODO: evoluir este código
		//Cada linha é montada neste loop
		for (Object o : getResultQueryRows(resultQueryObject) ) {

			if( o instanceof JSONArray){

				JSONArray jo = (JSONArray) o;

				entityLabel = jo.toString().replace("[\"","").replace("\"]","");

			}else if ( o instanceof JSONObject) {

				JSONObject jo = (JSONObject) o;

				for (Object key : jo.keySet()) {

					String jsonKey = (String) key;

					mapValues.put(jsonKey, (String) jo.get(jsonKey));
				}

				//mapRow.put(entityLabel, mapValues);
			}

			if(Objects.nonNull(entityLabel) && mapValues.size() > 0){
                mapRow.put(entityLabel, mapValues);
            }
		}
		//Fim do loop de montagem dos objetos representados na linha

		return mapRow;
	}

	private String fillParameters(CypherQueryType query, CypherQuery queryParams){

		if(query == null){
			throw new IllegalArgumentException("Invalid CypherQueryType value");
		}

		String queryString = query.getQuery();

		for(QueryCypherParamType queryCypherParamType :  queryParams.getParameters().keySet()){

			queryString = queryString.replace(queryCypherParamType.getKeyParam(), 
					queryParams.getParameters().get(queryCypherParamType).toString());
		}

		return queryString;
	}

	public Node getNode(URI location) throws ResourceException{

		return new Node(location, jerseyClient);
	}

	private Builder creatWebBuilder(final String propertyUri) throws ResourceException{

		WebResource webResource = jerseyClient.resource(propertyUri);

		WebResource.Builder builder = webResource.getRequestBuilder();
		builder.header("Authorization",AUTHORIZATION);

		return builder;

	}
	
	public String authorizationKey(){
		return AUTHORIZATION;
	}
}