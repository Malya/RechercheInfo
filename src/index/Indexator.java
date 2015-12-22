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

	private static final boolean log = false;
	private Database db;
	private Tokenizer tokenizer;
	private List<Token> blacklist;

	public Indexator() {
		this.db = new Database();
		this.tokenizer = this.db.getTokenizer();
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
		String result = this.getTextNodes(doc.children());
		Integer occurence;
		for (Token token : this.tokenizer.tokenize(result)) {
			occurence = index.get(token);
			if (occurence == null) { // Word already present in the index
				index.put(token, 1);
			} else {
				index.put(token, occurence + 1);
			}
		}
		// Remove the stop words
		this.clean(index);
		return index;
	}

	private StringBuilder getTextNodes(Elements elems, StringBuilder sb) {
		for (Element elem : elems) {
			for (TextNode node : elem.textNodes()) {
				sb.append(" ").append(node.text());
			}
			this.getTextNodes(elem.children(), sb);
		}
		return sb;
	}
	
	private String getTextNodes(Elements elems) {
		StringBuilder sb = new StringBuilder();
		this.getTextNodes(elems, sb);
		return sb.toString();
	}

	public void export() {
		this.db.export();
	}

}
