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
import format.Tokenizer;
import format.stemmer.Stemmer;


public class Matcher {
	
	/* Version 1 : TF
	 * Version 2 : Similarité Cosinus avec TF
	 * Version 3 : TF-IDF
	 * Version 4 : TF-IDF pondéré par la taille du Document
	 * Version 5 : Distance de Jaccard avec TF
	 * Version 6 : Similarité Cosinus avec TF-IDF
	 * Version 7 : Similarité Cosinus avec TF-IDF pondéré par la taille du Document
	 * 
	 * Classement approximatif : 3,6,1,4,2,7,5 
	 */
	
	private Tokenizer tokenizer;
	private Database database;
	private int version = 1;
	
	public Matcher() {
		this.tokenizer = new Stemmer();;
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
				TF = (float) link.getTF();
				if(version == 3 || version == 6) {
					TF = TF * term.getIDF();
				}
				if(version == 4 || version == 7) { 
					TF = TF / link.getDoc().getWeight() * term.getIDF() ;
				}
				if (score == null) {
					scores.put(link.getDoc(), TF);
				} else {
					scores.put(link.getDoc(), score + TF);
				}
				/*
				if(version == 2 || version == 5) {
					if (score == null) {
						//scores.put(link.getDoc(), TF * TF);
						scores.put(link.getDoc(), TF);
					} else {
						//scores.put(link.getDoc(), score + TF * TF);
						scores.put(link.getDoc(), score + TF);
					}
				} else { // VERSION 1 : somme des TF ; ou 3 : somme des TF_IDF 
					if (score == null) {
						scores.put(link.getDoc(), TF);
					} else {
						scores.put(link.getDoc(), score + TF);
					}
				} */
			}
		}
		
		if(version == 2 || version == 6 || version == 7) {
			for(Document doc : scores.keySet()) {
				//score = (float) Math.sqrt((double) scores.get(doc));
				//score = (float) (score / (Math.sqrt((double) doc.getWeight()) * Math.sqrt((double) terms.size())));
				score = (float) scores.get(doc);
				score = (float) (score / (Math.sqrt((double) doc.getWeight()*doc.getWeight() * terms.size()*terms.size())));
				scores.put(doc, score);
			}
		}
		if (version == 5) {
			for(Document doc : scores.keySet()) {
				score = (float) scores.get(doc);
				score = (float) (score / (doc.getWeight()*doc.getWeight() + terms.size()*terms.size() - score));
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