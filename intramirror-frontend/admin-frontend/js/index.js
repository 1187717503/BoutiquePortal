let token = sessionStorage.getItem('token');
if (!token) {
    token = localStorage.getItem('token');
}

// token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyNjEiLCJpYXQiOjE1MTAwNDMxNjJ9.VWNnIquVf5dnwijKgLC5Bd2er2wAvOVKZQWq4hcWMc-ddXGL4SFJ3Vq7mPnJx3xt6xZu-G9xRHxgMrgKNYxuoQ";

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

    if ($('.orderby.active use').length > 0) {
        searchObj.orderByColmun = $('.orderby.active use').attr('data-order-col');
        searchObj.orderByDesc = $('.orderby.active use').attr('data-order-desc');
    }

    return searchObj;
}

function getProdcutList(status, pageno) {
    let pagesize = localStorage.getItem('product-page-size');

    if (pagesize == null) {
        pagesize = 25;
        localStorage.setItem('product-page-size', 25);
    }

    let searchObj = getFilterFromDom();
    //return console.log(searchObj);

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

    if (searchObj.orderByColmun) {
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
    $('#order-head-list').empty();
    $('.pagination').empty();
    $('.control-pannel').empty();
    $("#order-head-cbx").prop('checked', false);
    
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
            $("#tmpl-order-head-list").tmpl({tab_status: status}).appendTo("#order-head-list");

            // set orderby icon status
            $('.orderby').each(function() {
                if ($(this).find('.use-icon').data('order-col') == searchObj.orderByColmun) {
                    if (searchObj.orderByDesc == 1) {
                        $(this).find('.use-icon').attr('xlink:href', '#icon-desc');
                        $(this).find('.use-icon').attr('data-order-desc', '1');
                    } else {
                        $(this).find('.use-icon').attr('xlink:href', '#icon-asc');
                        $(this).find('.use-icon').attr('data-order-desc', '0');
                    }
                    $(this).addClass('active');
                } else {
                    $(this).find('.use-icon').attr('xlink:href', '#icon-default-order');
                }
            })

            initActionEvent();
            finishLoading();
            getCountWithFilter(filter, status, pagesize, pageno);
        }, error: function(result, resp, par) {
            Materialize.toast(result.responseJSON.message, 3000);
            finishLoading();

            if (result.status == 401) {
                window.location.href = '../../login'
            }
        }
    });
}

function getCountWithFilter(filter, tarStatus, pagesize, pageno){
    var nStatusCount = 0;
    $.ajax({
        type: requestURL.getAllCount.method,
        contentType: "application/json",
        url: requestURL.getAllCount.url + filter,
        data: {},
        dataType: 'json',
        beforeSend: function(request) {
            request.setRequestHeader("token", token);
        },
        success: function(result) {
            //console.log(result);
            $(".tabs .tab.col a").each(function(e) {
                var curStatus = $(this).data('status');
                switch(curStatus){
                case 'new':
                    nStatusCount += dealCountWithFilter($(this),result.data.NEW,curStatus,tarStatus);
                    break;
                case 'processing':
                    nStatusCount += dealCountWithFilter($(this),result.data.PROCESSING,curStatus,tarStatus);
                    break;
                case 'readytosell':
                    nStatusCount += dealCountWithFilter($(this),result.data.READY_TO_SELL,curStatus,tarStatus);
                    break;
                case 'shopprocessing':
                    nStatusCount += dealCountWithFilter($(this),result.data.SHOP_PROCESSING,curStatus,tarStatus);
                    break;
                case 'shopreadytosell':
                    nStatusCount += dealCountWithFilter($(this),result.data.SHOP_READY_TO_SELL,curStatus,tarStatus);
                    break;
                case 'shopremoved':
                    nStatusCount += dealCountWithFilter($(this),result.data.SHOP_REMOVED,curStatus,tarStatus);
                    break;
                case 'shoponsale':
                    nStatusCount += dealCountWithFilter($(this),result.data.SHOP_ON_SALE,curStatus,tarStatus);
                    break;
                case 'shopsoldout':
                    nStatusCount += dealCountWithFilter($(this),result.data.SHOP_SOLD_OUT,curStatus,tarStatus);
                    break;
                case 'trash':
                    nStatusCount += dealCountWithFilter($(this),result.data.TRASH,curStatus,tarStatus);
                    break;
                }
            })
            //update the page index

            updatePagination(tarStatus, pagesize, pageno, Math.ceil(nStatusCount/pagesize));
        }, error: function(result, resp, par) {

            Materialize.toast(result.responseJSON.message, 3000);
            if (result.status == 401) {
                window.location.href = '../../login'
            }
        }
    });
}

