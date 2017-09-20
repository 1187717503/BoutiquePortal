package com.intramirror.web.controller.buyersys;

import java.util.HashMap;
import java.util.Map;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @auth:jerry
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class SystemCallFactory {

    private static Map nameMap = new HashMap<Long, String>();

    public SystemCallFactory () {
        //nameMap.put(7L, "eds");
        nameMap.put(8L, "intramirror");
        nameMap.put(9L, "eds");
        nameMap.put(10L, "intramirror");
        nameMap.put(11L, "intramirror");
        nameMap.put(12L, "intramirror");
        nameMap.put(13L, "intramirror");
        nameMap.put(14L, "intramirror");
        //nameMap.put(15L, "eds");
        nameMap.put(16L, "cloudstore");
        nameMap.put(17L, "filippo");
        nameMap.put(18L, "intramirror");
        nameMap.put(19L, "quadra");
        nameMap.put(20L, "xmag");
        nameMap.put(21L, "eds");
    }

    public static BuyerSystemCall createOrder (Long vendorId) {
        BuyerSystemCall buyerSystemCall = null;

        String className = nameMap.get(vendorId).toString();

        /*if ("OPS".equals(VendorEnum.OPS.getValue())) {
            buyerSystemCall = new Bu
        } else if ("LUC".equals(VendorEnum.LUC)) {

        } else if ("NUG".equals(VendorEnum.NUG)) {

        } else if ("DNT".equals(VendorEnum.DNT)) {

        } else if ("ICF".equals(VendorEnum.ICF)) {

        } else if ("MIA".equals(VendorEnum.MIA)) {

        } else if ("DIP".equals(VendorEnum.DIP)) {

        } else if ("GIS".equals(VendorEnum.GIS)) {

        } else if ("AVR".equals(VendorEnum.AVR)) {

        } else if ("TON".equals(VendorEnum.TON)) {

        } else if ("FPM".equals(VendorEnum.FPM)) {

        } else if ("WIS".equals(VendorEnum.WIS)) {

        } else if ("APR".equals(VendorEnum.APR)) {

        }*/
        return null;
    }
}
