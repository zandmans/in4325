package nl.tudelft.in4325.index;

public class Document {

	private String _title;
	private int _count = 0;
	
	public Document(String title)
	{
		setTitle(title);
	}
	
	
	public String getTitle()
	{
		return _title;
	}
	
	
	public void setTitle(String title)
	{
		_title = title;
	}
	
	
	public void incCounter()
	{
		_count++;
	}
	
}
