package database.writer.item;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import database.exception.DBException;
import database.item.Item;
import database.item.Join;
import database.item.Reference;
import database.support.DBHelper;

public class Terms extends Reference<Term, Root> {
	
	private static final String TABLE = "CREATE TABLE TERMS                      " + 
										"(Id   INTEGER PRIMARY KEY,              " + 
										" Term CHAR(20) NOT NULL UNIQUE,         " +
										" IDF  INT NOT NULL,                     " +
										" Root INT NOT NULL,                     " +
										" FOREIGN KEY(Root) REFERENCES ROOT(Id)) " ;

	private static final String SELECT = "SELECT Id, Term, IDF FROM TERMS; " ;
	
	
	public Terms(DBHelper db, Roots roots) throws DBException {
		super(db, roots);
	}

	@Override
	protected String table() {
		return TABLE;
	}

	@Override
	protected Term unique(String name) {
		return new Term(name);
	}

	@Override
	protected String query() {
		return SELECT;
	}

	@Override
	protected void refresh(Term term, ResultSet rs) throws DBException {
		try {
			term.links(rs.getInt(3));
		} catch (SQLException e) {
			throw new DBException("refresh: Unable to get IDF field");
		}
	}

	@Override
	protected String insert(Join<Term, Root> term) {
		return "INSERT INTO TERMS (Id, Term, IDF, Root) " + "VALUES ('" + term.getId() + "', '" + term.getUnique().getWord() + "', '" + term.getUnique().getIDF() + "', '" + term.getY().getId() + "');";
	}

	@Override
	protected String update(Join<Term, Root> term) {
		return "UPDATE TERMS set IDF='" + term.getUnique().getIDF() + "' where Id='" + term.getId() + "';";
	}
	
	protected Map<String, Item<Term>> map() throws DBException {
		Map<String, Item<Term>> map = super.map();
		return map;
	}
	
}
