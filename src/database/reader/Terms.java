package database.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

import database.reader.item.Items;
import database.support.DBHelper;

public class Terms extends Items<Term> {

	protected Terms(DBHelper database) {
		super(database);
	}

	private static final String SELECT = "SELECT R.Root, sum(T.IDF) FROM TERMS AS T JOIN ROOTS AS R ON T.Root=R.Id GROUP BY R.Root;";
	
	@Override
	protected Term item(String name) {
		return new Term(name);
	}

	@Override
	protected String query() {
		return SELECT;
	}

	@Override
	protected void refresh(Term term, ResultSet rs) throws SQLException {
		int idf = rs.getInt(2);
		term.setIDF(idf);
	}

}
