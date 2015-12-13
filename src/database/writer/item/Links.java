package database.writer.item;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import database.exception.DBException;
import database.support.DBHelper;
import database.support.Query;
import database.support.Table;

public class Links extends Table {

	private static final String TABLE =  "CREATE TABLE LINKS                            " + 
										 "(TermID INT  NOT NULL,                        " +
										 " DocID  INT  NOT NULL,                        " +
										 " TF     INT NOT NULL,                         " + 
										 " FOREIGN KEY(TermID) REFERENCES TERMS(Id),    " + 
										 " FOREIGN KEY(DocID) REFERENCES DOCUMENTS(Id)) " ;
	
	private static final String SELECT = "SELECT Term, Path, TF FROM LINKS AS L JOIN TERMS AS T JOIN DOCUMENTS AS D ON L.TermID = T.Id AND L.DocID = D.Id; " ;
	
	private Terms terms;
	private Documents docs;
	
	private List<Link> insert;
	private StringBuilder update; 
	
	public Links(DBHelper db, Terms terms, Documents docs) throws DBException {
		super(db);
		this.terms = terms;
		this.docs = docs;
		this.insert = new ArrayList<Link>();;
	}
	
	@Override
	protected String table() {
		return TABLE;
	}

	@Override
	protected void onExist() {
		this.update = new StringBuilder();
	}

	@Override
	protected void onCreate() {
		this.update = null;
	}
	
	public void links(Term term, Document doc, int tf) {
		this.insert.add(new Link(term, doc, tf));
	}
	
	private void unlinks(final Terms terms, final Documents docs) throws DBException {
		final Map<String, Document> refresh = docs.map();
		final Set<Document> done = new HashSet<Document>();
		new Query(this.db, "unlink()", SELECT) {
			@Override
			protected void process(ResultSet rs) throws DBException,
					SQLException {
				while (rs.next()) {
					String path = rs.getString(2);
					Document doc = refresh.get(path);
					if (doc != null) {
						String term = rs.getString(1);
						int tf = rs.getInt(3);
						terms.get(term).unlinks(tf);
						if (!done.contains(doc)) {
							done.add(doc);
							update.append("DELETE FROM LINKS WHERE DocID='" + doc.getId() + "';");
						}
					}
				}
			}	
		}.execute();
	}

	@Override
	protected String update() throws DBException {
		StringBuilder query = new StringBuilder();
		this.docs.flush();
		if (!this.justCreated()) { 
			this.unlinks(terms, docs);
			query.append(this.update);
		}
		this.terms.flush();
		for (Link link : this.insert) {
			query.append(link.insert());
		}
		this.docs.reset();
		this.terms.reset();
		this.insert = new ArrayList<Link>();
		this.update = new StringBuilder();
		return query.toString();
	}

}
