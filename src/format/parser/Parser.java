package format.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import format.Tag;
import format.Token;
import format.Tokenizer;

public abstract class Parser implements Tokenizer {
	
	private int tokens;
	
	@Override
	public List<Token> parse(File file) {
		this.tokens = 0;
		Document doc = null;
		List<Token> tokens = new ArrayList<Token>();
		try {
			doc = Jsoup.parse(file, "UTF-8");
			this.getTextNodes(doc.children(), tokens);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		return tokens;
	}
	
	protected abstract void setTag(Token token, Tag tag);
	protected abstract void setPos(Token token, int pos);

	private Collection<Token> getTextNodes(Elements elems, Collection<Token> tokens) {
		
		StringBuilder sb = new StringBuilder();
		for (Element elem : elems) {
			sb.setLength(0);
			for (TextNode node : elem.textNodes()) {
				sb.append(" ").append(node.text());
			}
			
			Collection<Token> line = this.tokenize(sb.toString());
			
			for(Token token : line) {
				this.setTag(token, Tag.from(elem.tagName()));
				this.setPos(token, this.tokens);
				this.tokens += 1;
				tokens.add(token);
			}
			this.getTextNodes(elem.children(), tokens);
		}
		return tokens;
	}
	
}
