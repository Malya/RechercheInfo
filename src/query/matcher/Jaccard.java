package query.matcher;

import java.util.Collection;
import java.util.Map;

import database.reader.Document;
import database.reader.Link;
import database.reader.Term;

public class Jaccard extends Basic {

	@Override
	protected Map<Document, Double> match(Collection<Term> terms) {
		Map<Document, Double> scores = super.match(terms);
		for(Document doc : scores.keySet()) {
			double score = scores.get(doc);
			score /= doc.getWeight()*doc.getWeight() + terms.size()*terms.size() - score;
			scores.put(doc, score);
		}
		return scores;
	}

	@Override
	protected double match(Term term, Link link) {
		return link.getTF();
	}

}
