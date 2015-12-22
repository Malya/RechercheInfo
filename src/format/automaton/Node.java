package format.automaton;

import java.util.Set;

public abstract class Node {

	protected String name;
	protected Integer end;
	
	protected Node() {
		this.name = null;
		this.end = null;
	}
	
	public Node setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Node setFinal(Integer f) {
		this.end = f;
		return this;
	}
	
	public Integer getFinal() {
		return this.end;
	}
	
	public abstract void map(Character c, Node n);
	
	public abstract void unmap(Character c, Node n);
	
	public abstract Integer isoClass(Set<Node> set);

	public abstract void getWay(Set<Character> set);
	
	public abstract Node getDestination(char c);
	
	public abstract Integer getDestination(Set<Node> dests, Character c);
	
}
