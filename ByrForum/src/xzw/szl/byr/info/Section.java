package xzw.szl.byr.info;

import java.util.ArrayList;

public class Section {
	
	private String name;	//string	分区名称	
	private String description;	//string	分区表述	
	private boolean is_root;	//boolean	是否是根分区	
	private String parent;	//string	该分区所属根分区名称
	private ArrayList<String> sub_section;
	private ArrayList<Board> board;
	
	
	public Section() {
		super();
	}
	
	public Section(String name, String description, boolean is_root,
			String parent, ArrayList<String> sub_section, ArrayList<Board> board) {
		super();
		this.name = name;
		this.description = description;
		this.is_root = is_root;
		this.parent = parent;
		this.sub_section = sub_section;
		this.board = board;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public boolean isIs_root() {
		return is_root;
	}


	public void setIs_root(boolean is_root) {
		this.is_root = is_root;
	}


	public String getParent() {
		return parent;
	}


	public void setParent(String parent) {
		this.parent = parent;
	}

	public ArrayList<String> getSub_section() {
		return sub_section;
	}
	
	public void setSub_section(ArrayList<String> sub_section) {
		this.sub_section = sub_section;
	}

	public ArrayList<Board> getBoard() {
		return board;
	}
	
	public void setBoard(ArrayList<Board> board) {
		this.board = board;
	}
}
