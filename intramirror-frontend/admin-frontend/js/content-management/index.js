
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
            initSelectItems('select-boutique', 'tmpl-boutique-select', result.data);
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

function initTag() {

    $.ajax({
        type: requestURL.getTag.method,
        contentType: "application/json",
        url: requestURL.getTag.url + "?orderBy=date",
        data: {},
        dataType: 'json',
        beforeSend: function(request) {
            request.setRequestHeader("token", token);
        },
        success: function(result) {
            initSelectItems('select-tag', 'tmpl-tag-select', result.data);
            initSelectItems('select-apply-tag', 'tmpl-apply-tag-select', result.data);
        }, error: function(code, exception) {

            if (code.status == 401) {
                window.location.href = '../../../login'
            }
        }
    });
}

function initDate() {
    /* 2017-12-11 需求变更，不要初始化日期
    let curDate = format(new Date(), 'yyyy-MM-dd');
    let endDate = new Date(curDate);
    endDate.setMonth(endDate.getMonth() - 1);
    let startDate = format(endDate, 'yyyy-MM-dd');

    $('#sale-at-start').val(startDate);
    $('#sale-at-end').val(curDate);
    */
}


function getFilterFromDom() {
    let searchObj = {};
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
    searchObj.image = $('#select-image').val();
    searchObj.modelImage = $('#select-model-image').val();
    searchObj.streetImage = $('#select-street-image').val();
    searchObj.designerId = $('#text-designerId').val();
    searchObj.colorCode = $('#text-color-code').val();
    searchObj.boutiqueId = $('#text-boutique').val();

    //App3.0 new added
    searchObj.minBoutiqueDiscount = $('#boutique-discount-left').val();
    searchObj.maxBoutiqueDiscount = $('#boutique-discount-right').val();

    searchObj.minIMDiscount = $('#im-discount-left').val();
    searchObj.maxIMDiscount = $('#im-discount-right').val();

    searchObj.minStock = $('#stock-left').val();
    searchObj.maxStock = $('#stock-right').val();

    searchObj.minPrice = $('#price-left').val();
    searchObj.maxPrice = $('#price-right').val();

    searchObj.saleAtFrom = $('#sale-at-start').val();
    searchObj.saleAtTo = $('#sale-at-end').val();

    searchObj.tagId = $('#select-tag').val();
    searchObj.status = $('#select-status').val();
    searchObj.statusText = $('#select-status').siblings('ul').find('li.active span').text();
    if (searchObj.statusText == '') {
        searchObj.statusText = 'ALL';
    }

    if ($('.orderby.active use').length > 0) {
        searchObj.orderByColmun = $('.orderby.active use').attr('data-order-col');
        searchObj.orderByDesc = $('.orderby.active use').attr('data-order-desc');
    }

    return searchObj;
}

function getProdcutList(pageno) {
    let pagesize = localStorage.getItem('content-page-size');

    if (pagesize == null) {
        pagesize = 25;
        localStorage.setItem('content-page-size', 25);
    }

    let searchObj = getFilterFromDom();

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

    //App3.0 add
    var nResult = checkNumRange(searchObj.minBoutiqueDiscount,searchObj.maxBoutiqueDiscount,"Boutique");
    if(-1 == nResult){
        return false;
    } else if (1 == nResult) {
        filter += 'minBoutiqueDiscount='+ searchObj.minBoutiqueDiscount + '&';
        filter += 'maxBoutiqueDiscount='+ searchObj.maxBoutiqueDiscount + '&';
    }

    nResult = checkNumRange(searchObj.minIMDiscount,searchObj.maxIMDiscount,"IM");
    if(-1 == nResult){
        return false;
    } else if(1 == nResult) {
        filter += 'minIMDiscount='+ searchObj.minIMDiscount + '&';
        filter += 'maxIMDiscount='+ searchObj.maxIMDiscount + '&';
    }
    nResult = checkNumRange(searchObj.minStock,searchObj.maxStock,"Stock");
    if(-1 == nResult){
        return false;
    } else if (1 == nResult) {
        filter += 'minStock='+ searchObj.minStock + '&';
        filter += 'maxStock='+ searchObj.maxStock + '&';
    }

    nResult = checkNumRange(searchObj.minPrice,searchObj.maxPrice,"Price");
    if(-1 == nResult){
        return false;
    } else if (1 == nResult) {
        filter += 'minPrice='+ searchObj.minPrice + '&';
        filter += 'maxPrice='+ searchObj.maxPrice + '&';
    }

    let obj = transDateRange(searchObj.saleAtFrom, searchObj.saleAtTo, "Sale At");
    if(obj.dStart != undefined && obj.dEnd != undefined) {

        filter += 'saleAtFrom='+ obj.dStart + '&';
        filter += 'saleAtTo='+ obj.dEnd + '&';
    }else if (obj.dStart == undefined && obj.dEnd == undefined) {
        ;
    }else {
        return false;
    }

    if (pagesize) {
        filter += 'pageSize='+ pagesize + '&';
    }

    if (pageno) {
        filter += 'pageNo='+ pageno + '&';
    }

    filter += 'tagId='+ searchObj.tagId + '&';
    filter += 'image='+ searchObj.image + '&';
    filter += 'modelImage='+ searchObj.modelImage + '&';
    filter += 'streetImage='+ searchObj.streetImage + '&';

    filter = filter.slice(0, filter.length - 1);

    loading();

    $('#order-list').empty();
    $('#order-head-list').empty();
    $('.pagination').empty();
    $("#order-head-cbx").prop('checked', false);

    $.ajax({
        type: requestURL.search.method,
        contentType: "application/json",
        url: requestURL.search.url + "/" + searchObj.status + filter,
        data: {},
        dataType: 'json',
        beforeSend: function(request) {
            request.setRequestHeader("token", token);
        },
        success: function(result) {
            if (result.data == null) {
                result.data = [];
            }

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

                if (!result.data[i].preview_price) {
                    result.data[i].preview_price = '-';
                } else {
                    result.data[i].preview_price = result.data[i].preview_price.toFixed(2);
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

                if (!result.data[i].preview_discount) {
                    result.data[i].preview_discount = '';
                } else {
                    result.data[i].preview_discount = Math.round(result.data[i].preview_discount * 100) + '%';
                }
            }

            $("#tmpl-order-list").tmpl({list: result.data}).appendTo("#order-list");
            $("#tmpl-order-head-list").tmpl().appendTo("#order-head-list");

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
            getCountWithFilter(filter, pagesize, pageno, searchObj.statusText);
        }, error: function(result, resp, par) {
            toashWithCloseBtn(result.responseJSON.message);
            finishLoading();

            if (result.status == 401) {
                window.location.href = '../../../login'
            }
        }
    });
}

