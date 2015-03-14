package xzw.szl.byr.info;

import java.util.ArrayList;

public class ByrVote {
	private Pagination pagination;
	private ArrayList<Vote> votes;
	
	
	
	
	public ByrVote() {
		super();
	}
	public ByrVote(Pagination pagination, ArrayList<Vote> votes) {
		super();
		this.pagination = pagination;
		this.votes = votes;
	}
	public Pagination getPagination() {
		return pagination;
	}
	public void setPagination(Pagination pagination) {
		this.pagination = pagination;
	}
	public ArrayList<Vote> getVotes() {
		return votes;
	}
	public void setVotes(ArrayList<Vote> votes) {
		this.votes = votes;
	}
}

