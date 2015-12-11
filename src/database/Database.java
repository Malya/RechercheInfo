package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;

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
			System.out.println("Term: " + term);
		}

		return result;
	}
	
	public String getDocPath(int docID) {
		String result = "";
		try {
			result = riDB.getSingleString("DOCUMENTS", "Path", "Id='" + docID + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public ResultSet getDocTf(int termId) {
		ResultSet result = null;
		try {
			result = riDB.select("SELECT DocID, TF FROM LINKS WHERE TermID='" + termId + "';");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

}
