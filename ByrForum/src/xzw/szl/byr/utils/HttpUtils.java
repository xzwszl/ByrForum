package xzw.szl.byr.utils;

import java.io.BufferedInputStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import xzw.szl.byr.assist.PictureActivity.OnPicitureDownloadListener;
import xzw.szl.byr.info.RequestError;
import xzw.szl.byr.info.VoteDetail;
import xzw.szl.byr.mananger.PrefernceManager;
import android.content.Context;
import android.os.Environment;
public class HttpUtils{
	
	private static final String SERVICE_FAILED = "服务失效,请求失败";
	public static Context context;
	private static HttpClient httpClient;
	
	
	
	public static HttpClient createHttpClient() {
		
		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schReg.register(new Scheme("https",SSLSocketFactory.getSocketFactory(), 443));
		
		HttpParams params = getHttpParams();
		//线程安全的连接管理
		ClientConnectionManager manager = new ThreadSafeClientConnManager(getHttpParams(),schReg);
		return new DefaultHttpClient(manager,params);
	}
	
	
	public static HttpClient getHttpClient() {
		
		if (httpClient  == null) {
			httpClient = createHttpClient();
			if (((DefaultHttpClient)httpClient).getCredentialsProvider() == null)
				setBasicCredentitalsProvider(PrefernceManager.getInstance().getCurrentUserName(context), 
						PrefernceManager.getInstance().getCurrentUserPassword(context));
		}
		return httpClient;
	}
	
	public static void shutdownHttpClient () {
		if (httpClient != null && httpClient.getConnectionManager() != null) {
			httpClient.getConnectionManager().shutdown();
			httpClient = null;
		}
	}
	
