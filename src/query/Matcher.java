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
	private int version = 1;
	
	public Matcher() {
		this.tokenizer = new TokensImpl();
		this.database = new Database();
	}
	
	public List<Entry<Document, Float>> match(String query) {
		return this.match(Arrays.asList(query.split("[\\s\\p{Punct}]+")));
	}
	
	public List<Entry<Document, Float>> match(List<String> query) {
		
		Map<Document, Float> scores = new HashMap<Document, Float>();
		
		Collection<Term> terms = null;
		try {
			terms = this.database.load(this.tokenizer.tokenize(query));
		} catch (DBException e) {
			e.printStackTrace();
			System.exit(0);
		}
		Float score, TF;
		for (Term term : terms) {
			for (Link link : term.getBinds()) {
				score = scores.get(link.getDoc());
				if(version == 1) {
					if (score == null) {
						scores.put(link.getDoc(), (float)link.getTF());
					} else {
						scores.put(link.getDoc(), score + link.getTF());
					}
				} else if(version == 2) {
					TF = (float)link.getTF();
					if (score == null) {
						scores.put(link.getDoc(), TF * TF);
					} else {
						scores.put(link.getDoc(), score + TF * TF);
					}
				}
			}
		}
		
		if(version == 2) {
			for(Document doc : scores.keySet()) {
				score = (float) Math.sqrt((double) scores.get(doc));
				score = (float) (score / (Math.sqrt((double) doc.getWeight()) * Math.sqrt((double) terms.size())));
				//System.out.println("Weight: " + doc.getWeight() + ", score: " + score);
				scores.put(doc, score);
			}
		}
		
		return this.sort(scores);
	}
	
	private List<Entry<Document, Float>> sort(Map<Document, Float> map) {
		List<Entry<Document, Float>> list = new LinkedList<Entry<Document, Float>>(map.entrySet());

		// Defined Custom Comparator here
		Collections.sort(list, new Comparator<Entry<Document, Float>>() {
			public int compare(Entry<Document, Float> e1, Entry<Document, Float> e2) {
				float result = e2.getValue() - e1.getValue();
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
	
	public void setVersion(int version) {
		this.version = version;
	}
	
}