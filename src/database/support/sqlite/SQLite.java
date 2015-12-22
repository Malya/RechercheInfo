package database.support.sqlite;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import database.support.DBHelper;

public class SQLite extends DBHelper {
	
	final String classPath = "org.sqlite.JDBC";
	
	public SQLite(String path) throws SQLException {
		super(Collections.singletonList(path));
	}
	
	protected String classPath() {
		return this.classPath;
	}
	
	protected Connection open(List<String> args) throws SQLException {
		return DriverManager.getConnection("jdbc:sqlite:" + args.get(0) + ".sqlite");
	}

}