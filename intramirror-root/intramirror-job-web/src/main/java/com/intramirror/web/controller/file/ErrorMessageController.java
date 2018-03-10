package com.intramirror.web.controller.file;

import com.alibaba.fastjson.JSONObject;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.web.mapping.api.IProductMapping;
import com.intramirror.web.mapping.api.IStockMapping;
import com.intramirror.web.mapping.impl.Matgento.MatgentoSynProductMapping;
import com.intramirror.web.mapping.impl.QuadraSynProductMapping;
import com.intramirror.web.mapping.impl.aiduca.AiDucaSynAllStockMapping;
import com.intramirror.web.mapping.impl.aiduca.AiDucaSynProductMapping;
import com.intramirror.web.mapping.impl.atelier.AtelierUpdateByProductMapping;
import com.intramirror.web.mapping.impl.prestashop.PrestashopProductMapping;
import com.intramirror.web.mapping.impl.prestashop.PrestashopStockMapping;
import com.intramirror.web.mapping.impl.shopify.ShopifyProductMapping;
import com.intramirror.web.mapping.vo.StockOption;
import com.intramirror.web.thread.CommonThreadPool;
import com.intramirror.web.thread.UpdateProductThread;
import com.intramirror.web.thread.UpdateStockThread;
import com.intramirror.web.util.ApiDataFileUtils;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.common.utils.FileUtils;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.service.CategoryService;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.MapUtils;

/**
 * Created by dingyifan on 2017/9/20.
 */
@Controller
@RequestMapping("/errormessage")
public class ErrorMessageController {

    // logger
    private static final Logger logger = Logger.getLogger(ErrorMessageController.class);

    @CrossOrigin
    @ResponseBody
    @RequestMapping("/select")
    public JSONObject getMessage(@Param("api_error_processing_id") String api_error_processing_id) {
        try {
            return this.readFile(api_error_processing_id);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("ErrorMessageControllerGetMessage,errorMessage:" + ExceptionUtils.getExceptionDetail(e));
        }
        return null;
    }

    @Autowired
    private QuadraSynProductMapping quadraSynProductMapping;

    @Resource(name = "edsUpdateByStockMapping")
    private IStockMapping edsUpdateByStockMapping;

    @Resource(name = "edsUpdateByProductMapping")
    private IProductMapping edsUpdateByProductMapping;

    @Resource(name = "cloudStoreProductMapping")
    private IProductMapping cloudStoreProductMapping;

    @Resource(name = "cloudStoreUpdateByStockMapping")
    private IStockMapping cloudStoreUpdateByStockMapping;

    @Resource(name = "atelierUpdateByProductMapping")
    private AtelierUpdateByProductMapping atelierUpdateByProductMapping;

    @Resource(name = "atelierUpdateByStockMapping")
    private IStockMapping atelierUpdateByStockMapping;

    @Resource(name = "filippoSynStockMapping")
    private IStockMapping filippoSynStockMapping;

    @Resource(name = "filippoSynProductMapping")
    private IProductMapping filippoSynProductMapping;

    @Resource(name = "xmagSynProductAllMapper")
    private IProductMapping xmagSynProductAllMapper;

    @Resource(name = "xmagSynAllStockMapping")
    private IStockMapping xmagSynAllStockMapping;

    @Resource(name = "coltoriProductMapping")
    private IProductMapping coltoriProductMapping;

    @Resource(name = "coltoriStockMapping")
    private IStockMapping coltoriStockMapping;

    @Autowired
    private AiDucaSynAllStockMapping aiDucaSynAllStockMapping;

    @Autowired
    private AiDucaSynProductMapping aiDucaSynProductMapping;

    @Resource(name = "shopifyProductMapping")
    private ShopifyProductMapping shopifyProductMapping;

    @Resource(name = "matgentoSynProductMapping")
    private MatgentoSynProductMapping matgentoSynProductMapping;

    @Resource(name = "prestashopProductMapping")
    private PrestashopProductMapping prestashopProductMapping;

    @Resource(name = "prestashopStockMapping")
    private PrestashopStockMapping prestashopStockMapping;

    private static final int threadNum = 5;

