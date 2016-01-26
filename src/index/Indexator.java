package index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import database.writer.Database;
import format.Token;
import format.Tokenizer;


public class Indexator {
		
	private static final boolean log = false;
	private Database db;
	private Tokenizer tokenizer;
	private Set<Token> blacklist;

	public Indexator() {
		this.db = new Database();
		this.tokenizer = this.db.getTokenizer();
		
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
			this.blacklist = new HashSet<Token>(this.tokenizer.tokenize(blacklist));
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
			String name = file.getName();
			for (Token value : this.tokenizer.parse(file)) {
				if (!this.blacklist.contains(value)) {
					db.links(value, name);
				}
			}
			if (log) {
				time += System.currentTimeMillis();
				System.out.println(name + " : " + time + "ms");
			}
		}
		this.db.flush();
	}

	public void export() {
		this.db.export();
	}

}
