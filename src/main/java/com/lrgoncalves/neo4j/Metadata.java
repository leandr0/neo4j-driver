package com.lrgoncalves.neo4j;

import java.util.Collection;
import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


final class Metadata{

	private final Long id;
	private final Collection<String> labels;

	public Metadata(Long id,Collection<String> labels) {
		this.id 	= id;
		this.labels = labels;
	}

	public Long getId() {
		return id;
	}

	public Collection<String> getLabels() {
		return labels;
	}

	@SuppressWarnings("unchecked")
	public static Collection<Metadata> JsonArrayToMetadataCollection(JSONArray jsonArray){
		
		Collection<Metadata> metadatas = new LinkedList<Metadata>();			
		
		for (Object object : jsonArray) {
		
			JSONObject 	jsonObject 	= (JSONObject) object;
			JSONObject 	metadata 	= (JSONObject) jsonObject.get(Neo4JClient.METADATA);

			Metadata metadataObject = new Metadata((Long) metadata.get("id"),
													(JSONArray) metadata.get("labels"));
			
			metadatas.add(metadataObject);
		}

		
		return metadatas;
	}
	
	@Override
	public String toString(){
		return "ID : "+id +" ;  LABELS :"+labels;
	}

}