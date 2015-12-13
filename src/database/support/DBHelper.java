package database.support;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public abstract class DBHelper {

	private Connection con;

	protected DBHelper(List<String> args) throws SQLException {
		try {
			Class.forName(this.classPath());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}
		this.con = this.open(args);
		this.con.setAutoCommit(false);
	}

	protected abstract String classPath();

	protected abstract Connection open(List<String> args) throws SQLException;

	public Connection get() {
		return this.con;
	}

	public void commit() throws SQLException {
		this.con.commit();
	}

	public void close() throws SQLException {
		this.con.close();
	}

	private StringBuilder appendCentered(StringBuilder sb, String value,
			int length) {
		int space = length - value.length() + 2;
		int left = space / 2;
		int rigth = space - left;
		for (int x = 0; x < left; x++)
			sb.append(" ");
		sb.append(value);
		for (int x = 0; x < rigth; x++)
			sb.append(" ");
		return sb;
	}

	public String toString(String table) throws SQLException {
		StringBuilder sb = new StringBuilder();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + table + ";");
			ResultSetMetaData rsmd = rs.getMetaData();
			int n = rsmd.getColumnCount();
			ArrayList<String> columns = new ArrayList<String>(n);
			ArrayList<ArrayList<String>> lines = new ArrayList<ArrayList<String>>();
			ArrayList<Integer> lengths = new ArrayList<Integer>(n);
			for (int i = 1; i <= n; i++) {
				String column = rsmd.getColumnName(i);
				columns.add(column);
				lengths.add(column.length());
			}
			lines.add(columns);
			while (rs.next()) {
				ArrayList<String> line = new ArrayList<String>(n);
				for (int i = 0; i < n; i++) {
					String value = rs.getString(columns.get(i));
					if (value == null)
						value = "";
					line.add(value);
					if (lengths.get(i) < value.length())
						lengths.set(i, value.length());
				}
				lines.add(line);
			}
			rs.close();
			stmt.close();
			for (int j = 0; j < lines.size(); j++) {
				for (int i = 0; i < n; i++) {
					this.appendCentered(sb, lines.get(j).get(i), lengths.get(i));
				}
				sb.append("\n");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

}
