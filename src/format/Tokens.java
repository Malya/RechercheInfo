package format;

import java.util.List;

public interface Tokens {

	public List<Token> tokenize(List<String> words);
	
	public Token get(String word);
	
	public List<Token> related(Token token);
	
	public List<Token> related(String word);
	
}
