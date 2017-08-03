package pk.shoplus.vo;

import java.util.List;

/**
 * 绘画Datatables的VO 返回类
 * @author yfding
 * @since 2017-4-12 11:13:37
 */
public class DrawTablesVO {
	private String draw;
	private String recordsTotal;
	private String recordsFiltered;
	private List<List<String>> data;
	
	public String getDraw() {
		return draw;
	}
	public void setDraw(String draw) {
		this.draw = draw;
	}
	public String getRecordsTotal() {
		return recordsTotal;
	}
	public void setRecordsTotal(String recordsTotal) {
		this.recordsTotal = recordsTotal;
	}
	public String getRecordsFiltered() {
		return recordsFiltered;
	}
	public void setRecordsFiltered(String recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}
	public List<List<String>> getData() {
		return data;
	}
	public void setData(List<List<String>> data) {
		this.data = data;
	}
}
