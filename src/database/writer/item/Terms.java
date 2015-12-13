package database.writer.item;

import java.sql.ResultSet;
import java.sql.SQLException;

import database.exception.DBException;
import database.support.DBHelper;

public class Terms extends Items<Term> {
	
	private static final String TABLE = "CREATE TABLE TERMS             " + 
										"(Id   INTEGER PRIMARY KEY,     " + 
										" Term CHAR(7) NOT NULL UNIQUE, " +
										" IDF  INT NOT NULL)            " ;

	private static final String SELECT = "SELECT Id, Term, IDF FROM TERMS; " ;
	
	public Terms(DBHelper db) throws DBException {
		super(db);
	}

	@Override
	protected String table() {
		return TABLE;
	}

	@Override
	protected Term item(String name) {
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
	
}
