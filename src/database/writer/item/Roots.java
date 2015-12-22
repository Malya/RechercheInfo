package database.writer.item;

import java.sql.ResultSet;

import database.exception.DBException;
import database.item.Item;
import database.item.Items;
import database.support.DBHelper;

public class Roots extends Items<Root> {

	private static final String TABLE = "CREATE TABLE ROOTS             " + 
										"(Id   INTEGER PRIMARY KEY,     " + 
										" Root CHAR(7) NOT NULL UNIQUE) " ;
	
	private static final String SELECT = "SELECT Id, Root FROM ROOTS; " ;
	
	public Roots(DBHelper db) throws DBException {
		super(db);
	}

	@Override
	protected String table() {
		return TABLE;
	}
	
	@Override
	protected Root unique(String name) {
		return new Root(name);
	}

	@Override
	protected String query() {
		return SELECT;
	}

	@Override
	protected void refresh(Root unique, ResultSet rs) throws DBException {}

	@Override
	protected String insert(Item<Root> root) {
		return "INSERT INTO ROOTS (Id, Root) " + "VALUES ('" + root.getId() + "', '" + root.getUnique().getRoot() + "');";
	}

	@Override
	protected String update(Item<Root> item) {
		return "";
	}

}
