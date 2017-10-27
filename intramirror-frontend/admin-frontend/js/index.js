let token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxOTciLCJpYXQiOjE1MDkxNzUzMzF9.dDQME0lwEeov_ub-QFjWhYnUh3IpScQQ4GJR9aqmKR6XqgYoabJQasCw3CBBOnMr8diMcccvDbvyBji-p14_Jw";

function initBrand() {

    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: requestURL.getBrand,
        data: {},
        dataType: 'json',
        beforeSend: function(request) {
            request.setRequestHeader("token", token);
        },
        success: function(result) {
            initSelectItems('select-brand', 'tmpl-brand-select', result.data);
        }
    });
}

function initVendor() {

    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: requestURL.getVendor,
        data: {},
        dataType: 'json',
        beforeSend: function(request) {
            request.setRequestHeader("token", token);
        },
        success: function(result) {
            initSelectItems('select-boutique', 'tmpl-boutique-select', result.data);
        }
    });
}

function initCategory() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: requestURL.getCategory,
        data: {},
        dataType: 'json',
        beforeSend: function(request) {
            request.setRequestHeader("token", token);
        },
        success: function(result) {
            var categoryData = [];
            for (var i = 0; i < result.data.length; i++) {
                // level 1
                var firstName = result.data[i].name;
                categoryData.push({"categoryId": result.data[i].categoryId , "name": firstName});

                if (result.data[i].children.length > 0) {
                    var secondData = result.data[i].children;
                    for (var j = 0; j < secondData.length; j++) {
                        // level 2
                        var secondName = secondData[j].name;
                        categoryData.push({"categoryId": secondData[j].categoryId , "name": firstName + "->" +secondData[j].name});
                        if (secondData[j].children.length > 0) {
                            var thirdData = secondData[j].children;
                            for (var k = 0; k < thirdData.length; k++) {
                                // level 3
                                categoryData.push({"categoryId": thirdData[k].categoryId , "name": firstName + "->" + secondName + "->" + thirdData[k].name});
                            }
                        }
                    }
                }
            }

            initSelectItems('select-category', 'tmpl-category-select', categoryData);
        }
    });
}


function initSeason() {
    
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: requestURL.getSeason,
        data: {},
        dataType: 'json',
        beforeSend: function(request) {
            request.setRequestHeader("token", token);
        },
        success: function(result) {
            initSelectItems('select-season', 'tmpl-season-select', result.data);
        }
    });
}

function getProdcutList() {
    var list = [];
    list.push({
        "order_id": 1,
        "designer_id": "IEKOQ819182-101",
        "brand": "Fendi",
        "Boutique": "Luciana",
        "boutique_id": "58383990338KS2",
        "Season": "14SS",
        "Category": "Women>Bags>Top handles",
        "Stock": 1,
        "Retail_Price": 2100.00,
        "Boutique_Price": 2100.00,
        "IM_Price": 2100.00,
        "details" : [{"sku_id": 1, "size": "39", "barcode": "123981729387192371", "available_stock": 5, "reserverd_stock": 2, "confirmed_stock": 4},
                     {"sku_id": 2, "size": "40", "barcode": "123981729387192372", "available_stock": 9, "reserverd_stock": 1, "confirmed_stock": 2},
                     {"sku_id": 3, "size": "41", "barcode": "123981729387192373", "available_stock": 8, "reserverd_stock": 3, "confirmed_stock": 6}]
    });

    list.push({
        "order_id": 3,
        "designer_id": "IEKOQ819182-101",
        "brand": "Fendi",
        "Boutique": "Luciana",
        "boutique_id": "58383990338KS2",
        "Season": "14SS",
        "Category": "Women>Bags>Top handles",
        "Stock": 1,
        "Retail_Price": 2100.00,
        "Boutique_Price": 2100.00,
        "IM_Price": 2100.00,
        "details" : [{"sku_id": 4, "size": "39", "barcode": "123986729387192371", "available_stock": 5, "reserverd_stock": 2, "confirmed_stock": 4},
                     {"sku_id": 5, "size": "40", "barcode": "123986729387192372", "available_stock": 9, "reserverd_stock": 1, "confirmed_stock": 2},
                     {"sku_id": 6, "size": "41", "barcode": "123982729387192373", "available_stock": 8, "reserverd_stock": 3, "confirmed_stock": 6}]
    });

    list.push({
        "order_id": 4,
        "designer_id": "IEKOQ819182-102",
        "brand": "Fendi",
        "Boutique": "Luciana",
        "boutique_id": "58383990338KS2",
        "Season": "15SS",
        "Category": "Women>Bags>Top handles",
        "Stock": 1,
        "Retail_Price": 2100.00,
        "Boutique_Price": 2100.00,
        "IM_Price": 2100.00,
        "details" : [{"sku_id": 14, "size": "41", "barcode": "123986729387192371", "available_stock": 5, "reserverd_stock": 2, "confirmed_stock": 4},
                     {"sku_id": 15, "size": "42", "barcode": "123986729387192372", "available_stock": 9, "reserverd_stock": 1, "confirmed_stock": 2},
                     {"sku_id": 26, "size": "43", "barcode": "123982729387192373", "available_stock": 8, "reserverd_stock": 3, "confirmed_stock": 6}]
    });

    list.push({
        "order_id": 5,
        "designer_id": "IEKOQ819182-103",
        "brand": "Fendi",
        "Boutique": "Luciana",
        "boutique_id": "58383990338KS2",
        "Season": "17SS",
        "Category": "Women>Bags>Top handles",
        "Stock": 1,
        "Retail_Price": 2900.00,
        "Boutique_Price": 2900.00,
        "IM_Price": 3100.00
    });

    return list;
}


function initSelectItems(elemId, tmplId, listData) {
    //$('#' + elemId).material_select();

    var $selectDropdown = $('#' + elemId).empty().html('');

    $('#' + tmplId).tmpl({list: listData}).appendTo('#' + elemId);
    $('#' + elemId).material_select();
}