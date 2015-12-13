package database.writer.item;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import database.exception.DBException;
import database.support.DBHelper;
import database.support.Query;
import database.support.Table;

public abstract class Items<I extends Item> extends Table {

	private Integer key;
	private Map<String, I> insert;
	private Map<String, I> update;

	protected Items(DBHelper db) throws DBException {
		super(db);
		this.insert = new HashMap<String, I>();
		this.update = null;
	}

	protected void onCreate() {
		this.key = 1;
	}

	protected void onExist() {
		this.key = null;
	}

	protected abstract I item(String name);

	public I get(String name) {
		I item = this.insert.get(name);
		if (item == null) {
			item = this.item(name);
			this.insert.put(name, item);
			if (this.justCreated()) {
				item.setId(this.key);
				this.key += 1;
			}
		}
		return item;
	}

	protected abstract String query();

	protected abstract void refresh(I item, ResultSet rs) throws DBException;

	protected Map<String, I> map() throws DBException {
		if (this.update != null) {
			return this.update;
		}
		this.update = new HashMap<String, I>();
		new Query(this.db, "map()", this.query()) {
			@Override
			protected void process(ResultSet rs) throws DBException,
					SQLException {
				while (rs.next()) {
					key = rs.getInt(1);
					String name = rs.getString(2);
					I item = insert.remove(name);
					if (item != null) {
						item.setId(key);
						refresh(item, rs);
						update.put(name, item);
					}
				}
				key += 1;
			}
		}.execute();
		for (I item : this.insert.values()) {
			item.setId(this.key);
			this.key += 1;
		}
		return this.update;
	}

	protected String update() throws DBException {
		StringBuilder query = new StringBuilder();
		if (!this.justCreated()) {
			for (I item : this.map().values()) {
				query.append(item.update());
			}
		}
		for (I item : this.insert.values()) {
			query.append(item.insert());
		}
		return query.toString();
	}
	
	protected void reset() {
		this.insert = new HashMap<String, I>();
		this.update = null;
	}

}