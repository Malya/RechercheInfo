package database.writer.item;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import database.exception.DBException;
import database.item.Item;
import database.item.Items;
import database.support.DBHelper;

public class Terms extends Items<Term> {
	
	private static final String TABLE = "CREATE TABLE TERMS                      " + 
										"(Id   INTEGER PRIMARY KEY,              " + 
										" Term CHAR(20) NOT NULL UNIQUE,         " +
										" GTF  INT NOT NULL)                     " ;

	private static final String SELECT = "SELECT Id, Term, GTF FROM TERMS; " ;
	
	public Terms(DBHelper db) throws DBException {
		super(db);
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
			throw new DBException("refresh: Unable to get GTF field");
		}
	}

	@Override
	protected String insert(Item<Term> term) {
		return "INSERT INTO TERMS (Id, Term, GTF) " + "VALUES ('" + term.getId() + "', '" + term.getUnique().getWord() + "', '" + term.getUnique().getGTF() + "');";
	}

	@Override
	protected String update(Item<Term> term) {
		return "UPDATE TERMS set GTF='" + term.getUnique().getGTF() + "' where Id='" + term.getId() + "';";
	}
	
	protected Map<String, Item<Term>> map() throws DBException {
		Map<String, Item<Term>> map = super.map();
		return map;
	}
	
}
