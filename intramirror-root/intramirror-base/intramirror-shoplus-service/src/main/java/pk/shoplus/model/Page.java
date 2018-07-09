package pk.shoplus.model;

import java.util.List;
import java.util.Map;

/**
 * @author 作者 :Jeff
 * @date 创建时间：2016年8月25日 下午7:11:47
 * @version 1.0
 */
public class Page {

	public List<Map<String, Object>> list; // 页面数据
	public Long pageNumber; // 页数
	public Long pageSize; // 每页包含的数据量
	public Long totalPage; // 总页数
	public Long totalRow; // 总行数
	// Init
	
	public Integer rows;

	public Integer getRows() {
		return rows;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	public Page() {
		this.list = null;
		this.pageNumber = 0l;
		this.pageSize = 0l;
		this.totalPage = 1l;
		this.totalRow = 0l;
	}

	/**
	 * Constructor.
	 * 
	 * @param arrayList
	 *            the list of paginate result
	 * @param pageNumber
	 *            the page number
	 * @param pageSize
	 *            the page size
	 * @param totalPage
	 *            the total page of paginate
	 * @param totalRow
	 *            the total row of paginate
	 */
	public Page(List<Map<String, Object>> arrayList, Long pageNumber, Long pageSize, Long totalPage, Long totalRow) {
		this.list = arrayList;
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.totalPage = totalPage;
		this.totalRow = totalRow;
	}
	
	public Page(List<Map<String, Object>> arrayList, Long pageNumber, Long pageSize) {
		this.list = arrayList;
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
	}

	public List<Map<String, Object>> getList() {
		return list;
	}

	public Long getPageNumber() {
		return pageNumber;
	}

	public Long getPageSize() {
		return pageSize;
	}

	public Long getTotalPage() {
		return totalPage;
	}

	public Long getTotalRow() {
		return totalRow;
	}

	public Page(List<Map<String, Object>> list, Integer rows) {
		super();
		this.list = list;
		this.rows = rows;
	}
}
