package database;

import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.Map.Entry;

import database.item.Documents;
import database.item.Links;
import database.item.Terms;

public class Database {

	JDBC riDB;
	Terms terms;
	Documents docs;
	Links links;

	public Database() {
		try {
			riDB = new SQLiteJDBC("riDB");
			this.terms = new Terms(riDB);
			this.docs = new Documents(riDB);
			this.links = new Links(riDB);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void links(String term, String doc, int tf) {
		this.links.links(this.terms.get(term), this.docs.get(doc), tf);
	}

	public void flush() {
		this.links.unlinks(this.terms, this.docs);
		try {
			this.docs.flush();
			this.terms.flush();
			this.links.flush();
			this.riDB.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void clear() {
		riDB.clear();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("TERMS:\n" + riDB.toString("TERMS") + "\n");
		sb.append("DOCUMENTS:\n" + riDB.toString("DOCUMENTS") + "\n");
		sb.append("LINKS:\n" + riDB.toString("LINKS") + "\n");
		return sb.toString();
	}

	public int getTermId(String term) {
		int result = -1;
		try {
			result = riDB.getSingleInt("TERMS", "Id", "Term='" + term + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public Entry<Integer, Integer> getDocTf(int termId) {
		Entry<Integer, Integer> result = null;
		Integer docId, tf;

		try {
			docId = riDB.getSingleInt("LINKS", "DocID", "TermID='" + termId
					+ "'");
			tf = riDB.getSingleInt("LINKS", "TF", "TermID='" + termId + "'");
			result = new AbstractMap.SimpleEntry<Integer, Integer>(docId, tf);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

}
