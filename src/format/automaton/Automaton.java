package format.automaton;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;
import java.util.Set;
import java.util.Stack;

public class Automaton {

	private Node start;
	private Set<Node> nodes;
	
	private static String name(int node) {
		StringBuilder sb = new StringBuilder();
		do {
			sb.append(Character.toString((char) ('A' + node%26 -1)));
			node /= 26;
		} while (node != 0);
		return sb.reverse().toString();
	}
	
	protected Automaton() {
		this.nodes = new HashSet<Node>();
	}
	
	public Automaton(String sequences[][]) {
		this();
		this.start = this.from(sequences);
		this.mutate();
	}

	private Node dnode() {
		Node node = new DNode();
		this.nodes.add(node);
		return node;
	}
	
	private Node inode() {
		Node node = new INode();
		this.nodes.add(node);
		return node;
	}
	
	protected Node from(String sequence, int type) {
		Node start = dnode();
		Node middle = start;
		Node end = start;
		for (char c : sequence.toCharArray()) {
			end = dnode();
			middle.map(c, end);
			middle = end;
		}
		end.setFinal(type);
		return start;
	}
	
	protected Node from(String sequences[][]) {
		Node union = inode();
		for (int i = 0; i < sequences.length; i += 1) {
			for (String sequence : sequences[i]) {
				union.map(null, from(sequence, i));
			}
		}
		return union;
	}
	
	private Automaton mutate() {
		Map<Set<Node>, Node> merged = new HashMap<Set<Node>, Node>();
		Set<Node> nodes = new HashSet<Node>();
		Stack<Set<Node>> todo = new Stack<Set<Node>>();
		
		Integer end = null;
		Node start = new DNode();
		nodes.add(start);
		Set<Node> iso = new HashSet<Node>();
		end = this.start.isoClass(iso);
		if (end != null) {
			start.setFinal(end);
		}
		merged.put(iso, start);
		todo.push(iso);
		
		
		Set<Node> from;
		Node cur;
		while (!todo.isEmpty()) {
			from = todo.pop();
			cur = merged.get(from);
			Set<Character> ways = new HashSet<Character>();
			for (Node node : from) {
				node.getWay(ways);
			}
			for (Character c : ways) {
				end = null;
				Set<Node> dests = new HashSet<Node>();
				for (Node node : from) {
					Integer res = node.getDestination(dests, c);
					if (res != null) {
						end = res;
					}
				}
				Node dest = merged.get(dests);
				if (dest == null) {
					dest = new DNode();
					if (end != null) {
						dest.setFinal(end);
					}
					nodes.add(dest);
					merged.put(dests, dest);
					todo.push(dests);
				}
				cur.map(c, dest);
			}
		}
		
		this.start = start;
		this.nodes = nodes;
		return this;
	}

	public final Entry<Integer, Integer> match(String word) {
		return match(word, 0, word.length());
	}

	public final Entry<Integer, Integer> match(CharSequence seq, int begin, int end) {
		Node cur = this.start;
		Entry<Integer, Integer> match = null;
		int i = this.init(begin, end);
		while ((cur != null) && this.carry(i, begin, end)) {
			char c = seq.charAt(i);
			cur = cur.getDestination(c);
			if (cur != null) {
				Integer f = cur.getFinal();
				if (f != null) {
					match = new SimpleEntry<Integer, Integer>(f, i);
				}
			}
			i = this.next(i);
		}
		return match;
	}
	
	protected int init(int begin, int end) {
		return begin;
	}
	
	protected int next(int i) {
		return i + 1;
	}
	
	protected boolean carry(int i, int begin, int end) {
		return i < end;
	}
	
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		int i = 1;
		for (Node node : this.nodes) {
			node.setName(name(i));
			i += 1;
		}
		for (Node node : this.nodes) {
			sb.append(node);
		}
		return sb.toString();
	}
	
	public final int size() {
		return this.nodes.size();
	}
	
}
