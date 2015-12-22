package database.item;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import database.exception.DBException;
import database.support.DBHelper;
import database.support.Query;
import database.support.Table;

public abstract class Items<U extends Unique> extends Table {

	private Integer key;
	public Map<String, Item<U>> insert;
	private Map<String, Item<U>> update;
	
	protected Items(DBHelper db) throws DBException {
		super(db);
		this.insert = new HashMap<String, Item<U>>();
		this.update = null;
	}
	
	protected void onCreate() {
		this.key = 1;
	}

	protected void onExist() {
		this.key = null;
	}
	
	protected abstract U unique(String name);
	
	protected Item<U> item(String name) {
		return new Item<U>(this.unique(name));
	}
	
	public Item<U> get(String name) {
		Item<U> item = this.insert.get(name);
		if (item == null) {
			if (this.update != null) {
				item = this.update.get(name);
			}
			if (item == null) {
				item = item(name);
				this.insert.put(name, item);
				if (this.justCreated()) {
					item.setId(this.key);
					this.key += 1;
				}
			}
		}
		return item;
	}
	
	protected abstract String query();

	protected abstract void refresh(U unique, ResultSet rs) throws DBException;
	
	protected Map<String, Item<U>> map() throws DBException {
		if (this.update == null) {
			this.update = new HashMap<String, Item<U>>();
			new Query(this.db, "map()", this.query()) {
				@Override
				protected void process(ResultSet rs) throws DBException,
				SQLException {
					while (rs.next()) {
						key = rs.getInt(1);
						String name = rs.getString(2);
						Item<U> item = insert.remove(name);
						if (item != null) {
							item.setId(key);
							refresh(item.getUnique(), rs);
							update.put(name, item);
						}
					}
					key += 1;
				}
			}.execute();
			for (Item<U> item : this.insert.values()) {
				item.setId(this.key);
				this.key += 1;
			}
		}
		return this.update;
	}
	
	protected abstract String insert(Item<U> item);
	
	protected abstract String update(Item<U> item);
	
	protected String update() throws DBException {
		StringBuilder query = new StringBuilder();
		if (!this.justCreated()) {
			for (Item<U> item : this.map().values()) {
				query.append(this.update(item));
			}
		}
		for (Item<U> item : this.insert.values()) {
			query.append(this.insert(item));
		}
		return query.toString();
	}
	
	protected void reset() {
		this.insert = new HashMap<String, Item<U>>();
		this.update = null;
	}
	
}
