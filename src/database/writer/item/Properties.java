package database.writer.item;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

import database.exception.DBException;
import database.support.DBHelper;
import database.support.Query;
import database.support.Table;

public class Properties extends Table {

	private static final String TABLE  = "CREATE TABLE STATS                  " + 
										 "(Id         INTEGER PRIMARY KEY,    " + 
										 " Document   INTEGER NOT NULL,       " +
										 " Term       INTEGER NOT NULL,       " +
										 " Date		  DATETIME NOT NULL)      " ;
	
	private static final String QUERY = "SELECT Id, Document, Term FROM STATS ORDER BY Date DESC LIMIT 1 ;" ;	
	
	private int id;
	private int documents;
	private int terms;
	
	public Properties(DBHelper db) throws DBException {
		super(db);
	}
	
	@Override
	protected String table() {
		return TABLE;
	}

	@Override
	protected void onExist() {
		
		try {
			new Query(this.db, "onExist()", QUERY) {
				@Override
				protected void process(ResultSet rs) throws DBException,
						SQLException {
					id = rs.getInt(1) + 1;
					documents = rs.getInt(2);
					terms = rs.getInt(3);
				}
			}.execute();
		} catch (DBException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	@Override
	protected void onCreate() {
		this.id = 0;
		this.documents = 0;
		this.terms = 0;
	}

	@Override
	protected String update() throws DBException {
		long t = System.currentTimeMillis();
		Time time = new Time(t);
		Date date = new Date(t);
		return "INSERT INTO STATS (Id, Document, Term, Date) " + "VALUES ('" + this.id + "', '" + this.documents + "', '" + this.terms + "', '" + date.toString() + " " + time.toString() + "');";
	}
	
	public void addDocument() {
		this.documents += 1;
	}
	
	public void addTerm(int tf) {
		this.terms += tf;
	}
	
	public void removeTerm(int tf) {
		this.terms -= tf;
	}

}
