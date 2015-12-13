package database.reader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.exception.DBException;
import database.reader.item.Items;
import database.support.DBHelper;
import database.support.Query;
import database.support.sqlite.SQLite;
import format.Token;

public class Database {

	public static final String NAME = "seDB";
	public static final String TABLES[] = { "TERMS", "DOCUMENTS", "LINKS" };

	private static final String SELECT = "SELECT T.Term, D.Path, L.TF FROM LINKS AS L JOIN TERMS AS T JOIN DOCUMENTS AS D ON L.TermID = T.Id AND L.DocID = D.Id WHERE " ;
	
	private DBHelper database;
	private Items<Term> terms;
	private Items<Document> docs;

	public Database() {
		try {
			this.database = new SQLite(NAME);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		terms = Term.collection();
		docs = Document.collection();
	}

	public List<Term> load(List<Token> tokens) throws DBException {
		List<Term> load = new ArrayList<Term>(tokens.size());
		StringBuilder query = new StringBuilder(SELECT);
		String or = "";
		for (Token token : tokens) {
			String root = token.getRoot();
			query.append(or).append(" T.Term='").append(root).append("'");
			or = " OR";
			load.add(terms.get(root));
		}
		query.append(";");
		new Query(this.database, "load()", query.toString()) {
			@Override
			protected void process(ResultSet rs) throws DBException,
					SQLException {
				while (rs.next()) {
					String term = rs.getString(1);
					String path = rs.getString(2);
					int tf = rs.getInt(3);
					terms.get(term).links(docs.get(path), tf);
				}
			}
		}.execute();
		return load;
	}
	
	public void reset() {
		this.terms.clear();
		this.docs.clear();
	}

}