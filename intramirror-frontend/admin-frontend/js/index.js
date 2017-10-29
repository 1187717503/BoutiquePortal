let token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxOTciLCJpYXQiOjE1MDk0NDYxNzF9.XE4-XATYwBXPvdOht6RinY099WQ_RsXtRnDP2Mn7vnLck0xRdqMqeKHwyechw9SyXs3wejCxHOeixQdAVhVKEg";

var searchObj = {}
searchObj.pageSize=10;

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


function getFilterFromDom() {
    var boutique = $('#select-boutique').val();
    if (boutique === '-1') {
        searchObj.boutique = '';
    } else {
        searchObj.boutique = boutique;
    }

    var category = $('#select-category').val();
    if (category === '-1') {
        searchObj.category = '';
    } else {
        searchObj.category = category;
    }

    var season = $('#select-season').val();
    if (season === '-1') {
        searchObj.season = '';
    } else {
        searchObj.season = season;
    }

    var brand = $('#select-brand').val();
    if (brand === '-1') {
        searchObj.brand = '';
    } else {
        searchObj.brand = brand;
    }
}

function getProdcutList(status, pagesize, pageno, totalsize) {

    getFilterFromDom();

    var filter = '?';
    if (searchObj.boutique) {
        filter += 'boutique='+ searchObj.boutique + '&';
    }

    if (searchObj.boutiqueid) {
        filter += 'boutiqueid='+ searchObj.boutiqueid + '&';
    }

    if (searchObj.brand) {
        filter += 'brand='+ searchObj.brand + '&';
    }

    if (searchObj.category) {
        filter += 'category='+ searchObj.category + '&';
    }

    if (searchObj.season) {
        filter += 'season='+ searchObj.season + '&';
    }

    if (pagesize) {
        filter += 'pagesize='+ pagesize + '&';
    }

    if (pageno) {
        filter += 'pageno='+ pageno + '&';
    }

    filter = filter.slice(0, filter.length-1);


    console.log(requestURL.search + "/" + status + filter);


    $('#order-list').empty();
    $('.pagination').empty();
    loading();
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: requestURL.search + "/" + status + filter,
        data: {},
        dataType: 'json',
        beforeSend: function(request) {
            request.setRequestHeader("token", token);
        },
        success: function(result) {
            $("#tmpl-order-list").tmpl({list: result.data}).appendTo("#order-list");
            $(".hide-icon").click(showDetail);
            updatePagination(status, pagesize, pageno, totalsize);
            finishLoading();
        }
    });
}

function showDetail() {
    if ($(this).hasClass("head-hide-icon")) {
        console.log('head-hide-icon is click');
        $(this).toggleClass("mdi-hardware-keyboard-arrow-down");
        $(this).toggleClass("mdi-hardware-keyboard-arrow-right");

        if ($(this).hasClass("mdi-hardware-keyboard-arrow-right")) {
            // 隐藏
            $(".product-list .hide-icon").removeClass("mdi-hardware-keyboard-arrow-down");
            $(".product-list .hide-icon").addClass("mdi-hardware-keyboard-arrow-right");
            $(".product-list .goods").addClass("show-detail");
        } else {
            // 显示
            $(".product-list .hide-icon").removeClass("mdi-hardware-keyboard-arrow-right");
            $(".product-list .hide-icon").addClass("mdi-hardware-keyboard-arrow-down");
            $(".product-list .goods").removeClass("show-detail");
        }
        
    } else {
        console.log('hide-icon is click');
        $(this).toggleClass("mdi-hardware-keyboard-arrow-down");
        $(this).toggleClass("mdi-hardware-keyboard-arrow-right");
        $(this).parent().parent().next().toggleClass("show-detail");
    }
}

function initSelectItems(elemId, tmplId, listData) {

    var $selectDropdown = $('#' + elemId).empty().html('');

    $('#' + tmplId).tmpl({list: listData}).appendTo('#' + elemId);
    $('#' + elemId).material_select();
}

function loading() {
    $('.load-data-holder').toggleClass("active");
}

function finishLoading() {
    $('.load-data-holder').toggleClass("active");
}

function updatePagination(status, pagesize, pageno, totalsize) {
    $('.pagination').empty();

    var pageinfo = {}
    pageinfo.totalPage = totalsize;
    pageinfo.currPage = pageno;
    pageinfo.list = [];
    var listData = pageinfo.list;

    if (pageno <= 3 ) {
        listData.push({"no": 1});
        listData.push({"no": 2});
        listData.push({"no": 3});
        listData.push({"no": 4});
        listData.push({"no": 5});
        listData[pageno-1].active = 1;
    } else if (pageno + 2 > totalsize) {
        listData.push({"no": totalsize - 4});
        listData.push({"no": totalsize - 3});
        listData.push({"no": totalsize - 2});
        listData.push({"no": totalsize - 1});
        listData.push({"no": totalsize});
        listData[4-totalsize+pageno].active = 1;
    } else {
        listData.push({"no": pageno - 2});
        listData.push({"no": pageno - 1});
        listData.push({"no": pageno, "active": 1});
        listData.push({"no": pageno + 1});
        listData.push({"no": pageno + 2});
    }
    
    $('#tmpl-pagination').tmpl({page: pageinfo}).appendTo('.pagination');

    $('.pagination-index').click(function() {
        getProdcutList(status, pagesize, $(this).data('pageno')+1, totalsize);

    })
}