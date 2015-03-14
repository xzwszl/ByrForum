package xzw.szl.byr.info;

import java.util.ArrayList;

public class TopTen{
	private String name;
	private String title;
	private int time;
	
	private ArrayList<Article> article;
	
	public TopTen() {
		super();
	}

	

	public TopTen(String name, String title, int time,
			ArrayList<Article> article) {
		super();
		this.name = name;
		this.title = title;
		this.time = time;
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



	public String getTitle() {
		return title;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public int getTime() {
		return time;
	}



	public void setTime(int time) {
		this.time = time;
	}

	
}