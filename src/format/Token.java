package format;


public class Token {

	private String normal;
	private String root;
	private String suffix;
	
	protected Token(String word) {
		this.normal = word;
		char seq[] = this.normal.toCharArray();
		StringBuilder sb = new StringBuilder(7);
		int i = 0, j = 0;
		while ((i < seq.length) && (j < 7)) {
			if (Character.isAlphabetic(seq[i])) {
				sb.append(Character.toLowerCase(seq[i]));
				j += 1;
			}
			i += 1;
		}
		this.root = sb.toString();
		sb = new StringBuilder();
		while (i < seq.length) {
			if (Character.isAlphabetic(seq[i])) {
				sb.append(Character.toLowerCase(seq[i]));
			}
			i += 1;
		}
		this.suffix = sb.toString();
	}
	
	protected void setRoot(String root) {
		this.root = root;
	}
	
	public String getRoot() {
		return this.root;
	}
	
	public String getSuffix() {
		return this.suffix;
	}
	
	public String toString() {
		return this.normal;
	}
	
}
