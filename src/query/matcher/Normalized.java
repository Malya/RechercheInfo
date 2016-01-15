package query.matcher;

import java.util.Collection;
import java.util.Map;

import database.reader.Document;
import database.reader.Term;

public abstract class Normalized extends Basic {

	@Override
	protected Map<Document, Double> match(Collection<Term> terms) {
		Map<Document, Double> scores = super.match(terms);
		for(Document doc : scores.keySet()) {
			double score = scores.get(doc);
			score /= Math.sqrt((double) doc.getWeight()*doc.getWeight() * terms.size()*terms.size());
			scores.put(doc, score);
		}
		return scores;
	}
	
}
