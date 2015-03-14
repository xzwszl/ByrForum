package xzw.szl.byr.info;

import java.util.ArrayList;

public class Board {
	private String name;//	string	版面名称	
	private String manager;	//string	版主列表，以空格分隔各个id	
	private String description;	//string	版面描述	
	private String classes;	// class string	版面所属类别	
	private String section;	//string	版面所属分区号	
	private int post_today_count;	//int	今日发文总数	
	private int post_threads_count;	//int	版面主题总数	
	private int post_all_count;	//int	版面文章总数	
	private boolean is_read_only;	//boolean	版面是否只读	
	private boolean is_no_reply;	//boolean	版面是否不可回复	
	private boolean allow_attachment;	//boolean	版面书否允许附件	
	private boolean allow_anonymous;	//boolean	版面是否允许匿名发文	
	private boolean allow_outgo;	//boolean	版面是否允许转信	
	private boolean allow_post;	//boolean	当前登陆用户是否有发文/回复权限	
	private int user_online_count;	//int	版面当前在线用户数
	private Pagination pagination;
	private ArrayList<Article> article;
	
	public Board() {
		super();
	}


	
	public Board(String name, String manager, String description,
			String classes, String section, int post_today_count,
			int post_threads_count, int post_all_count, boolean is_read_only,
			boolean is_no_reply, boolean allow_attachment,
			boolean allow_anonymous, boolean allow_outgo, boolean allow_post,
			int user_online_count, Pagination pagination,
			ArrayList<Article> article) {
		super();
		this.name = name;
		this.manager = manager;
		this.description = description;
		this.classes = classes;
		this.section = section;
		this.post_today_count = post_today_count;
		this.post_threads_count = post_threads_count;
		this.post_all_count = post_all_count;
		this.is_read_only = is_read_only;
		this.is_no_reply = is_no_reply;
		this.allow_attachment = allow_attachment;
		this.allow_anonymous = allow_anonymous;
		this.allow_outgo = allow_outgo;
		this.allow_post = allow_post;
		this.user_online_count = user_online_count;
		this.pagination = pagination;
		this.article = article;
	}



	public ArrayList<Article> getArticle() {
		return article;
	}

	public void setArticle(ArrayList<Article> article) {
		this.article = article;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}


	public String getManager() {
		return manager;
	}


	public void setManager(String manager) {
		this.manager = manager;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getClasses() {
		return classes;
	}


	public void setClasses(String classes) {
		this.classes = classes;
	}


	public String getSection() {
		return section;
	}


	public void setSection(String section) {
		this.section = section;
	}


	public int getPost_today_count() {
		return post_today_count;
	}


	public void setPost_today_count(int post_today_count) {
		this.post_today_count = post_today_count;
	}


	public int getPost_threads_count() {
		return post_threads_count;
	}


	public void setPost_threads_count(int post_threads_count) {
		this.post_threads_count = post_threads_count;
	}


	public int getPost_all_count() {
		return post_all_count;
	}


	public void setPost_all_count(int post_all_count) {
		this.post_all_count = post_all_count;
	}


	public boolean isIs_read_only() {
		return is_read_only;
	}


	public void setIs_read_only(boolean is_read_only) {
		this.is_read_only = is_read_only;
	}


	public boolean isIs_no_reply() {
		return is_no_reply;
	}


	public void setIs_no_reply(boolean is_no_reply) {
		this.is_no_reply = is_no_reply;
	}


	public boolean isAllow_attachment() {
		return allow_attachment;
	}


	public void setAllow_attachment(boolean allow_attachment) {
		this.allow_attachment = allow_attachment;
	}


	public boolean isAllow_anonymous() {
		return allow_anonymous;
	}


	public void setAllow_anonymous(boolean allow_anonymous) {
		this.allow_anonymous = allow_anonymous;
	}


	public boolean isAllow_outgo() {
		return allow_outgo;
	}


	public void setAllow_outgo(boolean allow_outgo) {
		this.allow_outgo = allow_outgo;
	}


	public boolean isAllow_post() {
		return allow_post;
	}


	public void setAllow_post(boolean allow_post) {
		this.allow_post = allow_post;
	}


	public int getUser_online_count() {
		return user_online_count;
	}


	public void setUser_online_count(int user_online_count) {
		this.user_online_count = user_online_count;
	}



	public Pagination getPagination() {
		return pagination;
	}



	public void setPagination(Pagination pagination) {
		this.pagination = pagination;
	}
	
	
		
}
