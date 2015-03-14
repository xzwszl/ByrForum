package xzw.szl.byr.info;

import java.util.ArrayList;

public class MyFavorite {
	
	private ArrayList<MyFavorite> sub_favorite;
	private ArrayList<Section> section;
	private ArrayList<Board> board;
	
	
	public MyFavorite() {
		super();
	}
	
	public MyFavorite(ArrayList<MyFavorite> sub_favorite,
			ArrayList<Section> section, ArrayList<Board> board) {
		super();
		this.sub_favorite = sub_favorite;
		this.section = section;
		this.board = board;
	}
	public ArrayList<MyFavorite> getSub_favorite() {
		return sub_favorite;
	}
	public void setSub_favorite(ArrayList<MyFavorite> sub_favorite) {
		this.sub_favorite = sub_favorite;
	}
	public ArrayList<Section> getSection() {
		return section;
	}
	public void setSection(ArrayList<Section> section) {
		this.section = section;
	}
	public ArrayList<Board> getBoard() {
		return board;
	}
	public void setBoard(ArrayList<Board> board) {
		this.board = board;
	}
	
	
}
