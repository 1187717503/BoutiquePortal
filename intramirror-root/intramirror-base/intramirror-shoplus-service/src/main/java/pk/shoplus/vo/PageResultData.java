package pk.shoplus.vo;

import java.util.HashMap;
import java.util.Map;


public class PageResultData {
	// output param
	private Object result = null ; // 响应数据
	private int totalPage = 0 ; // 总页数
	private String msg = null ; // 响应消息
	private boolean status = true ; // 状态 true-SUCCESS false-ERROR
	private int sIndex = 0 ; // mysql start index
	private int eIndex = 0 ; // mysql end index
	private int count = 0 ; // 数据总条数
	
	// input param
	private int currentPage = 0 ; // 当前页码
	private int pageSize = 10 ; // 每页显示Size
	private Map<String,Object> conditions = new HashMap<String,Object>(); // 注入条件
	
	
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getsIndex() {
		return sIndex;
	}
	public void setsIndex(int sIndex) {
		this.sIndex = sIndex;
	}
	public int geteIndex() {
		return eIndex;
	}
	public void seteIndex(int eIndex) {
		this.eIndex = eIndex;
	}
	
	public int sIndex(){
		int si = currentPage<=0?0:(currentPage-1)*pageSize;
		this.sIndex = si>=count?0:si;
		return this.sIndex;
	}
	public int eIndex(){
		int ei = currentPage<=0?pageSize:currentPage*pageSize;
		this.eIndex = ei;
		return this.eIndex;
	}
	public Map<String, Object> getConditions() {
		return conditions;
	}
	public void setConditions(Map<String, Object> conditions) {
		this.conditions = conditions;
	}
	public int totalPage(){
		int tp = count%pageSize==0?count/pageSize:count/pageSize+1;
		return tp;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public void count(int count) {
		this.totalPage = count%pageSize==0?count/pageSize:count/pageSize+1;
		this.count = count;
	}
	
}
