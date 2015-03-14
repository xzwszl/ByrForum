package xzw.szl.byr.info;

public class Favorite {
	private int level;	//int	收藏夹级数，顶层收藏夹level为0	
	private String description;	//string	收藏夹目录	
	private int position;	//int	收藏夹目录位置，该值用于删除收藏夹目录
	
	
	public Favorite() {
		super();
	}
	public Favorite(int level, String description, int position) {
		super();
		this.level = level;
		this.description = description;
		this.position = position;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
}
