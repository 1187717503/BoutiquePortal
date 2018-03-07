package com.intramirror.web.controller.api.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson15.JSONArray;
import com.google.gson.Gson;
import com.intramirror.utils.transform.JsonTransformUtil;
import static com.intramirror.web.controller.api.service.ApiCommonUtils.escape;
import com.intramirror.web.distributed.utils.KafkaMqUtil;
import com.intramirror.web.mapping.vo.StockOption;
import com.intramirror.web.util.Facility;
import com.intramirror.web.util.HttpUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.common.Contants;
import pk.shoplus.common.utils.StringUtil;
import pk.shoplus.enums.ApiErrorTypeEnum;
import pk.shoplus.enums.ProductPropertyEnumKeyName;
import pk.shoplus.model.BoutiqueImage;
import pk.shoplus.model.CategoryProductInfo;
import pk.shoplus.model.Product;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.model.ProductInfo;
import pk.shoplus.model.ProductProperty;
import pk.shoplus.model.Vendor;
import pk.shoplus.parameter.EnabledType;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.BoutiqueImageService;
import pk.shoplus.service.CategoryProductInfoService;
import pk.shoplus.service.ProductInfoService;
import pk.shoplus.service.ProductPropertyService;
import pk.shoplus.service.ProductService;
import pk.shoplus.service.VendorService;
import pk.shoplus.service.price.api.IPriceService;
import pk.shoplus.service.price.impl.PriceServiceImpl;
import pk.shoplus.util.ExceptionUtils;

/**
 * 修改商品优化
 */
public class ApiUpdateProductService extends AbstractService{

    private static final Logger logger = Logger.getLogger(ApiCreateProductService.class);



    private List<StockOption> stockOptions;



    private Boolean no_img;

    private boolean modify = false;

    private String error_type;

    private List<Map<String, Object>> warningList = new ArrayList<>();

