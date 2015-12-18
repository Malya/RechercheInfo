package format.impl;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import format.Token;
import format.Tokenizer;



public class Tokens implements Tokenizer {

	private Map<String, Token> tokens;
	private Map<String, List<Token>> roots;
	
	public Tokens() {
		this.tokens = new HashMap<String, Token>();
		this.roots = new HashMap<String, List<Token>>();
	}
	
	public List<Token> tokenize(List<String> words) {
		List<Token> tokens = new ArrayList<Token>(words.size());
		for (String word : words) {
			tokens.add(this.get(word));
		}
		return tokens;
	}
	
	protected Token token(String word) {
		return new Truncaction(word);
	}
	
	public Token get(String word) {
		word = Normalizer.normalize(word, Normalizer.Form.NFD);
		Token token = this.tokens.get(word);
		if (token == null) {
			token = token(word);
			this.tokens.put(word, token);
			String root = token.getRoot();
			List<Token> related = this.roots.get(root);
			if (related == null) {
				related = new ArrayList<Token>();
				this.roots.put(root, related);
			} else {
				root = related.get(0).getRoot();
				((Truncaction) token).setRoot(root);
			}
			related.add(token);
		}
		return token;
	}
	
	public List<Token> related(Token token) {
		return this.roots.get(token.getRoot());
	}
	
	public List<Token> related(String word) {
		return this.related(this.get(word));
	}
	
	public static void main(String args[]) {
		final String words[] = {"Désespérant", "desesperance", "desespoir", "desesperer", "désespérément", "désespérant", "désesperant", "désespéré", "désespérée"};
		Tokens tokens = new Tokens();
		for (String word : words) {
			Token token = tokens.get(word);
			System.out.println("[" + word + "::" + token + "::" + token.getRoot() + "::" + token.getSuffix() + "]");
		}
		for (Entry<String, List<Token>> entry : tokens.roots.entrySet()) {
			List<Token> related = entry.getValue();
			System.out.print("[" + entry.getKey() + "::" + related.get(0).getSuffix());
			for (int i = 1; i < related.size(); i += 1) {
				System.out.print(", " + related.get(i).getSuffix());
			}
			System.out.println("]");
		}
	}
	
}
