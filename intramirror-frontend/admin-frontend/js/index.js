let token = sessionStorage.getItem('token');
if (!token) {
    token = localStorage.getItem('token');
}

//token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyNjEiLCJpYXQiOjE1MDk3Nzk5OTB9.kgF0fSmyQSGWsKG3TGhQqfrPPc-2efAgsF53PF3gdnSHa1mh1eDbvOHaGp7sy8k6YlF4ufHv-18HRoEoonSykw";

function initBrand() {

    $.ajax({
        type: requestURL.getBrand.method,
        contentType: "application/json",
        url: requestURL.getBrand.url,
        data: {},
        dataType: 'json',
        beforeSend: function(request) {
            request.setRequestHeader("token", token);
        },
        success: function(result) {
            initSelectItems('select-brand', 'tmpl-brand-select', result.data);
        },
        error: function(code, xx) {
            if (code.status == 401) {
                window.location.href = '../../login'
            }
        }
  
    });
}

function initVendor() {

    $.ajax({
        type: requestURL.getVendor.method,
        contentType: "application/json",
        url: requestURL.getVendor.url,
        data: {},
        dataType: 'json',
        beforeSend: function(request) {
            request.setRequestHeader("token", token);
        },
        success: function(result) {
            initSelectItems('select-boutique', 'tmpl-boutique-select', result.data);
        }, error : function (code, exception) {
            if (code.status == 401) {
                window.location.href = '../../login'
            }
        }
    });
}

function initCategory() {

    $.ajax({
        type: requestURL.getCategory.method,
        contentType: "application/json",
        url: requestURL.getCategory.url,
        data: {},
        dataType: 'json',
        beforeSend: function(request) {
            request.setRequestHeader("token", token);
        },
        success: function(result) {
            $('#category-menu').empty();
            $('#tmpl-category-menu').tmpl({list: result.data}).appendTo('#category-menu');

            $('#category-menu .multi-menu li').click(function(e) {

                $('#category-input').val($(this).find('span').data('display-name'));
                $('#category-input').data('category-id', $(this).find('span').data('category-id'));
                $('#category-menu').hide();
                e.stopPropagation();
            });


            // var categoryData = [];
            // for (var i = 0; i < result.data.length; i++) {
            //     // level 1
            //     var firstName = result.data[i].name;
            //     categoryData.push({"categoryId": result.data[i].categoryId , "name": firstName});

            //     if (result.data[i].children.length > 0) {
            //         var secondData = result.data[i].children;
            //         for (var j = 0; j < secondData.length; j++) {
            //             // level 2
            //             var secondName = secondData[j].name;
            //             categoryData.push({"categoryId": secondData[j].categoryId , "name": firstName + "->" +secondData[j].name});
            //             if (secondData[j].children.length > 0) {
            //                 var thirdData = secondData[j].children;
            //                 for (var k = 0; k < thirdData.length; k++) {
            //                     // level 3
            //                     categoryData.push({"categoryId": thirdData[k].categoryId , "name": firstName + "->" + secondName + "->" + thirdData[k].name});
            //                 }
            //             }
            //         }
            //     }
            // }

            // initSelectItems('select-category', 'tmpl-category-select', categoryData);
        }, error : function (code, exception) {
            if (code.status == 401) {
                window.location.href = '../../login'
            }
        }
    });
}


function initSeason() {
    
    $.ajax({
        type: requestURL.getSeason.method,
        contentType: "application/json",
        url: requestURL.getSeason.url,
        data: {},
        dataType: 'json',
        beforeSend: function(request) {
            request.setRequestHeader("token", token);
        },
        success: function(result) {
            initSelectItems('select-season', 'tmpl-season-select', result.data);
        }, error: function(code, exception) {
            console.log(code);
            if (code.status == 401) {
                window.location.href = '../../login'
            }
        }
    });
}


function getFilterFromDom() {
    let searchObj = {}
    searchObj.boutique = $('#select-boutique').val();
    searchObj.category = $('#category-input').data('category-id');
    searchObj.season = $('#select-season').val();
    searchObj.brand = $('#select-brand').val();
    searchObj.stock = $('#select-stock').val();
    searchObj.image = $('#select-image').val();
    searchObj.modelImage = $('#select-model-image').val();
    searchObj.streetImage = $('#select-street-image').val();
    searchObj.designerId = $('#text-designerId').val();
    searchObj.colorCode = $('#text-color-code').val();
    searchObj.boutiqueId = $('#text-boutique').val();
    
    console.log(searchObj);
    if ($('.orderby.active').length > 0) {
        searchObj.orderByColmun = $('.orderby.active use').attr('data-order-col');
        searchObj.orderByDesc = $('.orderby.active use').attr('data-order-desc');
        console.log(searchObj);
    }

    return searchObj;
}

