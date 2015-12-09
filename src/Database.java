import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map.Entry;

public class Database {

	final String TERMS = "CREATE TABLE IF NOT EXISTS TERMS              " + 
			 			 "(Id   INTEGER PRIMARY KEY AUTOINCREMENT,      " + 
			 			 " Term CHAR(7) NOT NULL UNIQUE,                " +
			 			 " IDF  INT NOT NULL)                           " ;
	
	final String DOCS  = "CREATE TABLE IF NOT EXISTS DOCUMENTS          " + 
			  			 "(Id   INTEGER PRIMARY KEY AUTOINCREMENT,      " + 
			  			 " Path CHAR(50) NOT NULL UNIQUE)               " ;
	
	final String LINKS = "CREATE TABLE IF NOT EXISTS LINKS              " + 
			  			 "(TermID INT  NOT NULL,                        " +
			  			 " DocID  INT  NOT NULL,                        " +
			  			 " TF     INT NOT NULL,                         " + 
			  			 " FOREIGN KEY(TermID) REFERENCES TERMS(Id),    " + 
			  			 " FOREIGN KEY(DocID) REFERENCES DOCUMENTS(Id)) ";
	
	SQLiteJDBC riDB;
	HashMap<String, Entry<Integer, Integer>> terms;
	HashMap<String, Integer> docs;

	public Database() {
		try {
			riDB = new SQLiteJDBC("riDB");
			riDB.createTable(TERMS);
			riDB.createTable(DOCS);
			riDB.createTable(LINKS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		terms = new HashMap<String, Entry<Integer, Integer>>();
		docs = new HashMap<String, Integer>();
	}
	
	private Entry<Integer, Integer> refTerm(String term) {
		Entry<Integer, Integer> ref = terms.get(term);
		Integer key = null;
		Integer idf;
		if (ref == null)  {
			try {
				riDB.insert("TERMS", "Term, IDF", "'" + term + "', 0");
				key = riDB.lastInsertedId();
				ref = new AbstractMap.SimpleEntry<Integer, Integer>(key, 0);
			} catch (SQLException e) {
				try {
					key = riDB.getSingleInt("TERMS", "Id", "Term='" + term + "'");
					idf = riDB.getSingleInt("TERMS", "IDF", "Term='" + term + "'");
					ref = new AbstractMap.SimpleEntry<Integer, Integer>(key, idf);
				} catch (SQLException x) {}
			}
			terms.put(term, ref);
		}
		return ref;
	}
	
	private int refDoc(String path) {
		Integer key = docs.get(path);
		if (key == null) {
			try {
				riDB.insert("DOCUMENTS", "Path", "'" + path + "'");
				key = riDB.lastInsertedId();
			} catch (SQLException e) {
				try {
					key = riDB.getSingleInt("DOCUMENTS", "Id", "Path='" + path + "'");
				} catch (SQLException x) {}
			}
			docs.put(path, key);
		}
		return key;

	}
	
	public void links(String term, String doc, int tf) {
		try {
			Entry<Integer, Integer> termRef = refTerm(term);
			int termID = termRef.getKey();
			int idf = termRef.getValue();
			int docID = refDoc(doc);
			riDB.insert("LINKS", "TermID, DocID, TF", termID + ", " + docID + ", " + tf);
			riDB.update("TERMS", "IDF=" + (idf + tf), "Id='" + termID + "'");
			termRef.setValue(idf+tf);
		} catch (SQLException e) {}
		
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("TERMS:\n" + riDB.toString("TERMS") + "\n");
		sb.append("DOCUMENTS:\n" + riDB.toString("DOCUMENTS") + "\n");
		sb.append("LINKS:\n" + riDB.toString("LINKS") + "\n");
		return sb.toString();
	}

}
