package pk.shoplus.service.excel.api;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/7/3.
 */
public interface IExcelService {

    /**
     * 生成excel,并返回文件名称
     * @param rows
     * @return
     */
    public String genExcel(List<List<String>> rows) throws IOException;

    public String converMap(List<Map<String,Object>> mapList) throws IOException;

}
