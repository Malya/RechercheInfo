package query;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import database.exception.DBException;
import database.reader.Database;
import database.reader.Document;
import database.reader.Link;
import database.reader.Term;
import format.Tokens;
import format.impl.TokensImpl;


public class Matcher {
	
	private Tokens tokenizer;
	private Database database;
	
	public Matcher() {
		this.tokenizer = new TokensImpl();
		this.database = new Database();
	}
	
	public List<Entry<Document, Integer>> match(String query) {
		return this.match(Arrays.asList(query.split("[\\s\\p{Punct}]+")));
	}
	
	public List<Entry<Document, Integer>> match(List<String> query) {
		
		Map<Document, Integer> scores = new HashMap<Document, Integer>();
		
		Collection<Term> terms = null;
		try {
			terms = this.database.load(this.tokenizer.tokenize(query));
		} catch (DBException e) {
			e.printStackTrace();
			System.exit(0);
		}

		for (Term term : terms) {
			for (Link link : term.getBinds()) {
				Integer score = scores.get(link.getDoc());
				if (score == null) {
					scores.put(link.getDoc(), link.getTF());
				} else {
					scores.put(link.getDoc(), score + link.getTF());
				}
			}
		}
		
		return this.sort(scores);
	}
	
	private List<Entry<Document, Integer>> sort(Map<Document, Integer> map) {
		List<Entry<Document, Integer>> list = new LinkedList<Entry<Document, Integer>>(map.entrySet());

		// Defined Custom Comparator here
		Collections.sort(list, new Comparator<Entry<Document, Integer>>() {
			public int compare(Entry<Document, Integer> e1, Entry<Document, Integer> e2) {
				return e2.getValue() - e1.getValue();
			}
		});
		
		return list;
	}
	
}