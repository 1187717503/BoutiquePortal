
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
            
            $('#select-brand').empty();
            $('#tmpl-brand-select').tmpl({list: result.data}).appendTo('#select-brand');
            
            $('#select-brand').selectize({
                maxItems: 30
            });
        },
        error: function(code, xx) {
            if (code.status == 401) {
                window.location.href = '../../../login'
            }
        }
    });
}


function selectAll() {
    $('#select-boutique').siblings('ul.select-dropdown').find('li').each(function() {
        if (!$(this).find('input').prop('checked')) {
            $(this).trigger('click');
        }
    })
}

function resetAll() {
    $('#select-boutique').siblings('ul.select-dropdown').find('li').each(function() {
        if ($(this).find('input').prop('checked')) {
            $(this).trigger('click');
        }
    })
}

function initActionforBoutique() {
    // 反选
    $('#select-boutique').siblings('ul.select-dropdown').find('li').on('click', function() {
        if ($(this).index() == 0) {
            if ($(this).find('input').prop('checked')) {
                selectAll();
            } else {
                resetAll();
            }
        }

        $('#select-boutique').siblings('input').attr('title', getSelectedText());
    });
}

function getSelectedText() {
    let selected = '';
    let anyChecked = false;
    $('#select-boutique').siblings('ul.select-dropdown').find('li').each(function() {
        if ($(this).find('input').prop('checked')) {
            anyChecked = true;
            if (selected !== '') {
                selected += ',';
            }
            let idx = $(this).index() + 1;
            let t = $('#select-boutique option:nth-child('+idx+')').text();
            if (t != '-1') {
                selected += t;
            }
        }
    });

    if (!anyChecked) {
        $('#select-boutique').siblings('input').attr('placeholder', "All Boutique");
    }

    return selected;
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
            let list = result.data;
            let nameList = [];
            list.forEach(function(item){
                nameList.push(item.vendor_name);
            });
            nameList.sort();
            let sortList = [];
            nameList.forEach(function(name){
                const temp = _.find(list,function(o){
                    return o.vendor_name === name;
                });
                sortList.push(temp);
            });

            initSelectItems('select-boutique', 'tmpl-boutique-select', sortList);
            $('#select-boutique').siblings('input').attr('title', 'All Boutique');
            initActionforBoutique();
        }, error : function (code, exception) {
            if (code.status == 401) {
                window.location.href = '../../../login'
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
                window.location.href = '../../../login'
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
                window.location.href = '../../../login'
            }
        }
    });
}