function getCountWithFilter(filter, pagesize, pageno, statusText){
    console.log(statusText);
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
            updatePagination(pagesize, pageno, Math.ceil(result.data[statusText]/pagesize), pageAction);
            $('#page-size').val(localStorage.getItem('content-page-size'));
            $('#page-size').material_select();
            $('#page-size').change(function() {
                localStorage.setItem('content-page-size', $(this).val());
                pageAction(1);
            });
        }, error: function(result, resp, par) {

            toashWithCloseBtn(result.responseJSON.message);
            if (result.status == 401) {
                window.location.href = '../../../login'
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

function pageAction(pageno) {
    getProdcutList(pageno);
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

        getProdcutList(1);

    })

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
    });


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

                Materialize.toast(getActionMessage(action) + ' Success', 3000);
                let current = $('.pagination .active.pagination-index').data('pageno') + 1;

                getProdcutList(current);
            }, error: function(result, resp, par) {
                toashWithCloseBtn(result.responseJSON.message);

                if (result.status == 401) {
                    window.location.href = '../../../login';
                }
            }
        });
    });

    $('.remove-tag-icon').click(function() {
        var tag = $(this).closest('.list-detail-item.tags');
        let tagId = tag.data('tag-id');
        let productId = tag.data('product-id');
        let reqUrl = requestURL.delTagFromProduct.url;
        reqUrl = reqUrl.replace("{tagId}", tagId);
        reqUrl = reqUrl.replace("{productId}", productId);
        // tags/{tagId}/products/{productId}
        $.ajax({
            type: requestURL.delTagFromProduct.method,
            contentType: "application/json",
            url: reqUrl,
            data: {},
            dataType: 'json',
            beforeSend: function(request) {
                request.setRequestHeader("token", token);
            },
            success: function(result) {
                tag.remove();
            }, error: function(result, resp, par) {
                if (result.status == 401) {
                    window.location.href = '../../../login';
                } else {
                    toashWithCloseBtn(result.responseJSON.message);
                }
            }
        });

    })
}


function getCheckedParam() {
    let param = {};
    param.productIdList = [];
    param.tagId = $('#select-apply-tag').val();
    param.sortNum = -1;

    let count = 0;
    $('.product-list .item input[type=checkbox]').each(function(idx, item) {
        if ($(item).prop('checked')) {
            param.productIdList.push($(item).data('product-id'));
            count++;
        }
    })

    if (param.tagId == -1) {
        Materialize.toast('Please select tag', 3000);
        return false;
    }
    if (count == 0) {
        Materialize.toast('Please select at least one product', 3000);
        return false;
    }
    return param;
}

