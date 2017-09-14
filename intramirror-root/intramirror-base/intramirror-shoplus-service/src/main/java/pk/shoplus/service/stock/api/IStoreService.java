package pk.shoplus.service.stock.api;

import org.sql2o.Connection;
import pk.shoplus.model.SkuStore;
import pk.shoplus.vo.ResultMessage;

/**
 * Created by dingyifan on 2017/6/9.
 */
public interface IStoreService {

    /**
     * 处理库存规则
     * @param qtyType
     * @param qtyDiff
     * @param size
     * @param productCode
     * @return
     * @throws Exception
     */
    public ResultMessage handleApiStockRule(int qtyType, int qtyDiff, String size, String productCode,String queueNameEnum) throws Exception;

    public ResultMessage handleApiStockRuleService(Connection conn,int qtyType, int qtyDiff, String size, String productCode,String queueNameEnum) throws Exception;

}
