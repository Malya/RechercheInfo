package database.item;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import database.JDBC;

public class Links {

	final String TABLE =  "CREATE TABLE LINKS                            " + 
			 			  "(TermID INT  NOT NULL,                        " +
			 			  " DocID  INT  NOT NULL,                        " +
			 			  " TF     INT NOT NULL,                         " + 
			 			  " FOREIGN KEY(TermID) REFERENCES TERMS(Id),    " + 
			 			  " FOREIGN KEY(DocID) REFERENCES DOCUMENTS(Id)) " ;
	
	final String SELECT = "SELECT Term, Path, TF FROM LINKS AS L JOIN TERMS AS T JOIN DOCUMENTS AS D ON L.TermID = T.Id AND L.DocID = D.Id; " ;
	
	private JDBC db;
	private List<Link> insert = new ArrayList<Link>();
	
	public Links(JDBC db) {
		this.db = db;
		this.insert = new ArrayList<Link>();;
		try {
			db.createTable(TABLE);
		} catch (SQLException e) {}
	}
	
	public void links(Term term, Document doc, int tf) {
		this.insert.add(new Link(term, doc, tf));
	}
	
	public void unlinks(Terms terms, Documents docs) {
		Map<String, Document> refresh = docs.toRefresh();
		try {
			ResultSet rs = db.select(SELECT);
			while (rs.next()) {
				String path = rs.getString(2);
				Document doc = refresh.get(path);
				if (doc != null) {
					String term = rs.getString(1);
					int tf = rs.getInt(3);
					terms.get(term).unlinks(tf);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void flush() throws SQLException {
		StringBuilder query = new StringBuilder();
		for (Link link : this.insert) {
			query.append(link.insert());
		}
		db.execute(query.toString());
	}
}
