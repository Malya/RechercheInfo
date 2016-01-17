package query.matcher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import query.Matcher;
import sparqlclient.SparqlClient;
import database.writer.Database;
import database.reader.Document;
import format.Token;
import format.Tokenizer;

public class Semantic implements Matcher {

	private Matcher matcher;
	
	private SparqlClient sparqlClient ;
	
	private boolean serverIsUp ;
	
	private List<Token> blacklist;
	
	private Tokenizer tokenizer ;
	
	public Semantic(Matcher matcher) {
		this.matcher = matcher;
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
		List<String> terms = new ArrayList<String>() ;
		if (serverIsUp) {
			HashSet<Map<String, String>> enrichedQuery = new HashSet<Map<String, String>>();
	        for(String term : query.split(";")) {
	        	String sparqlQuery = createSynonymQuery(term.trim().toLowerCase());
	        	enrichedQuery.addAll((Collection<? extends Map<String, String>>) sparqlClient.select(sparqlQuery));
	        }
	        
	        for(Map<String, String> res : enrichedQuery) {
            	for(Entry<String, String> entry : res.entrySet()) {
            		System.out.println(entry.getKey() + " : " + entry.getValue());
            		terms.addAll(Arrays.asList(entry.getValue().split("[\\s\\p{Punct}]+")));
            	}
            }
		}
		return this.match(terms);
	}

	@Override
	public List<Entry<Document, Double>> match(List<String> query) {
		System.out.println("List to match: "+ clean(query));
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
	
	private List<String> clean(List<String> list) {
		for (Token word : this.blacklist) {
			list.removeAll(Collections.singleton(word.toString()));
		}
		return list;
	}
}
