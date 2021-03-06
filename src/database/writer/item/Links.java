package database.writer.item;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import database.exception.DBException;
import database.item.Bind;
import database.item.Binds;
import database.item.Correlation;
import database.item.Item;
import database.item.Items;
import database.support.DBHelper;
import database.support.Query;
import format.Tag;

public class Links extends Binds<Term, Document> {

	private static final String TABLE =  "CREATE TABLE LINKS                            " + 
										 "(TermID INT  NOT NULL,                        " +
										 " DocID  INT  NOT NULL,                        " +
										 " Tag    INT  NOT NULL,                        " + 
										 " Pos    INT  NOT NULL,                        " +
										 " FOREIGN KEY(TermID) REFERENCES TERMS(Id),    " + 
										 " FOREIGN KEY(DocID) REFERENCES DOCUMENTS(Id)) " ;
	
	private static final String SELECT = "SELECT Term, Path, count(Pos) FROM LINKS AS L JOIN TERMS AS T JOIN DOCUMENTS AS D ON L.TermID = T.Id AND L.DocID = D.Id GROUP BY Term, Path; " ;
	
	private Properties stats;
	
	public Links(DBHelper db, Terms terms, Documents docs, Properties stats) throws DBException {
		super(db, terms, docs);
		this.stats = stats;
	}
	
	@Override
	protected String table() {
		return TABLE;
	}
	
	@Override
	public void links(String term, String doc, Object... args) {
		super.links(term, doc, args);
		this.stats.addTerm(1);
	}
	
	@Override
	protected Correlation<Term, Document> correlation(Term x, Document y,
			Object... args) {
		return new Link(x, y, (Tag) args[0], (Integer) args[1]);
	}
	
	protected String insert(Bind<Term, Document> link) {
		return "INSERT INTO LINKS (TermId, DocID, Tag, Pos) " + "VALUES ('" + link.getX().getId() + "', '" + link.getY().getId() + "', '" + ((Link) link.getCorrelation()).getTag().ordinal()+ "', '" + ((Link) link.getCorrelation()).getPos() + "');";
	}
	
	protected String unlinks(final Items<Term> terms, final Items<Document> docs, final Map<String, Item<Term>> mt, final Map<String, Item<Document>> md) throws DBException {
		final Set<Item<Document>> done = new HashSet<Item<Document>>();
		final StringBuilder sb = new StringBuilder();
		new Query(this.db, "unlink()", SELECT) {
			@Override
			protected void process(ResultSet rs) throws DBException,
					SQLException {
				while (rs.next()) {
					String path = rs.getString(2);
					Item<Document> doc = md.get(path);
					if (doc != null) {
						String term = rs.getString(1);
						int tf = rs.getInt(3);
						terms.get(term).getUnique().unlinks(tf);
						stats.removeTerm(tf);
						if (!done.contains(doc)) {
							done.add(doc);
							sb.append("DELETE FROM LINKS WHERE DocID='" + doc.getId() + "';");
						}
					}
				}
			}	
		}.execute();
		return sb.toString();
	}

}
