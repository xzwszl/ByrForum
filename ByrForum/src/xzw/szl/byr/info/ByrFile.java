package xzw.szl.byr.info;

import android.os.Parcel;
import android.os.Parcelable;

public class ByrFile implements Parcelable{
	private String name;	//string	文件名	
	private String url;	//string	文件链接，在用户空间的文件，该值为空	
	private String size;	//string	文件大小	
	private String thumbnail_small;	//string	小缩略图链接(宽度120px)，用户空间的文件，该值为空	附件为图片格式(jpg,png,gif)
	private String thumbnail_middle;	//string	中缩略图链接(宽度240px)，用户空间的文件，该值为空	附件为图片格式(jpg,png,gif)
	
	public ByrFile() {
		super();
	}
	public ByrFile(String name, String url, String size,
			String thumbnail_small, String thumbnail_middle) {
		
		this.name = name;
		this.url = url;
		this.size = size;
		this.thumbnail_small = thumbnail_small;
		this.thumbnail_middle = thumbnail_middle;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getThumbnail_small() {
		return thumbnail_small;
	}
	public void setThumbnail_small(String thumbnail_small) {
		this.thumbnail_small = thumbnail_small;
	}
	public String getThumbnail_middle() {
		return thumbnail_middle;
	}
	public void setThumbnail_middle(String thumbnail_middle) {
		this.thumbnail_middle = thumbnail_middle;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeString(name);
		dest.writeString(url);
		dest.writeString(size);
		dest.writeString(thumbnail_small);
		dest.writeString(thumbnail_middle);
	}
	
	public static final Parcelable.Creator<ByrFile> CREATOR  = new Creator<ByrFile>() {
		
		@Override
		public ByrFile[] newArray(int size) {
			return new ByrFile[size];
		}
		
		@Override
		public ByrFile createFromParcel(Parcel source) {

			ByrFile file = new ByrFile();
			
			file.name = source.readString();
			file.url = source.readString();
			file.size = source.readString();
			file.thumbnail_small = source.readString();
			file.thumbnail_middle = source.readString();
			
			return file;
		}
	}; 
	
	
}
