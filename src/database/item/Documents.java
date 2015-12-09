package database.item;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import database.JDBC;

public class Documents {

	final String TABLE  = "CREATE TABLE DOCUMENTS               " + 
 			 			  "(Id   INTEGER PRIMARY KEY,           " + 
 			 			  " Path CHAR(50) NOT NULL UNIQUE)      " ;
	
	final String SELECT = "SELECT Id, Path FROM DOCUMENTS;      " ;
	
	private JDBC db;
	private int key;
	private Map<String, Document> map;
	private Map<String, Document> old;
	private Set<Document> insert;
	private Map<String, Document> refresh;
	
	public Documents(JDBC db) {
		this.db = db;
		this.map = new HashMap<String, Document>();
		this.old = new HashMap<String, Document>();
		this.insert = new HashSet<Document>();
		this.refresh = new HashMap<String, Document>();
		try {
			db.createTable(TABLE);
			this.key = 1;
		} catch (SQLException e) {
			try {
				ResultSet rs = db.select(SELECT);
				while (rs.next()) {
					this.key = rs.getInt(1);
					String path = rs.getString(2);
					this.old.put(path, new Document(this.key, path));
				}
				this.key += 1;
			} catch (SQLException x) {
				x.printStackTrace();
			}
		}
	}
	
	public Document get(String path) {
		Document doc = this.map.get(path);
		if (doc == null) {
			doc = this.old.get(path);
			if (doc != null) {
				this.map.put(path, doc);
				this.refresh.put(path, doc);
			} else {
				doc = new Document(key, path);
				this.map.put(path, doc);
				this.insert.add(doc);
				this.key += 1;
			}
		}
		return doc;
	}
	
	public Map<String, Document> toRefresh() {
		return this.refresh;
	}
	
	public void flush() throws SQLException {
		StringBuilder query = new StringBuilder();
		for (Document doc : this.refresh.values()) {
			query.append(doc.unlinks());
		}
		for (Document doc : this.insert) {
			query.append(doc.insert());
		}
		db.execute(query.toString());
	}
	
}
