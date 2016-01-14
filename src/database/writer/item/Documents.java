package database.writer.item;

import java.sql.ResultSet;

import database.exception.DBException;
import database.support.DBHelper;

import database.item.Item;
import database.item.Items;

public class Documents extends Items<Document> {

	private static final String TABLE  = "CREATE TABLE DOCUMENTS                  " + 
										 "(Id     INTEGER PRIMARY KEY, 	          " + 
										 " Path   CHAR(50) NOT NULL UNIQUE,       " +
										 " Weight INT NOT NULL)                   " ;
	
	private static final String SELECT = "SELECT Id, Path FROM DOCUMENTS; " ;
	
	private Properties stats;
	
	public Documents(DBHelper db, Properties stats) throws DBException {
		super(db);
		this.stats = stats;
	}

	@Override
	protected String table() {
		return TABLE;
	}

	@Override
	protected Document unique(String name) {
		return new Document(name);
	}

	@Override
	protected String query() {
		return SELECT;
	}

	@Override
	protected void refresh(Document doc, ResultSet rs) throws DBException {
	}

	@Override
	protected String insert(Item<Document> doc) {
		this.stats.addDocument();
		return "INSERT INTO DOCUMENTS (Id, Path, Weight) " + "VALUES ('" + doc.getId() + "', '" + doc.getUnique().getPath() + "', '" + doc.getUnique().getWeight() + "');";
	}

	@Override
	protected String update(Item<Document> doc) {
		return "UPDATE DOCUMENTS set Weight='" + doc.getUnique().getWeight() + "' where Id='" + doc.getId() + "';";
	}
	
	
}
