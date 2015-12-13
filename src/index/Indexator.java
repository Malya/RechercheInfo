package index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import format.Tokens;

public class Indexator {

	private static final boolean log = true;
	private Database db;
	private Tokens tokenizer;
	private List<String> blacklist;

	public Indexator() {
		this.db = new Database();
		this.tokenizer = this.db.getTokenizer();
		this.blacklist = new ArrayList<String>();
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
				this.blacklist.add(word);
			}
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
			Map<String, Integer> index = index(file);
			String name = file.getName();
			for (String value : index.keySet()) {
				db.links(value, name, index.get(value));
			}
			if (log) {
				time += System.currentTimeMillis();
				System.out.println(name + " : " + time + "ms");
			}
		}
		this.db.flush();
	}
	
	private Map<String, Integer> clean(Map<String, Integer> words) {
		for (String word : this.blacklist) {
			words.remove(word);
		}
		return words;
	}
	
	/*
	 * Create the index of words associated with his hit number in one document
	 * Input : File input = one document
	 */
	private Map<String, Integer> index(File file) {
		Map<String, Integer> index = new HashMap<String, Integer>();
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
		for (String word : result.split("[\\s\\p{Punct}]+")) {
			Token token = this.tokenizer.get(word);
			word = token.getRoot();
			if (word.length() > 1) {
				occurence = index.get(word);
				if (occurence == null) { // Word already present in the index
					index.put(word, 1);
				} else {
					index.put(word, occurence + 1);
				}
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

	public static void main(String[] args) {
		long time = 0;
		if (log) {
			time = -System.currentTimeMillis();
		}
		File input = new File("CORPUS");
		Indexator indexator = new Indexator();
		indexator.index(Arrays.asList(input.listFiles()));
		if (log) {
			time += System.currentTimeMillis();
			System.out.println("Time : " + time / 1000 + "s");
			indexator.db.export();
		} 
	}

}