function initBatchAction() {
    $('#apply-tag-btn').click(function() {
        let param = getCheckedParam();
        if (!param) {
            return;
        }

        loading();
        $.ajax({
            type: requestURL.applyTag.method,
            contentType: "application/json",
            url: requestURL.applyTag.url.replace("{tagId}",param.tagId),
            data: JSON.stringify(param),
            dataType: 'json',
            beforeSend: function(request) {
                request.setRequestHeader("token", token);
            },
            success: function(result) {
                Materialize.toast('Apply Tag Success', 3000);
                // 获取当前页数
                let current = $('.pagination .active.pagination-index').data('pageno') + 1;
                getProdcutList(current);
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


    $('#remove-tag-btn').click(function() {
        let param = [];

        let tagId = $('#select-apply-tag').val();

        let count = 0;
        $('.product-list .item input[type=checkbox]').each(function(idx, item) {
            if ($(item).prop('checked')) {
                param.push({"productId": $(item).data('product-id'), "tagId" : tagId});
                count++;
            }
        })

        if (param.tagId == -1) {
            Materialize.toast('Please select tag', 3000);
            return false;
        }
        if (count == 0) {
            Materialize.toast('Please select at least one product', 3000);
            return false;
        }

        loading();
        $.ajax({
            type: requestURL.delTagForProducts.method,
            contentType: "application/json",
            url: requestURL.delTagForProducts.url.replace("{tagId}", tagId),
            data: JSON.stringify(param),
            dataType: 'json',
            beforeSend: function(request) {
                request.setRequestHeader("token", token);
            },
            success: function(result) {
                Materialize.toast('Remove Tag Success', 3000);
                // 获取当前页数
                let current = $('.pagination .active.pagination-index').data('pageno') + 1;
                getProdcutList(current);
                finishLoading()
            }, error: function(result, resp, par) {
                finishLoading()
                if (result.status == 401) {
                    window.location.href = '../../../login';
                }  else {
                   toashWithCloseBtn(result.responseJSON.message);
                }
            }
        });

    });


    $('#edit-tag-btn').click(function() {
        getTags();

        $('#edit-tag').openModal({
            dismissible: false,
            opacity: .5,
            inDuration: 300,
            outDuration: 200,
            startingTop: '4%',
            endingTop: '10%'
        });
    });

    $('#edit-tag .model-yes').click(function() {
        $('#edit-tag').closeModal();
    });

    $('#add-tag-btn').click(function() {
        loading();
        let param = {};
        param.tagName = $('#text-new-tag').val();
        if(param.tagName == "") {
            toashWithCloseBtn("Please enter the tag name!");
            finishLoading();
            return;
        }
        param.enabled = 1;
        //掉后台接口
        $.ajax({
            type: requestURL.createTag.method,
            contentType: "application/json",
            url: requestURL.createTag.url,
            data: JSON.stringify(param),
            dataType: 'json',
            beforeSend: function(request) {
                request.setRequestHeader("token", token);
            },
            success: function(result) {
                Materialize.toast("Create Tag Success!", 3000);
                $('#text-new-tag').val('');
                $('#text-new-tag').trigger('blur');
                getTags();
                initTag();
                finishLoading();
            }, error: function(result, resp, par) {
                toashWithCloseBtn(result.responseJSON.message);
                finishLoading();
                if (result.status == 401) {
                    window.location.href = '../../../login';
                }
            }
        });
    });
}

function getTags () {
    $('#tag-head-list').empty();
    $('#tag-list').empty();
    // 查询所有tag
    $.ajax({
        type: requestURL.getTag.method,
        contentType: "application/json",
        url: requestURL.getTag.url + "?orderBy=date",
        data: {},
        dataType: 'json',
        beforeSend: function(request) {
            request.setRequestHeader("token", token);
        },
        success: function(result) {
            $("#tmpl-tag-head-list").tmpl().appendTo("#tag-head-list");
            $("#tmpl-tag-list").tmpl({list: result.data}).appendTo("#tag-list");
            initAcitonEvent();
        }, error: function(code, exception) {
            if (code.status == 401) {
                window.location.href = '../../../login'
            }
        }
    });
}

function initAcitonEvent() {
    $('.tag-list .action .action-icon').click(function() {
        loading();
        let tagId = $(this).parent().data('tag-id');
        
        $.ajax({
            type: requestURL.delTag2Rel.method,
            contentType: "application/json",
            url: requestURL.delTag2Rel.url + "/" + tagId,
            data: {},
            dataType: 'json',
            beforeSend: function(request) {
                request.setRequestHeader("token", token);
            },
            success: function(result) {
                Materialize.toast('Delete Tag Success', 3000);
                let current = $('.pagination .active.pagination-index').data('pageno') + 1;
                initTag();
                getProdcutList(current);
                getTags();
                finishLoading();
            }, error: function(result, resp, par) {
                toashWithCloseBtn(result.responseJSON.message);
                finishLoading();
                if (result.status == 401) {
                    window.location.href = '../../../login';
                }
            }
        });
    });
}
