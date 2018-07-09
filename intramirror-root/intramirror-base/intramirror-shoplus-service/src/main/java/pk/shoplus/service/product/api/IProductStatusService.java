package pk.shoplus.service.product.api;

import org.sql2o.Connection;
import pk.shoplus.enums.ProductStatusEnum;
import pk.shoplus.vo.AddShopVo;
import pk.shoplus.vo.ResultMessage;
import pk.shoplus.vo.StatusMachineVO;

import java.util.Map;

/**
 * Created by dingyifan on 2017/6/1.
 */
public interface IProductStatusService {

    /**
     * 状态机变化
     * @param conn
     * @param statusMachineVO
     * @param changeEnum
     * @return ResultMessage
     */
    public ResultMessage statusMachineChange(Connection conn, StatusMachineVO statusMachineVO, ProductStatusEnum changeEnum);

    /**
     * 添加商品到shop
     * @param asv
     * @param userId
     * @return ResultMessage
     */
    public ResultMessage addToShop(AddShopVo asv, Long userId);

    /**
     * shop上架商品
     * @param statusMachineVO
     * @return
     */
    public ResultMessage onSaleAndoffSaleProduct(Connection conn,StatusMachineVO statusMachineVO,ProductStatusEnum changeEnum);

    /**
     * 在shop中删除商品
     * @param conn
     * @param statusMachineVO
     * @return
     */
    public ResultMessage deleteShop(Connection conn,StatusMachineVO statusMachineVO);
}
