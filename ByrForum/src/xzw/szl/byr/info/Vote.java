package xzw.szl.byr.info;

import java.util.ArrayList;

public class Vote {
	private String  vid;
    private String title;
    private String start;
    private String end;
    private String user_count;
    private String type;
    private String limit;
    private String aid;
    private boolean is_end;
    private boolean is_deleted;
    private boolean is_result_voted;
    private User user;
    private ArrayList<Option> options;
    private int vote_count;
    private MyVote voted;
    
	public String getVid() {
		return vid;
	}
	public void setVid(String vid) {
		this.vid = vid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Vote(String vid, String title, String start, String end,
			String user_count, String type, String limit, String aid,
			boolean is_end, boolean is_deleted, boolean is_result_voted,
			User user, ArrayList<Option> options, int vote_count,
			MyVote voted) {
		super();
		this.vid = vid;
		this.title = title;
		this.start = start;
		this.end = end;
		this.user_count = user_count;
		this.type = type;
		this.limit = limit;
		this.aid = aid;
		this.is_end = is_end;
		this.is_deleted = is_deleted;
		this.is_result_voted = is_result_voted;
		this.user = user;
		this.options = options;
		this.vote_count = vote_count;
		this.voted = voted;
	}
	public MyVote getVoted() {
		return voted;
	}
	public void setVoted(MyVote voted) {
		this.voted = voted;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public String getUser_count() {
		return user_count;
	}
	public void setUser_count(String user_count) {
		this.user_count = user_count;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLimit() {
		return limit;
	}
	public void setLimit(String limit) {
		this.limit = limit;
	}
	public String getAid() {
		return aid;
	}
	public void setAid(String aid) {
		this.aid = aid;
	}
	public boolean isIs_end() {
		return is_end;
	}
	public void setIs_end(boolean is_end) {
		this.is_end = is_end;
	}
	public boolean isIs_deleted() {
		return is_deleted;
	}
	public void setIs_deleted(boolean is_deleted) {
		this.is_deleted = is_deleted;
	}
	public boolean isIs_result_voted() {
		return is_result_voted;
	}
	public void setIs_result_voted(boolean is_result_voted) {
		this.is_result_voted = is_result_voted;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public ArrayList<Option> getOptions() {
		return options;
	}
	public void setOptions(ArrayList<Option> options) {
		this.options = options;
	}
	public int getVote_count() {
		return vote_count;
	}
	public void setVote_count(int vote_count) {
		this.vote_count = vote_count;
	}

	public Vote() {
		super();
	} 
	
	public static class Option {
		private String viid;
		private String label;
		private String num;
		public String getViid() {
			return viid;
		}
		public void setViid(String viid) {
			this.viid = viid;
		}
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		public String getNum() {
			return num;
		}
		public void setNum(String num) {
			this.num = num;
		}
		public Option(String viid, String label, String num) {
			super();
			this.viid = viid;
			this.label = label;
			this.num = num;
		}
		public Option() {
			super();
		}	
	}
	
	public static class MyVote {
		private ArrayList<String> viid;
		private String time;
		public MyVote(ArrayList<String> viid, String time) {
			super();
			this.viid = viid;
			this.time = time;
		}
		public MyVote() {
			super();
		}
		public ArrayList<String> getViid() {
			return viid;
		}
		public void setViid(ArrayList<String> viid) {
			this.viid = viid;
		}
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
	}
}