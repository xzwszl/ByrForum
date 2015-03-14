package xzw.szl.byr.info;

public class Widget {
	private String name;	//string	widget标识	
	private String title;	//string	widget标题	
	private int time;	//int	上次修改时间
	
	
	public Widget() {
		super();
	}
	public Widget(String name, String title, int time) {
		super();
		this.name = name;
		this.title = title;
		this.time = time;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	
	
	
}
