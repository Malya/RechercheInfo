package format.stemmer;

import java.util.ArrayList;
import java.util.List;

import format.Tag;
import format.Token;
import format.parser.Parser;



public class Stemmer extends Parser {
	
	public List<Token> tokenize(String sentence) {
		List<Token> tokens = new ArrayList<Token>();
		for (String word : sentence.split("[\\s\\p{Punct}\\'’]+")) {
			if(word.length() > 1) {
				Stem stem = new Stem(word);
				if (stem.length() > 1) {
					tokens.add(stem);
				}
			}
		}
		return tokens;
	}
	
	public List<Token> tokenize(List<String> words) {
		List<Token> tokens = new ArrayList<Token>(words.size());
		for (String word : words) {
			if (word.length() > 1) {
				Stem stem = new Stem(word);
				if (stem.length() > 1) {
					tokens.add(stem);
				}
			}
		}
		return tokens;
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
