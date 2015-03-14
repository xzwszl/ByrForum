package xzw.szl.byr.info;

public class Refer {
	private int index;	//int	提醒编号，此编号用于提醒的相关操作	
	private int id;	//int	提醒文章的id	
	private int group_id;	//int	提醒文章的group id	
	private int reply_id;	//int	提醒文章的reply id	
	private String board_name;	//string	提醒文章所在版面	
	private String title;	//string	提醒文章的标题	
	private User user;	//user|string	提醒文章的发信人，此为user元数据，如果user不存在则为用户id	
	private int time;	//int	发出提醒的时间	
	private boolean is_read;	//boolean	提醒是否已读

	public Refer() {
		super();
	}
	public Refer(int index, int id, int group_id, int reply_id,
			String board_name, String title, User user, int time,
			boolean is_read) {
		super();
		this.index = index;
		this.id = id;
		this.group_id = group_id;
		this.reply_id = reply_id;
		this.board_name = board_name;
		this.title = title;
		this.user = user;
		this.time = time;
		this.is_read = is_read;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getGroup_id() {
		return group_id;
	}
	public void setGroup_id(int group_id) {
		this.group_id = group_id;
	}
	public int getReply_id() {
		return reply_id;
	}
	public void setReply_id(int reply_id) {
		this.reply_id = reply_id;
	}
	public String getBoard_name() {
		return board_name;
	}
	public void setBoard_name(String board_name) {
		this.board_name = board_name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public boolean isIs_read() {
		return is_read;
	}
	public void setIs_read(boolean is_read) {
		this.is_read = is_read;
	}
	
	
}
