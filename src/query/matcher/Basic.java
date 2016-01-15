package query.matcher;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import database.reader.Document;
import database.reader.Link;
import database.reader.Term;
import query.core.Core;

public abstract class Basic extends Core {

	@Override
	protected Map<Document, Double> match(Collection<Term> terms) {
		Map<Document, Double> scores = new HashMap<Document, Double>();
		for (Term term : terms) {
			for (Link link : term.getBinds()) {
				Double score = scores.get(link.getDoc());
				if (score == null) {
					score = 0.0;
				}
				score += match(term, link);
				scores.put(link.getDoc(), score);
			}
		}
		return scores;
	}

	protected abstract double match(Term term, Link link);
	
}
