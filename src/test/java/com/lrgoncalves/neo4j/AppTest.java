package com.lrgoncalves.neo4j;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase{
	
	
	public static void main(String[] args) {

		try {

			final String RESOURCE_E 		= "Resource";
			final String DOCUMENTATION_E 	= "Documentation";
			final String XSD_T 				= "XSD";
			final String WSDL_T 			= "WSDL";
			
			
			
			
			
			/**
					" MATCH (order:Order {identifier: '0003'} ) -[*]->(item:Item),"
					+ "(order)-[*]->(status:Status),"
					+ "(order)-[*]->(location:Location),"
					+ "(order)-[*]->(hypermedia:Hypermedia),"
					+ "(hypermedia)-[*]->(link:Link) "
					+ "return order, item, status, location, hypermedia, link");
			**/		
					//"MATCH (order:Order {identifier: '0001'}) --(has) RETURN order");
			
			//System.out.println(response);
			
			/**
			Node status = client.createNode("Status")
					.addProperty("value", "UNPAID" );
			
			Node cappuccinno =client.createNode("Item")
					.addProperty("milk", "SEMI")
					.addProperty("size", "MEDIUM" )
					.addProperty("drink", "CAPPUCCINO" );
			
			Node espresso = client.createNode("Item")
					.addProperty("milk", "NONE")
					.addProperty("size", "MEDIUM" )
					.addProperty("drink", "ESPRESSO" );
			
			Node location = client.createNode("Location")
					.addProperty("value", "TAKEAWY");
					
			Node paymentLink = client.createNode("Link")
					.addProperty("action", "payment");

			Node selfLink = client.createNode("Link")
					.addProperty("action", "self");			
			
			Node cancelLink = client.createNode("Link")
					.addProperty("action", "cancel");
			
			Node updateLink = client.createNode("Link")
					.addProperty("action", "update");
			
			Node hypermedia = client.createNode("Hypermedia")
					.addRelationship(paymentLink, "has", null)
					.addRelationship(selfLink	, "has", null)
					.addRelationship(cancelLink	, "has", null)
					.addRelationship(updateLink	, "has", null);
			
			Node order = client.createNode("Order")
					.addProperty("identifier", "0003")
					.addRelationship(hypermedia	, "has"		, null)
					.addRelationship(status		, "has"		, null)
					.addRelationship(location	, "belong"	, null)
					.addRelationship(cappuccinno, "has"		, null)
					.addRelationship(espresso	, "has"		, null);
		**/	
					//.addProperty("location", "http://wiki.lrgoncalves.com/xsd/person" );
			
			
					//"Match(o:Order) where o.identifier = \"0001\" return o");
					//.addProperty("location", "http://wiki.lrgoncalves.com/xsd/address" );			

			/*Node documentationPerson = client.createNode("Status")
					.addProperty("value", "unpaid" );*/
					//.addProperty("location", "http://wiki.lrgoncalves.com/xsd/person" );

			/*Node address = client.createNode("Location")
							.addProperty("value", "takeway" );*/
							//.addProperty("type", XSD_T )
							//.addRelationship(documentationAddress, HAVE.name(), "{\"type\" : \"sharepoint\"}");

			/*Node person = client.createNode("Item")
					.addProperty("milk", "SEMI")
					.addProperty("size", "MEDIUM" )
					.addProperty("drink", "CAPPUCCINO" );*/
					//.addRelationship(address, USE.name(),null )
					//.addRelationship(documentationPerson, HAVE.name(), "{\"type\" : \"sharepoint\"}");
			/*			
			Node documentationAddress = client.createNode("Order")
					.addProperty("identifier", "0001" )
					.addRelationship(documentationPerson, "has", null)
					.addRelationship(address, "belong", null)
					.addRelationship(person, "has", null);*/
			
			
			/*Node documentationWSDL = client.createNode(DOCUMENTATION_E)
										.addProperty("name", "HOW TO USE" ) 
										.addProperty("location", "http://wiki.lrgoncalves.com/wsdl/person" );
			
			client.createNode(RESOURCE_E)
								.addProperty("name", "person" )
								.addProperty("type", WSDL_T )
								.addRelationship( person, USE.name(),null )
								.addRelationship(documentationWSDL, HAVE.name(), "{\"type\" : \"wiki\"}");
			*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
}
