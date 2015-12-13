package database.support;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import database.exception.DBException;

public abstract class Query {

	protected String name;
	private DBHelper database;
	private String query;
	
	public Query(DBHelper database, String name, String query) {
		this.name = name;
		this.database = database;
		this.query = query;
	}
	
	protected abstract void process(ResultSet rs) throws DBException, SQLException;
	
	public void execute() throws DBException {
		Statement stmt = null;
		try {
			stmt = this.database.get().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		} catch (SQLException e) {
			throw new DBException(this.name + ": Unable to create statement");
		}
		try {
			ResultSet rs = stmt.executeQuery(this.query);
			this.process(rs);
		} catch (SQLException e) {
			throw new DBException(this.name + ": Unable to get result");
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				throw new DBException(this.name + ": Unable to close statement");
			}
		}
	}
	
}
