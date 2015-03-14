package xzw.szl.byr.info;

import java.util.ArrayList;

public class ByrRefer {

	private String description;
	private ArrayList<Refer> article;
	private Pagination pagination;
	
	public ByrRefer(String description, ArrayList<Refer> article,
			Pagination pagination) {
		super();
		this.description = description;
		this.article = article;
		this.pagination = pagination;
	}

	public ByrRefer() {
		super();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ArrayList<Refer> getArticle() {
		return article;
	}

	public void setArticle(ArrayList<Refer> article) {
		this.article = article;
	}

	public Pagination getPagination() {
		return pagination;
	}

	public void setPagination(Pagination pagination) {
		this.pagination = pagination;
	}
}
