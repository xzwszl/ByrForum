package xzw.szl.byr.info;

import java.util.ArrayList;

public class Threads {
	//有用的就是这2个属性
	private Pagination pagination;
	private ArrayList<Article> article;
	
	
	public Threads() {
		super();
	}


	public Threads(Pagination pagination, ArrayList<Article> article) {
		super();
		this.pagination = pagination;
		this.article = article;
	}


	public Pagination getPagination() {
		return pagination;
	}


	public void setPagination(Pagination pagination) {
		this.pagination = pagination;
	}


	public ArrayList<Article> getArticle() {
		return article;
	}


	public void setArticle(ArrayList<Article> article) {
		this.article = article;
	}
}
