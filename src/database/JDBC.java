package database;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface JDBC {
	public void createTable(String sql) throws SQLException;

	public void execute(String query) throws SQLException;
	
	public ResultSet select(String query) throws SQLException;
	
	public void insert(String table, String fields, String values) throws SQLException;
	
	public int lastInsertedId() throws SQLException;
	
	public void update(String table, String set, String where) throws SQLException;
	
	public int getSingleInt(String table, String field, String where) throws SQLException;
	
	public void commit();
	
	public void clear();

	public String toString(String table);
	
}