    private ProductEDSManagement productEDSManagement = new ProductEDSManagement();

    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    @CrossOrigin
    @ResponseBody
    @RequestMapping("/processing")
    public Map<String, Object> processing(@Param(value = "data") String data) {
        MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());
        try {
            logger.info("ErrorMessageControllerProcessing,inputParams,data:" + data);
            if (StringUtils.isBlank(data)) {
                return mapUtils.putData("status", StatusType.FAILURE).putData("info", "data is null").getMap();
            }

            // 查询api_error_processing
            List<Map<String, Object>> selectProcessMaps = this.getApiErrorProcessMap(data);

            for (Map<String, Object> processMap : selectProcessMaps) {
                String api_error_processing_id = processMap.get("api_error_processing_id").toString();
                String vendor_id = processMap.get("vendor_id").toString();
                String name = processMap.get("name").toString();
                logger.info("ErrorMessageControllerProcessing,startProcessing,processMap:" + JSONObject.toJSON(processMap));

                // 读取文件
                JSONObject jsonObject = this.readFile(api_error_processing_id);
                logger.info("ErrorMessageControllerProcessing,readFile,jsonObject:" + jsonObject.toJSONString() + ",api_error_processing_id:"
                        + api_error_processing_id);

                Map<String, Object> originDataMap = jsonObject.getObject("originData", Map.class);
                String error_type = jsonObject.getString("error_type");
                ApiDataFileUtils apiDataFileUtils = jsonObject.getObject("apiDataFileUtils", ApiDataFileUtils.class);
                //                ProductEDSManagement.ProductOptions productOptions = jsonObject.getObject("productOptions", ProductEDSManagement.ProductOptions.class);
                //                ProductEDSManagement.VendorOptions vendorOptions = jsonObject.getObject("vendorOptions", ProductEDSManagement.VendorOptions.class);
                //                StockOption stockOption = jsonObject.getObject("stockOption", StockOption.class);

                // quadra
                if (vendor_id.equals("19")) {
                    if (name.equals("product_all_update") || name.equals("product_delta_update_bydate")) {
                        ProductEDSManagement.ProductOptions productOptions = quadraSynProductMapping.mapping(originDataMap);
                        ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                        vendorOptions.setVendorId(Long.parseLong(vendor_id));
                        productOptions.setModifyPrice("1");
                        productOptions.setError_type(error_type);
                        CommonThreadPool.execute(name, executor, threadNum,
                                new UpdateProductThread(productOptions, vendorOptions, apiDataFileUtils, originDataMap));
                    }
                }

                // eds nugnes
                if (vendor_id.equals("9")) {
                    if (name.equals("stock_all_update")) {
                        StockOption stockOption = edsUpdateByStockMapping.mapping(originDataMap);
                        CommonThreadPool.execute(name, executor, threadNum, new UpdateStockThread(stockOption, apiDataFileUtils, originDataMap));
                    } else if (name.equals("product_delta_create") || name.equals("product_delta_create_bydate") || name.equals("product_all_update")) {
                        ProductEDSManagement.ProductOptions productOptions = edsUpdateByProductMapping.mapping(originDataMap);
                        ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                        vendorOptions.setVendorId(Long.parseLong(vendor_id));
                        productOptions.setModifyPrice("1");
                        productOptions.setError_type(error_type);
                        CommonThreadPool.execute(name, executor, threadNum,
                                new UpdateProductThread(productOptions, vendorOptions, apiDataFileUtils, originDataMap));
                    }
                }

                // eds baseblu
                if (vendor_id.equals("21")) {
                    if (name.equals("stock_all_update")) {
                        StockOption stockOption = edsUpdateByStockMapping.mapping(originDataMap);
                        CommonThreadPool.execute(name, executor, threadNum, new UpdateStockThread(stockOption, apiDataFileUtils, originDataMap));
                    } else if (name.equals("product_delta_create") || name.equals("product_delta_create_bydate") || name.equals("product_all_update")) {
                        ProductEDSManagement.ProductOptions productOptions = edsUpdateByProductMapping.mapping(originDataMap);
                        ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                        vendorOptions.setVendorId(Long.parseLong(vendor_id));
                        productOptions.setModifyPrice("1");
                        productOptions.setError_type(error_type);
                        CommonThreadPool.execute(name, executor, threadNum,
                                new UpdateProductThread(productOptions, vendorOptions, apiDataFileUtils, originDataMap));
                    }
                }

                // cloud_store
                if (vendor_id.equals("16") || vendor_id.equals("43")) {
                    if (name.equals("product_all_update")) {
                        originDataMap.put("vendor_id", vendor_id);
                        ProductEDSManagement.ProductOptions productOptions = cloudStoreProductMapping.mapping(originDataMap);
                        ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                        vendorOptions.setVendorId(Long.parseLong(vendor_id));
                        productOptions.setModifyPrice("1");
                        productOptions.setError_type(error_type);
                        CommonThreadPool.execute(name, executor, threadNum,
                                new UpdateProductThread(productOptions, vendorOptions, apiDataFileUtils, originDataMap));
                    } else if (name.equals("product_delta_update")) {
                        if (jsonObject.get("stockOption") == null) {
                            ProductEDSManagement.ProductOptions productOptions = cloudStoreProductMapping.mapping(originDataMap);
                            ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                            vendorOptions.setVendorId(Long.parseLong(vendor_id));
                            productOptions.setModifyPrice("1");
                            productOptions.setError_type(error_type);
                            CommonThreadPool.execute(name, executor, threadNum,
                                    new UpdateProductThread(productOptions, vendorOptions, apiDataFileUtils, originDataMap));
                        } else {
                            StockOption stockOption = cloudStoreUpdateByStockMapping.mapping(originDataMap);
                            CommonThreadPool.execute(name, executor, threadNum, new UpdateStockThread(stockOption, apiDataFileUtils, originDataMap));
                        }
                    }
                }

                // atelier
                if (vendor_id.equals("8") || vendor_id.equals("10") || vendor_id.equals("11") || vendor_id.equals("12") || vendor_id.equals("13") || vendor_id
                        .equals("14") || vendor_id.equals("18") || vendor_id.equals("26") || vendor_id.equals("28") || vendor_id.equals("31") || vendor_id
                        .equals("25") || vendor_id.equals("23") || vendor_id.equals("29") || vendor_id.equals("27") || vendor_id.equals("33") || vendor_id
                        .equals("35")) {
                    if (name.equals("stock_delta_stock")) {
                        StockOption stockOption = atelierUpdateByStockMapping.mapping(originDataMap);
                        CommonThreadPool.execute(name, executor, threadNum, new UpdateStockThread(stockOption, apiDataFileUtils, originDataMap));
                    } else if (name.equals("product_all_update") || name.equals("product_delta_update") || name.equals("product_delta_create")) {
                        originDataMap.put("vendor_id", vendor_id);
                        ProductEDSManagement.ProductOptions productOptions = atelierUpdateByProductMapping.mapping(originDataMap);
                        ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                        vendorOptions.setVendorId(Long.parseLong(vendor_id));
                        productOptions.setModifyPrice("1");
                        productOptions.setError_type(error_type);
                        CommonThreadPool.execute(name, executor, threadNum,
                                new UpdateProductThread(productOptions, vendorOptions, apiDataFileUtils, originDataMap));
                    }
                }

                // filippo
                if (vendor_id.equals("17")) {
                    if (name.equals("stock_delta_update")) {
                        StockOption stockOption = filippoSynStockMapping.mapping(originDataMap);
                        CommonThreadPool.execute(name, executor, threadNum, new UpdateStockThread(stockOption, apiDataFileUtils, originDataMap));
                    } else if (name.equals("product_delta_update") || name.equals("product_all_update")) {
                        ProductEDSManagement.ProductOptions productOptions = filippoSynProductMapping.mapping(originDataMap);
                        ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                        vendorOptions.setVendorId(Long.parseLong(vendor_id));
                        productOptions.setModifyPrice("1");
                        productOptions.setError_type(error_type);
                        CommonThreadPool.execute(name, executor, threadNum,
                                new UpdateProductThread(productOptions, vendorOptions, apiDataFileUtils, originDataMap));
                    }
                }

                // xmag apartment
                if (vendor_id.equals("20")) {
                    if (name.equals("apartment_product_all_update") || name.equals("apartment_product_delta_update")) {
                        ProductEDSManagement.ProductOptions productOptions = xmagSynProductAllMapper.mapping(originDataMap);
                        ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                        vendorOptions.setVendorId(Long.parseLong(vendor_id));
                        productOptions.setModifyPrice("1");
                        productOptions.setError_type(error_type);
                        CommonThreadPool.execute(name, executor, threadNum,
                                new UpdateProductThread(productOptions, vendorOptions, apiDataFileUtils, originDataMap));
                    } else if (name.equals("apartment_stock_all_update")) {
                        StockOption stockOption = xmagSynAllStockMapping.mapping(originDataMap);
                        CommonThreadPool.execute(name, executor, threadNum, new UpdateStockThread(stockOption, apiDataFileUtils, originDataMap));
                    }
                }

                if (vendor_id.equals("37")) {
                    if (name.equals("dolci_product_all_update") || name.equals("dolci_product_delta_update")) {
                        ProductEDSManagement.ProductOptions productOptions = xmagSynProductAllMapper.mapping(originDataMap);
                        ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                        vendorOptions.setVendorId(Long.parseLong(vendor_id));
                        productOptions.setModifyPrice("1");
                        productOptions.setError_type(error_type);
                        CommonThreadPool.execute(name, executor, threadNum,
                                new UpdateProductThread(productOptions, vendorOptions, apiDataFileUtils, originDataMap));
                    } else if (name.equals("dolci_stock_all_update")) {
                        StockOption stockOption = xmagSynAllStockMapping.mapping(originDataMap);
                        CommonThreadPool.execute(name, executor, threadNum, new UpdateStockThread(stockOption, apiDataFileUtils, originDataMap));
                    }
                }

                if (vendor_id.equals("12")) {
                    if (name.equals("mimma_product_all_update") || name.equals("mimma_product_delta_update")) {
                        ProductEDSManagement.ProductOptions productOptions = xmagSynProductAllMapper.mapping(originDataMap);
                        ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                        vendorOptions.setVendorId(Long.parseLong(vendor_id));
                        productOptions.setModifyPrice("1");
                        productOptions.setError_type(error_type);
                        CommonThreadPool.execute(name, executor, threadNum,
                                new UpdateProductThread(productOptions, vendorOptions, apiDataFileUtils, originDataMap));
                    } else if (name.equals("mimma_stock_all_update")) {
                        StockOption stockOption = xmagSynAllStockMapping.mapping(originDataMap);
                        CommonThreadPool.execute(name, executor, threadNum, new UpdateStockThread(stockOption, apiDataFileUtils, originDataMap));
                    }
                }

                if (vendor_id.equals("39")) {
                    if (name.equals("mimma2_product_all_update") || name.equals("mimma2_product_delta_update")) {
                        ProductEDSManagement.ProductOptions productOptions = xmagSynProductAllMapper.mapping(originDataMap);
                        ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                        vendorOptions.setVendorId(Long.parseLong(vendor_id));
                        productOptions.setModifyPrice("1");
                        productOptions.setError_type(error_type);
                        CommonThreadPool.execute(name, executor, threadNum,
                                new UpdateProductThread(productOptions, vendorOptions, apiDataFileUtils, originDataMap));
                    } else if (name.equals("mimma2_stock_all_update")) {
                        StockOption stockOption = xmagSynAllStockMapping.mapping(originDataMap);
                        CommonThreadPool.execute(name, executor, threadNum, new UpdateStockThread(stockOption, apiDataFileUtils, originDataMap));
                    }
                }

                if (vendor_id.equals("38")) {
                    if (name.equals("spinnaker_product_all_update") || name.equals("spinnaker_product_delta_update")) {
                        ProductEDSManagement.ProductOptions productOptions = xmagSynProductAllMapper.mapping(originDataMap);
                        ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                        vendorOptions.setVendorId(Long.parseLong(vendor_id));
                        productOptions.setModifyPrice("1");
                        productOptions.setError_type(error_type);
                        CommonThreadPool.execute(name, executor, threadNum,
                                new UpdateProductThread(productOptions, vendorOptions, apiDataFileUtils, originDataMap));
                    } else if (name.equals("spinnaker_stock_all_update")) {
                        StockOption stockOption = xmagSynAllStockMapping.mapping(originDataMap);
                        CommonThreadPool.execute(name, executor, threadNum, new UpdateStockThread(stockOption, apiDataFileUtils, originDataMap));
                    }
                }

                if (vendor_id.equals("40")) {
                    if (name.equals("spinnakerSanremo_product_all_update") || name.equals("spinnakerSanremo_product_delta_update")) {
                        ProductEDSManagement.ProductOptions productOptions = xmagSynProductAllMapper.mapping(originDataMap);
                        ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                        vendorOptions.setVendorId(Long.parseLong(vendor_id));
                        productOptions.setModifyPrice("1");
                        productOptions.setError_type(error_type);
                        CommonThreadPool.execute(name, executor, threadNum,
                                new UpdateProductThread(productOptions, vendorOptions, apiDataFileUtils, originDataMap));
                    } else if (name.equals("spinnakerSanremo_stock_all_update")) {
                        StockOption stockOption = xmagSynAllStockMapping.mapping(originDataMap);
                        CommonThreadPool.execute(name, executor, threadNum, new UpdateStockThread(stockOption, apiDataFileUtils, originDataMap));
                    }
                }

                if (vendor_id.equals("41")) {
                    if (name.equals("spinnakerPortofino_product_all_update") || name.equals("spinnakerPortofino_product_all_update")) {
                        ProductEDSManagement.ProductOptions productOptions = xmagSynProductAllMapper.mapping(originDataMap);
                        ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                        vendorOptions.setVendorId(Long.parseLong(vendor_id));
                        productOptions.setModifyPrice("1");
                        productOptions.setError_type(error_type);
                        CommonThreadPool.execute(name, executor, threadNum,
                                new UpdateProductThread(productOptions, vendorOptions, apiDataFileUtils, originDataMap));
                    } else if (name.equals("spinnakerPortofino_stock_all_update")) {
                        StockOption stockOption = xmagSynAllStockMapping.mapping(originDataMap);
                        CommonThreadPool.execute(name, executor, threadNum, new UpdateStockThread(stockOption, apiDataFileUtils, originDataMap));
                    }
                }

                if (vendor_id.equals("46")) {
                    if (name.equals("satu_product_all_update") || name.equals("satu_product_delta_update")) {
                        ProductEDSManagement.ProductOptions productOptions = xmagSynProductAllMapper.mapping(originDataMap);
                        ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                        vendorOptions.setVendorId(Long.parseLong(vendor_id));
                        productOptions.setModifyPrice("1");
                        productOptions.setError_type(error_type);
                        CommonThreadPool.execute(name, executor, threadNum,
                                new UpdateProductThread(productOptions, vendorOptions, apiDataFileUtils, originDataMap));
                    } else if (name.equals("satu_stock_all_update")) {
                        StockOption stockOption = xmagSynAllStockMapping.mapping(originDataMap);
                        CommonThreadPool.execute(name, executor, threadNum, new UpdateStockThread(stockOption, apiDataFileUtils, originDataMap));
                    }
                }

                if (vendor_id.equals("47")) {
                    if (name.equals("suite_product_all_update") || name.equals("suite_product_delta_update")) {
                        ProductEDSManagement.ProductOptions productOptions = xmagSynProductAllMapper.mapping(originDataMap);
                        ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                        vendorOptions.setVendorId(Long.parseLong(vendor_id));
                        productOptions.setModifyPrice("1");
                        productOptions.setError_type(error_type);
                        CommonThreadPool.execute(name, executor, threadNum,
                                new UpdateProductThread(productOptions, vendorOptions, apiDataFileUtils, originDataMap));
                    } else if (name.equals("suite_stock_all_update")) {
                        StockOption stockOption = xmagSynAllStockMapping.mapping(originDataMap);
                        CommonThreadPool.execute(name, executor, threadNum, new UpdateStockThread(stockOption, apiDataFileUtils, originDataMap));
                    }
                }

                if (vendor_id.equals("48")) {
                    if (name.equals("gallery_product_all_update") || name.equals("gallery_product_delta_update")) {
                        ProductEDSManagement.ProductOptions productOptions = xmagSynProductAllMapper.mapping(originDataMap);
                        ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                        vendorOptions.setVendorId(Long.parseLong(vendor_id));
                        productOptions.setModifyPrice("1");
                        productOptions.setError_type(error_type);
                        CommonThreadPool.execute(name, executor, threadNum,
                                new UpdateProductThread(productOptions, vendorOptions, apiDataFileUtils, originDataMap));
                    } else if (name.equals("gallery_stock_all_update")) {
                        StockOption stockOption = xmagSynAllStockMapping.mapping(originDataMap);
                        CommonThreadPool.execute(name, executor, threadNum, new UpdateStockThread(stockOption, apiDataFileUtils, originDataMap));
                    }
                }

                // alduca
                if (vendor_id.equals("22")) {
                    if (name.equals("alduca_product_delta_update") || name.equals("alduca_product_all_update")) {
                        ProductEDSManagement.ProductOptions productOptions = aiDucaSynProductMapping.mapping(originDataMap);
                        ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                        vendorOptions.setVendorId(Long.parseLong(vendor_id));
                        productOptions.setModifyPrice("1");
                        productOptions.setError_type(error_type);
                        CommonThreadPool.execute(name, executor, threadNum,
                                new UpdateProductThread(productOptions, vendorOptions, apiDataFileUtils, originDataMap));
                    } else if (name.equals("alduca_stock_all_update")) {
                        StockOption stockOption = aiDucaSynAllStockMapping.mapping(originDataMap);
                        CommonThreadPool.execute(name, executor, threadNum, new UpdateStockThread(stockOption, apiDataFileUtils, originDataMap));
                    }
                }

                // Coltori
                if (vendor_id.equals("24")) {
                    if (name.equals("product_all_update") || name.equals("product_delta_update")) {
                        ProductEDSManagement.ProductOptions productOptions = coltoriProductMapping.mapping(originDataMap);
                        ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                        vendorOptions.setVendorId(Long.parseLong(vendor_id));
                        productOptions.setModifyPrice("1");
                        productOptions.setError_type(error_type);
                        CommonThreadPool.execute(name, executor, threadNum,
                                new UpdateProductThread(productOptions, vendorOptions, apiDataFileUtils, originDataMap));

                    } else if (name.equals("stock_all_update") || name.equals("stock_delta_update")) {
                        StockOption stockOption = coltoriStockMapping.mapping(originDataMap);
                        CommonThreadPool.execute(name, executor, threadNum, new UpdateStockThread(stockOption, apiDataFileUtils, originDataMap));
                    }
                }

                if (vendor_id.equals("32")) {
                    if (name.equals("product_all_update") || name.equals("product_delta_update")) {
                        ProductEDSManagement.ProductOptions productOptions = shopifyProductMapping.mapping(originDataMap);
                        ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                        vendorOptions.setVendorId(Long.parseLong(vendor_id));
                        productOptions.setModifyPrice("1");
                        productOptions.setError_type(error_type);
                        CommonThreadPool.execute(name, executor, threadNum,
                                new UpdateProductThread(productOptions, vendorOptions, apiDataFileUtils, originDataMap));

                    }
                }

                if (vendor_id.equals("34")) {
                    if (name.equals("product_all_update") || name.equals("product_delta_update")) {
                        ProductEDSManagement.ProductOptions productOptions = matgentoSynProductMapping.mapping(originDataMap);
                        ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                        vendorOptions.setVendorId(Long.parseLong(vendor_id));
                        productOptions.setModifyPrice("1");
                        productOptions.setError_type(error_type);
                        CommonThreadPool.execute(name, executor, threadNum,
                                new UpdateProductThread(productOptions, vendorOptions, apiDataFileUtils, originDataMap));
                    }
                }

                if (vendor_id.equals("45")) {
                    if (name.equals("product_all_update") || name.equals("product_delta_update")) {
                        ProductEDSManagement.ProductOptions productOptions = prestashopProductMapping.mapping(originDataMap);
                        ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                        vendorOptions.setVendorId(Long.parseLong(vendor_id));
                        productOptions.setModifyPrice("1");
                        productOptions.setError_type(error_type);
                        CommonThreadPool.execute(name, executor, threadNum,
                                new UpdateProductThread(productOptions, vendorOptions, apiDataFileUtils, originDataMap));
                    }
                }

                if(vendor_id.equals("42")) {
                    if(name.equals("product_all_update") || name.equals("product_delta_update") ) {
                        ProductEDSManagement.ProductOptions productOptions = matgentoSynProductMapping.mapping(originDataMap);
                        ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                        vendorOptions.setVendorId(Long.parseLong(vendor_id));
                        productOptions.setModifyPrice("1");
                        productOptions.setError_type(error_type);
                        CommonThreadPool.execute(name,executor,threadNum,new UpdateProductThread(productOptions,vendorOptions,apiDataFileUtils,originDataMap));
                    }
                }

                if(vendor_id.equals("44")) {
                    if(name.equals("product_all_update") || name.equals("product_delta_update") ) {
                        ProductEDSManagement.ProductOptions productOptions = matgentoSynProductMapping.mapping(originDataMap);
                        ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                        vendorOptions.setVendorId(Long.parseLong(vendor_id));
                        productOptions.setModifyPrice("1");
                        productOptions.setError_type(error_type);
                        CommonThreadPool.execute(name,executor,threadNum,new UpdateProductThread(productOptions,vendorOptions,apiDataFileUtils,originDataMap));
                    }
                }

                this.updateProcessing(api_error_processing_id);
            }
            mapUtils.putData("status", StatusType.SUCCESS).putData("info", "success");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("ErrorMessageControllerProcessing,errorMessage:" + ExceptionUtils.getExceptionDetail(e));
            mapUtils.putData("status", StatusType.FAILURE).putData("info", ExceptionUtils.getExceptionDetail(e));
        }
        return mapUtils.getMap();
    }

    public boolean updateProcessing(String api_error_processing_id) throws Exception {
        Connection conn = null;
        try {
            conn = DBConnector.sql2o.beginTransaction();
            CategoryService categoryService = new CategoryService(conn);
            // update processing
            String updateProcessingSQL =
                    "update api_error_processing aep set no_process = 0,`process_time` = now() where aep.enabled = 1 and aep.api_error_processing_id in("
                            + api_error_processing_id + ")";
            logger.info("ErrorMessageControllerProcessing,start,updateProcessingSQL:" + updateProcessingSQL);
            categoryService.updateBySQL(updateProcessingSQL);
            logger.info("ErrorMessageControllerProcessing,end,updateProcessingSQL:" + updateProcessingSQL);
            if (conn != null) {
                conn.commit();
                conn.close();
            }
            return true;
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
                conn.close();
            }
            e.printStackTrace();
            throw e;
        }
    }

    public List<Map<String, Object>> getApiErrorProcessMap(String data) throws Exception {
        Connection conn = null;
        try {
            conn = DBConnector.sql2o.beginTransaction();
            CategoryService categoryService = new CategoryService(conn);
            String selectProcessingSQL = "select aep.api_error_processing_id,aep.error_id,va.* from api_error_processing aep \n"
                    + "inner join vendor_api va on(va.vendor_api_id = aep.vendor_api_id)\n"
                    + "where aep.enabled = 1 and aep.no_process = 1 and va.enabled = 1 and aep.api_error_processing_id in(" + data + ")";
            logger.info("ErrorMessageControllerProcessing,selectProcessingSQL:" + selectProcessingSQL);
            List<Map<String, Object>> selectProcessMaps = categoryService.executeSQL(selectProcessingSQL);
            logger.info("ErrorMessageControllerProcessing,selectProcessMaps:" + JSONObject.toJSON(selectProcessMaps) + ",selectProcessingSQL:"
                    + selectProcessingSQL);
            if (conn != null) {
                conn.commit();
                conn.close();
            }
            return selectProcessMaps;
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
                conn.close();
            }
            e.printStackTrace();
            throw e;
        }
    }

    public JSONObject readFile(String api_error_processing_id) throws Exception {
        Connection conn = null;
        try {
            conn = DBConnector.sql2o.beginTransaction();
            CategoryService categoryService = new CategoryService(conn);
            String selectPathSQL = "select aep.error_id,va.file_location from api_error_processing aep \n"
                    + "inner join vendor_api va on(aep.vendor_api_id = va.vendor_api_id)\n"
                    + "where aep.enabled = 1 and va.enabled = 1 and aep.api_error_processing_id = '" + api_error_processing_id + "'";
            logger.info("ErrorMessageControllerGetMessage,executeSQL,selectPathSQL:" + selectPathSQL);
            List<Map<String, Object>> pathMap = categoryService.executeSQL(selectPathSQL);
            logger.info("ErrorMessageControllerGetMessage,executeSQL,pathMap:" + JSONObject.toJSONString(pathMap) + ",selectPathSQL:" + selectPathSQL);

            String error_id = pathMap.get(0).get("error_id").toString();
            String file_location = pathMap.get(0).get("file_location").toString();
            String datePath = error_id.substring(5, 13);

            String path = file_location + "/" + datePath + "/" + error_id + ".txt";

            String body = FileUtils.file2String(new File(path), "UTF-8");
            JSONObject jsonObject = JSONObject.parseObject(body);
            logger.info("ErrorMessageControllerGetMessage,outputParams,api_error_processing_id:" + api_error_processing_id + ",body:" + body);
            if (conn != null) {
                conn.commit();
                conn.close();
            }
            return jsonObject;
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
                conn.close();
            }
            e.printStackTrace();
            throw e;
        }
    }
}
