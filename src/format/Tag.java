package format;

public enum Tag {
	
	title(8),
	h1(6),
	h2(5),
	h3(4),
	h4(3),
	h5(3),
	h6(3),
	strong(3),
	cite(3),
	big(3),
	em(2),
	none(1);
	
	public final int weight;
	
	private Tag(int weight) {
		this.weight = weight;
	}
	
	public static Tag from(String name) {
		try {
			return Tag.valueOf(Tag.class, name.toLowerCase());
		} catch (IllegalArgumentException e) {
			return Tag.none;
		}
	}
	
	public static Tag from(int id) {
		return Tag.values()[id];
	}

}
