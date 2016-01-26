package format;

public interface Token {

	public String getRoot();
	
	public String toString();
	
	public Tag getTag();
	
	public int getPos();
	
	public Double getWeight();
	
}
