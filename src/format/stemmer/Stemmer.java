package format.stemmer;

import java.util.ArrayList;
import java.util.List;

import format.Token;
import format.Tokenizer;



public class Stemmer implements Tokenizer {
	
	public List<Token> tokenize(String sentence) {
		List<Token> tokens = new ArrayList<Token>();
		for (String word : sentence.split("[\\s\\p{Punct}\\'’]+")) {
			tokens.add(new Stem(word));
		}
		return tokens;
	}
	
	public List<Token> tokenize(List<String> words) {
		List<Token> tokens = new ArrayList<Token>(words.size());
		for (String word : words) {
			if (word.length() > 1) {
				tokens.add(new Stem(word));
			}
		}
		return tokens;
	}
	
}