	public static void setBasicCredentitalsProvider(String account, String password) {
		BasicCredentialsProvider bcp = basicAuth(account, password);
		((DefaultHttpClient)getHttpClient()).setCredentialsProvider(bcp);
	}
	
	
	private static HttpParams getHttpParams () {
		
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		HttpProtocolParams.setUseExpectContinue(params, true);
		
		//
		ConnManagerParams.setTimeout(params, 1000);
		//连接超时
		HttpConnectionParams.setConnectionTimeout(params, 3000);
		//请求超时
		HttpConnectionParams.setSoTimeout(params,5000);
		
		return params;
	}
	//使用httpGet获取论坛的数据，
	/**
	 * @param url	如果带参数，格式化的参数,such as: ***?name=qwer&id=12,没有的话为""
	 */
	public static String getJsonData(String url) { 
		
		HttpClient httpClient = getHttpClient();
//		
//	    ((DefaultHttpClient) httpClient).setCredentialsProvider(bcp);
//	    ((DefaultHttpClient) httpClient).setParams(getHttpParams());
		HttpGet httpGet = new HttpGet(ByrBase.BASE_URL + url);
		
		try {
			HttpResponse httpResponse = httpClient.execute(httpGet);
			
			int status = httpResponse.getStatusLine().getStatusCode();
			
			if (status == HttpStatus.SC_OK) {
				//返回json数据
				return EntityUtils.toString(httpResponse.getEntity());	
			} else {
				return null;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} 
	}
	
	private static String getRealUrl(String url) {
		if (url.contains("?")) {
			url+="&";
		} else {
			url+="?";
		}
		
		return url + "appkey=" + ByrBase.APPKEY;
	}
	
	public static String convertURL(String str) {

		if (str == null) return null;
		
	    return new String(str.trim().replace("%","").replace(" ", "%20").replace("&", "%26")
	            .replace(",", "%2c").replace("(", "%28").replace(")", "%29")
	            .replace("!", "%21").replace("=", "%3D").replace("<", "%3C")
	            .replace(">", "%3E").replace("#", "%23").replace("$", "%24")
	            .replace("'", "%27").replace("*", "%2A").replace("-", "%2D")
	            .replace(";", "%3B").replace("?", "%3F").replace("@", "%40")
	            .replace("[", "%5B").replace("\\", "%5C").replace("]", "%5D")
	            .replace("_", "%5F").replace("`", "%60").replace("{", "%7B")
	            .replace("|", "%7C").replace("}", "%7D"));
	}
	
	public static void httpRequest(String url,HttpRequestListener listener) { 
		
		
		HttpClient httpClient = getHttpClient();
//		
//	    ((DefaultHttpClient) httpClient).setCredentialsProvider(bcp);
//	    ((DefaultHttpClient) httpClient).setParams(getHttpParams());
		
		
		try {
			
			HttpGet httpGet = new HttpGet(ByrBase.BASE_URL+ getRealUrl(url));
			HttpResponse httpResponse = httpClient.execute(httpGet);
			
			int status = httpResponse.getStatusLine().getStatusCode();
			
			if (status == HttpStatus.SC_OK) {
				//返回json数据
				String js = EntityUtils.toString(httpResponse.getEntity());
				RequestError error = (RequestError) JsonUtils.toBean(js, RequestError.class);
				if (error == null) {
					listener.onSuccess(js);
				} else {
					listener.onFailed(error.getMsg());
				}
			} else {
				listener.onFailed(SERVICE_FAILED);
			}
		} catch (ClientProtocolException e) {
			listener.onError(e);
		} catch (IOException e) {
			listener.onError(e);
		}
	}
	
	public static void getBitmap(String url) { 
		
		HttpClient httpClient = getHttpClient();
//		
//	    ((DefaultHttpClient) httpClient).setCredentialsProvider(bcp);
//	    ((DefaultHttpClient) httpClient).setParams(getHttpParams());

		try {

			HttpGet httpGet = new HttpGet(getRealUrl(convertURL(url)));
			HttpResponse httpResponse = httpClient.execute(httpGet);
			
			int status = httpResponse.getStatusLine().getStatusCode();
			
			if (status == HttpStatus.SC_OK) {
				//图片
//				BitmapFactory.Options options = new BitmapFactory.Options();
//				DisplayMetrics metrics = context.getResources().getDisplayMetrics();
//				options.inScreenDensity = metrics.densityDpi;
//				options.inTargetDensity = metrics.densityDpi;
//				options.inDensity = DisplayMetrics.DENSITY_HIGH;
			//	Bitmap bitmap = BitmapFactory.decodeStream();
				ImageUtils.downloadImagetoSD(httpResponse.getEntity().getContent(),url);
//				return bitmap;	
			} else {
				return;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	public static void getPicture(String url,String type,OnPicitureDownloadListener listener,int start) { 
		
		HttpClient httpClient = getHttpClient();
//		
//	    ((DefaultHttpClient) httpClient).setCredentialsProvider(bcp);
//	    ((DefaultHttpClient) httpClient).setParams(getHttpParams());
		
	
		HttpGet httpGet = new HttpGet(getRealUrl(convertURL(url)));
		httpGet.addHeader("Range", "bytes=" + start);
		
		try {
			
			HttpResponse httpResponse;
			try {
				httpResponse = httpClient.execute(httpGet);
			} catch (IOException e) {
				listener.onRefreshDownloadState(OnPicitureDownloadListener.DOWNLOAD_ERROR);
				return;
			}
			
			int status = httpResponse.getStatusLine().getStatusCode();
			
			if (status == HttpStatus.SC_OK) {

				HttpEntity httpEntity = httpResponse.getEntity();
				int total = (int)httpEntity.getContentLength();
				listener.onRefreshDownloadState(OnPicitureDownloadListener.DOWNLOADING);
				listener.onUpdateDownloadProgress(0, total);
				
				byte[] fb = new byte[1024 * 2];
				int len = 0;
				int count = 0;
				FileOutputStream out;
				try {
					out = new FileOutputStream(Environment.getExternalStorageDirectory()+
							ByrBase.PIC_DIR + DataUtils.getPathFromUrl(url+type));
				} catch (FileNotFoundException e) {
					
					e.printStackTrace();
					return;
				}
				BufferedInputStream in = new BufferedInputStream(httpEntity.getContent());
				while ((len = in.read(fb)) != -1) {
					out.write(fb, 0, len);
					count +=len;
					listener.onUpdateDownloadProgress(count,total);
				}
				
				out.close();
				//暂停50ms
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				listener.onRefreshDownloadState(OnPicitureDownloadListener.DOWNLOADED);
				listener.onUpdateDownloadProgress(count,total);
			} else {
				listener.onRefreshDownloadState(OnPicitureDownloadListener.DOWNLOAD_ERROR);
			}
		} catch (ClientProtocolException e) {
			listener.onRefreshDownloadState(OnPicitureDownloadListener.DOWNLOAD_ERROR);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			listener.onRefreshDownloadState(OnPicitureDownloadListener.DOWNLOAD_ERROR);
		} catch (IOException e) {
			listener.onRefreshDownloadState(OnPicitureDownloadListener.DOWNLOAD_ERROR);
		} 
	}
	public static BasicCredentialsProvider basicAuth(String username,String password) {
		
		UsernamePasswordCredentials upc = new UsernamePasswordCredentials(username,password);
		AuthScope as = new AuthScope(null,-1);
		
		BasicCredentialsProvider bcp = new BasicCredentialsProvider();
		bcp.setCredentials(as, upc);
		
		return bcp;
	}
	
	public static void postArticle (String name,String title,String content,int reid,List<String> filepathList,int id,
			ProgressListener listener, HttpRequestListener requestListener) {
		
		int current = 0;
		int total = 1;
		if (filepathList != null) {
			total +=filepathList.size();
			for (String path : filepathList)
				if (!postAttachment(name, -1,path,requestListener)) return; 
				else listener.updateProgress(++current, total);
		}
		
		String url = "";
		if (id == -1)
			url = ByrBase.BASE_URL + "/article/" + name + "/post.json";
		else
			url = ByrBase.BASE_URL + "/article/" + name + "/" +id +".json";
		HttpPost httpPost = new HttpPost(getRealUrl(url));
		HttpClient httpClient = getHttpClient();
		
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("title", title));
		params.add(new BasicNameValuePair("content", content));
		if (reid != -1)
			params.add(new BasicNameValuePair("reid", reid+""));
		
		try {
			HttpEntity httpEntity = new UrlEncodedFormEntity(params,HTTP.UTF_8);
			httpPost.setEntity(httpEntity);
			
			HttpResponse httpResponse = httpClient.execute(httpPost);
			
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

				String js = EntityUtils.toString(httpResponse.getEntity());
				RequestError error = (RequestError) JsonUtils.toBean(js, RequestError.class);
				if (error == null) {
					if (listener != null) {
						listener.updateProgress(++current, total);
					}
					
					if (requestListener != null) {
						requestListener.onSuccess(js);
					}
				} else {
					requestListener.onFailed(error.getMsg());
				}
			} else {
				requestListener.onFailed(SERVICE_FAILED);
			}
			
		} catch (UnsupportedEncodingException e) {
			requestListener.onError(e);
		} catch (ClientProtocolException e) {
			requestListener.onError(e);
		} catch (IOException e) {
			requestListener.onError(e);
		} 

	}
	
	
	public static boolean postForward (String boardname, int id, String target, HttpRequestListener listener, String... params) {
		
		String url = ByrBase.BASE_URL + "/article/" + boardname + "/forward/" + id + ".json";
		
		HttpPost httpPost = new HttpPost(getRealUrl(url));
		HttpClient httpClient = getHttpClient();
//		((DefaultHttpClient) httpClient).setCredentialsProvider(bcp);
		
		List<NameValuePair> namePairs = new ArrayList<NameValuePair>();
		namePairs.add(new BasicNameValuePair("threads", params[0]));
		namePairs.add(new BasicNameValuePair("noref", params[1]));
		namePairs.add(new BasicNameValuePair("noatt", params[2]));
		namePairs.add(new BasicNameValuePair("target", target));
		
		try {
			HttpEntity httpEntity = new UrlEncodedFormEntity(namePairs,HTTP.UTF_8);
			httpPost.setEntity(httpEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				
				String js = EntityUtils.toString(httpResponse.getEntity());
				RequestError error = (RequestError) JsonUtils.toBean(js, RequestError.class);
				if (error == null) {
					listener.onSuccess(js);
				} else {
					listener.onFailed(error.getMsg());
				}
			} else {
				listener.onFailed(SERVICE_FAILED);
			}
			
		} catch (UnsupportedEncodingException e) {
			listener.onError(e);
		} catch (ClientProtocolException e) {
			listener.onError(e);
		} catch (IOException e) {
			listener.onError(e);
		}
		return false;
	}
	public static boolean postAttachment (String name,int id,String filepath,HttpRequestListener listener) {
		
		String sid = "";
		if (id != -1) sid+="/" + id;
		String url = ByrBase.BASE_URL + "/attachment/" + name + "/" + "add" + sid + ".json?name=abc.txt";
		
		HttpPost  httpPost = new HttpPost(getRealUrl(url));
		
		HttpClient httpClient = getHttpClient();
//		httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
//		((DefaultHttpClient)httpClient).setCredentialsProvider(bcp);
		
		
		File file = new File(filepath);
		if (file.exists()) {
			MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.STRICT);
			multipartEntity.addPart("file", new FileBody(file));
			httpPost.setEntity(multipartEntity);
			HttpResponse httpResponse;
			try {
				httpResponse = httpClient.execute(httpPost);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					
					String js = EntityUtils.toString(httpResponse.getEntity());
					RequestError error = (RequestError) JsonUtils.toBean(js, RequestError.class);
					if (error == null) {
						return true;
					} else {
						listener.onFailed(error.getMsg());
					}
				} else {
					listener.onFailed(SERVICE_FAILED);
				}
			} catch (ClientProtocolException e) {
				listener.onError(e);
			} catch (IOException e) {
				listener.onError(e);
			}	
		}
		return false;
	}
	
