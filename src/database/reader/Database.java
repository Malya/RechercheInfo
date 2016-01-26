package database.reader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import database.exception.DBException;
import database.support.DBHelper;
import database.support.Query;
import database.support.sqlite.SQLite;
import format.Tag;
import format.Token;

public class Database {

	public static final String NAME = "seDB";
	public static final String TABLES[] = { "STATS", "TERMS", "DOCUMENTS", "LINKS" };

	private static final String STATS = "SELECT Document, Term FROM STATS ORDER BY Date DESC LIMIT 1;";
	
	private static final String SELECT = "SELECT T.Term, D.Path, L.Tag, L.Pos FROM LINKS AS L JOIN TERMS AS T JOIN DOCUMENTS AS D ON L.TermID = T.Id AND L.DocID = D.Id WHERE " ;
	private static final String END = ";";
	
	
	private DBHelper database;
	private Terms terms;
	private Documents docs;

	public Database()throws DBException {
		try {
			this.database = new SQLite(NAME);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		terms = new Terms(this.database);
		docs = new Documents(this.database);
		new Query(this.database, "init()", STATS) {
			@Override
			protected void process(ResultSet rs) throws DBException,
					SQLException {
				while (rs.next()) {
					int docs = rs.getInt(1);
					int terms = rs.getInt(2);
					Term.setDocuments(docs);
					Term.setTerms(terms);
				}
			}
		}.execute();
	}

	public Collection<Term> load(final Map<Integer, Token> tokens) throws DBException {
		Collection<Term> load = new ArrayList<Term>(tokens.size());
		StringBuilder query = new StringBuilder(SELECT);
		String or = "";
		boolean pool = false;
		for (Token token : tokens.values()) {
			String root = token.getRoot();
			if (!terms.contains(root)) {
				pool = true;
				query.append(or).append(" T.Term='").append(root).append("' ");
				or = "OR ";
			}
			load.add(this.terms.get(root));
		}
		query.append(END);
		if (pool) {
			new Query(this.database, "load()", query.toString()) {
				@Override
				protected void process(ResultSet rs) throws DBException,
				SQLException {
					while (rs.next()) {
						String name = rs.getString(1);
						String path = rs.getString(2);
						int tag = rs.getInt(3);
						int pos = rs.getInt(4);
						Term term = terms.get(name);
						term.links(docs.get(path), Tag.from(tag), pos);
						Token token = tokens.get(name.hashCode());
						term.setWeight(token.getWeight());
					}
				}
			}.execute();
			terms.load();
			docs.load();
		}
		return load;
	}
	
	public void reset() {
		this.terms.clear();
		this.docs.clear();
	}

}
