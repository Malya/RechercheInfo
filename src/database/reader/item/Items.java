package database.reader.item;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import database.exception.DBException;
import database.item.Unique;
import database.support.DBHelper;
import database.support.Query;

public abstract class Items<I extends Unique> {

	private static final String WHERE = " WHERE";
	private static final String END = ";";
	
	private Map<String, I> map;
	private DBHelper database;
	private ArrayList<I> todo;
	
	protected Items(DBHelper database) {
		this.map = new HashMap<String, I>();
		this.database = database;
		this.todo = new ArrayList<I>();
	}
	
	public I get(String name) {
		I item = this.map.get(name);
		if (item == null) {
			item = this.item(name);
			this.map.put(name, item);
			this.todo.add(item);
		}
		return item;
	}
	
	public boolean contains(String name) {
		return this.map.containsKey(name);
	}
	
	protected abstract I item(String name);
	
	protected abstract String query();
	
	protected abstract String property();
	
	protected abstract void refresh(I item, ResultSet rs) throws SQLException;
	
	public void load() throws DBException {
		if (this.todo.size() != 0) {
			StringBuilder query = new StringBuilder(this.query()).append(WHERE);
			String or = "";
			for (I item : this.todo) {
				query.append(or).append(" ").append(this.property()).append("='").append(item.getName()).append("' ");
				or = "OR ";
			}
			query.append(END);
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
	}
	
	public void clear() {
		this.map.clear();
	}
	
}