function getFilterFromDom() {
    let searchObj = {}
    let selected = '';

    $('#select-boutique').siblings('ul.select-dropdown').find('li').each(function() {
        if ($(this).find('input').prop('checked')) {
            if (selected !== '') {
                selected += ',';
            }
            let idx = $(this).index() + 1;
            let t = $('#select-boutique option:nth-child('+idx+')').val();
            if (t != '-1') {
                selected += t;
            }
        }
    })

    searchObj.boutique = selected;

    searchObj.category = $('#category-input').data('category-id');
    searchObj.season = $('#select-season').val();
    searchObj.brand = $('#select-brand').val();
    searchObj.stock = $('#select-stock').val();
    searchObj.image = $('#select-image').val();
    searchObj.modelImage = $('#select-model-image').val();
    searchObj.streetImage = $('#select-street-image').val();
    searchObj.exception = $('#select-exception').val();
    searchObj.designerId = $('#text-designerId').val();
    searchObj.colorCode = $('#text-color-code').val();
    searchObj.boutiqueId = $('#text-boutique').val();

    if ($('.orderby.active use').length > 0) {
        searchObj.orderByColmun = $('.orderby.active use').attr('data-order-col');
        searchObj.orderByDesc = $('.orderby.active use').attr('data-order-desc');
    }

    console.log(searchObj);

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
    if (searchObj.boutique !== '-1' && typeof(searchObj.boutique) !== 'undefined' && searchObj.boutique !== '') {
        filter += 'vendorId='+ searchObj.boutique + '&';
    }

    if (searchObj.boutiqueId) {
        filter += 'boutiqueId='+ searchObj.boutiqueId + '&';
    }

    if (searchObj.brand !== '-1' && searchObj.brand) {
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
    filter += 'stock=' + searchObj.stock + '&'
    filter += 'image='+ searchObj.image + '&';
    filter += 'modelImage='+ searchObj.modelImage + '&';
    filter += 'streetImage='+ searchObj.streetImage + '&';
    filter += 'exception='+ searchObj.exception + '&';

    filter = filter.slice(0, filter.length - 1);

    loading();

    $('#order-list').empty();
    $('#order-head-list').empty();
    $('.pagination').empty();
    $('.control-pannel').empty();
    $("#order-head-cbx").prop('checked', false);
    
    let btnStatus = getBtnStatus(status);
    $("#tmpl-control-pannel").tmpl({list: btnStatus}).appendTo(".control-pannel");
   
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

                if (!result.data[i].retail_price) {
                    result.data[i].retail_price = '-';
                } else {
                    result.data[i].retail_price = result.data[i].retail_price.toFixed(2);
                }

                if (!result.data[i].boutique_price) {
                    result.data[i].boutique_price = '-';
                } else {
                    result.data[i].boutique_price = result.data[i].boutique_price.toFixed(2);
                }

                if (!result.data[i].im_price) {
                    result.data[i].im_price = '-';
                } else {
                    result.data[i].im_price = result.data[i].im_price.toFixed(2);
                }

                if (!result.data[i].sale_price) {
                    result.data[i].sale_price = '-';
                } else {
                    result.data[i].sale_price = result.data[i].sale_price.toFixed(2);
                }

                if (!result.data[i].boutique_discount) {
                    result.data[i].boutique_discount = '';
                } else {
                    result.data[i].boutique_discount = Math.round(result.data[i].boutique_discount * 100) + '%';
                }

                if (!result.data[i].im_discount) {
                    result.data[i].im_discount = '';
                } else {
                    result.data[i].im_discount = Math.round(result.data[i].im_discount * 100) + '%';
                }
                
                if (!result.data[i].sale_discount) {
                    result.data[i].sale_discount = '';
                } else {
                    result.data[i].sale_discount = Math.round(result.data[i].sale_discount * 100) + '%';
                }
            }

            $("#tmpl-order-list").tmpl({list: result.data, tab_status: status, action: btnStatus}).appendTo("#order-list");
            $("#tmpl-order-head-list").tmpl({tab_status: status}).appendTo("#order-head-list");

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
            toashWithCloseBtn(result.responseJSON.message);
            finishLoading();

            if (result.status == 401) {
                window.location.href = '../../../login'
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
                    nStatusCount += dealCountWithFilter($(this), result.data.NEW, curStatus, tarStatus);
                    break;
                case 'processing':
                    nStatusCount += dealCountWithFilter($(this), result.data.PROCESSING, curStatus, tarStatus);
                    break;
                case 'readytosell':
                    nStatusCount += dealCountWithFilter($(this), result.data.READY_TO_SELL, curStatus, tarStatus);
                    break;
                case 'shopprocessing':
                    nStatusCount += dealCountWithFilter($(this), result.data.SHOP_PROCESSING, curStatus, tarStatus);
                    break;
                case 'shopreadytosell':
                    nStatusCount += dealCountWithFilter($(this), result.data.SHOP_READY_TO_SELL, curStatus, tarStatus);
                    break;
                case 'shopremoved':
                    nStatusCount += dealCountWithFilter($(this), result.data.SHOP_REMOVED, curStatus, tarStatus);
                    break;
                case 'shoponsale':
                    nStatusCount += dealCountWithFilter($(this), result.data.SHOP_ON_SALE, curStatus, tarStatus);
                    break;
                case 'shopsoldout':
                    nStatusCount += dealCountWithFilter($(this), result.data.SHOP_SOLD_OUT, curStatus, tarStatus);
                    break;
                case 'trash':
                    nStatusCount += dealCountWithFilter($(this), result.data.TRASH, curStatus, tarStatus);
                    break;
                }
            })
            //update the page index

            updatePagination(pagesize, pageno, Math.ceil(nStatusCount/pagesize), pageAction, tarStatus);
            // localStorage.setItem('product-page-size', $(this).val());

            $('#page-size').val(localStorage.getItem('product-page-size'));
            $('#page-size').material_select();
            $('#page-size').change(function() {
                localStorage.setItem('product-page-size', $(this).val());
                pageAction(1, tarStatus);
            });

        }, error: function(result, resp, par) {

            toashWithCloseBtn(result.responseJSON.message);

            if (result.status == 401) {
                window.location.href = '../../../login'
            }
        }
    });
}

