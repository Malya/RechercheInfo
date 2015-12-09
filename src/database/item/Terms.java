package database.item;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import database.JDBC;

public class Terms {
	
	final String TABLE = "CREATE TABLE TERMS                " + 
			 			 "(Id   INTEGER PRIMARY KEY,        " + 
			 			 " Term CHAR(7) NOT NULL UNIQUE,    " +
			 			 " IDF  INT NOT NULL)               " ;

	final String SELECT = "SELECT Id, Term, IDF FROM TERMS; " ;
	
	private JDBC db;	
	private int key;
	private Map<String, Term> map;
	private Set<Term> insert;
	private Set<Term> update;
	
	public Terms(JDBC db) {
		this.db = db;
		this.map = new HashMap<String, Term>();
		this.insert = new HashSet<Term>();
		this.update = new HashSet<Term>();
		try {
			db.createTable(TABLE);
			this.key = 1;
		} catch (SQLException e) {
			try {
				ResultSet rs = db.select(SELECT);
				while (rs.next()) {
					this.key = rs.getInt(1);
					String term = rs.getString(2);
					int idf = rs.getInt(3);
					this.map.put(term, new Term(this.key, term, idf));
				}
				this.key += 1;
			} catch (SQLException x) {
				x.printStackTrace();
			}
		}
	}
	
	public Term get(String word) {
		Term term = this.map.get(word);
		if (term == null) {
			term = new Term(this.key, word);
			this.map.put(word, term);
			this.insert.add(term);
			this.key += 1;
		} else if (!this.insert.contains(term)) {
			this.update.add(term);
		}
		return term;
	}
	
	public void flush() throws SQLException {
		StringBuilder query = new StringBuilder();
		for (Term term : this.insert) {
			query.append(term.insert());
		}
		for (Term term : this.update) {
			query.append(term.update());
		}
		db.execute(query.toString());
	}
	
}
