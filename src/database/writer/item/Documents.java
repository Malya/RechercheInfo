package database.writer.item;

import java.sql.ResultSet;

import database.exception.DBException;
import database.support.DBHelper;

public class Documents extends Items<Document> {

	private static final String TABLE  = "CREATE TABLE DOCUMENTS                  " + 
										 "(Id     INTEGER PRIMARY KEY, 	          " + 
										 " Path   CHAR(50) NOT NULL UNIQUE,       " +
										 " Weight INT NOT NULL)                   " ;
	
	private static final String SELECT = "SELECT Id, Path FROM DOCUMENTS; " ;
	
	public Documents(DBHelper db) throws DBException {
		super(db);
	}

	@Override
	protected String table() {
		return TABLE;
	}

	@Override
	protected Document item(String name) {
		return new Document(name);
	}

	@Override
	protected String query() {
		return SELECT;
	}

	@Override
	protected void refresh(Document doc, ResultSet rs) throws DBException {
	}
	
	
}
