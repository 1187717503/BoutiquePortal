function initTag() {
    
    $.ajax({
        type: requestURL.getTag.method,
        contentType: "application/json",
        url: requestURL.getTag.url,
        data: {},
        dataType: 'json',
        beforeSend: function(request) {
            request.setRequestHeader("token", token);
        },
        success: function(result) {
            initSelectItems('select-tag', 'tmpl-tag-select', result.data);
        }, error: function(code, exception) {

            if (code.status == 401) {
                window.location.href = '../../../login'
            }
        }
    });
}

function initDate() {
    let curDate = format(new Date(), 'yyyy-MM-dd');
    let endDate = new Date(curDate);
    endDate.setMonth(endDate.getMonth() - 1);
    let startDate = format(endDate, 'yyyy-MM-dd');

    $('#modified-start').val(startDate);
    $('#modified-end').val(curDate);
}

function initSelectItems(elemId, tmplId, listData) {
    var $selectDropdown = $('#' + elemId).empty().html('');
    $('#' + tmplId).tmpl({list: listData}).appendTo('#' + elemId);
    $('#' + elemId).material_select();
}

function getFilterFromDom() {
    let searchObj = {};

    searchObj.modifiedStart = $('#modified-start').val();
    searchObj.modifiedEnd = $('#modified-end').val();
    searchObj.blockName = $('#text-blockName').val();
    searchObj.status = $('#select-status').val();
    searchObj.tagId = $('#select-tag').val();

    console.log(searchObj);

    if ($('.orderby.active use').length > 0) {
        searchObj.orderByColmun = $('.orderby.active use').attr('data-order-col');
        searchObj.orderByDesc = $('.orderby.active use').attr('data-order-desc');
    }

    return searchObj;
}

function getBlockList(pageno) {
    let pagesize = localStorage.getItem('content-page-size');

    if (pagesize == null) {
        pagesize = 25;
        localStorage.setItem('content-page-size', 25);
    }

    let searchObj = getFilterFromDom();

    var filter = '?';
    // if (searchObj.boutique !== '-1' && typeof(searchObj.boutique) !== 'undefined' && searchObj.boutique !== '') {
    //     filter += 'vendorId='+ searchObj.boutique + '&';
    // }

    if (searchObj.orderByColmun) {
        filter += 'orderBy='+ searchObj.orderByColmun + '&';
        filter += 'desc='+ searchObj.orderByDesc + '&';
    }

    //App3.0 add
    let obj = transDateRange(searchObj.modifiedStart, searchObj.modifiedEnd, "Last Modified");
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

    filter = filter.slice(0, filter.length - 1);

    loading();

    $('#block-list').empty();
    $('#block-head-list').empty();
    $('.pagination').empty();
    $("#block-head-cbx").prop('checked', false);
    
    $("#tmpl-block-head-list").tmpl().appendTo("#block-head-list");

    finishLoading();

    // $.ajax({
    //     type: requestURL.search.method,
    //     contentType: "application/json",
    //     url: requestURL.search.url + "/" + searchObj.status + filter,
    //     data: {},
    //     dataType: 'json',
    //     beforeSend: function(request) {
    //         request.setRequestHeader("token", token);
    //     },
    //     success: function(result) {
    //         if (result.data == null) {
    //             result.data = [];
    //         }

    //         for (var i = 0; i < result.data.length; i++) {
                
    //         }

    //         $("#tmpl-block-list").tmpl({list: result.data}).appendTo("#block-list");
    //         $("#tmpl-block-head-list").tmpl().appendTo("#block-head-list");

    //         $('.orderby').each(function() {
    //             if ($(this).find('.use-icon').data('order-col') == searchObj.orderByColmun) {
    //                 if (searchObj.orderByDesc == 1) {
    //                     $(this).find('.use-icon').attr('xlink:href', '#icon-desc');
    //                     $(this).find('.use-icon').attr('data-order-desc', '1');
    //                 } else {
    //                     $(this).find('.use-icon').attr('xlink:href', '#icon-asc');
    //                     $(this).find('.use-icon').attr('data-order-desc', '0');
    //                 }
    //                 $(this).addClass('active');
    //             } else {
    //                 $(this).find('.use-icon').attr('xlink:href', '#icon-default-order');
    //             }
    //         })

    //         initActionEvent();
    //         finishLoading();
    //         getCountWithFilter(filter, pagesize, pageno, searchObj.statusText);
    //     }, error: function(result, resp, par) {
    //         toashWithCloseBtn(result.responseJSON.message);
    //         finishLoading();

    //         if (result.status == 401) {
    //             window.location.href = '../../../login'
    //         }
    //     }
    // });
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

function pageAction(pageno) {
    getProdcutList(pageno);
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
}
