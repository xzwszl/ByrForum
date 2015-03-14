package xzw.szl.byr.info;

public class Count {
	private boolean enable;
	private int new_count;
	
	
	public Count() {
		super();
	}
	public Count(boolean enable, int new_count) {
		super();
		this.enable = enable;
		this.new_count = new_count;
	}
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	public int getNew_count() {
		return new_count;
	}
	public void setNew_count(int new_count) {
		this.new_count = new_count;
	}
}