function getProdcutList(status, pagesize, pageno) {
    let searchObj = getFilterFromDom();

    var filter = '?';
    if (searchObj.boutique !== '-1') {
        filter += 'vendorId='+ searchObj.boutique + '&';
    }

    if (searchObj.boutiqueId) {
        filter += 'boutiqueId='+ searchObj.boutiqueId + '&';
    }

    if (searchObj.brand !== '-1') {
        filter += 'brandId='+ searchObj.brand + '&';
    }

    if (searchObj.category !== '-1' && searchObj.category !== -1) {
        filter += 'categoryId='+ searchObj.category + '&';
    }

    if (searchObj.season !== '-1') {
        filter += 'season='+ searchObj.season + '&';
    }

    if (searchObj.designerId) {
        filter += 'designerId='+ searchObj.designerId + '&';
    }

    if (searchObj.colorCode) {
        filter += 'colorCode='+ searchObj.colorCode + '&';
    }

    if ( searchObj.orderByColmun) {
        filter += 'orderBy='+ searchObj.orderByColmun + '&';
        filter += 'desc='+ searchObj.orderByDesc + '&';
    }

    if (pagesize) {
        filter += 'pageSize='+ pagesize + '&';
    }

    if (pageno) {
        filter += 'pageNo='+ pageno + '&';
    }

    filter += 'image='+ searchObj.image + '&';
    filter += 'modelImage='+ searchObj.modelImage + '&';
    filter += 'streetImage='+ searchObj.streetImage + '&';

    filter = filter.slice(0, filter.length - 1);

    $('#order-list').empty();
    $('.pagination').empty();
    $('.control-pannel').empty();
    
    let btnStatus = getBtnStatus(status);
    $("#tmpl-control-pannel").tmpl({list: btnStatus}).appendTo(".control-pannel");
   
    loading();
    $.ajax({
        type: requestURL.search.method,
        contentType: "application/json",
        url: requestURL.search.url + "/" + status + filter,
        data: {},
        dataType: 'json',
        beforeSend: function(request) {
            request.setRequestHeader("token", token);
        },
        success: function(result) {
            for (var i = 0; i < result.data.length; i++) {
                if (result.data[i].cover_img.length > 0) {
                    let obj = JSON.parse(result.data[i].cover_img);
                    if (obj.length > 0 && obj[0].length > 0) {
                        result.data[i].display_img = obj[0];
                    }
                }
            }

            $("#tmpl-order-list").tmpl({list: result.data, tab_status: status, action: btnStatus}).appendTo("#order-list");
            $(".hide-icon").click(showDetail);
            updatePagination(status, pagesize, pageno, 100);
            initActionEvent();
            finishLoading();
        }, error: function(result, resp, par) {
            console.log(result);
            Materialize.toast(result.responseJSON.message + " : "+ result.responseJSON.detail, 3000);
            finishLoading();

            if (result.status == 401) {
                window.location.href = '../../login'
            }
        }
    });
}

function showDetail() {
    if ($(this).hasClass("head-hide-icon")) {
        
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

    let pageinfo = {}
    pageinfo.totalPage = totalsize;
    pageinfo.currPage = pageno;
    pageinfo.list = [];
    let listData = pageinfo.list;

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
        getProdcutList(status, pagesize, $(this).data('pageno')+1);

    })
}


function getBtnStatus(status) {
    let btnStatus = {};
    if (status === 'new') {
        btnStatus.approve = 1;
        btnStatus.remove = 1;
        btnStatus.process = 1;
    } else if (status === 'processing') {
        btnStatus.approve = 1;
        btnStatus.remove = 1;
    } else if (status === 'trash') {
        btnStatus.approve = 1;
        btnStatus.process = 1;
    } else if (status === 'readytosell') {
        btnStatus.process = 1;
        btnStatus.remove = 1;
        btnStatus.add_to_shop = 1;
    } else if (status === 'shopreadytosell') {
        btnStatus.process = 1;
        btnStatus.remove = 1;
        btnStatus.remove_from_shop = 1;
        btnStatus.on_sale = 1
    } else if (status === 'shopprocessing') {
        btnStatus.process = 1;
        btnStatus.remove = 1;
    } else if (status === 'shopremoved') {
        btnStatus.approve = 1;
    } else if (status === 'shopsoldout') {
        btnStatus.process = 1;
        btnStatus.remove = 1;
    } else if (status === 'shoponsale') {
        btnStatus.process = 1;
        btnStatus.remove = 1;
        btnStatus.off_sale = 1;
    }
    return btnStatus;
}

function initActionEvent() {

    $('#approve-btn').click(function() {
        console.log('approve-btn');
        Materialize.toast('I am a toast', 4000);
    })

    $('#processing-btn').click(function() {
        $('.product-list .item input[type=checkbox]').each(function(idx, item) {
            if ($(item).prop('checked')) {
                console.log($(item).data('product-id'));

            }
        })
    })

    $('.product-list .action .action-icon').click(function() {

        let productId = $(this).parent().data('product-id');
        let shopProductId = $(this).parent().data('shop-product-id');
        let action = $(this).data('action');

        let param = {};
        param.productId = productId;
        if (shopProductId.length !== 0) {
            param.shopProductId = shopProductId;
        }
        
        $.ajax({
            type: requestURL.productAction.method,
            contentType: "application/json",
            url: requestURL.productAction.url + "/" + action,
            data: JSON.stringify(param),
            dataType: 'json',
            beforeSend: function(request) {
                request.setRequestHeader("token", token);
            },
            success: function(result) {
                console.log(result);
                let status = $('.tabs .tab a.active').data('status');
                
                Materialize.toast(action + ' success', 3000);
                getProdcutList(status, 25, 1);
                
            }, error: function(result, resp, par) {
                console.log(result);
                Materialize.toast(result.responseJSON.message + " : "+ result.responseJSON.detail, 3000);

                if (result.status == 401) {
                    window.location.href = '../../login';
                }
            }
        });

        console.log('product-id=' + productId + ',shop_product_id=' + shopProductId + ',action=' + action);
    });
}