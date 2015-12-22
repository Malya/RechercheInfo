package format.automaton;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class DNode extends Node {

	
	private Map<Character, Node> transitions;
	
	protected DNode() {
		this.transitions = new HashMap<Character, Node>();
	}
		
	@Override
	public void map(Character c, Node n) {
		this.transitions.put(c, n);
	}
	
	@Override
	public void unmap(Character c, Node n) {
		this.transitions.remove(c);
	}
	
	@Override
	public Integer isoClass(Set<Node> set) {
		set.add(this);
		return this.end;
	}
	
	@Override
	public void getWay(Set<Character> set) {
		for (Character c : this.transitions.keySet()) {
			set.add(c);
		}
	}
	
	@Override
	public Node getDestination(char c) {
		return this.transitions.get(c);
	}
	
	@Override
	public Integer getDestination(Set<Node> set, Character c) {
		Node node = this.transitions.get(c);
		if (node != null) {
			return node.isoClass(set);
		}
		return null;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(name);
		if (this.end != null) {
			sb.append("(").append(this.end).append(")");
		}
		sb.append("\n");
		for (Entry<Character, Node> e : this.transitions.entrySet()) {
			sb.append(e.getKey()).append(" => ").append(e.getValue().getName()).append("\n");
		}
		return sb.toString();
	}
	
}
