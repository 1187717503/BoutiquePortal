var baseUrl = "http://admin2.intramirror.com:8085/";

var requestURL = {
    "getBrand" : {"url":baseUrl + "product/filter/brand/list", "method": "GET"},
    "getVendor" : {"url":baseUrl + "vendor/select/queryAllVendor.htm", "method": "GET"},
    "getCategory": {"url":baseUrl + "category/selectActiveCategorys.htm", "method": "GET"},
    "getSeason" : {"url":baseUrl + "product/filter/season/list", "method": "GET"},
    "search" : {"url":baseUrl + "product/fetch/list", "method": "GET"},
    "productAction" : {"url": baseUrl + "product/operate/single" , "method": "PUT"},
    "getAllCount": {"url" : baseUrl + "product/fetch/state/count", "method" : "GET"},
    "productBatchAction": {"url": baseUrl + "product/operate/batch" , "method": "PUT"}
}