package database.reader;

import java.util.ArrayList;
import java.util.List;

import format.Tag;


public class Link {

	private List<Tag> tags;
	private List<Integer> pos;
	
	protected Link() {
		this.tags = new ArrayList<Tag>();
		this.pos = new ArrayList<Integer>();
	}
	
	protected void addTag(Tag tag) {
		this.tags.add(tag);
	}
	
	public List<Tag> getTags() {
		return this.tags;
	}
	
	protected void addPos(int pos) {
		this.pos.add(pos);
	}
	
	public List<Integer> getPos() {
		return this.pos;
	}
	
	public int getTF() {
		return this.tags.size();
	}
	
}
