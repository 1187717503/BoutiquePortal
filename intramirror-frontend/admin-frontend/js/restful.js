var requestURL = {
    "getTag" : {"url":baseUrl + "content/tag/list", "method": "GET"},
    "applyTag" : {"url":baseUrl + "content/savetagproductrel", "method": "POST"},
    "getBrand" : {"url":baseUrl + "product/filter/brand/list", "method": "GET"},
    "getVendor" : {"url":baseUrl + "vendor/select/queryAllVendor.htm", "method": "GET"},
    "getCategory": {"url":baseUrl + "category/selectActiveCategorys.htm", "method": "GET"},
    "getSeason" : {"url":baseUrl + "product/filter/season/list", "method": "GET"},
    "search" : {"url":baseUrl + "product/fetch/list", "method": "GET"},
    "productAction" : {"url": baseUrl + "product/operate/single" , "method": "PUT"},
    "getAllCount": {"url" : baseUrl + "product/fetch/state/count", "method" : "GET"},
    "productBatchAction": {"url": baseUrl + "product/operate/batch" , "method": "PUT"},
    "saveProductException": {"url": baseUrl + "product/exception/saveProductException" , "method": "POST"},
    "updateProductException": {"url": baseUrl + "product/exception/updateProductException" , "method": "PUT"},
    "getBlock" : {"url" : baseUrl + "content/block/list", "method": "GET"},
    "getProductsByTag" : {"url" : baseUrl + "content/tag/products", "method": "GET"},
    "uploadImage": {"url" : baseUrl + "file", "method":"POST"}
}
