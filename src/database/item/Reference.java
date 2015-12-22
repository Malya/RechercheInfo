package database.item;

import java.util.Map;

import database.exception.DBException;
import database.support.DBHelper;

public abstract class Reference<X extends Unique, Y extends Unique> extends Items<X> {

	private Items<Y> y;
	
	protected Reference(DBHelper db, Items<Y> y) throws DBException {
		super(db);
		this.y = y;
	}
	
	protected Item<X> item(String name) {
		return new Join<X, Y>(this.unique(name));
	}
	
	@SuppressWarnings("unchecked")
	public Join<X, Y> get(String name) {
		return (Join<X, Y>) super.get(name);
	}
	
	public void attach(String x, String y) {
		((Join<X, Y>) this.get(x)).attach(this.y.get(y));
	}
	
	protected Map<String, Item<X>> map() throws DBException {
		this.y.map();
		return super.map();
	}
	
	@SuppressWarnings("unchecked")
	protected String insert(Item<X> item) {
		return this.insert((Join<X, Y>) item);
	}
	
	protected abstract String insert(Join<X, Y> join);
	
	@SuppressWarnings("unchecked")
	protected String update(Item<X> item) {
		return this.update((Join<X, Y>) item);
	}
	
	protected abstract String update(Join<X, Y> join);
	
	@Override
	protected String update() throws DBException {
		this.y.flush();
		this.y.reset();
		return super.update();
	}


}