    public Map<String, Object> updateProduct(ProductEDSManagement.ProductOptions productOptions, ProductEDSManagement.VendorOptions vendorOptions) {
        long start = System.currentTimeMillis();

        Map<String, Object> resultMap = new HashMap<>();

        this.productOptions = productOptions;
        this.vendorOptions = vendorOptions;

        Connection conn = null;

        try {
            conn = DBConnector.sql2o.beginTransaction();

            this.checkProductParams(conn);
            this.checkMappingParams(conn);

            // update product,product_property
            this.setProduct(conn);

            this.checkFilter(conn);

            // update product_info
            this.setProductInfo(conn);

            // update sku
            this.setSku(conn);

            this.exceptional(conn);

            if (warningList == null || warningList.size() == 0) {
                resultMap = ApiCommonUtils.successMap();
            } else {
                resultMap.put("status", StatusType.WARNING);
                resultMap.put("warningMaps", warningList);
            }
            this.printChangeLog(conn);
            if (conn != null) {
                conn.commit();
                conn.close();
            }
            productOptions.setError_type(error_type);
        } catch (UpdateException e) {
            resultMap = ApiCommonUtils.errorMap(e.getErrorType(), e.getKey(), e.getValue());
            if (conn != null) {
                conn.rollback();
                conn.close();
            }
        } catch (FilterException e) {
            resultMap = ApiCommonUtils.successMap();
            logger.info("ApiUpdateProductService,FilterException,errorMsg:" + e.getMessage() + ",productOptions:" + JSONObject.toJSONString(productOptions)
                    + ",vendorOptions:" + JSONObject.toJSONString(vendorOptions));
            if (conn != null) {
                conn.rollback();
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultMap = ApiCommonUtils.errorMap(ApiErrorTypeEnum.errorType.error_runtime_exception, "errorMessage", ExceptionUtils.getExceptionDetail(e));
            if (conn != null) {
                conn.rollback();
                conn.close();
            }
        }
        long end = System.currentTimeMillis();
        logger.info("Job_Run_Time,ApiUpdateProductService_updateProduct,start:" + start + ",end:" + end + ",time:" + (end - start));
        if (StringUtils.isNotBlank(productOptions.getRequestId())) {
            result.put("id", productOptions.getRequestId());
            result.put("exceptionData", resultMap);
            result.put("time", new Date());
            KafkaMqUtil.sendResultMessage(productOptions.getRequestId(), JsonTransformUtil.toJson(result));
        }

        return resultMap;
    }

    public void exceptional(Connection conn) throws Exception {
        Product product = this.getProduct(conn);
        ProductService productService = new ProductService(conn);

        // AD 买手店如果有相同designer_id和color_code，但是不同season和boutique_id的商品，需要将老season的商品库存set 0
        if (product.getVendor_id() == 22L) {
            List<Map<String, Object>> products = productService.selectProductByDesignerColor(product.designer_id, product.color_code, product.vendor_id);

            if (products != null && products.size() > 1) {
                String productIds = "";
                /*if(product.getSeason_code().equals("CarryOver")) { // 如果是CarryOver过来，清除其他的season的库存

                    for(int i = 0;i<products.size();i++) {
                        String product_id = products.get(i).get("product_id").toString();
                        String season_code = products.get(i).get("season_code").toString();

                        if(!season_code.equals("CarryOver")) {
                            productIds += product_id+",";
                        }
                    }

                } else {*/
                for (int i = 0; i < products.size(); i++) {
                    String product_id = products.get(i).get("product_id").toString();

                    if (i != 0) {
                        productIds += product_id + ",";
                    }
                }
                /*}*/

                if (StringUtils.isNotBlank(productIds)) {
                    productIds = productIds.substring(0, productIds.length() - 1);
                    String sql = "update `sku_store` ss set ss.`confirmed` = 0,ss.`store` = (0 - abs(cast(ss.`reserved` as signed))) \n"
                            + " where ss.`enabled` = 1 \n" + " and ss.`product_id` in(" + productIds + ")";
                    productService.updateBySQL(sql);
                    logger.info("AlDuca,exceptional:" + sql + ",products:" + JSONObject.toJSONString(products));
                }
            }
        }

        // 不允许存在重复的BrandID,
        if (vendorOptions.getVendorId().intValue() == 22) {
            boolean flag = productService.duplicateColorBrandByADUpdate(product.getDesigner_id(), product.getColor_code(), product.getSeason_code());
            if (flag) {
                throw new UpdateException("color_code,BrandID", product.getDesigner_id() + "," + product.getColor_code() + "," + product.getSeason_code(),
                        ApiErrorTypeEnum.errorType.error_duplicate_product);
            }
        }
    }

    public void checkFilter(Connection conn) throws Exception {
        ProductService productService = new ProductService(conn);
        Product product = this.getProduct(conn);
        String season_code = product.getSeason_code();
        String brand_id = product.getBrand_id().toString();

        // 判断season是否需要
        String seasonFilterSQL = " select * from `season_filter` sf " + " where sf.`enabled`  =1  and sf.`vendor_id`  = " + vendorOptions.getVendorId()
                + " and (sf.`season_code` = '-1' or sf.`season_code`  = \"" + season_code + "\")";
        List<Map<String, Object>> seasonFilterMap = productService.executeSQL(seasonFilterSQL);
        if (seasonFilterMap != null && seasonFilterMap.size() > 0) {
            throw new FilterException("season_filter_msg:" + JSONObject.toJSONString(seasonFilterMap));
        }

        // 判断Brand是否需要
        String brandFilterSQL = " select * from `brand_filter`  bf " + " where bf.`enabled`  = 1 and bf.`vendor_id`  =" + vendorOptions.getVendorId()
                + " and (bf.`brand_id` = '-1' or bf.`brand_id`  = '" + brand_id + "')";
        List<Map<String, Object>> brandFilterMap = productService.executeSQL(brandFilterSQL);
        if (brandFilterMap != null && brandFilterMap.size() > 0) {
            throw new FilterException("brand_filter_msg:" + JSONObject.toJSONString(brandFilterMap));
        }
    }

    public void setSku(Connection conn) throws Exception {
        if (this.stockOptions != null && this.stockOptions.size() > 0) {
            for (StockOption stockOption : stockOptions) {

                logger.info("ApiUpdateProductService,setSku,updateStock,stockOption:" + JSONObject.toJSONString(stockOption));
                ApiUpdateStockSerivce apiUpdateStockSerivce = new ApiUpdateStockSerivce();
                Map<String, Object> resultMap = apiUpdateStockSerivce.updateStock(stockOption, conn);
                logger.info("ApiUpdateProductService,setSku,updateStock,resultMap:" + JSONObject.toJSONString(resultMap));

                if (resultMap != null && !resultMap.get("status").toString().equals("1")) {
                    if (resultMap.get("warningMaps") != null) {
                        List<Map<String, Object>> warningList = (List<Map<String, Object>>) resultMap.get("warningMaps");
                        this.warningList.addAll(warningList);
                    } else {
                        this.warningList.add(resultMap);
                    }
                }
            }
        }
    }

    public void setProductInfo(Connection conn) throws Exception {
        CategoryProductInfoService categoryProductInfoService = new CategoryProductInfoService(conn);
        Map<String, Object> categoryProductInfoMap = new HashMap<>();
        categoryProductInfoMap.put("enabled", EnabledType.USED);
        categoryProductInfoMap.put("category_id", this.uProduct.getCategory_id());
        List<CategoryProductInfo> categoryProductInfoList = categoryProductInfoService.getCategoryProductInfoListByCondition(categoryProductInfoMap);
        if (categoryProductInfoList != null && categoryProductInfoList.size() > 0) {
            CategoryProductInfo categoryProductInfo = categoryProductInfoList.get(0);
            ProductInfoService productInfoService = new ProductInfoService(conn);

            Map<String, Object> productInfoConditions = new HashMap<>();
            productInfoConditions.put("category_prodcut_info_id", categoryProductInfo.category_product_info_id);
            productInfoConditions.put("product_id", this.uProduct.getProduct_id());
            productInfoConditions.put("enabled", EnabledType.USED);
            List<ProductInfo> productInfos = productInfoService.getProductInfoByCondition(productInfoConditions);

            if (productInfos != null && productInfos.size() > 0) {
                ProductInfo productInfo = productInfos.get(0);
                productInfo.weight_amount = new BigDecimal(productOptions.getWeight());
                productInfo.length_amount = new BigDecimal(productOptions.getLength());
                productInfo.width_amount = new BigDecimal(productOptions.getWeight());
                productInfo.height_amount = new BigDecimal(productOptions.getHeigit());
                productInfoService.updateProductInfoById(productInfo);
                logger.info("ApiUpdateProductService,setProductInfo,productInfo:" + JSONObject.toJSONString(productInfo));
            }
        }
    }

    private void setProduct(Connection conn) throws Exception {
        ProductService productService = new ProductService(conn);
        Product product = this.getProduct(conn);
        logger.info("ApiUpdateProductService,setProduct,start,updateProduct,product:" + JSONObject.toJSONString(product));
        product.last_check = new Date();

        /*// updateProduct, brand不一致则报error，error_data_can_not_find_mapping，不修改数据。而且reprocess也不处理
        String brand_id = productOptions.getBrandId();
        if(StringUtils.isNotBlank(brand_id) && product.vendor_id == 32 && product.brand_id != Long.parseLong(brand_id)) {
            product.setBrand_id(Long.parseLong(brand_id));
            IPriceService iPriceService = new PriceServiceImpl();
            iPriceService.synProductPriceRule(product,product.getMin_retail_price(),conn);
        }*/
        productOptions.setVendor_id(product.getVendor_id());
        ApiCommonUtils.sortProductOptionsImage(productOptions);

        if (product.vendor_id == 26L) {
            if (productService.isClothing(product.category_id)) {
                try {
                    List<String> originList = JSONArray.parseArray(productOptions.getCoverImg(), String.class);
                    String one = originList.get(0);
                    originList.remove(0);
                    originList.add(one);
                    productOptions.setCoverImg(new Gson().toJson(originList));
                    productOptions.setDescImg(new Gson().toJson(originList));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Image：不予更新。
        String cover_img = product.getCover_img();
        boolean noImg = this.getNoImg(conn);
        if (StringUtils.isBlank(cover_img) || cover_img.length() < 5) {
            cover_img = productOptions.getCoverImg();
            if (StringUtils.isNotBlank(cover_img) && !noImg) {
                String downImgs = ApiCommonUtils.downloadImgs(cover_img);
                product.cover_img = downImgs;
                product.description_img = downImgs;
            }
        } else {
            VendorService vendorService = new VendorService(conn);
            Vendor vendor = vendorService.getVendorByVendorId(product.getVendor_id());

            if (vendor.getCover_image_flag() && !noImg) {
                try {
                    String apiImg = productOptions.getCoverImg();
                    String coverImg = product.getCover_img();
                    List<String> apiImgList = JSONArray.parseArray(apiImg, String.class);
                    List<String> coverImgList = JSONArray.parseArray(coverImg, String.class);

                    if (apiImgList.size() > coverImgList.size()) {
                        String downImgs = ApiCommonUtils.downloadImgs(apiImg);
                        if (product.status == 1 || product.status == 5) {
                            product.cover_img = downImgs;
                            product.description_img = downImgs;
                            logger.info("ApiUpdateProductService,setProduct,updateImg,coverImg:" + coverImg + ",downImgs:" + downImgs + ",product:" + JSONObject
                                    .toJSONString(product));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info("ApiUpdateProductService,setProduct,Error:" + e);
                }
            }

            /** FM 接口特殊判断*/
            if (product.vendor_id == 17 && product.status == 1) {
                try {
                    //only one
                    List<String> apiImgList = JsonTransformUtil.readValue(productOptions.getCoverImg(), ArrayList.class);
                    String apiImage = apiImgList.get(0);
                    //                    logger.info("Product [" + product.product_code + "] ");
                    Map<String, Object> productImages = productService.selProductImage(product.product_id);
                    String image = "[]";
                    String oss_image = "[]";
                    if (productImages != null) {
                        image = productImages.get("image") == null ? "[]" : productImages.get("image").toString();

                    }
                    List<Map<String, String>> imgList = JsonTransformUtil.readValue(image, ArrayList.class);
                    if (imgList == null) {
                        imgList = new ArrayList<>();
                    }
                    boolean existed = false;
                    for (Map<String, String> imageEntry : imgList) {
                        if (imageEntry.get("src_image").equals(apiImage)) {
                            existed = true;
                        }
                    }

                    if (!existed) {

                        String ossImage = ApiCommonUtils.downloadSingleImg(apiImage);
                        Map<String, String> newImage = new HashMap<>();
                        newImage.put("src_image", apiImage);
                        newImage.put("oss_image", ossImage);
                        imgList.add(newImage);

                        imgList.sort((a, b) -> {
                            String srcImageA = a.get("src_image");
                            String srcImageB = b.get("src_image");

                            String[] retA = srcImageA.split("/");
                            String[] retB = srcImageB.split("/");
                            String srcImageAName = "", srcImageBName = "";
                            if (retA.length > 1) {
                                srcImageAName = retA[retA.length - 1];
                            }
                            if (retB.length > 1) {
                                srcImageBName = retB[retB.length - 1];
                            }

                            if (srcImageAName.length() != srcImageBName.length()) {
                                return srcImageAName.length() > srcImageBName.length() ? 1 : -1;
                            }

                            logger.info("ret : " + srcImageAName.compareTo(srcImageBName));

                            return srcImageAName.compareTo(srcImageBName);
                        });

                        productService.insertProductImage(product.product_id, JsonTransformUtil.toJson(imgList));

                        List<String> ossImageList = new ArrayList<>();
                        for (Map<String, String> imageEntry : imgList) {
                            ossImageList.add(imageEntry.get("oss_image"));
                        }
                        oss_image = JsonTransformUtil.toJson(ossImageList);

                        if (StringUtils.isNotBlank(oss_image) && oss_image.length() > 5) {
                            product.cover_img = oss_image;
                            product.description_img = oss_image;
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("ApiUpdateProductService,FM,setProduct,Error:" + e);
                }
            }
        }

        // 新数据为空或者不能找到season code Mapping则报Error不更新已有season code，但其他字段继续更新。判断只有当新season比现有season新时才更新。
        String seasonCode = productOptions.getSeasonCode();
        if (StringUtils.isNotBlank(seasonCode) && !seasonCode.equals(product.getSeason_code())) {
            /* update by yf 12/14*/
            boolean updateSeasonFlag = false;
            String newSeasonCode = seasonCode;
            String oldSeasonCode = product.getSeason_code();

            String selSeasonCodeSQL =
                    "select * from `season`  s  where s.`season_code`  in(\"" + newSeasonCode + "\",\"" + oldSeasonCode + "\") and s.`enabled`  = 1";
            List<Map<String, Object>> seasonMapList = productService.executeSQL(selSeasonCodeSQL);
            logger.info("ApiUpdateProductService,seasonMapList:" + JSONObject.toJSONString(seasonMapList));

            if (seasonMapList != null && seasonMapList.size() > 0) {
                int oldSort = 0;
                int newSort = 0;

                for (Map<String, Object> map : seasonMapList) {
                    if (map.get("season_code") != null && map.get("season_code").toString().equals(oldSeasonCode)) {
                        oldSort = Integer.parseInt(map.get("season_sort").toString());
                    } else if (map.get("season_code") != null && map.get("season_code").toString().equals(newSeasonCode)) {
                        newSort = Integer.parseInt(map.get("season_sort").toString());
                    }
                }

                if (newSort > oldSort) {
                    updateSeasonFlag = true;
                }

                if ((newSeasonCode.equalsIgnoreCase("CarryOver") || oldSeasonCode.equalsIgnoreCase("CarryOver")) && !newSeasonCode.equalsIgnoreCase(
                        oldSeasonCode) && newSort != 0) {
                    if (!oldSeasonCode.equals("18SS") && !oldSeasonCode.equals("18FW")) {
                        updateSeasonFlag = true;
                    }
                }

            }
            /* update by yf 12/14*/

            if (updateSeasonFlag) {
                product.setSeason_code(seasonCode);
                String sql = "update product set preview_im_price = null where product_id =" + product.getProduct_id();
                productService.updateBySQL(sql);
                logger.info("ApiUpdateProductService,updatePreviewImPrice,sql:" + sql);

                BigDecimal retailPrice = new BigDecimal(productOptions.getSalePrice());
                int rs = ApiCommonUtils.ifUpdatePrice(product.getMax_retail_price(), retailPrice);

                // 当season_code发生变化，买手店价格和product.retail_price相同（0），或者超出20%，重新同步价格（2），
                if (rs == 0 || rs == 2) {
                    IPriceService iPriceService = new PriceServiceImpl();
                    logger.info("ApiUpdateProductService,setProduct,seasonCodeChange:" + JSONObject.toJSONString(product) + ",retailPrice:" + retailPrice);
                    iPriceService.synProductPriceRule(product, product.getMin_retail_price(), conn);
                }
            }
        }

        // 获取新的BrandID和ColorCode
        String newBrandCode = productOptions.getBrandCode();
        String newColorCode = productOptions.getColorCode();

        String oldBrandCode = product.getDesigner_id();
        String oldColorCode = product.getColor_code();

        /*if(StringUtils.isBlank(oldBrandCode)) {
            product.designer_id = newBrandCode;
        }

        if(StringUtils.isBlank(oldColorCode)) {
            product.color_code = newColorCode;
        }*/

        // 如果消息重置时,BrandID和ColorCode不为空,直接更新
        /*if(modify) {
            if(StringUtils.isNotBlank(newBrandCode)) {
                product.designer_id = newBrandCode;
            }

            if(StringUtils.isNotBlank(newColorCode)) {
                product.color_code = newColorCode;
            }
        } else { // 非消息重置时,BrandID和ColorCode和原来不一致或者为空报警告
            if(StringUtils.isBlank(newBrandCode) || !newBrandCode.equalsIgnoreCase(oldBrandCode)) {
                this.setWarning(ApiErrorTypeEnum.errorType.error_BrandID_change,"BrandID",newBrandCode);
            }

            if(oldColorCode.equals("#") && StringUtils.isNotBlank(newColorCode)) {
                product.color_code = newColorCode;
            } else if(StringUtils.isBlank(newColorCode) || !newColorCode.equalsIgnoreCase(oldColorCode)) {
                this.setWarning(ApiErrorTypeEnum.errorType.error_ColorCode_change,"ColorCode",newColorCode);
            }
        }*/

        // designer_id和Color_code接口里有改变直接覆盖修改，并报Warning。
        if (StringUtils.isBlank(newBrandCode) || !newBrandCode.equalsIgnoreCase(oldBrandCode)) {
            this.setWarning(ApiErrorTypeEnum.errorType.error_BrandID_change, "BrandID", newBrandCode);

        }

        if (StringUtils.isBlank(newColorCode) || !newColorCode.equalsIgnoreCase(oldColorCode)) {
            this.setWarning(ApiErrorTypeEnum.errorType.error_ColorCode_change, "ColorCode", newColorCode);

        }

        if ((StringUtils.isNotBlank(newBrandCode) && !newBrandCode.equalsIgnoreCase(oldBrandCode)) || (StringUtils.isNotBlank(newColorCode)
                && !newColorCode.equalsIgnoreCase(oldColorCode))) {
            if (product.spu_id != null) {
                logger.info("Try to unbind product [" + product.product_id + "] -- [" + product.spu_id + "]");
                unbindProductSpu(product.product_id, product.spu_id);
                product.spu_id = null;
            }

        }

        if (StringUtils.isNotBlank(newBrandCode)) {
            product.designer_id = newBrandCode;
        }
        if (StringUtils.isNotBlank(newColorCode)) {
            product.color_code = newColorCode;
        }

        this.updateProductProperty(conn, product.getProduct_id(), ProductPropertyEnumKeyName.BrandID.getCode(), product.getDesigner_id());
        this.updateProductProperty(conn, product.getProduct_id(), ProductPropertyEnumKeyName.ColorCode.getCode(), product.getColor_code());
        this.updateProductProperty(conn, product.getProduct_id(), ProductPropertyEnumKeyName.CarryOver.getCode(), productOptions.getCarryOver());

        /*if ((productOptions.vendor_id == 12L || productOptions.vendor_id == 37L || productOptions.vendor_id == 38L || productOptions.vendor_id == 39L
                || productOptions.vendor_id == 40L || productOptions.vendor_id == 41L) && product.status == 1) {
            String img = productOptions.getCoverImg();
            if (StringUtils.isNotBlank(cover_img)) {
                String downImgs = ApiCommonUtils.downloadImgs(img);
                product.cover_img = downImgs;
                product.description_img = downImgs;
            }
        }*/

        /** 只发一次后面注释*//*
        if(product.vendor_id == 32L) {
            if(StringUtils.isNotBlank(newColorCode)) {
                product.color_code = newColorCode;
                this.updateProductProperty(conn,product.getProduct_id(),ProductPropertyEnumKeyName.ColorCode.getCode(),newColorCode);
            }
        }*/
        /** 只发一次后面注释
         if(product.getVendor_id().intValue() == 24 && StringUtils.isNotBlank(productOptions.getCategoryId())) {
         product.category_id = Long.parseLong(productOptions.getCategoryId());
         }
         * 只发一次后面注释 */

        /*// Lungolivigno
        if(product.getVendor_id().intValue() == 33 && StringUtils.isNotBlank(productOptions.getCategoryId())) {
            product.category_id = Long.parseLong(productOptions.getCategoryId());
        }*/

        /*if (product.vendor_id == 37L && product.status == 38L) {
            String srcImages = productOptions.getCoverImg();
            if (StringUtils.isNotBlank(srcImages)) {
                String downImgs = ApiCommonUtils.downloadImgs(srcImages);
                product.cover_img = downImgs;
                product.description_img = downImgs;
            }
        }*/

        if (product.getVendor_id().intValue() == 20 && StringUtils.isNotBlank(productOptions.getCategoryId())) {
            product.category_id = Long.parseLong(productOptions.getCategoryId());
            IPriceService iPriceService = new PriceServiceImpl();
            iPriceService.synProductPriceRule(product, product.getMin_retail_price(), conn);
            logger.info("apartment 修改product :" + new Gson().toJson(product));
        }

        if (StringUtils.isNotBlank(productOptions.getError_type()) && modify) {
            if (StringUtils.isNotBlank(productOptions.getBrandId()) && productOptions.getError_type().equals(
                    ApiErrorTypeEnum.errorType.error_Brand_change.getCode()) && product.getBrand_id() != Long.parseLong(productOptions.getBrandId())) {
                logger.info("ApiUpdateProductService,setProduct,categoryChange,sBrand:" + product.brand_id + ",eBrand_id:" + productOptions.getBrandId()
                        + ",product:" + product.getProduct_id());
                product.brand_id = Long.parseLong(productOptions.getBrandId());
                IPriceService iPriceService = new PriceServiceImpl();
                iPriceService.synProductPriceRule(product, product.getMin_retail_price(), conn);
            }

            if (StringUtils.isNotBlank(productOptions.getCategoryId()) && productOptions.getError_type().equals(
                    ApiErrorTypeEnum.errorType.error_Category_change.getCode()) && product.getCategory_id() != Long.parseLong(productOptions.getCategoryId())) {
                logger.info(
                        "ApiUpdateProductService,setProduct,categoryChange,sCategory:" + product.category_id + ",eCategory:" + productOptions.getCategoryId()
                                + ",product:" + product.getProduct_id());
                product.category_id = Long.parseLong(productOptions.getCategoryId());
                IPriceService iPriceService = new PriceServiceImpl();
                iPriceService.synProductPriceRule(product, product.getMin_retail_price(), conn);
            }
        }

        /* update by yf 2018/02/26 FM , Tony2 , AD , NG  */
        if (product.vendor_id == 17L || product.vendor_id == 43L || product.vendor_id == 22L || product.vendor_id == 9L) {
            if (product.season_code.equalsIgnoreCase("17SS")) {
                product.setSeason_code("Closeout");
                String sql = "update product set preview_im_price = null where product_id =" + product.getProduct_id();
                productService.updateBySQL(sql);

                logger.info("17ssToCloseout,product:" + JSONObject.toJSONString(product));
                IPriceService iPriceService = new PriceServiceImpl();
                iPriceService.synProductPriceRule(product, product.getMin_retail_price(), conn);
            }
        }
        /* update by yf 2018/02/26 */

        product.status = null;
        productService.updateProduct(product);
        this.saveBoutiqueImage(product, conn);
        logger.info("ApiUpdateProductService,setProduct,end,updateProduct,product:" + JSONObject.toJSONString(product));
    }

    private boolean unbindProductSpu(Long productId, Long spuId) {
        String baseUrl = Facility.getInstance().getMicroProperties().getBaseUrl();
        if (StringUtil.isEmpty(HttpUtils.getToken())) {
            login(baseUrl);
        }
        Map<String, Object> response = sendUnbindRequest(baseUrl, productId, spuId);
        if (Integer.parseInt(response.get("status").toString()) == 401) {
            login(baseUrl);
            response = sendUnbindRequest(baseUrl, productId, spuId);
        }

        if (Integer.parseInt(response.get("status").toString()) != 200) {
            logger.error("Fail to unbind product [" + productId + "] -- [" + spuId + "] : " + response);

            return false;
        }
        logger.info("Success to unbind product [" + productId + "] -- [" + spuId + "]");
        return true;
    }

    private Map<String, Object> sendUnbindRequest(String baseUrl, Long productId, Long spuId) {
        String url = baseUrl + "/product/spus/" + spuId + "/products/" + productId;
        Map<String, Object> response = HttpUtils.httpPut(url, HttpUtils.getToken(), "");
        return response;
    }

    private void login(String baseUrl) {
        String loginUrl = baseUrl + "/auth/login";
        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("email", Facility.getInstance().getMicroProperties().getUsername());
        loginBody.put("password", Facility.getInstance().getMicroProperties().getPassword());
        Map<String, Object> loginResp = HttpUtils.httpPost2(loginUrl, JsonTransformUtil.toJson(loginBody));
        if (loginResp.get("resultMessage") != null) {
            Map<String, Object> resultMessage = JsonTransformUtil.readValue(loginResp.get("resultMessage").toString(), HashMap.class);
            String token = (String) resultMessage.get("token");

            if (token != null) {
                HttpUtils.setToken(token);
            }
        }
    }

    private void checkMappingParams(Connection conn) throws Exception {
        ProductService productService = new ProductService(conn);
        Product product = this.getProduct(conn);
        String boutique_category_id = escape(StringUtils.trim(productOptions.getCategory_boutique_id()));
        String category1 = escape(StringUtils.trim(productOptions.getCategory1()));
        String category2 = escape(StringUtils.trim(productOptions.getCategory2()));
        String category3 = escape(StringUtils.trim(productOptions.getCategory3()));
        String brandName = escape(StringUtils.trim(productOptions.getBrandName()));
        String seasonCode = escape(StringUtils.trim(productOptions.getSeasonCode()));
        String mappingSeason = "";

        productOptions.setCategory_boutique_id(boutique_category_id);
        productOptions.setCategory1(category1);
        productOptions.setCategory2(category2);
        productOptions.setCategory3(category3);
        productOptions.setBrandName(brandName);
        productOptions.setSeasonCode(seasonCode);

        // get category
        if (StringUtils.isNotBlank(boutique_category_id)) {
            Map<String, Object> categoryMap = productService.getBoutiqueCategory(vendorOptions.getVendorId().toString(), boutique_category_id);
            if (categoryMap != null) {
                productOptions.setCategoryId(categoryMap.get("category_id").toString());
            }
        }
        if (StringUtils.isNotBlank(productOptions.getCategory1()) && StringUtils.isNotBlank(productOptions.getCategory2()) && StringUtils.isNotBlank(
                productOptions.getCategory3())) {
            Map<String, Object> categoryMap = productService.getCategory(category1, category2, category3);
            if (categoryMap != null) {
                productOptions.setCategoryId(categoryMap.get("category_id").toString());
            } else {
                categoryMap = productService.getThreeCategory(vendorOptions.getVendorId().toString(), category1, category2, category3);
                if (categoryMap != null) {
                    productOptions.setCategoryId(categoryMap.get("category_id").toString());
                }
            }
        }
        if (StringUtils.isBlank(productOptions.getCategoryId()) && StringUtils.isBlank(category3) && StringUtils.isNotBlank(productOptions.getCategory1())
                && StringUtils.isNotBlank(productOptions.getCategory2())) {
            String categoryId = productService.getDeafultCategory(vendorOptions.getVendorId(), category1, category2);
            productOptions.setCategoryId(categoryId);
        }

        if (vendorOptions.getVendorId() == 42 && StringUtils.isBlank(productOptions.getCategoryId()) && StringUtils.isNotBlank(category3)
                && StringUtils.isNotBlank(category1) && StringUtils.isBlank(category2)) {
            Map<String, Object> categoryMap = productService.getCategoryWithoutC2(vendorOptions.getVendorId().toString(), category1, category3);
            if (categoryMap != null) {
                productOptions.setCategoryId(categoryMap.get("category_id").toString());
            }
        }
        // get brand
        if (StringUtils.isNotBlank(brandName)) {
            Map<String, Object> brandMap = productService.getBrand(brandName);
            if (brandMap != null) {
                productOptions.setBrandId(brandMap.get("brand_id").toString());
                productOptions.setBrand_name(brandMap.get("english_name").toString());
            } else {
                brandMap = productService.getBrandMapping(brandName);
                if (brandMap != null) {
                    productOptions.setBrandId(brandMap.get("brand_id").toString());
                    productOptions.setBrand_name(brandMap.get("english_name").toString());
                }
            }
        }

        // get season
        if (StringUtils.isNotBlank(seasonCode)) {
            Map<String, Object> seasonMap = productService.getSeason(seasonCode);
            if (seasonMap != null) {
                mappingSeason = seasonMap.get("season_code").toString();
                productOptions.setSeasonCode(mappingSeason);
            }
        }

        Map<String, Object> mappingCategory = new HashMap<>();
        mappingCategory.put("boutique_category_id", boutique_category_id);
        mappingCategory.put("category1", category1);
        mappingCategory.put("categroy2", category2);
        mappingCategory.put("category3", category3);

        int cId = -1;
        int bId = -1;
        try {
            cId = Integer.parseInt(productOptions.getCategoryId());
        } catch (Exception e) {
            cId = -1;
        }
        try {
            bId = Integer.parseInt(productOptions.getBrandId());
        } catch (Exception e) {
            bId = -1;
        }

        if (StringUtils.isBlank(productOptions.getCategoryId())) {
            this.setWarning(ApiErrorTypeEnum.errorType.error_data_can_not_find_mapping, "category", JSONObject.toJSONString(mappingCategory));
            logger.info("ApiUpdateProductService,checkMappingParams,category,categoryIsNull,productOptions:" + new Gson().toJson(productOptions));
        } else if (!productService.ifCategory(productOptions.getCategoryId())) {
            this.setWarning(ApiErrorTypeEnum.errorType.error_data_can_not_find_mapping, "category", JSONObject.toJSONString(mappingCategory));
            logger.info("ApiUpdateProductService,checkMappingParams,category,categoryIsError,productOptions:" + new Gson().toJson(productOptions));
        } else if (cId != product.getCategory_id().intValue()) {
            this.setWarning(ApiErrorTypeEnum.errorType.error_Category_change, "category", JSONObject.toJSONString(mappingCategory));
            logger.info("ApiUpdateProductService,checkMappingParams,category,categoryIsChange,productOptions:" + new Gson().toJson(productOptions));
            //            productOptions.setError_type(ApiErrorTypeEnum.errorType.error_Category_change.getCode());
            error_type = ApiErrorTypeEnum.errorType.error_Category_change.getCode();
        }

        if (StringUtils.isBlank(productOptions.getBrandId())) {
            this.setWarning(ApiErrorTypeEnum.errorType.error_data_can_not_find_mapping, "brand", brandName);
            logger.info("ApiUpdateProductService,checkMappingParams,brand,brandIsNull,productOptions:" + new Gson().toJson(productOptions));
        } else if (!productService.ifBrand(productOptions.getBrandId())) {
            this.setWarning(ApiErrorTypeEnum.errorType.error_data_can_not_find_mapping, "brand", brandName);
            logger.info("ApiUpdateProductService,checkMappingParams,brand,brandIsError,productOptions:" + new Gson().toJson(productOptions));
        } else if (bId != product.getBrand_id()) {
            this.setWarning(ApiErrorTypeEnum.errorType.error_Brand_change, "brand", brandName);
            logger.info("ApiUpdateProductService,checkMappingParams,brand,brandIsChange,productOptions:" + new Gson().toJson(productOptions));
            //            productOptions.setError_type(ApiErrorTypeEnum.errorType.error_Brand_change.getCode());
            error_type = ApiErrorTypeEnum.errorType.error_Brand_change.getCode();
        }

        if (StringUtils.isBlank(mappingSeason)) {
            this.setWarning(ApiErrorTypeEnum.errorType.error_data_can_not_find_mapping, "season", seasonCode);
        }
    }

    private void checkProductParams(Connection conn) throws Exception {
        String product_code = escape(StringUtils.trim(productOptions.getCode()));
        String designer_id = escape(StringUtils.trim(productOptions.getBrandCode()));
        String brand_name = escape(StringUtils.trim(productOptions.getBrandName()));
        String color_code = escape(StringUtils.trim(productOptions.getColorCode()));
        String cover_img = escape(StringUtils.trim(productOptions.getCoverImg()));
        String price = escape(StringUtils.trim(productOptions.getSalePrice()));
        String product_name = escape(StringUtils.trim(productOptions.getName()));
        String product_desc = escape(StringUtils.trim(productOptions.getDesc()));
        String carryOver = escape(StringUtils.trim(productOptions.getCarryOver()));
        String colorDesc = escape(StringUtils.trim(productOptions.getColorDesc()));
        String desc = escape(StringUtils.trim(productOptions.getDesc()));
        String composition = escape(StringUtils.trim(productOptions.getComposition()));
        String madeIn = escape(StringUtils.trim(productOptions.getMadeIn()));
        String sizeFit = escape(StringUtils.trim(productOptions.getSizeFit()));
        String weight = escape(StringUtils.trim(productOptions.getWeight()));
        String length = escape(StringUtils.trim(productOptions.getLength()));
        String width = escape(StringUtils.trim(productOptions.getWidth()));
        String height = escape(StringUtils.trim(productOptions.getHeigit()));

        Long vendor_id = vendorOptions.getVendorId();
        productOptions.setCode(product_code);
        productOptions.setBrandCode(designer_id);
        productOptions.setBrandName(brand_name);
        productOptions.setColorCode(color_code);
        productOptions.setCoverImg(cover_img);
        productOptions.setSalePrice(price);
        productOptions.setName(product_name);
        productOptions.setDesc(product_desc);
        productOptions.setCarryOver(carryOver);
        productOptions.setColorDesc(colorDesc);
        productOptions.setDesc(desc);
        productOptions.setComposition(composition);
        productOptions.setMadeIn(madeIn);
        productOptions.setSizeFit(sizeFit);

        if (StringUtils.isBlank(weight)) {
            productOptions.setWeight("1");
        } else {
            productOptions.setWeight(weight);
        }
        if (StringUtils.isBlank(length)) {
            productOptions.setLength("0");
        } else {
            productOptions.setLength(length);
        }
        if (StringUtils.isBlank(width)) {
            productOptions.setWidth("0");
        } else {
            productOptions.setWidth(width);
        }
        if (StringUtils.isBlank(height)) {
            productOptions.setHeigit("0");
        } else {
            productOptions.setHeigit(height);
        }

        if (StringUtils.isNotBlank(productOptions.getModifyPrice()) && productOptions.getModifyPrice().equalsIgnoreCase("1")) {
            modify = true;
        }

        if (StringUtils.isBlank(product_code)) {
            throw new UpdateException("product_code", product_code, ApiErrorTypeEnum.errorType.error_data_is_null);
        }

        if (vendor_id == null) {
            throw new UpdateException("vendor_id", "", ApiErrorTypeEnum.errorType.error_data_is_null);
        }

        Product product = this.getProduct(conn);
        if (product == null) {
            throw new UpdateException("product_code", product_code, ApiErrorTypeEnum.errorType.error_boutique_id_not_exists);
        }

        List<ProductEDSManagement.SkuOptions> skuOptionList = productOptions.getSkus();
        stockOptions = new ArrayList<>();
        if (skuOptionList != null && skuOptionList.size() > 0) {

            for (int i = 0, len = skuOptionList.size(); i < len; i++) {
                ProductEDSManagement.SkuOptions skuOption = skuOptionList.get(i);
                String size = skuOption.getSize();
                String sku_code = skuOption.getBarcodes();
                String quantity = skuOption.getStock();

                StockOption stockOption = new StockOption();
                stockOption.setSizeValue(size);
                stockOption.setSku_code(sku_code);
                stockOption.setQuantity(quantity);
                stockOption.setType(Contants.STOCK_QTY + "");
                stockOption.setVendor_id(vendor_id.toString());
                stockOption.setProductCode(product_code);
                stockOption.setLast_check(productOptions.getLast_check());
                stockOption.setModify(modify);
                stockOption.setBoutique_sku_id(skuOption.getBoutique_sku_id());
                if (i == 0) {
                    stockOption.setPrice(price);
                }
                stockOptions.add(stockOption);
            }

            if (productOptions.getDuplicateSkus() != null && productOptions.getDuplicateSkus().size() > 0) {
                this.setWarning(ApiErrorTypeEnum.errorType.warning_duplicated_skusize, "skus", JSONObject.toJSONString(productOptions.getDuplicateSkus()));
            }
        }
    }



    private boolean getNoImg(Connection conn) throws Exception {
        if (no_img != null) {
            return no_img;
        }

        ProductService productService = new ProductService(conn);
        no_img = productService.getNoImg(productOptions.getBrandName(), vendorOptions.getVendorId());
        return no_img;
    }

    public void updateProductProperty(Connection conn, Long productId, String keyName, String value) throws Exception {
        ProductPropertyService productPropertyService = new ProductPropertyService(conn);

        if (org.apache.commons.lang3.StringUtils.isNotBlank(value)) {
            ProductProperty productProperty = new ProductProperty();
            productProperty.value = value;

            Map<String, Object> productPropertyConditions = new HashMap<>();
            productPropertyConditions.put("product_id", productId);
            productPropertyConditions.put("key_name", keyName);
            productPropertyConditions.put("enabled", EnabledType.USED);

            productPropertyService.updateProductPropertyByCondition(productProperty, productPropertyConditions);
            logger.info("ApiUpdateProductSerice,updateProductPropertyByCondition,productProperty:" + JSONObject.toJSONString(productProperty)
                    + ",productPropertyConditions:" + JSONObject.toJSONString(productPropertyConditions));
        }
    }

    private void setWarning(ApiErrorTypeEnum.errorType errorType, String key, String value) {
        Map<String, Object> warningMap = new HashMap<>();
        warningMap.put("status", StatusType.WARNING);
        warningMap.put("error_enum", errorType);
        warningMap.put("key", key);
        warningMap.put("value", value);
        warningMap.put("info", errorType.getDesc());

        if (warningList == null || warningList.size() == 0) {
            warningList = new ArrayList<>();
        }

        warningList.add(warningMap);
    }

    private void saveBoutiqueImage(Product product, Connection conn) {
        try {
            if (product == null) {
                return;
            }

            BoutiqueImageService boutiqueImageService = new BoutiqueImageService(conn);
            Map<String, Object> condition = new HashMap<>();
            condition.put("product_id", product.getProduct_id());

            BoutiqueImage boutiqueImage = boutiqueImageService.getBoutiqueImageByCondition(condition);
            List<String> coverImgs = JSONArray.parseArray(this.productOptions.getCoverImg(), String.class);

            if (boutiqueImage == null) {
                boutiqueImage = new BoutiqueImage();
                boutiqueImage.setCreated_at(new Date());
                boutiqueImage.setUpdated_at(new Date());
                boutiqueImage.setImage(JsonTransformUtil.toJson(coverImgs));
                boutiqueImage.setProduct_id(product.getProduct_id());
                boutiqueImageService.createBoutiqueImage(boutiqueImage);
            } else {
                List<String> oldImages = JSONArray.parseArray(boutiqueImage.getImage(), String.class);
                List<String> images = new ArrayList<>();
                for (String newImage : coverImgs) {
                    boolean newFlag = true;
                    for (String oldImage : oldImages) {
                        if (newImage.equalsIgnoreCase(oldImage)) {
                            newFlag = false;
                            break;
                        }
                    }
                    if (newFlag) {
                        images.add(newImage);
                    }
                }
                oldImages.addAll(images);
                boutiqueImage.setImage(JsonTransformUtil.toJson(oldImages));
                boutiqueImageService.updateBoutiqueImage(boutiqueImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
