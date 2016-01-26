package query.matcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import query.Matcher;
import sparqlclient.SparqlClient;
import database.reader.Document;

public class Semantic implements Matcher {

	private Matcher matcher;
	
	private SparqlClient sparqlClient ;
	
	private boolean serverIsUp ;
	
	public Semantic(Matcher matcher) {
		this.matcher = matcher;
		this.sparqlClient = new SparqlClient("localhost:3030/space");
		String upQuery = "ASK WHERE { ?s ?p ?o }";
        serverIsUp = sparqlClient.ask(upQuery);
	}
	
	@Override
	public List<Entry<Document, Double>> match(String query) {
		ArrayList<String> enrichedQuery = new ArrayList<String>();
		if (serverIsUp) {
			String sparqlQuery;
	        for(String term : query.split(";")) {
	        	enrichedQuery.add(term);
	        	sparqlQuery = createSynonymQuery(term.trim().toLowerCase()); //TODO: Add a cleaning function ? 
	        	enrichedQuery.addAll(sparqlClient.select(sparqlQuery));
	        	
	        	for(String term2 : query.split(";")) {
	        		if(!term.contentEquals(term2)) {
	        			sparqlQuery = createInstanceQuery(term, term2);
	        			enrichedQuery.addAll(sparqlClient.select(sparqlQuery));
	        		}
	        	}
	        }
	        

		}
		return this.match(enrichedQuery /* TODO pass a weighted list */);
	}

	@Override
	public List<Entry<Document, Double>> match(List<String> query) {
		return this.matcher.match(query);
	}
	
	@Override
	public List<Entry<Document, Double>> match(List<String> query,
			List<Double> weight) {
		return this.matcher.match(query, weight);
	}
	
	private String createSynonymQuery(String term) {
    	return "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
        		"SELECT ?label WHERE { \n" +
        		 "?res rdfs:label ?labels. \n" +
        		 "FILTER (lcase(str(?labels)) = \"" + term +"\"). \n" +
        		 "?res rdfs:label ?label. \n" +
    			"} \n"	+
    			"LIMIT 20";
    }
	
	private String createInstanceQuery(String res, String property) {
    	return "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
        		"SELECT ?label WHERE { \n" +
        		 "?res rdfs:label ?labelsRes. \n" +
        		 "FILTER (lcase(str(?labelsRes)) = \"" + res +"\"). \n" +
        		 "?prop rdfs:label ?labelsProp. \n" +
        		 "FILTER (lcase(str(?labelsProp)) = \"" + property +"\"). \n" +
        		 "?res ?prop ?result. \n" +
        		 "?result rdfs:label ?label. \n" +
    			"} \n"	+
    			"LIMIT 20";
	}

}
