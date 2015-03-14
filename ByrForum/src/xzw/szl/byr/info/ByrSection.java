package xzw.szl.byr.info;

import java.util.ArrayList;

public class ByrSection {
	private int section_count;
	private ArrayList<Section> section;
	
	
	
	public ByrSection() {
		super();
	}
	
	public ByrSection(int section_count, ArrayList<Section> section) {
		super();
		this.section_count = section_count;
		this.section = section;
	}
	public int getSection_count() {
		return section_count;
	}
	public void setSection_count(int section_count) {
		this.section_count = section_count;
	}
	public ArrayList<Section> getSection() {
		return section;
	}
	public void setSection(ArrayList<Section> section) {
		this.section = section;
	}
}
