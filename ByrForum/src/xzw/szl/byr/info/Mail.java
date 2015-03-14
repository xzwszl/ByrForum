package xzw.szl.byr.info;

public class Mail {
	private int index;	//int	信件编号，此编号为/mail/:box/:num中的num	
	private boolean is_m;	//boolean	是否标记为m	
	private boolean is_read;	//boolean	是否已读	
	private boolean is_reply;	//boolean	是否回复	
	private boolean has_attachment;	//boolean	是否有附件	
	private String title;	//string	信件标题	
	private User user;	//user|string	发信人，此为user元数据，如果user不存在则为用户id	
	private int post_time;	//int	发信时间	
	private String box_name;	//string	所属信箱名	
	private String content;	//string	信件内容	只存在于/mail/:box/:num中
	private Attachment attachment;	//attachment	信件的附件列表	只存在于/mail/:box/:num中
	
	
	public Mail() {
		super();
	}
	public Mail(int index, boolean is_m, boolean is_read, boolean is_reply,
			boolean has_attachment, String title, User user, int post_time,
			String box_name, String content, Attachment attachment) {
		super();
		this.index = index;
		this.is_m = is_m;
		this.is_read = is_read;
		this.is_reply = is_reply;
		this.has_attachment = has_attachment;
		this.title = title;
		this.user = user;
		this.post_time = post_time;
		this.box_name = box_name;
		this.content = content;
		this.attachment = attachment;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public boolean isIs_m() {
		return is_m;
	}
	public void setIs_m(boolean is_m) {
		this.is_m = is_m;
	}
	public boolean isIs_read() {
		return is_read;
	}
	public void setIs_read(boolean is_read) {
		this.is_read = is_read;
	}
	public boolean isIs_reply() {
		return is_reply;
	}
	public void setIs_reply(boolean is_reply) {
		this.is_reply = is_reply;
	}
	public boolean isHas_attachment() {
		return has_attachment;
	}
	public void setHas_attachment(boolean has_attachment) {
		this.has_attachment = has_attachment;
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
	public int getPost_time() {
		return post_time;
	}
	public void setPost_time(int post_time) {
		this.post_time = post_time;
	}
	public String getBox_name() {
		return box_name;
	}
	public void setBox_name(String box_name) {
		this.box_name = box_name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Attachment getAttachment() {
		return attachment;
	}
	public void setAttachment(Attachment attachment) {
		this.attachment = attachment;
	}
}
