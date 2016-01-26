package query;

import java.util.List;
import java.util.Map.Entry;

import database.reader.Document;

public interface Matcher {
	
	public List<Entry<Document, Double>> match(String query);
	
	public List<Entry<Document, Double>> match(List<String> query);
	
	public List<Entry<Document, Double>> match(List<String> query, List<Double> weight);
}
