package format;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Tokenizer {

	public List<Token> parse(File file);
	
	public Collection<Token> tokenize(String sentence);
	
	public Collection<Token> tokenize(List<String> words);
	
	public Map<Integer, Token> tokenize(List<String> words, List<Double> weights);
	
}
