package query.matcher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import database.reader.Document;
import database.writer.Database;
import format.Token;
import format.Tokenizer;
import query.Matcher;
import sparqlclient.SparqlClient;

public class Semantic implements Matcher {

	private Matcher matcher;
	
	private SparqlClient sparqlClient ;
	
	private boolean serverIsUp ;
	
	private List<Token> blacklist;
	
	private Tokenizer tokenizer ;
	
	private int version;
	
	private HashMap<String, List<String>> cache = new HashMap<>();
	
	public Semantic(Matcher matcher, int version) {
		this.matcher = matcher;
		this.version = version;
		this.sparqlClient = new SparqlClient("localhost:3030/space");
		String upQuery = "ASK WHERE { ?s ?p ?o }";
        serverIsUp = sparqlClient.ask(upQuery);
        this.tokenizer = (new Database()).getTokenizer();
        
        List<String> blacklist = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("src/index/stopliste.txt"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		try {
			String word;
			while ((word = reader.readLine()) != null) {
				blacklist.add(word);
			}
			this.blacklist = this.tokenizer.tokenize(blacklist);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
	}
	
	@Override
	public List<Entry<Document, Double>> match(String query) {
		if(cache.containsKey(query)) {
			return this.match(cache.get(query));
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
		        			sparqlQuery = createInstanceQueryV2(term.trim().toLowerCase(), term2.trim().toLowerCase());
		        			enrichedQuery.addAll(sparqlClient.select(sparqlQuery));
		        		}
		        	}
	        	}
	        }
		}
		
		for(String result : enrichedQuery) {
			for(Token token : tokenizer.tokenize(result)) {
				if(!finalQuery.contains(token.getRoot())) {
					finalQuery.add(token.getRoot());
				}
			}
		}
		
		cache.put(query, finalQuery);
		
		return this.match(finalQuery);
	}

	@Override
	public List<Entry<Document, Double>> match(List<String> query) {
		System.out.println("List to match: " + clean(query));
		return this.matcher.match(clean(query));
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
        		 "FILTER contains(lcase(str(?labelsProp)), \"" + property +"\"). \n" +
        		 "?res ?prop ?result. \n" +
        		 "?result rdfs:label ?label. \n" +
    			"} \n"	+
    			"LIMIT 20";
	}
	
	private String createInstanceQueryV2(String res, String property) {
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
	
	private List<String> clean(List<String> list) {
		for (Token word : this.blacklist) {
			list.removeAll(Collections.singleton(word.toString()));
		}
		return list;
	}
}