	public static void deleteAttachment(String boardName, String id, String fielName,HttpRequestListener listener) {
		String url = ByrBase.BASE_URL + "/attachment/" + boardName + "/delete/" + id + ".json";
		
		HttpPost httpPost = new HttpPost(getRealUrl(url));
		HttpClient httpClient = getHttpClient();
//		((DefaultHttpClient) httpClient).setCredentialsProvider(bcp);
//		
		try {
			HttpResponse httpResponse = httpClient.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				
				String js = EntityUtils.toString(httpResponse.getEntity());
				RequestError error = (RequestError) JsonUtils.toBean(js, RequestError.class);
				if (error == null) {
					listener.onSuccess(js);
				} else {
					listener.onFailed(error.getMsg());
				}
			} else {
				listener.onFailed(SERVICE_FAILED);
			}
		} catch ( ClientProtocolException e) {
			listener.onError(e);
		} catch (IOException e) {
			listener.onError(e);
		} 
	}
	
	public static void deleteArticle (String name, int id,HttpRequestListener listener) {
		String url = ByrBase.BASE_URL + "/article/" + name + "/delete/" + id + ".json";
		HttpPost httpPost = new HttpPost(getRealUrl(url));
		HttpClient httpClient = getHttpClient();
//		((DefaultHttpClient)httpClient).setCredentialsProvider(bcp);
		
		try {
			HttpResponse httpResponse = httpClient.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String js = EntityUtils.toString(httpResponse.getEntity());
				RequestError error = (RequestError) JsonUtils.toBean(js, RequestError.class);
				if (error == null) {
					listener.onSuccess(js);
				} else {
					listener.onFailed(error.getMsg());
				}
			} else {
				listener.onFailed(SERVICE_FAILED);
			}
		} catch (ClientProtocolException e) {
			listener.onError(e);
		} catch (IOException e) {
			listener.onError(e);
		} 
	}
	
	public static void postVote(String voteid, List<String> votes, VoteDetail votedetail,HttpRequestListener listener) {
		
		String url = ByrBase.BASE_URL + "/vote/" + voteid + ".json";
		HttpPost httpPost = new HttpPost(getRealUrl(url));
		HttpClient httpClient = getHttpClient();
//		((DefaultHttpClient) httpClient).setCredentialsProvider(bcp);
		
		
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		
		int len = votes.size();
		
		if (votedetail.getVote().getLimit().equals("0")) {
			params.add(new BasicNameValuePair("vote",votes.get(0)));
		} else {
			for (int i = 0; i<len; i++) {
				params.add(new BasicNameValuePair("vote[" + i + "]" , votes.get(i)));
			}
		}

		try{
			
			HttpEntity httpEntity = new UrlEncodedFormEntity(params,HTTP.UTF_8);
			httpPost.setEntity(httpEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String js = EntityUtils.toString(httpResponse.getEntity());
				RequestError error = (RequestError) JsonUtils.toBean(js, RequestError.class);
				if (error == null) {
					listener.onSuccess(js);
				} else {
					listener.onFailed(error.getMsg());
				}
			} else {
				listener.onFailed(SERVICE_FAILED);
			}
			
		} catch (ClientProtocolException e) {
			listener.onError(e);
		} catch (IOException e) {
			listener.onError(e);
		}
	}
	
	//发送新信件
	public static void postMail(String id, String title, String content,HttpRequestListener listener) {
		
		String url = ByrBase.BASE_URL + "/mail/send.json";
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		params.add(new BasicNameValuePair("title", title));
		params.add(new BasicNameValuePair("content",content));
		
		HttpPost httpPost = new HttpPost(getRealUrl(url));
			
		HttpClient httpClient = getHttpClient();

		try {
			HttpEntity httpEntity = new UrlEncodedFormEntity(params,HTTP.UTF_8);
			httpPost.setEntity(httpEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				
				String js = EntityUtils.toString(httpResponse.getEntity());
				RequestError error = (RequestError) JsonUtils.toBean(js, RequestError.class);
				
				if (error == null) {
					listener.onSuccess(content);
				} else {
					listener.onFailed(error.getMsg());
				}
			} else {
				listener.onFailed(SERVICE_FAILED);
			}
		} catch (UnsupportedEncodingException e) {
			listener.onError(e);
		} catch (ClientProtocolException e) {
			listener.onError(e);
		} catch (IOException e) {
			listener.onError(e);
		}	
	}
	// delete Mail
	public static void deleteMail(String box, int num, HttpRequestListener listener) {
		
		String url = ByrBase.BASE_URL + "/mail/" + box + "/delete/" + num + ".json";
		
		HttpPost httpPost = new HttpPost(getRealUrl(url));
		HttpClient httpClient = getHttpClient();
		
		try {
			HttpResponse httpResponse = httpClient.execute(httpPost);
			
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String js = EntityUtils.toString(httpResponse.getEntity());
				RequestError error = (RequestError) JsonUtils.toBean(js, RequestError.class);
				if (error == null) {
					listener.onSuccess(js);
				} else {
					listener.onFailed(error.getMsg());
				}
			} else {
				listener.onFailed(SERVICE_FAILED);
			}
		} catch (ClientProtocolException e) {
			listener.onError(e);
		} catch (IOException e) {
			listener.onError(e);
		}
	}
	
	//转寄邮箱
	public static void postForwardBox(String box, int num, String target, HttpRequestListener listener) {
		
		String url = ByrBase.BASE_URL + "/mail/" + box + "/forward/" + num + ".json";
		
		HttpPost httpPost = new HttpPost(getRealUrl(url));
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("target",target));
		try {
			HttpEntity httpEntity = new UrlEncodedFormEntity(params,HTTP.UTF_8);
			httpPost.setEntity(httpEntity);
			
			HttpClient httpClient = getHttpClient();
			
			HttpResponse httpResponse = httpClient.execute(httpPost);
			
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String js = EntityUtils.toString(httpResponse.getEntity());
				RequestError error = (RequestError) JsonUtils.toBean(js, RequestError.class);
				if (error == null) {
					listener.onSuccess(js);
				} else {
					listener.onFailed(error.getMsg());
				}
			} else {
				listener.onFailed(SERVICE_FAILED);
			}
			
		} catch (UnsupportedEncodingException e) {
			listener.onError(e);
		} catch (ClientProtocolException e) {
			listener.onError(e);
		} catch (IOException e) {
			listener.onError(e);
		}
		
	}
	//发送新信件
	public static void replyMail(String box, int num, String title, String content,HttpRequestListener listener) {
		
		String url = ByrBase.BASE_URL + "/mail/" + box + "/reply/" + num + ".json";
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("title", title));
		params.add(new BasicNameValuePair("content",content));
		
		HttpPost httpPost = new HttpPost(getRealUrl(url));
			
		HttpClient httpClient = getHttpClient();

		try {
			HttpEntity httpEntity = new UrlEncodedFormEntity(params,HTTP.UTF_8);
			httpPost.setEntity(httpEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				
				String js = EntityUtils.toString(httpResponse.getEntity());
				RequestError error = (RequestError) JsonUtils.toBean(js, RequestError.class);
				
				if (error == null) {
					listener.onSuccess(content);
				} else {
					listener.onFailed(error.getMsg());
				}
			}  else {
				listener.onFailed(SERVICE_FAILED);
			}
		} catch (UnsupportedEncodingException e) {
			listener.onError(e);
		} catch (ClientProtocolException e) {
			listener.onError(e);
		} catch (IOException e) {
			listener.onError(e);
		}	
	}
	
	//favorite。。
	public static void dealFavorite(String type, int level, String name, String dir, HttpRequestListener listener) {
		
		String url = ByrBase.BASE_URL + "/favorite/" + type + "/" + level + ".json" ;
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("name", name));
		params.add(new BasicNameValuePair("dir", dir));
		
		HttpPost httpPost = new HttpPost(getRealUrl(url));
		
		try {
			HttpEntity httpEntity = new UrlEncodedFormEntity(params,HTTP.UTF_8);
			
			httpPost.setEntity(httpEntity);
			HttpResponse httpResponse = getHttpClient().execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				
				String js = EntityUtils.toString(httpResponse.getEntity());
				RequestError error = (RequestError) JsonUtils.toBean(js, RequestError.class);
				
				if (error == null) {
					listener.onSuccess(js);
				} else {
					listener.onFailed(error.getMsg());
				}
			}  else {
				listener.onFailed(SERVICE_FAILED);
			}
		} catch (UnsupportedEncodingException e) {
			listener.onError(e);
		} catch (ClientProtocolException e) {
			listener.onError(e);
		} catch (IOException e) {
			listener.onError(e);
		}
	}
	
    // @ reply文章的设置已读
	
	public static void setReferRead(String type, int index, HttpRequestListener listener) {
		
		String url = ByrBase.BASE_URL + "/refer/" + type + "/setRead/" + index + ".json" ;
		
		HttpPost httpPost = new HttpPost(getRealUrl(url));
		
		try {
			
			HttpResponse httpResponse = getHttpClient().execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				
				String js = EntityUtils.toString(httpResponse.getEntity());
				RequestError error = (RequestError) JsonUtils.toBean(js, RequestError.class);
				
				if (error == null) {
					listener.onSuccess(js);
				} else {
					listener.onFailed(error.getMsg());
				}
			}  else {
				listener.onFailed(SERVICE_FAILED);
			}
		} catch (UnsupportedEncodingException e) {
			listener.onError(e);
		} catch (ClientProtocolException e) {
			listener.onError(e);
		} catch (IOException e) {
			listener.onError(e);
		}
	}
	
	public static void deleteRefer(String type, int index, HttpRequestListener listener) {
		
		String url = ByrBase.BASE_URL + "/refer/" + type + "/delete/" + index + ".json" ;
		
		HttpPost httpPost = new HttpPost(getRealUrl(url));
		
		try {
			
			HttpResponse httpResponse = getHttpClient().execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				
				String js = EntityUtils.toString(httpResponse.getEntity());
				RequestError error = (RequestError) JsonUtils.toBean(js, RequestError.class);
				
				if (error == null) {
					listener.onSuccess(js);
				} else {
					listener.onFailed(error.getMsg());
				}
			}  else {
				listener.onFailed(SERVICE_FAILED);
			}
		} catch (UnsupportedEncodingException e) {
			listener.onError(e);
		} catch (ClientProtocolException e) {
			listener.onError(e);
		} catch (IOException e) {
			listener.onError(e);
		}
	}
	/**
	 *监听HTTP请求 ，成功或者失败
	 *
	 */
	public interface HttpRequestListener {
		
		//请求成功
		void onSuccess(String content);
		
		//请求失败
		void onFailed(String reason);
		
		//异常情况
		void onError (Throwable e);
		
	}
	
	
	public interface ProgressListener {
		
		// update current progress info
		void updateProgress(int current, int total);
	}
}
