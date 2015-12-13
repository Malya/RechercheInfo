package database.reader.item;

import java.util.HashMap;
import java.util.Map;

public abstract class Items<I> {

	private Map<String, I> map;
	
	protected Items() {
		this.map = new HashMap<String, I>();
	}
	
	public I get(String name) {
		I item = this.map.get(name);
		if (item == null) {
			item = this.item(name);
			this.map.put(name, item);
		}
		return item;
	}
	
	protected abstract I item(String name);
	
	public void clear() {
		this.map.clear();
	}
	
}
