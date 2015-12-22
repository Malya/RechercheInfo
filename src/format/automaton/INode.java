package format.automaton;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class INode extends Node {

	private Map<Character, Set<Node>> transitions;
	
	protected INode() {
		this.transitions = new HashMap<Character, Set<Node>>();
	}
	
	@Override
	public void map(Character c, Node n) {
		Set<Node> transitions = this.transitions.get(c);
		if (transitions == null) {
			transitions = new HashSet<Node>();
			this.transitions.put(c, transitions);
		}
		transitions.add(n);
	}
	
	@Override
	public void unmap(Character c, Node n) {
		Set<Node> set = this.transitions.get(c);
		if (set != null) {
			set.remove(n);
			if (set.isEmpty()) {
				this.transitions.remove(c);
			}
		}
	}
	
	@Override
	public Integer isoClass(Set<Node> set) {
		Integer end = this.end;
		if (set.add(this)) {
			for (Node node : this.transitions.get(null)) {
				Integer res = node.isoClass(set);
				if (res != null) {
					end = res;
				}
			}
		}
		return end;
	}
	
	@Override
	public void getWay(Set<Character> set) {
		for (Character c : this.transitions.keySet()) {
			if (c != null) {
				set.add(c);
			}
		}
	}
	
	@Override
	public Node getDestination(char c) {
		return null;
	}
	
	@Override
	public Integer getDestination(Set<Node> set, Character c) {
		Integer end = null;
		Set<Node> list = this.transitions.get(c);
		if (list != null) {
			for (Node node : list) {
				Integer res = node.isoClass(set);
				if (res != null) {
					end = null;
				}
			}
		}
		return end;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(name);
		if (this.end != null) {
			sb.append("(").append(this.end).append(")");
		}
		sb.append("\n");
		for (Entry<Character, Set<Node>> e : this.transitions.entrySet()) {
			sb.append(e.getKey()).append(" =>");
			for (Node n : e.getValue()) {
				sb.append(" ").append(n.getName());
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
}
