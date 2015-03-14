package xzw.szl.byr.info;


/*
 * @author shilei 12/27/2013
 * */
public class User {
	private String id;	//用户ID	
	
	private String user_name; // 用户昵称
	
	private String face_url;		//用户头像地址
	
	private int face_width;		//用户头像宽度
	
	private int face_height;	  //用户头像高度
	
	private String gender;		//用户性别：m表示男，f表示女，n表示隐藏性别
	
	private String astro;		//用户星座，若隐藏则星座为空
	
	private int life; 		//用户生命值
	
	private String qq; 		//用户QQ
	
	private String msn;	 	//	用户msn
	
	private String home_page;	//用户个人主页
	
	private String level;		//用户身份
	
	private boolean is_online;  		//用户是否在线
	
	private int post_count; 		//用户发文数量
	
	private int last_login_time;	//用户上次登录时间  unixtimestamp
	
	private String last_login_ip;		//用户上次登录IP
	
	private boolean is_hide;		//用户是否隐藏性别和星座
	
	private boolean is_register;	//用户是否通过注册审批
	
	private int first_login_time;	//用户注册时间，unixtimestamp，当前登陆用户为 自己或是当前用户具有管理权限
	
	private int login_count;		//用户登录次数，当前登陆用户为 自己或是当前用户具有管理权限
	
	private boolean is_admin;		//用户是否为管理员，当前登陆用户为 自己或是当前用户具有管理权限
	
	private int stay_count;		//用户挂站时间，以秒为单位，当前登陆用户为 自己或是当前用户具有管理权限

	
	
	public User() {
		
	}
	public User(String id, String user_name, String face_url,
			int face_width, int face_height, String gender, String astro,
			int life, String qq, String msn, String home_page, String level,
			boolean is_online, int post_count, int last_login_time,
			String last_login_ip, boolean is_hide, boolean is_register,
			int first_login_time, int login_count, boolean is_admin,
			int stay_count) {
		super();
		this.id = id;
		this.user_name = user_name;
		this.face_url = face_url;
		this.face_width = face_width;
		this.face_height = face_height;
		this.gender = gender;
		this.astro = astro;
		this.life = life;
		this.qq = qq;
		this.msn = msn;
		this.home_page = home_page;
		this.level = level;
		this.is_online = is_online;
		this.post_count = post_count;
		this.last_login_time = last_login_time;
		this.last_login_ip = last_login_ip;
		this.is_hide = is_hide;
		this.is_register = is_register;
		this.first_login_time = first_login_time;
		this.login_count = login_count;
		this.is_admin = is_admin;
		this.stay_count = stay_count;
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getFace_url() {
		return face_url;
	}

	public void setFace_url(String face_url) {
		this.face_url = face_url;
	}

	public int getFace_width() {
		return face_width;
	}

	public void setFace_width(int face_width) {
		this.face_width = face_width;
	}

	public int getFace_height() {
		return face_height;
	}

	public void setFace_height(int face_height) {
		this.face_height = face_height;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAstro() {
		return astro;
	}

	public void setAstro(String astro) {
		this.astro = astro;
	}

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getMsn() {
		return msn;
	}

	public void setMsn(String msn) {
		this.msn = msn;
	}

	public String getHome_page() {
		return home_page;
	}

	public void setHome_page(String home_page) {
		this.home_page = home_page;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public boolean isIs_online() {
		return is_online;
	}

	public void setIs_online(boolean is_online) {
		this.is_online = is_online;
	}

	public int getPost_count() {
		return post_count;
	}

	public void setPost_count(int post_count) {
		this.post_count = post_count;
	}

	public int getLast_login_time() {
		return last_login_time;
	}

	public void setLast_login_time(int last_login_time) {
		this.last_login_time = last_login_time;
	}

	public String getLast_login_ip() {
		return last_login_ip;
	}

	public void setLast_login_ip(String last_login_ip) {
		this.last_login_ip = last_login_ip;
	}

	public boolean isIs_hide() {
		return is_hide;
	}

	public void setIs_hide(boolean is_hide) {
		this.is_hide = is_hide;
	}

	public boolean isIs_register() {
		return is_register;
	}

	public void setIs_register(boolean is_register) {
		this.is_register = is_register;
	}

	public int getFirst_login_time() {
		return first_login_time;
	}

	public void setFirst_login_time(int first_login_time) {
		this.first_login_time = first_login_time;
	}

	public int getLogin_count() {
		return login_count;
	}

	public void setLogin_count(int login_count) {
		this.login_count = login_count;
	}

	public boolean isIs_admin() {
		return is_admin;
	}

	public void setIs_admin(boolean is_admin) {
		this.is_admin = is_admin;
	}

	public int getStay_count() {
		return stay_count;
	}

	public void setStay_count(int stay_count) {
		this.stay_count = stay_count;
	}
	
	public String toString() {
		return "id:" + id + "\n" + 
				"user_name:" + user_name + "\n" + 
				"face_url:" + face_url + "\n" + 
				"face_width:" +face_width   + "\n" + 
				"face_height:" +face_height   + "\n" + 
				"gender:" + gender  + "\n" + 
				"astro:" +  astro + "\n" + 
				"life:" + life  + "\n" + 
				"qq:" + qq  + "\n" + 
				"msn:" + msn  + "\n" + 
				"home_page:" + home_page   + "\n" + 
				"level:" + level   + "\n" + 
				"is_online:" + is_online   + "\n" + 
				"post_count:" + post_count  + "\n" + 
				"last_lgoin_time:" + last_login_time   + "\n" + 
				"last_login_ip:" + last_login_ip  + "\n" + 
				"is_hide:" + is_hide  + "\n" + 
				"is_register:" +  is_register  + "\n" + 
				"first_lgoin_time:" + first_login_time  + "\n" + 
				"login_count:" + login_count  + "\n" + 
				"is_admin:" + is_admin  + "\n" + 
				"stay_count:" + stay_count;			
	}
	
}
