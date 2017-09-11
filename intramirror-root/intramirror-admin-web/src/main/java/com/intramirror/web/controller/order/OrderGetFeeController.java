package com.intramirror.web.controller.order;

import com.intramirror.common.Helper;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.main.api.service.*;
import com.intramirror.product.api.service.SkuService;
import com.intramirror.user.api.model.User;
import com.intramirror.user.api.service.VendorService;
import com.intramirror.web.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@CrossOrigin
@Controller
@RequestMapping("/order")
public class OrderGetFeeController extends BaseController {

    public static final String DISCOUNT_CONFIG_FILE = "/tax.properties";

    private final BigDecimal basicNum = new BigDecimal(1);

    private static String discountOne;

    private static String discountTwo;

    private static String discountThree;

    private static String discountFour;

    private static String discountFive;

    static {
        // OSS
        InputStream in = TaxService.class.getResourceAsStream(DISCOUNT_CONFIG_FILE);
        Properties props = new Properties();
        try {
            props.load(in);

            discountOne = props.getProperty("discount.one");
            discountTwo = props.getProperty("discount.two");
            discountThree = props.getProperty("discount.three");
            discountFour = props.getProperty("discount.four");
            discountFive = props.getProperty("discount.five");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Autowired
    private ShippingRuleService shippingRuleService;

    @Autowired
    private AddressCountryCoefficientService addressCountryCoefficientService;

    @Autowired
    private ExchangeRateService exchangerRateService;

    @Autowired
    private TaxService taxService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private SkuService skuService;

    /**
     * Wang
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/get_fee", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage orderGetFee(@RequestBody Map<String, Object> map, HttpServletRequest httpRequest) throws Exception {
        ResultMessage result = new ResultMessage();
        Map<String, Object> results = new HashMap<>();
        result.errorStatus();

        User user = this.getUserInfo(httpRequest);
        if (user == null) {
            result.setMsg("Please log in again");
            return result;
        }

        String productIds = null;
        String geographyId = null;
        String countryId = null;
        String shopProductSkuIds = null;
        String categoryId = null;

        if (map.get("product_ids") != null && StringUtils.isNotBlank(map.get("product_ids").toString())) {
            productIds = map.get("product_ids").toString();
        }

        if (map.get("geography_id") != null && StringUtils.isNotBlank(map.get("geography_id").toString())) {
            geographyId = map.get("geography_id").toString();
        }

        if (map.get("country_id") != null && StringUtils.isNotBlank(map.get("country_id").toString())) {
            countryId = map.get("country_id").toString();
        }

        if (map.get("shop_product_sku_ids") != null && StringUtils.isNotBlank(map.get("shop_product_sku_ids").toString())) {
            shopProductSkuIds = map.get("shop_product_sku_ids").toString();
        }

        if (map.get("category_id") != null && StringUtils.isNotBlank(map.get("category_id").toString())) {
            categoryId = map.get("category_id").toString();
        }

        try {
             /*获取Vat增值税费*/
            Map taxFeeMap = new HashMap();
            if (("3").equals(geographyId)) {
                taxFeeMap = this.getTaxFeeBySku(categoryId, shopProductSkuIds, geographyId);
            } else {
                taxFeeMap.put("taxFees", 0);
            }
            results.put("taxFeeMap", taxFeeMap);

            /*获取运费*/
            List<Map<String, Object>> shipFeeListMap = getShippingFee(productIds, countryId);
            results.put("shipFeeListMap", shipFeeListMap);

            /*汇率*/
            List<Map<String, Object>> exchangerRate = exchangerRateService.getShipFeeByCityId(null, null);
            BigDecimal rate = BigDecimal.valueOf(Double.valueOf(exchangerRate.get(0).get("exchange_rate").toString()));
            results.put("rate", rate);

            result.successStatus();
            result.setData(results);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    private List<Map<String, Object>> getShippingFee(String productIds, String toCountryId) {
        //获取运费列表
        List<Map<String, Object>> list = shippingRuleService.getShippingFeeByProductIds(productIds.split(","), Integer.parseInt(toCountryId));
        List<Map<String, Object>> resultList = null;
        List<Map<String, Object>> coefficientList = null;
        Integer fromCountryId = null;

        if (null != list && list.size() > 0) {
            resultList = new ArrayList<>(list.size());
            for (Map<String, Object> map : list) {
                if (null != map) {
                    String addressCountryId = "";
                    String currentRate = "";

                    if (null != map.get("address_country_id")) {
                        addressCountryId = map.get("address_country_id").toString();
                    }

                    if (null != map.get("current_rate")) {
                        currentRate = map.get("current_rate").toString();
                    }
                    if (org.apache.commons.lang3.StringUtils.isNotBlank(addressCountryId)) {
                        fromCountryId = Integer.parseInt(addressCountryId);
                        String key = fromCountryId + "|" + toCountryId;
                        coefficientList = addressCountryCoefficientService.getAddressCountryCoefficientByCountry(fromCountryId, Integer.parseInt(toCountryId));
                    }

                    if (null != coefficientList && coefficientList.size() > 0) {
                        if (null != map.get("fee")) {
                            BigDecimal resultFee = new BigDecimal(map.get("fee").toString());

                            Integer operation_type = 0;
                            BigDecimal coefficient = null;
                            for (Map<String, Object> coefficientMap : coefficientList) {
                                if (null != coefficientMap && null != coefficientMap.get("operation_type")) {
                                    operation_type = Integer.parseInt(coefficientMap.get("operation_type").toString());
                                }
                                if (null != coefficientMap && null != coefficientMap.get("coefficient")) {
                                    coefficient = new BigDecimal(coefficientMap.get("coefficient").toString());
                                }
                                if (0 != operation_type && coefficient != null) {
                                    if (1 == operation_type) {
                                        resultFee = resultFee.add(coefficient);
                                    } else if (2 == operation_type) {
                                        resultFee = resultFee.subtract(coefficient);
                                    } else if (3 == operation_type) {
                                        resultFee = resultFee.multiply(coefficient);
                                    } else if (4 == operation_type) {
                                        resultFee = resultFee.divide(coefficient);
                                    }
                                }
                            }
                            if (null != map.get("min_number")) {
                                BigDecimal minFee = new BigDecimal(map.get("min_number").toString());
                                if (resultFee.compareTo(minFee) == -1) {
                                    resultFee = minFee;
                                }

                                map.put("fee", resultFee.multiply(new BigDecimal(currentRate).setScale(2, RoundingMode.HALF_UP)));
                                map.put("eurFee", resultFee.setScale(2, RoundingMode.HALF_UP));
                            } else {
                                map.put("fee", resultFee.setScale(2, RoundingMode.HALF_UP));
                                map.put("eurFee", resultFee.divide(new BigDecimal(currentRate), 2, RoundingMode.HALF_UP));
                            }

                        }
                    } else {
                        BigDecimal resultFee;
                        if (map.get("fee") != null) {
                            resultFee = new BigDecimal(map.get("fee").toString());
                            map.put("fee", resultFee.setScale(2, RoundingMode.HALF_UP));
                            map.put("eurFee", resultFee.divide(new BigDecimal(currentRate), 2, RoundingMode.HALF_UP));
                        } else {
                            resultFee = new BigDecimal(0);
                            map.put("fee", resultFee.setScale(2, RoundingMode.HALF_UP));
                            map.put("eurFee", resultFee.setScale(2, RoundingMode.HALF_UP));
                        }

                    }
                }

                resultList.add(map);
            }
        }

        return resultList;
    }

    public Map<String, Object> getTaxFeeBySku(String categoryId, String shopProductSkuIds, String geographyId) {
        Map<String, Object> result = new HashMap<String, Object>();

        try {
            BigDecimal shippingFees = new BigDecimal(0.0);

            result.put("shippingFee", shippingFees);
            if (Helper.checkNotNull(geographyId)) {
                BigDecimal taxFees = new BigDecimal(0.0);
                BigDecimal allTaxFees = new BigDecimal(0.0);
                switch (geographyId) {
                    case "1":
                        String taxType = "2";// 在tax表中，tax-type为2，为关税，大陆，默认显示关税
                        List<Map<String, Object>> list = taxService.getTaxByCategoryId(taxType, categoryId);
                        result.put("customDuty", list);
                        break;
                    case "2":
                        result.put("taxFees", taxFees);
                        break;
                    case "3":// 欧盟计算税费（增值税）
                        String taxTypeVAT = "1";// 1.表示增值税
                        BigDecimal fee = new BigDecimal(0.0);
                        Map<String, String> vendorIdMap = vendorService.getProductSkuVendorIdMap(shopProductSkuIds.split(","));
                        String vendorIds = vendorIdMap.get(shopProductSkuIds);
                        List<Map<String, Object>> fromCountryId = vendorService.getAllVendorCountryById(vendorIds.split(","));
                        List<Map<String, Object>> taxRatelist = taxService.getTaxRateListById(
                                fromCountryId.get(0).get("address_country_id").toString(), taxTypeVAT);

                        Map<String, BigDecimal> prices = skuService.getSkuInfoBySkuId(shopProductSkuIds);

                        BigDecimal taxRate = new BigDecimal(taxRatelist.get(0).get("tax_rate").toString());
                        BigDecimal markupRate = new BigDecimal(taxRatelist.get(0).get("markup_rate").toString());
                        BigDecimal coefficient = basicNum.add(taxRate);

                        BigDecimal boutiquePrice = prices.get("inPrice");
                        BigDecimal retailPrice = prices.get("price");
                        BigDecimal salePrice = prices.get("salePrice");
                        BigDecimal imPrice = prices.get("imPrice");

                        BigDecimal boutiqueDiscount = boutiquePrice.multiply(coefficient).divide(retailPrice, 4, RoundingMode.HALF_UP);
                        BigDecimal imDiscount = imPrice.divide(retailPrice, 4, RoundingMode.HALF_UP);

                        if (salePrice.compareTo(boutiquePrice.multiply(coefficient)) == -1) {
                            System.out.println("retailPrice:" + retailPrice.doubleValue() + " boutiquePrice:" + boutiquePrice.doubleValue() + " salePrice:" + salePrice + " taxRate:" + taxRate.doubleValue() + " markupRate:" + markupRate.doubleValue() + " imPrice:" + imPrice.doubleValue());

                            //fee = salePrice.multiply(new BigDecimal("0.15")).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal discountTarget = new BigDecimal(discountOne)
                                    .subtract(boutiqueDiscount)
                                    .divide(new BigDecimal(discountTwo), 4, RoundingMode.HALF_UP)
                                    .add(new BigDecimal(discountThree))
                                    .multiply(imDiscount)
                                    .subtract(boutiqueDiscount)
                                    .setScale(2, RoundingMode.HALF_UP);
                            if (discountTarget.compareTo(new BigDecimal(discountFour)) == 1) {
                                discountTarget = new BigDecimal(discountFour);
                            } else if (discountTarget.compareTo(new BigDecimal(discountFive)) == -1) {
                                discountTarget = new BigDecimal(discountFive);
                            }

                            fee = discountTarget.add(boutiqueDiscount).subtract(imDiscount).multiply(retailPrice);
                            System.out.println("增值税: " + fee.doubleValue());
                        } else {
                            BigDecimal zero = new BigDecimal(0.0);
                            fee = zero;
                        }
                        allTaxFees = (allTaxFees.add(fee)).setScale(2, RoundingMode.HALF_UP);
                        result.put("taxFees", allTaxFees);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
