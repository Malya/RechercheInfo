package query.core;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import query.Matcher;
import database.exception.DBException;
import database.reader.Database;
import database.reader.Document;
import database.reader.Term;
import format.Tokenizer;
import format.stemmer.Stemmer;


public abstract class Core implements Matcher {

	private static final Database database;
	static {
		Database open = null;
		try {
			open = new Database();
		} catch (DBException e) {
			e.printStackTrace();
			System.exit(0);
		}
		database = open;
	}
	
	private Tokenizer tokenizer;
	
	public Core() {
		this.tokenizer = new Stemmer();
	}
	
	public List<Entry<Document, Double>> match(String query) {
		return this.match(Arrays.asList(query.split("[\\s\\p{Punct}]+")));
	}
	
	public List<Entry<Document, Double>> match(List<String> query) {
		return this.match(query, null);
	}
	
	public List<Entry<Document, Double>> match(List<String> query, List<Double> weight) {
		
		Map<Document, Double> scores = new HashMap<Document, Double>();
		
		Collection<Term> terms = null;
		try {
			terms = database.load(this.tokenizer.tokenize(query, weight));
		} catch (DBException e) {
			e.printStackTrace();
			System.exit(0);
		}
		scores = this.match(terms);
		return this.sort(scores);
	}
	
	protected abstract Map<Document, Double> match(Collection<Term> terms);
	
	private List<Entry<Document, Double>> sort(Map<Document, Double> map) {
		List<Entry<Document, Double>> list = new LinkedList<Entry<Document, Double>>(map.entrySet());

		// Defined Custom Comparator here
		Collections.sort(list, new Comparator<Entry<Document, Double>>() {
			public int compare(Entry<Document, Double> e1, Entry<Document, Double> e2) {
				double result = e2.getValue() - e1.getValue();
				if(result < 0)
					return -1;
				else if(result == 0) 
					return 0;
				else 
					return 1;
			}
		});
		
		return list;
	}
	
}