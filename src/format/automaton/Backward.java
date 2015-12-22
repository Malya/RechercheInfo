package format.automaton;


public class Backward extends Automaton {

	public Backward(String suffix[][]) {
		super(suffix);
	}
	
	@Override
	protected Node from(String suffix, int type) {
		return super.from(new StringBuilder(suffix).reverse().toString(), type);
	}
	
	@Override
	protected int init(int begin, int end) {
		return end - 1;
	}
	
	@Override
	protected int next(int i) {
		return i - 1;
	}
	
	@Override
	protected boolean carry(int i, int begin, int end) {
		return i >= begin;
	}
	
}
