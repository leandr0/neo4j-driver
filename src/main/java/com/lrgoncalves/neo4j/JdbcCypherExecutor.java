package com.lrgoncalves.neo4j;

        import com.sun.jersey.api.client.Client;
        import com.sun.jersey.api.client.ClientResponse;
        import com.sun.jersey.api.client.WebResource;
        import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
        import org.json.simple.JSONArray;
        import org.json.simple.JSONObject;
        import org.json.simple.parser.JSONParser;
        import org.json.simple.parser.ParseException;

        import javax.ws.rs.core.MediaType;
        import java.sql.*;
        import java.util.*;

/**
 * @author Michael Hunger @since 22.10.13
 */
public class JdbcCypherExecutor implements CypherExecutor {

    private final java.sql.Connection conn;

    private JdbcCypherExecutor(){
        conn = null;
    }
    public JdbcCypherExecutor(String url) {
        this(url,null,null);
    }
    public JdbcCypherExecutor(String url,String username, String password) {
        try {
            conn = DriverManager.getConnection(url.replace("http://","jdbc:neo4j://"),username,password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {

        try{

            final String url        =   "http://localhost:7474/db/data/";
            final String password   =   "123456";
            final String username   =   "neo4j";

            String query = "MATCH (order:Order {identifier: '$identifier' } ) -[*0..1]-(nodes) return labels(nodes) , nodes";

            Client jerseyClient = Client.create();

            jerseyClient.addFilter(new HTTPBasicAuthFilter("neo4j", "123456"));

            WebResource webResource = jerseyClient.resource(url+"transaction/commit");

            Map<String,Object> params = new HashMap<>();
            params.put("$identifier","1d701843-6ed4-4987-b009-a6f8097b6d76");


            for( Map.Entry<String, Object> entry : params.entrySet()){

                query = query.replace(entry.getKey(),""+entry.getValue());
            }

            final String QUERY_PAYLOAD = "{\"statements\" : [ {\"statement\" : \" $query \"} ]}";

            ClientResponse response = webResource
                    .accept( MediaType.APPLICATION_JSON )
                    .type( MediaType.APPLICATION_JSON )
                    .entity( QUERY_PAYLOAD.replace("$query", query) )
                    .post( ClientResponse.class );

            final String result = response.getEntity(String.class);

            response.close();

            JdbcCypherExecutor cypherExecutor = new JdbcCypherExecutor();

            //System.out.println(cypherExecutor.processQueryResult(cypherExecutor.getJsonData(result)));

        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public Iterator<Map<String, Object>> query(String query, Map<String, Object> params) {
        try {
            final PreparedStatement statement = conn.prepareStatement(query);
            // setParameters(statement, params);
            final ResultSet result = statement.executeQuery();
            return new Iterator<Map<String, Object>>() {

                boolean hasNext = result.next();
                public List<String> columns;

                @Override
                public boolean hasNext() {
                    return hasNext;
                }

                private List<String> getColumns() throws SQLException {
                    if (columns != null) return columns;
                    ResultSetMetaData metaData = result.getMetaData();
                    int count = metaData.getColumnCount();
                    List<String> cols = new ArrayList<>(count);
                    for (int i = 1; i <= count; i++) cols.add(metaData.getColumnName(i));
                    return columns = cols;
                }

                @Override
                public Map<String, Object> next() {
                    try {
                        if (hasNext) {
                            Map<String, Object> map = new LinkedHashMap<>();
                            for (String col : getColumns()) map.put(col, result.getObject(col));
                            hasNext = result.next();
                            if (!hasNext) {
                                result.close();
                                statement.close();
                            }
                            return map;
                        } else throw new NoSuchElementException();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void remove() {
                }
            };
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setParameters(PreparedStatement statement, Map<String, Object> params) throws SQLException {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            int index = Integer.parseInt(entry.getKey());
            statement.setObject(index, entry.getValue());
        }
    }
}

