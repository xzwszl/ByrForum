package xzw.szl.byr.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Stack;

import xzw.szl.byr.info.Attachment;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class DataUtils {
	
	private static int minute = 60;
	private static int hour = 3600;
	private static int day = 86400;
	private static int month = 2592000;
	private static int year = 31536000;
	private static SimpleDateFormat  simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS",Locale.CHINA);
	private static Date date = new Date();
	private static DisplayMetrics metrics;

	public static String getBase64(String str) {
		return Base64.encodeToString(str.getBytes(), Base64.DEFAULT);	
	}
	
	public static String getDateString(int time) {
		
		date.setTime((long)time * 1000);
		return simpleDateFormat.format(date);
	}
	
	public static String getRelativeDateString(int time) {
		
		
		long ctime = System.currentTimeMillis() / 1000 - time;
		
		if (ctime >= year) {
			return ctime /year + "年前";
		} else if (ctime >= month){
			return ctime/month + "月前";
		} else if (ctime >= day) {
			return ctime/day + "天前";
		} else if (ctime >= hour) {
			return ctime/hour + "小时前";
		} else if (ctime >= minute) {
			return ctime/ minute + "分钟前";
		} else {
			return "刚刚";
		}
	}
	
	/**
	 * 解析[],[upload]1[/upload],[size][/size],[face][/face],[color][/color],[e开头的动态图片]
	 * 判断【】,后面的:开头的置为蓝色 
	 * 判断\n 替换成<br> 
	 * @param str 待解析的字符串
	 * 		  att 如果没有，则传递空的Attachment
	 * @return
	 */
	
	//
	public static String getHtmlFromString(String str,Attachment att) {
		
		if (TextUtils.isEmpty(str)) return "";
		int attSize = att==null ?0:att.getFile().size();
		boolean [] usedArray = null;
		if (attSize > 0) {
			usedArray= new boolean[attSize+1];
			for (int i=0;i<=attSize;i++) usedArray[i] = false;
		}
		
		Stack<Item> stack = new Stack<Item>(); //同一行的标签匹配
		
		boolean isUploadValid = true;
		//html 空格等的初步处理
		//【 在 zishi 的大作中提到: 】
		for (int j=0; j<str.length(); j++) {
			
			if (str.charAt(j) == '【') {
				if ((j==0 || str.charAt(j-1) == '\n') &&
						j<(str.length()-14) && str.charAt(j+1)==' ' &&
						str.charAt(j+2) == '在') {
					int i=j+3;
					while (i<str.length() && str.charAt(i) !='\n') {
						i++;
					}
					if (i-10 < 0 || i >= str.length()) continue;
					
					String temp = str.substring(i-10,i);
					if (temp.equals(" 的大作中提到: 】")) {
						str = str.substring(0,j) + "<font color=\"#7A3804\">" + 
								str.substring(j,i) + "</font>" + str.substring(i);
						j += 29 + i - j -1;
					}
				}
			} else if (str.charAt(j) == ':') {
				if ((j == 0 || str.charAt(j-1) == '\n') &&
						j<str.length() -2 && str.charAt(j+1) == ' ') {
					int i= j+2;
					while (i<str.length() && str.charAt(i) !='\n') {
						i++;
					}
					
					if (i >= str.length()) continue;
					
					str = str.substring(0,j) + "<font color=\"#087A03\">" +
							str.substring(j,i) + "</font>" + str.substring(i);
					j = i+28;
					
					isUploadValid = false;
				}
			} else if (str.charAt(j) == '\n'){
				isUploadValid = true;
			} else if (str.charAt(j) == ' ') {
				String temp = str.substring(0, j) + "&nbsp;";
				if (j+1 <str.length()) {
					temp += str.substring(j+1);
				}
				str = temp;
				j+=5;
			}else if (str.charAt(j) == '[') {
				int low=j;
				StringBuilder  sb = new StringBuilder();
				j++;
				while (j<str.length() && str.charAt(j) != ']') {
					
					if (str.charAt(j) == '[' || str.charAt(j) == '\n') break;
					sb.append(str.charAt(j));
					j++;
				}
				if (j >= str.length()) continue;
				
				if (str.charAt(j) =='[' || str.charAt(j) == '\n') {
					j--;continue;
				}
				
				//判断sb的值，
				String tab = sb.toString();
				if (tab.startsWith("upload=") && isUploadValid && attSize > 0) {
					
					stack.add(new Item(tab.substring(0, 6), tab.substring(7), low,j,false));
					
				} else if (tab.startsWith("img=")) {
					String url = tab.substring(4);
					if (url.startsWith("http")) {
						stack.add(new Item("img", tab.substring(4), low, j, false));
					}
				}else if (tab.startsWith("face=") && tab.length() > 5) {           //face,size,color不能同名嵌套
					
					String value = tab.substring(5);   
//						if (false) continue;                         //判断value的有效性
					
					if (isValid(stack, "face")) {
						stack.add(new Item("face",value ,low,j, false));
					}
					
				} else if (tab.startsWith("color=") && tab.length() > 6) {
					
					String value = tab.substring(6);
//						if (false) continue;                         //判断value的有效性
					if (isValid(stack, "color"))
						stack.add(new Item("color",value ,low,j, false));
					
				} else if (tab.startsWith("size=") && tab.length() > 5) {
					
				    String value = tab.substring(5);
//					    if (false) continue;                         //判断value的有效性
				    if (isValid(stack,"size"))
				    	stack.add(new Item("size",value ,low,j, false));
					
				} else if (tab.startsWith("em") && isIconValid(tab)) {                   //动画小图片
					stack.add(new Item("em",tab,low,j,true));
					
				} else if ("u".equals(tab.toLowerCase(Locale.US)) || "b".equals(tab.toLowerCase(Locale.US))
						|| "i".equals(tab.toLowerCase(Locale.US))) {
					
					if (isValid(stack,tab.toLowerCase(Locale.US))) {
						stack.add(new Item(tab.toLowerCase(Locale.US),null,low,j,false));
					}
					
				} else if (tab.startsWith("url=")) {
					
					if (isValid(stack,"url"))
						stack.add(new Item("url",tab.substring(4),low,j,false));
					
				}else if ("/upload".equals(tab)) {
					//stack顶部必须是upload,并且[upload=1][/upload]中间值必须有效
					if (stack.size() == 0) continue;
					Item item = stack.lastElement();
					if (item.name.equals("upload")) {
						//检验值是否有效
						if (item.high+1 == low) {
							try {
								int p = Integer.parseInt(item.value);
								if (p > 0 && p<= attSize && !usedArray[p]) {
									usedArray[p] = true;
									//使用缩略图
								//	item.value = att.getFile().get(p-1).getThumbnail_middle();
									item.high = j;
									item.ispeered = true;
									continue;
								}
							}catch (NumberFormatException e) {
								//v转换为整数失败
							}
							
						}
						//匹配失败，将stack中的标签去除掉
					    stack.remove(stack.size()-1);
					}
				} else if ("/img".equals(tab)) {
					if (stack.size() == 0) continue;
					Item item = stack.lastElement();
					
					if (item.name.equals("img")) {
						if (item.high + 1 == low) {
							item.high = j;
							item.ispeered = true;
						} else {
							stack.remove(stack.size()-1);
						}
					}
				}else if ("/face".equals(tab) || "/color".equals(tab) 
						|| "/size".equals(tab) || "/u".equals(tab.toLowerCase(Locale.US)) 
						|| "/b".equals(tab.toLowerCase(Locale.US)) || "/i".equals(tab.toLowerCase(Locale.US))
						|| "/url".equals(tab.toLowerCase(Locale.US))){
					
					if (canAndBePeered(stack, tab.toLowerCase(Locale.US).substring(1))){
						stack.add(new Item(tab.toLowerCase(Locale.US),null,low,j,true));
						
					}
				}
			}
		}
		
		StringBuilder builder = new StringBuilder();
		int pos = 0;
		for (int i = 0;i<stack.size();i++) {
			Item item = stack.elementAt(i);
			while (pos < item.low) {
				if (str.charAt(pos) == '\n'){
					builder.append("<br>");
				} else
					builder.append(str.charAt(pos));
				pos++;
			}
			if ("/face".equals(item.name) || "/size".equals(item.name) ||
					"/color".equals(item.name)) {
				builder.append("</font>");
			} else if ("/u".equals(item.name) 
					|| "/b".equals(item.name) || "/i".equals(item.name)
					|| "u".equals(item.name) || "b".equals(item.name) 
					|| "i".equals(item.name)){
				
				builder.append("<" + item.name + ">");
			} else if ("/url".equals(item.name)) {
				builder.append("</a>");
			}else if ("url".equals(item.name)){
				builder.append("<a href=\""+ item.value + "\">");
			}else if ("em".equals(item.name)) {
				
				builder.append("<img src=\"" +  item.value + "\"/>");

			} else if ("upload".equals(item.name)) {
				if (item.ispeered) {
					int index = Integer.parseInt(item.value);
					String name = att.getFile().get(index-1).getName();
					int p = name.lastIndexOf('.');
					if (p>=0 || p < name.length()-1) {
						String suffix = name.substring(p+1).toLowerCase(Locale.US);   //图片
						if (suffix.equals("jpg") || suffix.equals("png") || 
								suffix.equals("gif") || suffix.equals("jpeg")) {
							builder.append("<img src=\"" + item.value + "\"/><br>");
						} else {
							builder.append("<a href=\""+ item.value +"\">" + 
									"附件(" + att.getFile().get(index-1).getSize() + ") " + name + "</a>");
						}
					}
				}
			} else if ("img".equals(item.name)) {
				builder.append("<img src=\"" + item.value + "\"/><br>");
			}else if ("face".equals(item.name) || "size".equals(item.name) ||
					"color".equals(item.name)) {
				
				builder.append("<font "+ item.name + "=\"" +
						item.value + "\">");
			}
			pos = item.high+1;
		}
		while (pos < str.length()) {
			if (str.charAt(pos) == '\n'){
				builder.append("<br>");
			} else
				builder.append(str.charAt(pos));
			pos++;
		}
//		for (int i= stack.size()-1;i>=0;i--) {
//			Item item = stack.elementAt(i);
//			
//			if ("/face".equals(item.name) || "/size".equals(item.name) ||
//					"/color".equals(item.name)) {
//				String temp = str.substring(0, item.low) + "</font>";
//				if (item.high < str.length() -1 )
//					temp +=  str.substring(item.high+1);
//				
//				str = temp;
//			} else if ("em".equals(item.name)) {
//				
//				String temp = str.substring(0, item.low) + "<img src=\"" +  item.value + "\">小动图</img>";
//				if (item.high < str.length() -1 )
//					temp +=  str.substring(item.high+1);
//				
//				str = temp;
//			} else if ("upload".equals(item.name)) {
//				if (item.ispeered) {
//					
//					String temp = str.substring(0, item.low) + "<img src=\"" +
//					item.value + "\"/>";
//					if (item.high < str.length() -1 )
//						temp +=  str.substring(item.high+1);
//					
//					str = temp;
//				}
//			} else if ("face".equals(item.name) || "size".equals(item.name) ||
//					"color".equals(item.name)) {
//				
//				String temp = str.substring(0, item.low) + "<font "+ item.name + "=\"" +
//						item.value + "\">";
//						if (item.high < str.length() -1 )
//							temp +=  str.substring(item.high+1);
//						
//						str = temp;
//			}
//		}
		
		//System.out.println();
		for (int i=1;i<=attSize;i++) {
			if (!usedArray[i]) {		//未使用的附件需要添加,目前先考虑只有图片
				String name = att.getFile().get(i-1).getName();
				int p = name.lastIndexOf('.');
				if (p>=0 || p < name.length()-1) {
					String suffix = name.substring(p+1).toLowerCase(Locale.US);   //图片
					if (suffix.equals("jpg") || suffix.equals("png") || 
							suffix.equals("gif") || suffix.equals("jpeg")) {
						builder.append("<br><img src=\"" + i + "\"/><br>");
						continue;
					}
				}

				builder.append("<br><a href=\""+ i +"\">" + 
				"附件(" + att.getFile().get(i-1).getSize() + ") " + name + "</a>");
			}
		}
		//String sssss = builder.toString();
	//	System.out.println(sssss);
		return builder.toString();
	}
	
	private static boolean isIconValid(String tab) {
		int low = 0,high = 73,start = 3;
		if (tab.startsWith("ema")) high = 41;
		else if (tab.startsWith("emb")) high = 24;
		else if (tab.startsWith("emc")) high = 58;
		else start = 2;
		
		try {
			int p = Integer.parseInt(tab.substring(start));
			
			if (p>=low && p <= high) return true;
		} catch (NumberFormatException e) {
			
		}
		return false;
	}
	
	//标签是否有效    face、color、size
	private static boolean isValid(Stack<Item> stack,String name) {
		if (stack.size() == 0) return true;
		for (int i=stack.size()-1;i>=0;i--) {
			Item item = stack.elementAt(i);
			if (item.name.equals(name) && !item.ispeered) {
				return false;
			}
		}
		return true;
	}
	
	private static boolean canAndBePeered(Stack<Item> stack,String name) {
		if (stack.size() == 0) return false;
		
		for (int i=stack.size()-1;i>=0;i--) {
			Item item = stack.elementAt(i);
			if (item.name.equals(name) && !item.ispeered) {
				item.ispeered = true;
				return true;
			} 
		}
		return false;
	}
	
//	private static class Font {
//		String face;
//		String color;
//		String size;
//		
//		
//		@Override
//		public String toString() {
//			String s =  "<font";
//			if (face != null) 
//				s+=" face\""+ face+"\"";
//			if (color != null)
//				s+=" color=\"" +color+ "\"" ;
//			if (size != null) 
//				s+=  " size=\"" +size +"\"";
//			s+=">";
//			return s;
//		}
//	}
	
	  private static class Item {
		String name;
		String value;
		int low;
		int high;
		boolean ispeered;
		


		public Item(String name, String value, int low, int high,boolean ispeered) {
			super();
			this.name = name;
			this.value = value;
			this.low = low;
			this.high = high;
			this.ispeered = ispeered;
		}	
	}
	  
	public static int getFileLength(String fileLength) {
		double length = 0;
		int index = 0;
		if (fileLength.endsWith("KB")) {
			index  = fileLength.lastIndexOf("KB");
			length = Double.parseDouble(fileLength.substring(0,index)) * 1024;
		} else if (fileLength.endsWith("B")) {
			index  = fileLength.lastIndexOf("B");
			length = Double.parseDouble(fileLength.substring(0,index));
		} else if (fileLength.endsWith("MB")) {
			index  = fileLength.lastIndexOf("MB");
			length = Double.parseDouble(fileLength.substring(0,index)) * 1024 * 1024;
		} 
		return (int)length;
	}
	
	public static String getPathFromUrl(String url) {
		
		if (!Environment.getExternalStorageState().
				equals(Environment.MEDIA_MOUNTED)) {
			return null;
		}
		
		int pos = url.lastIndexOf('/');
		if (pos == -1) return null;
		int start = 0;
		if (url.startsWith("https://")) start = 8;
		else if(url.startsWith("http://")) start = 7;
		return url.substring(start).replace('/', '_');
	}
	
	public static String getReplyContent(String content,String username) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("【 在 " + username + " 的大作中提到: 】");
		
		String[] data = content.split("\n");
		
		int pos = 0;
		if (data.length > 1 && data[0].equals("")  && data[1].startsWith("【 在 ") && data[1].endsWith(" 的大作中提到: 】")) pos+=2;
		if (data.length > 2 && data[2].startsWith(":") && pos > 0) pos++;
		if (data.length > 3 && data[3].startsWith(":") && pos > 0) pos++;
		if (data.length > 4 && data[4].startsWith(":") && pos > 0) pos++;
		if (data.length > 5 && data[5].startsWith(":")&& pos > 0) pos++;
		
		int count = 4;
		while (pos<data.length-1 && count > 1) {
			sb.append("\n: " + data[pos++]);
			count--;
		}
		
		if (count ==1) {
			if (pos == data.length-2) {
				sb.append("\n: " + data[pos]);
			} else {
				sb.append("\n: " + "...................");
			}
		}
		return sb.toString();
	}
	
	public static int getDisplayValue(int sp,Context context) {
		
		if (metrics == null) {
			metrics = getMetrics(context);
		}
		
		return Math.round(sp * metrics.density);
	}
	
	public static int getScreenWidth(Context context) {
		
		if (metrics == null) metrics = getMetrics(context);
		return metrics.widthPixels;
	}
	
	public static int getScreenHeight(Context context) {
		if (metrics == null) metrics = getMetrics(context);
		return metrics.heightPixels;
	}
	
	private static  DisplayMetrics getMetrics(Context context) {
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager windowManager =  (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(metrics);
		return metrics;
	}
	
	//替换第num个key的值
	public static String getStringAfterDeal(String content, String key,String replace,int num) {

		int pos = 0;
		while (num > 0) {         
			pos = content.indexOf(key,pos) + 1;
			num--;
		}
		
		if (pos > 0) pos--;
		int start = pos + key.length() + 2;
		int end = content.indexOf(",",start);
		return content.substring(0, start) + replace +content.substring(end);
	}
}
