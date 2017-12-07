var requestURL = {
    "getTag" : {"url":baseUrl + "content/tags", "method": "GET"},
    "applyTag" : {"url":baseUrl + "content/tags/{tagId}/products", "method": "POST"},
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
    "getBlock" : {"url" : baseUrl + "content/blocks/simple", "method": "GET"},
    "getProductsByTag" : {"url" : baseUrl + "content/tags/{tagId}/products", "method": "GET"},
    "uploadImage": {"url" : baseUrl + "file", "method":"POST"},
    "getBlockDetail": {"url": baseUrl + "content/blocks/{blockId}", "method" : "GET"},
    "getUnBindTags": {"url" : baseUrl + "content/tags/unbind", "method": "GET"},
    "saveBlock" : {"url" : baseUrl + "content/operation/save", "method" : "PUT"}
    "getBlockTagRel" : {"url" : baseUrl + "content/blocks", "method" : "GET"}
}
