//var baseUrl="http://sha.staging.admin2.intramirror.com:8096/";
var baseUrl = "http://test.admin.intramirror.com:8085/";
// var baseUrl = "http://localhost:8080/";
var requestURL = {
    "getBrand" : {"url":baseUrl + "product/filter/brand/list", "method": "GET"},
    "getVendor" : {"url":baseUrl + "vendor/select/queryAllVendor.htm", "method": "GET"},
    "getCategory": {"url":baseUrl + "category/selectActiveCategorys.htm", "method": "GET"},
    "getSeason" : {"url":baseUrl + "product/filter/season/list", "method": "GET"},
    "search" : {"url":baseUrl + "product/fetch/list", "method": "GET"},
    "productAction" : {"url": baseUrl + "product/operate" , "method": "PUT"},
    "getAllCount": {"url" : baseUrl + "product/fetch/state/count", "method" : "GET"}
}

console.log(requestURL)