package database.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

import database.exception.DBException;
import database.reader.item.Items;
import database.support.DBHelper;
import database.support.Query;

public class Documents extends Items<Document> {

	private static final String SELECT = "SELECT Path, Weight FROM DOCUMENTS;";
	
	@Override
	protected Document item(String name) {
		return new Document(name);
	}
	
	public void update(DBHelper database) throws DBException {
		StringBuilder query = new StringBuilder(SELECT);
		new Query(database, "load()", query.toString()) {
			@Override
			protected void process(ResultSet rs) throws DBException,
					SQLException {
				while (rs.next()) {
					String path = rs.getString(1);
					int weight = rs.getInt(2);
					get(path).setWeight(weight);
				}
			}
		}.execute();
	}

}
