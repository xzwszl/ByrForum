package xzw.szl.byr.info;

public class Mailbox {
	private boolean new_mail;	//boolean	是否有新邮件	
	private boolean full_mail;	//boolean	信箱是否已满	
	private String space_used;	//string	信箱已用空间	
	private boolean can_send;	//boolean	当前用户是否能发信
	
	
	
	public Mailbox() {
		super();
	}
	public Mailbox(boolean new_mail, boolean full_mail, String space_used,
			boolean can_send) {
		super();
		this.new_mail = new_mail;
		this.full_mail = full_mail;
		this.space_used = space_used;
		this.can_send = can_send;
	}
	public boolean isNew_mail() {
		return new_mail;
	}
	public void setNew_mail(boolean new_mail) {
		this.new_mail = new_mail;
	}
	public boolean isFull_mail() {
		return full_mail;
	}
	public void setFull_mail(boolean full_mail) {
		this.full_mail = full_mail;
	}
	public String getSpace_used() {
		return space_used;
	}
	public void setSpace_used(String space_used) {
		this.space_used = space_used;
	}
	public boolean isCan_send() {
		return can_send;
	}
	public void setCan_send(boolean can_send) {
		this.can_send = can_send;
	}
	
	
}
