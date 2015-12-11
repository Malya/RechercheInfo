import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import database.Database;


public class Matcher {
	
	private ArrayList<String> queryWords; 
	private HashMap<Integer, Integer> scores;
	private Database db;
	
	public Matcher(Indexator index) {
		queryWords = new ArrayList<>();
		scores = new HashMap<>();
		
		db = index.getDatabase();
	}
	
	public List<Entry<Integer, Integer>> match(String query) {
		
		cleanQuery(query);
		//printCleanedQuery();
		
		scores.clear();
		
		int termId;
		Integer score;
		ResultSet results;
		for(String word : queryWords) {
			termId = db.getTermId(word);
			if(termId != -1) {
				results = db.getDocTf(termId);
				
				if(results != null) {
					try {
						
						while(results.next()) {
							int docID = results.getInt("DocId");
							int tf = results.getInt("TF");
							if(scores.containsKey(docID)) {
								score = scores.get(docID);
								scores.put(docID, score + tf);
							} else {
								scores.put(docID, tf);
							}
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		
				}
			}
		}
		
		return sort();
	}
	
	private void printCleanedQuery() {
		System.out.println("MOTS A ANALYSER : ");
		for(String words : queryWords) {
			System.out.println(words);
		}
	}
	
	private void cleanQuery(String query) {
		queryWords.clear();
		
		for(String word : query.split("[\\s\\p{Punct}]+")) {
			word = Indexator.cleanWord(word);
			if(word.length() > 1) {
				this.queryWords.add(word);
			}
		}
	}
	
	private List<Entry<Integer, Integer>> sort() {
		List<Entry<Integer, Integer>> list = new LinkedList<>(scores.entrySet());

		// Defined Custom Comparator here
		Collections.sort(list, new Comparator<Entry<Integer, Integer>>() {
			public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
				return o2.getValue() - o1.getValue();
			}
		});

		/*Iterator<Entry<Integer, Integer>> it = list.iterator();
		Entry<Integer, Integer> entry;
		while(it.hasNext()) {
			entry = it.next();
			System.out.println("Doc: " + db.getDocPath(entry.getKey()) + ", Score: " + entry.getValue());
		}*/
		
		return list;
	}
	
	public Database getDatabase() {
		return db;
	}
	


}
