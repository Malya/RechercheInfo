package query.matcher;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import database.reader.Document;
import format.Token;
import format.Tokenizer;
import format.stemmer.Stemmer;
import query.Matcher;
import sparqlclient.SparqlClient;


public class Semantic implements Matcher {

	private Matcher matcher;
	
	private SparqlClient sparqlClient ;
	
	private boolean serverIsUp ;
	
	private Map<String, Entry<List<String>, List<Double>>> cache;
	
	private Tokenizer tokenizer;
	
	final int version;
	
	public Semantic(Matcher matcher, final int version) {
		this.tokenizer = new Stemmer();
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
		List<String> terms = Arrays.asList(query.split(";"));
		if (serverIsUp) {
			String sparqlQuery;
	        for(String term : terms) {
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
		
		ArrayList<Double> weights = new ArrayList<>();
		for(String term : enrichedQuery) {
			if(terms.contains(term)) {
				for(Token t : tokenizer.tokenize(term)) {
					if(!finalQuery.contains(t.getRoot())) {
						finalQuery.add(t.getRoot());
						weights.add(3.);
					}
				}
			} else {
				for(Token t : tokenizer.tokenize(term)) {
					if(!finalQuery.contains(t.getRoot())) {
						finalQuery.add(t.getRoot());
						weights.add(1.);
					}
				}
			}
		}
		cache.put(query, new SimpleEntry<List<String>, List<Double>>(finalQuery, null));
		
		return this.match(finalQuery, weights);
	}

	@Override
	public List<Entry<Document, Double>> match(List<String> query) {
		System.out.println("List to match: "+ query);
		return this.matcher.match(query);
	}
	
	@Override
	public List<Entry<Document, Double>> match(List<String> query,
			List<Double> weight) {
		System.out.println("List to match: "+ query);
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
