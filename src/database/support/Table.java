package database.support;

import java.sql.SQLException;
import java.sql.Statement;

import database.exception.DBException;

public abstract class Table {

	private boolean created;
	protected DBHelper db;
	
	protected Table(DBHelper db) throws DBException {
		this.db = db;
		Statement stmt;
		try {
			stmt = db.get().createStatement();
		} catch (SQLException e) {
			throw new DBException("create(): Unable to open statement");
		}
		try {
			stmt.executeUpdate(this.table());
			this.created = true;
			this.onCreate();
		} catch (SQLException e) {
			this.created = false;
			this.onExist();
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				throw new DBException("create(): Unable to close statement");
			}
		}
	}
	
	protected abstract String table();
	
	protected abstract void onExist();
	
	protected abstract void onCreate();
	
	protected boolean justCreated() {
		return this.created;
	}
	
	protected abstract String update() throws DBException;
	
	public void flush() throws DBException {
		String query = this.update();
		Statement stmt = null;
		try {
			stmt = db.get().createStatement();
		} catch (SQLException e) {
			throw new DBException("flush(): Unable to create statement");
		} 
		try {
			stmt.executeUpdate(query);
		}
		catch (SQLException e) {
			throw new DBException("flush(): Unable to update table");
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				throw new DBException("flush(): Unable to close statement");
			}
		}
	}
}