function dealCountWithFilter(handler,data,curStatus,tarStatus){
    var nCount = 0;
    if(undefined != data){
        nCount = data;
    }
    handler.find('span').text('('+nCount+')');
    if(curStatus == tarStatus){
        return nCount;
    }else{
        return 0;
    }
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

    if (totalsize == 0) {
        return;
    }

    if (pageno <= 3 ) {
        for (let i = 1; i <= totalsize && i <= 5; i++) {
            listData.push({"no": i});
        }
        listData[pageno - 1].active = 1;

    } else if (pageno + 2 > totalsize) {
        listData.push({"no": totalsize - 4});
        listData.push({"no": totalsize - 3});
        listData.push({"no": totalsize - 2});
        listData.push({"no": totalsize - 1});
        listData.push({"no": totalsize});
        listData[4 - totalsize + pageno].active = 1;
    } else {
        listData.push({"no": pageno - 2});
        listData.push({"no": pageno - 1});
        listData.push({"no": pageno, "active": 1});
        listData.push({"no": pageno + 1});
        listData.push({"no": pageno + 2});
    }
    
    $('#tmpl-pagination').tmpl({page: pageinfo}).appendTo('.pagination');

    $('#page-size').val(localStorage.getItem('product-page-size'));
    $('#page-size').material_select();

    $('#page-size').change(function() {
        localStorage.setItem('product-page-size', $(this).val());
        getProdcutList(status, 1);
    }) 

    $('.pagination-index').click(function() {
        getProdcutList(status, $(this).data('pageno') + 1);

    })
}

function getActionMessage(action) {
    let actionDesc = {
        "process":"Process",
        "approve":"Approve",
        "addtoshop":"Add to shop",
        "removefromshop": "Remove from shop",
        "offsale": "Off sale",
        "onsale": "On sale",
        "remove": "Remove"
    };
    return actionDesc[action];
}

function getBtnStatus(status) {
    let btnStatus = {};
    if (status === 'new') {
        btnStatus.approve = 1;
        btnStatus.trash = 1;
        btnStatus.process = 1;
    } else if (status === 'processing') {
        btnStatus.approve = 1;
        btnStatus.trash = 1;
    } else if (status === 'trash') {
        btnStatus.approve = 1;
        // btnStatus.process = 1;
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
        btnStatus.approve = 1;
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
    $("#order-head-cbx").change(function() {
        if ($(this).prop('checked') === true) {
            $(".product-list .page-order [type='checkbox']").prop('checked', true);
        } else {
            $(".product-list .page-order [type='checkbox']").prop('checked', false);
        }
    })

    $(".head-hide-icon, .hide-icon").click(showDetail);

    $('.orderby').click(function() {

        var orderby = $(this).find('use').attr('xlink:href');
        $('.orderby use').attr('xlink:href', '#icon-default-order');
        $('.orderby').removeClass('active');

        if (orderby == '#icon-asc') {
            $(this).find('use').attr('xlink:href', '#icon-desc');
            $(this).find('.use-icon').attr('data-order-desc', '1');
        } else {
            $(this).find('use').attr('xlink:href', '#icon-asc');
            $(this).find('.use-icon').attr('data-order-desc', '0');
        }

        $(this).addClass('active');

        let status = $('.tabs .tab a.active').data('status');
        getProdcutList(status, 1);
        
    })

    $('.batch-action').click(function() {
        
        let param = {};
        let action = $(this).data('action');
        param.originalState = $('.tabs .tab a.active').data('status');
        param.ids = [];

        let count = 0;
        $('.product-list .item input[type=checkbox]').each(function(idx, item) {
            if ($(item).prop('checked')) {
                param.ids.push({"productId": $(item).data('product-id'), "shopProductId": $(item).data('shop-product-id')});
                count++;
            }
        })

        if (count == 0) {
            Materialize.toast('Please select at least one', 3000);
            return;
        }

        loading();
        $.ajax({
            type: requestURL.productBatchAction.method,
            contentType: "application/json",
            url: requestURL.productBatchAction.url + "/" + action,
            data: JSON.stringify(param),
            dataType: 'json',
            beforeSend: function(request) {
                request.setRequestHeader("token", token);
            },
            success: function(result) {

                if (result.data.failed.length > 0) {
                    let msg = '';
                    for (var i = 0; i < result.data.failed.length; i++) {
                        msg += "[" + result.data.failed[i].productId + "] : " + result.data.failed[i].message + "<br/>";
                    }
                    Materialize.toast(msg, 3000);
                } else {
                    Materialize.toast(getActionMessage(action) + ' success', 3000);
                }

                getProdcutList(param.originalState, 1);
                finishLoading()
                
            }, error: function(result, resp, par) {

                Materialize.toast(result.responseJSON.message, 3000);
                finishLoading()
                if (result.status == 401) {
                    window.location.href = '../../login';
                }
            }
        });

    });

    // 展示图片
    $('.product-cover-img').click(function() {
        let images = $(this).data('images');

        let itemsValue = [];
        for (var i = 0; i < images.length; i++) {
            itemsValue.push({"src": images[i]});
        }
     
        $.magnificPopup.open({
            items: itemsValue,
            type: 'image',
            fixedContentPos: true,
            gallery: {
                enabled: true,
                navigateByImgClick: true,
                preload: [0, 1]
            },
        }, 0);
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
                let status = $('.tabs .tab a.active').data('status');
                
                Materialize.toast(getActionMessage(action) + ' success', 3000);
                getProdcutList(status, 1);
                
            }, error: function(result, resp, par) {

                Materialize.toast(result.responseJSON.message, 3000);

                if (result.status == 401) {
                    window.location.href = '../../login';
                }
            }
        });
    });
}