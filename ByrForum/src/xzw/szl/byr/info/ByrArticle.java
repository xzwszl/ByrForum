package xzw.szl.byr.info;

import java.util.ArrayList;

public class ByrArticle {
	
	private String description;
	private ArrayList<Article> article;
	private Pagination pagination;
	
	public ByrArticle() {
		super();
	}
	
	public ByrArticle(String description, ArrayList<Article> article,
			Pagination pagination) {
		super();
		this.description = description;
		this.article = article;
		this.pagination = pagination;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public ArrayList<Article> getArticle() {
		return article;
	}
	public void setArticle(ArrayList<Article> article) {
		this.article = article;
	}
	public Pagination getPagination() {
		return pagination;
	}
	public void setPagination(Pagination pagination) {
		this.pagination = pagination;
	}
	
	class MyArticle {
		
	}
}
