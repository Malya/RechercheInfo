import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class SQLiteJDBC {

	private Connection con;
	private String name;

	public SQLiteJDBC(String name) throws Exception {
		Class.forName("org.sqlite.JDBC");
		this.name = name;
		con = DriverManager.getConnection("jdbc:sqlite:" + name + ".db");
	}

	public void createTable(String sql) throws SQLException {
		Statement stmt;
		stmt = con.createStatement();
		stmt.executeUpdate(sql);
		stmt.close();
	}

	public void insert(String table, String fields, String values) throws SQLException {
		Statement stmt;
		String sql;
		stmt = con.createStatement();
		sql = "INSERT INTO " + table + " (" + fields + ") " + "VALUES ("
				+ values + ");";
		stmt.executeUpdate(sql);
	}
	
	public int lastInsertedId() throws SQLException {
		Statement stmt;
		stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ROWID();");
		if (!rs.next())
			throw new SQLException();
		return rs.getInt(1);
	}
	
	public void update(String table, String set, String where) throws SQLException {
		Statement stmt;
		String sql;
		stmt = con.createStatement();
		sql = "UPDATE " + table + " set " + set + " where " + where + ";";
		stmt.executeUpdate(sql);
	}
	
	public int getSingleInt(String table, String field, String where) throws SQLException {
		Statement stmt;
		stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT " + field + " FROM " + table + " WHERE " + where + ";");
		if (!rs.next())
			throw new SQLException();
		return rs.getInt(1);
	}
	
	public void clear() {
	    try {
	        new File(name + ".db").delete();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }	
	}

	public String toString(String table) {
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
					int space = lengths.get(i) - lines.get(j).get(i).length()
							+ 2;
					int left = space / 2;
					int rigth = space - left;
					for (int x = 0; x < left; x++)
						sb.append(" ");
					sb.append(lines.get(j).get(i));
					for (int x = 0; x < rigth; x++)
						sb.append(" ");
				}
				sb.append("\n");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

}