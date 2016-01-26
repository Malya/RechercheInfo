package query.matcher;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import database.reader.Document;
import database.reader.Link;
import database.reader.Term;
import query.core.Core;

public abstract class Basic extends Core {

	@Override
	protected Map<Document, Double> match(Collection<Term> terms) {
		Map<Document, Double> scores = new HashMap<Document, Double>();
		for (Term term : terms) {
			for (Entry<Document, Link> entry : term.getBinds().entrySet()) {
				Document doc = entry.getKey();
				Link link = entry.getValue();
				Double score = scores.get(doc);
				if (score == null) {
					score = 0.0;
				}
				score += term.getWeight() * match(term, doc, link);
				scores.put(doc, score);
			}
		}
		return scores;
	}

	protected abstract double match(Term term, Document doc, Link link);
	
}