function dealCountWithFilter(handler, data, curStatus, tarStatus){
    var nCount = 0;
    if (undefined != data) {
        nCount = data;
    }

    handler.find('span').text('('+nCount+')');
    if (curStatus == tarStatus) {
        return nCount;
    } else {
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

// function updatePagination(status, pagesize, pageno, totalsize) {
//     $('.pagination').empty();

//     let pageinfo = {}
//     pageinfo.totalPage = totalsize;
//     pageinfo.currPage = pageno;
//     pageinfo.list = [];
//     let listData = pageinfo.list;

//     if (totalsize == 0) {
//         return;
//     }

//     if (pageno > totalsize + 1) {
//         pageno = totalsize + 1;
//     }

//     if (pageno < 0) {
//         pageno = 0;
//     }

//     if (pageno <= 3 ) {
//         for (let i = 1; i <= totalsize && i <= 5; i++) {
//             listData.push({"no": i});
//         }
//         listData[pageno - 1].active = 1;

//     } else if (pageno + 2 > totalsize) {
//         listData.push({"no": totalsize - 4});
//         listData.push({"no": totalsize - 3});
//         listData.push({"no": totalsize - 2});
//         listData.push({"no": totalsize - 1});
//         listData.push({"no": totalsize});
//         if (4 - totalsize + pageno > 4) {
//             listData.push({"no": totalsize + 1});
//             listData[4 - totalsize + pageno].active = 1;
//         } else {
//             listData[4 - totalsize + pageno].active = 1;
//         }
        
//     } else {
//         listData.push({"no": pageno - 2});
//         listData.push({"no": pageno - 1});
//         listData.push({"no": pageno, "active": 1});
//         listData.push({"no": pageno + 1});
//         listData.push({"no": pageno + 2});
//     }
    
//     $('#tmpl-pagination').tmpl({page: pageinfo}).appendTo('.pagination');

//     $('#page-size').val(localStorage.getItem('product-page-size'));
//     $('#page-size').material_select();

//     $('#page-size').change(function() {
//         localStorage.setItem('product-page-size', $(this).val());
//         getProdcutList(status, 1);
//     }) 

//     $('.pagination-index').click(function() {
//         pageAction(param, $(this).data('pageno') + 1);
//         //getProdcutList(status, $(this).data('pageno') + 1);

//     })
// }

function pageAction(pageno, param) {
    getProdcutList(param, pageno);
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
        btnStatus.trash = 1;
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
                        msg += result.data.failed[i].message + "<br/>";
                    }
                    toashWithCloseBtn(msg);
                } else {
                    Materialize.toast(getActionMessage(action) + ' Success', 3000);
                }

                // 获取当前页数
                let current = $('.pagination .active.pagination-index').data('pageno') + 1;

                getProdcutList(param.originalState, current);
                finishLoading()
                
            }, error: function(result, resp, par) {
                toashWithCloseBtn(result.responseJSON.message);

                finishLoading()
                if (result.status == 401) {
                    window.location.href = '../../../login';
                }
            }
        });

    });


    $('.exception-batch-action').click(function() {
        let data = [];
        let count = 0;
        data.originalState = $('.tabs .tab a.active').data('status');
        $('.product-list').each(function(idx, item) {
            if ($(item).find('.item input[type=checkbox]').prop('checked')) {
                $(item).find('.goods .exception').each(function(deatilIdx, detailItem){
                    data.push($(detailItem).data('sku-id'));
                })
                count++;
            }
        })
        
        if (count == 0) {
            Materialize.toast('Please select at least one', 3000);
            return;
        }
        if (data.length == 0) {
            Materialize.toast('No exception in selected product.', 3000);
            return;
        }
        console.log(JSON.stringify(data));
        loading();
        $.ajax({
            type: requestURL.updateProductException.method,
            contentType: "application/json",
            url: requestURL.updateProductException.url ,
            data: JSON.stringify(data),
            dataType: 'json',
            beforeSend: function(request) {
                request.setRequestHeader("token", token);
            },
            success: function(result) {
                if (result.data.failed.length > 0) {
                    let msg = '';
                    for (var i = 0; i < result.data.failed.length; i++) {
                        msg += result.data.failed[i].message + "<br/>";
                    }
                    toashWithCloseBtn(msg);
                } else {
                    Materialize.toast('Resolve Success', 3000);
                }

                // 获取当前页数
                let current = $('.pagination .active.pagination-index').data('pageno') + 1;

                getProdcutList(data.originalState, current);
                finishLoading()
                
            }, error: function(result, resp, par) {
                toashWithCloseBtn(result.responseJSON.message);

                finishLoading()
                if (result.status == 401) {
                    window.location.href = '../../../login';
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
                
                Materialize.toast(getActionMessage(action) + ' Success', 3000);
                let current = $('.pagination .active.pagination-index').data('pageno') + 1;

                getProdcutList(status, current);
            }, error: function(result, resp, par) {
                toashWithCloseBtn(result.responseJSON.message);

                if (result.status == 401) {
                    window.location.href = '../../../login';
                }
            }
        });
    });

    $('.product-list .spu-id').click(function(){
        const spuId = $(this).data('spu-id');
        if(spuId !== ''){
            clearAllCookie();
            let token = sessionStorage.getItem('token');
            if (!token) {
                token = localStorage.getItem('token');
            }
            if(baseUrl.match(/staging/)){
                document.cookie = "staging-admin3-token=" + token + ";domain=intramirror.com;path=/;max-age=10";
                document.cookie = "staging-admin3-user=" + localStorage.getItem('userName') + ";domain=intramirror.com;path=/;max-age=10";
                let href = 'http://sha.staging.admin3.intramirror.com/admin#/spuinfo/' + spuId;
                window.open(href);
            }else{
                document.cookie = "prod-admin3-token=" + token + ";domain=intramirror.com;path=/;max-age=10";
                document.cookie = "prod-admin3-user=" + localStorage.getItem('userName') + ";domain=intramirror.com;path=/;max-age=10";
                let href = 'http://admin3.intramirror.com/admin#/spuinfo/' + spuId;
                window.open(href);
            }
        }
    });

    function clearAllCookie() {  
        var keys = document.cookie.match(/[^ =;]+(?=\=)/g);  
        if(keys) {  
            for(var i = keys.length; i--;)  
                document.cookie = keys[i] + '=0;expires=' + new Date(0).toUTCString()  
        }  
    }

    $('.product-list .exception-ops ').click(function() {
        let skuId = $(this).parent().parent().data('sku-id');
        let productId = $(this).parent().parent().data('product-id');
        let action = $(this).data('action');



        if(action === 'set0'){
            let param = {};
            param.productId = productId;
            param.skuId = skuId;
            $.ajax({
                type: requestURL.saveProductException.method,
                contentType: "application/json",
                url: requestURL.saveProductException.url ,
                data: JSON.stringify(param),
                dataType: 'json',
                beforeSend: function(request) {
                    request.setRequestHeader("token", token);
                },            
                success: function(result) {
                    let status = $('.tabs .tab a.active').data('status');
                    Materialize.toast('Save success', 3000);
                    let current = $('.pagination .active.pagination-index').data('pageno') + 1;
                    getProdcutList(status, current);
                }, error: function(result, resp, par) {
                    toashWithCloseBtn(result.responseJSON.message);
                    if (result.status == 401) {
                        window.location.href = '../../../login';
                    }
                }
            });
        } else if(action === 'resolve'){
            let param = [];
            param.push(skuId);
            $.ajax({
                type: requestURL.updateProductException.method,
                contentType: "application/json",
                url: requestURL.updateProductException.url ,
                data: JSON.stringify(param),
                dataType: 'json',
                beforeSend: function(request) {
                    request.setRequestHeader("token", token);
                },            
                success: function(result) {
                    let status = $('.tabs .tab a.active').data('status');
                    Materialize.toast('Resolve success', 3000);
                    let current = $('.pagination .active.pagination-index').data('pageno') + 1;
                    getProdcutList(status, current);
                }, error: function(result, resp, par) {
                    toashWithCloseBtn(result.responseJSON.message);
                    if (result.status == 401) {
                        window.location.href = '../../../login';
                    }
                }
            });
        }
        
        
    });
}

