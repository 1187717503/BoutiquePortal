package pk.shoplus.service.excel.vo;

import java.util.List;

/**
 * Created by dingyifan on 2017/7/3.
 */
public class ExcelObject {
    private List<ExcelCell> rows;

    public List<ExcelCell> getRows() {
        return rows;
    }

    public void setRows(List<ExcelCell> rows) {
        this.rows = rows;
    }
}
