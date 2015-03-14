package xzw.szl.byr.info;

public class VoteDetail {
	
	private Vote vote;

	public Vote getVote() {
		return vote;
	}

	public void setVote(Vote vote) {
		this.vote = vote;
	}

	public VoteDetail(Vote vote) {
		super();
		this.vote = vote;
	}

	public VoteDetail() {
		super();
	}
}
