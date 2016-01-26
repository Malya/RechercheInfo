package format.stemmer;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import format.Tag;
import format.Token;
import format.parser.Parser;



public class Stemmer extends Parser {
	
	public Collection<Token> tokenize(String sentence) {
		return this.tokenize(Arrays.asList(sentence.split("[\\s\\p{Punct}\\'’]+")));
	}
	
	public Collection<Token> tokenize(List<String> words) {
		return this.tokenize(words, null).values();
	}
	
	@SuppressWarnings("unchecked")
	public Map<Integer, Token> tokenize(List<String> words, List<Double> weights) {
		Map<Integer, Stem> map = new HashMap<Integer, Stem>(words.size());
		for (int i = 0; i < words.size(); i += 1) {
			String word = words.get(i);
			if (word.length() > 1) {
				Stem stem = new Stem(word);
				if (stem.length() > 1) {
					Stem pres = map.get(stem.hashCode());
					if (pres == null) {
						pres = stem;
						map.put(stem.hashCode(), stem);
					}
					double weight = 1.0;
					if (weights != null) {
						weight = weights.get(i);
					}
					pres.addWeight(weight);
				}
			}
		}
		return (Map<Integer, Token>) (Map<?, ?>) map;
	}

	@Override
	protected void setTag(Token token, Tag tag) {
		((Stem) token).setTag(tag);
	}
	
	@Override
	protected void setPos(Token token, int pos) {
		((Stem) token).setPos(pos);
	}
	
}
