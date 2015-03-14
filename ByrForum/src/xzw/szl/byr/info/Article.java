package xzw.szl.byr.info;

public class Article {

	private int id;	//int	文章id	
	private int group_id;	//int	该文章所属主题的id
	private int reply_id;	//int	该文章回复文章的id	
	private String flag;	//string	文章标记 分别是m g ; b u o 8
	//两处json不一致，所以改成object。
	//private int position;	//int	文章所在主题的位置或文章在默写浏览模式下的位置	/board/:name的非主题模式下为访问此文章的id，在/threads/:board/:id中为所在主题中的位置，其余为空
	private Object position;
	private boolean is_top;	//boolean	文章是否置顶	
	private boolean is_subject;	//boolean	该文章是否是主题帖
	private boolean has_attachment;	//boolean	文章是否有附件
	private boolean is_admin;	//boolean	当前登陆用户是否对文章有管理权限 包括编辑，删除，修改附件
	private String title;	//string	文章标题	
	private User user;	//user	文章发表用户，这是一个用户元数据	
	private int post_time;	//int	文章发表时间，unixtimestamp	
	private String board_name;	//string	所属版面名称	
	private String content;	//string	文章内容	在/board/:name的文章列表和/search/(article|threads)中不存在此属性
	private Attachment attachment;	//attachment	文章附件列表，这是一个附件元数据	在/board/:name的文章列表和/search/(article|threads)中不存在此属性
	private int previous_id;	//int	该文章的前一篇文章id	只存在于/article/:board/:id中
	private int next_id;	//int	该文章的后一篇文章id	只存在于/article/:board/:id中
	private int threads_previous_id;	//int	该文章同主题前一篇文章id	只存在于/article/:board/:id中
	private int threads_next_id;	//int	该文章同主题后一篇文章id	只存在于/article/:board/:id中
	private int reply_count;	//int	该主题回复文章数	只存在于/board/:name，/threads/:board/:id和/search/threads中
	private String last_reply_user_id;	//string	该文章最后回复者的id	只存在于/board/:name，/threads/:board/:id和/search/threads中
	private int last_reply_time;	//int	该文章最后回复的时间 unxitmestamp	只存在于/board/:name，/threads/:board/:id和/search/threads中
	
	
	
	public Article() {
		super();
	}
	public Article(int id, int group_id, int reply_id, String flag,
			Object position, boolean is_top, boolean is_subject,
			boolean has_attachment, boolean is_admin, String title, User user,
			int post_time, String board_name, String content,
			Attachment attachment, int previous_id, int next_id,
			int threads_previous_id, int threads_next_id, int reply_count,
			String last_reply_user_id, int last_reply_time) {
		
		this.id = id;
		this.group_id = group_id;
		this.reply_id = reply_id;
		this.flag = flag;
		this.position = position;
		this.is_top = is_top;
		this.is_subject = is_subject;
		this.has_attachment = has_attachment;
		this.is_admin = is_admin;
		this.title = title;
		this.user = user;
		this.post_time = post_time;
		this.board_name = board_name;
		this.content = content;
		this.attachment = attachment;
		this.previous_id = previous_id;
		this.next_id = next_id;
		this.threads_previous_id = threads_previous_id;
		this.threads_next_id = threads_next_id;
		this.reply_count = reply_count;
		this.last_reply_user_id = last_reply_user_id;
		this.last_reply_time = last_reply_time;
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
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public Object getPosition() {
		return position;
	}
	public void setPosition(Object position) {
		this.position = position;
	}
	public boolean isIs_top() {
		return is_top;
	}
	public void setIs_top(boolean is_top) {
		this.is_top = is_top;
	}
	public boolean isIs_subject() {
		return is_subject;
	}
	public void setIs_subject(boolean is_subject) {
		this.is_subject = is_subject;
	}
	public boolean isHas_attachment() {
		return has_attachment;
	}
	public void setHas_attachment(boolean has_attachment) {
		this.has_attachment = has_attachment;
	}
	public boolean isIs_admin() {
		return is_admin;
	}
	public void setIs_admin(boolean is_admin) {
		this.is_admin = is_admin;
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
	public String getBoard_name() {
		return board_name;
	}
	public void setBoard_name(String board_name) {
		this.board_name = board_name;
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
	public int getPrevious_id() {
		return previous_id;
	}
	public void setPrevious_id(int previous_id) {
		this.previous_id = previous_id;
	}
	public int getNext_id() {
		return next_id;
	}
	public void setNext_id(int next_id) {
		this.next_id = next_id;
	}
	public int getThreads_previous_id() {
		return threads_previous_id;
	}
	public void setThreads_previous_id(int threads_previous_id) {
		this.threads_previous_id = threads_previous_id;
	}
	public int getThreads_next_id() {
		return threads_next_id;
	}
	public void setThreads_next_id(int threads_next_id) {
		this.threads_next_id = threads_next_id;
	}
	public int getReply_count() {
		return reply_count;
	}
	public void setReply_count(int reply_count) {
		this.reply_count = reply_count;
	}
	public String getLast_reply_user_id() {
		return last_reply_user_id;
	}
	public void setLast_reply_user_id(String last_reply_user_id) {
		this.last_reply_user_id = last_reply_user_id;
	}
	public int getLast_reply_time() {
		return last_reply_time;
	}
	public void setLast_reply_time(int last_reply_time) {
		this.last_reply_time = last_reply_time;
	}
}
