package xzw.szl.byr.info;

public class Pagination {
	private int page_all_count;	//int	总页数	
	private int page_current_count;	//int	当前页数	
	private int item_page_count;	//int	每页元素个数	
	private String item_all_count;	//int	所有元素个数
	
	
	public Pagination() {
		super();
	}
	public Pagination(int page_all_count, int page_current_count,
			int item_page_count, String item_all_count) {
		super();
		this.page_all_count = page_all_count;
		this.page_current_count = page_current_count;
		this.item_page_count = item_page_count;
		this.item_all_count = item_all_count;
	}
	public int getPage_all_count() {
		return page_all_count;
	}
	public void setPage_all_count(int page_all_count) {
		this.page_all_count = page_all_count;
	}
	public int getPage_current_count() {
		return page_current_count;
	}
	public void setPage_current_count(int page_current_count) {
		this.page_current_count = page_current_count;
	}
	public int getItem_page_count() {
		return item_page_count;
	}
	public void setItem_page_count(int item_page_count) {
		this.item_page_count = item_page_count;
	}
	public String getItem_all_count() {
		return item_all_count;
	}
	public void setItem_all_count(String item_all_count) {
		this.item_all_count = item_all_count;
	}
	
	
}
