package format;

public enum Tag {
	
	title(20),
	h1(18),
	h2(15),
	h3(10),
	h4(5),
	h5(5),
	h6(5),
	strong(5),
	cite(5),
	big(5),
	em(5),
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
