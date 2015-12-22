package database.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import database.exception.DBException;
import database.support.DBHelper;
import database.support.Table;

public abstract class Binds<X extends Unique, Y extends Unique> extends Table {

	private Items<X> x;
	private Items<Y> y;
	
	private List<Bind<X, Y>> insert;
	
	protected Binds(DBHelper database, Items<X> x, Items<Y> y) throws DBException {
		super(database);
		this.x = x;
		this.y = y;
		this.insert = new ArrayList<Bind<X, Y>>(); 
	}
	
	@Override
	protected void onExist() {}

	@Override
	protected void onCreate() {}
	
	protected abstract Correlation<X, Y> correlation(X x, Y y, Object...args);
	
	public void links(String x, String y, Object... args) {
		Item<X> xi = this.x.get(x);
		Item<Y> yi = this.y.get(y);
		Correlation<X, Y> correlation = this.correlation(xi.getUnique(), yi.getUnique(), args);
		this.insert.add(new Bind<X, Y>(xi, yi, correlation));
	}
	
	protected abstract String unlinks(Items<X> x, Items<Y> y, Map<String, Item<X>> mx, Map<String, Item<Y>> my) throws DBException;
	
	protected String unlinks() throws DBException {
		return this.unlinks(this.x, this.y, this.x.map(), this.y.map());
	}
	
	protected abstract String insert(Bind<X, Y> link);
	
	@Override
	protected String update() throws DBException {
		StringBuilder query = new StringBuilder();
		if (!this.justCreated()) { 
			query.append(this.unlinks());
		}
		for (Bind<X, Y> link : this.insert) {
			query.append(this.insert(link));
		}
		this.x.flush();
		this.y.flush();
		this.x.reset();
		this.y.reset();
		this.insert = new ArrayList<Bind<X, Y>>();
		return query.toString();
	}
	
}
