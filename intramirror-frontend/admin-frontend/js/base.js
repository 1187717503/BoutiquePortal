//var baseUrl="http://sha.staging.admin2.intramirror.com:8096/";
var baseUrl = "http://localhost:8080/";

var requestURL = {
    "getBrand" : {"url":baseUrl + "brand/selectActiveBrands.htm", "method": "GET"},
    "getVendor" : {"url":baseUrl + "vendor/select/queryAllVendor.htm", "method": "GET"},
    "getCategory": {"url":baseUrl + "category/selectActiveCategorys.htm", "method": "GET"},
    "getSeason" : {"url":baseUrl + "product/filter/season/list", "method": "GET"},
    "search" : {"url":baseUrl + "product/fetch/list", "method": "GET"},
    "productAction" : {"url": baseUrl + "product/operate" , "method": "POST"}
}

console.log(requestURL)