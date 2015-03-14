package xzw.szl.byr.info;

import java.util.ArrayList;

public class ByrMail {
	
	private String description;
	private ArrayList<Mail> mail;
	private Pagination pagination;
	
	public ByrMail() {
		super();
	}
	
	public ByrMail(String description, ArrayList<Mail> mail,
			Pagination pagination) {
		super();
		this.description = description;
		this.mail = mail;
		this.pagination = pagination;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public ArrayList<Mail> getMail() {
		return mail;
	}
	public void setMail(ArrayList<Mail> mail) {
		this.mail = mail;
	}
	public Pagination getPagination() {
		return pagination;
	}
	public void setPagination(Pagination pagination) {
		this.pagination = pagination;
	}
	
	
	
}
