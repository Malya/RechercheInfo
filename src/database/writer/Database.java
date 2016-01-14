package database.writer;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.sql.SQLException;

import database.exception.DBException;
import database.support.DBHelper;
import database.support.sqlite.SQLite;
import database.writer.item.Documents;
import database.writer.item.Links;
import database.writer.item.Properties;
import database.writer.item.Terms;
import format.Token;
import format.Tokenizer;
import format.stemmer.Stemmer;

public class Database {

	public static final String NAME = "seDB";
	public static final String TABLES[] = {"STATS", "TERMS", "DOCUMENTS", "LINKS"};
 	
	private DBHelper db;
	private Tokenizer tk;
	private Properties stats;
	private Terms terms;
	private Documents docs;
	private Links links;

	public Database() {
		try {
			this.db = new SQLite(NAME);
			this.tk = new Stemmer();
			this.stats = new Properties(this.db);
			this.terms = new Terms(this.db);
			this.docs = new Documents(this.db, this.stats);
			this.links = new Links(db, this.terms, this.docs, this.stats);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public Tokenizer getTokenizer() {
		return tk;
	}
	
	public void links(Token value, String doc, int tf) {
		this.links.links(value.getRoot(), doc, tf);
	}

	public void flush() {
		try {
			this.links.flush();
			this.stats.flush();
		} catch (DBException e) {
			e.printStackTrace();
			System.exit(0);
		}
		try {
			this.db.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void clear() {
		try {
	        new File(NAME + ".db").delete();
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.exit(0);
	    }	
	}

	public void export() {
		for (String table : TABLES) {
			File file = new File(table + ".txt");
			try {
				Writer writer = new FileWriter(file);
				writer.append(db.toString(table));
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		try {
			for (String table : TABLES) {
				sb.append(table + ":\n" + db.toString(table) + "\n");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		return sb.toString();
	}

}
