package format;

import java.io.File;
import java.util.List;

public interface Tokenizer {

	public List<Token> parse(File file);
	
	public List<Token> tokenize(String sentence);
	
	public List<Token> tokenize(List<String> words);	
	
}
