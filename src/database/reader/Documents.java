package database.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

import database.reader.item.Items;
import database.support.DBHelper;

public class Documents extends Items<Document> {

	private static final String SELECT = "SELECT Path, Weight FROM DOCUMENTS";
	
	public Documents(DBHelper database) {
		super(database);
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
	protected void refresh(Document doc, ResultSet rs) throws SQLException {
		int weight = rs.getInt(2);
		doc.setWeight(weight);
	}

	@Override
	protected String property() {
		return "Path";
	}

}
