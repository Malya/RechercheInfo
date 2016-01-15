package query.matcher;

import java.util.List;
import java.util.Map.Entry;

import query.Matcher;
import database.reader.Document;

public class Semantic implements Matcher {

	private Matcher matcher;
	
	public Semantic(Matcher matcher) {
		this.matcher = matcher;
	}
	
	@Override
	public List<Entry<Document, Double>> match(String query) {
		// TODO
		return null;//this.match(Arrays.asList(query.split("[\\s\\p{Punct}]+")));
	}

	@Override
	public List<Entry<Document, Double>> match(List<String> query) {
		return this.matcher.match(query);
	}
}
