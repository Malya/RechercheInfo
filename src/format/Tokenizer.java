package format;

import java.util.List;

public interface Tokenizer {

	public List<Token> tokenize(String sentence);
	
	public List<Token> tokenize(List<String> words);	
	
}
