package xzw.szl.byr.info;

import java.util.ArrayList;


public class Attachment{
	
	private  ArrayList<ByrFile> file;	//array	文件列表	
	private String remain_space;	//string	剩余空间大小	
	private int remain_count;	//int	剩余附件个数
	
	
	public Attachment() {
		super();
	}


	public Attachment(ArrayList<ByrFile> file, String remain_space,
			int remain_count) {
		super();
		this.file = file;
		this.remain_space = remain_space;
		this.remain_count = remain_count;
	}


	public ArrayList<ByrFile> getFile() {
		return file;
	}


	public void setFile(ArrayList<ByrFile> file) {
		this.file = file;
	}


	public String getRemain_space() {
		return remain_space;
	}


	public void setRemain_space(String remain_space) {
		this.remain_space = remain_space;
	}


	public int getRemain_count() {
		return remain_count;
	}


	public void setRemain_count(int remain_count) {
		this.remain_count = remain_count;
	}


	
	
}
