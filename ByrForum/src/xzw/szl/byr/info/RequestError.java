package xzw.szl.byr.info;

public class RequestError {
	
	private String request;
	private String code;
	private String msg;
	
	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public RequestError(String request, String code, String msg) {
		super();
		this.request = request;
		this.code = code;
		this.msg = msg;
	}
	public RequestError() {
		super();
	}
	
	
}
