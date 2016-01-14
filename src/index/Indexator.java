package index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import database.writer.Database;
import format.Token;
import format.Tokenizer;


public class Indexator {

	/* Version 1 : Poids de 1 pour chaque token
	 * Version 2 : Prise en compte des balises
	 * 
	 */
	
	private static final int VERSION = 1;
		
	private static final boolean log = false;
	private Database db;
	private Tokenizer tokenizer;
	private List<Token> blacklist;
	
	private static final Map<String, Integer> TAG_WEIGHT = new HashMap<>();

	public Indexator() {
		this.db = new Database();
		this.tokenizer = this.db.getTokenizer();
		
		/* INIT TAG WEIGHT */
		if(VERSION == 2) {
			TAG_WEIGHT.put("em", 2);
			TAG_WEIGHT.put("strong", 3);
			TAG_WEIGHT.put("h3", 4);
			TAG_WEIGHT.put("h2", 5);
			TAG_WEIGHT.put("h1", 6);
			TAG_WEIGHT.put("title", 8);
		}
			
		
		/* INIT BLACKLIST */
		List<String> blacklist = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("src/index/stopliste.txt"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		try {
			String word;
			while ((word = reader.readLine()) != null) {
				blacklist.add(word);
			}
			this.blacklist = this.tokenizer.tokenize(blacklist);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
	}

	public void index(Collection<File> files) {
		long time = 0;
		for (File file : files) {
			if (log) {
				time = - System.currentTimeMillis();
			}
			Map<Token, Integer> index = index(file);
			String name = file.getName();
			for (Token value : index.keySet()) {
				db.links(value, name, index.get(value));
			}
			if (log) {
				time += System.currentTimeMillis();
				System.out.println(name + " : " + time + "ms");
			}
		}
		this.db.flush();
	}
	
	private Map<Token, Integer> clean(Map<Token, Integer> index) {
		for (Token word : this.blacklist) {
			index.remove(word);
		}
		return index;
	}
	
	/*
	 * Create the index of words associated with his hit number in one document
	 * Input : File input = one document
	 */
	private Map<Token, Integer> index(File file) {
		Map<Token, Integer> index = new HashMap<Token, Integer>();
		Document doc = null;
		try {
			doc = Jsoup.parse(file, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		// Get all the words of the document (removing the words with less than
		// two characters)
		List<Token> result = this.getTextNodes(doc.children());
		Integer occurence, weight;
		for (Token token : result) {
			// Get the weight of the token
			weight = TAG_WEIGHT.get(token.getTag());
			if(weight == null) {
				weight = 1;
			}
			
			// Update the weight of the token
			occurence = index.get(token);
			if (occurence == null) { // Word already present in the index
				index.put(token, weight);
			} else {
				index.put(token, occurence + weight);
			}
		}
		// Remove the stop words
		this.clean(index);
		return index;
	}

	private List<Token> getTextNodes(Elements elems) {
		List<Token> result = new ArrayList<>();
		
		StringBuilder sb = new StringBuilder();
		for (Element elem : elems) {
			sb.setLength(0);
			for (TextNode node : elem.textNodes()) {
				sb.append(" ").append(node.text());
			}
			
			List<Token> tokens = this.tokenizer.tokenize(sb.toString());
			
			for(Token token : tokens) {
				token.setTag(elem.tagName());
				if (log) { 
					System.out.println("Token: " + token.getRoot() + ", tag: " + token.getTag());
				}
				result.add(token);
			}
			
			result.addAll(this.getTextNodes(elem.children()));
		}
		
		return result;
	}
	
	/*private String getTextNodes(Elements elems) {
		StringBuilder sb = new StringBuilder();
		this.getTextNodes(elems, sb);
		return sb.toString();
	}*/

	public void export() {
		this.db.export();
	}

}
