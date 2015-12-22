package database.reader.item;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import database.exception.DBException;
import database.support.DBHelper;
import database.support.Query;

public abstract class Items<I> {

	private Map<String, I> map;
	private DBHelper database;
	
	protected Items(DBHelper database) {
		this.map = new HashMap<String, I>();
		this.database = database;
	}
	
	public I get(String name) {
		I item = this.map.get(name);
		if (item == null) {
			item = this.item(name);
			this.map.put(name, item);
		}
		return item;
	}
	
	protected abstract I item(String name);
	
	protected abstract String query();
	
	protected abstract void refresh(I item, ResultSet rs) throws SQLException;
	
	public void load() throws DBException {
		StringBuilder query = new StringBuilder(this.query());
		new Query(this.database, "load()", query.toString()) {
			@Override
			protected void process(ResultSet rs) throws DBException,
					SQLException {
				while (rs.next()) {
					String name = rs.getString(1);
					I item = map.get(name);
					if (item != null) {
						refresh(item, rs);
					}
				}
			}
		}.execute();
	}
	
	public void clear() {
		this.map.clear();
	}
	
}
