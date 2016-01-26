package query.matcher;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import query.Matcher;
import sparqlclient.SparqlClient;
import database.reader.Document;


public class Semantic implements Matcher {

	private Matcher matcher;
	
	private SparqlClient sparqlClient ;
	
	private boolean serverIsUp ;
	
	private Map<String, Entry<List<String>, List<Double>>> cache;
	
	final int version;
	
	public Semantic(Matcher matcher, final int version) {
		this.matcher = matcher;
		this.version = version;
		this.sparqlClient = new SparqlClient("localhost:3030/space");
		String upQuery = "ASK WHERE { ?s ?p ?o }";
        serverIsUp = sparqlClient.ask(upQuery);
        this.cache = new HashMap<String, Entry<List<String>, List<Double>>>();
	}
	
	@Override
	public List<Entry<Document, Double>> match(String query) {
		Entry<List<String>, List<Double>> saved = cache.get(query);
		if (saved != null) {
			return this.match(saved.getKey(), saved.getValue());
		}
		
		ArrayList<String> enrichedQuery = new ArrayList<String>();
		List<String> finalQuery = new ArrayList<String>();
		if (serverIsUp) {
			String sparqlQuery;
	        for(String term : query.split(";")) {
	        	enrichedQuery.add(term);
	        	if(version == 1 || version == 3) {
		        	sparqlQuery = createSynonymQuery(term.trim().toLowerCase()); //TODO: Add a cleaning function ? 
		        	enrichedQuery.addAll(sparqlClient.select(sparqlQuery));
	        	}
	        	
	        	if(version == 2 || version == 3) {
		        	for(String term2 : query.split(";")) {
		        		if(!term.contentEquals(term2)) {
		        			sparqlQuery = createInstanceQuery(term.trim().toLowerCase(), term2.trim().toLowerCase());
		        			enrichedQuery.addAll(sparqlClient.select(sparqlQuery));
		        		}
		        	}
	        	}
	        }
		}
		
		cache.put(query, new SimpleEntry<List<String>, List<Double>>(finalQuery, null /* TODO pass the list of weight*/));
		
		return this.match(enrichedQuery, null /* TODO pass the list of weight */);
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
    			"PREFIX irit:  <http://www.irit.fr/recherches/MELODI/ontologies/FilmographieV1.owl#> \n" +
    			"SELECT ?label WHERE { \n" +
        		 "?prop rdfs:label ?labelsProp. \n" +
        		 "FILTER contains(lcase(str(?labelsProp)), \"" + property +"\"). \n" +
        		 "?resQ ?prop ?result. \n" +
        		 "?result ?propQ ?res. \n" +
        		 "FILTER contains(lcase(str(?res)), \"" + res +"\"). \n" +
        		 "{?result rdfs:label ?label.} \n"
        		 + "UNION \n" +
        		 "{?result irit:aPourNom ?label.} \n" +
    			"} \n"	+
    			"LIMIT 20";
	}

}
