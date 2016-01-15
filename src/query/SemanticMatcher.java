package query;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import database.reader.Document;

public class SemanticMatcher extends Matcher {

	@Override
	public List<Entry<Document, Double>> match(String query) {
		return this.match(Arrays.asList(query.split("[\\s\\p{Punct}]+")));
	}
}
