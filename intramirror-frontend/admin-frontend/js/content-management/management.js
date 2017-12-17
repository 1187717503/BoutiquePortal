
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
    endDate.setDate(endDate.getDate() - 2);
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

    searchObj.modifiedAtFrom = $('#modified-start').val();
    searchObj.modifiedAtTo = $('#modified-end').val();
    searchObj.blockName = $('#text-blockName').val();
    searchObj.status = $('#select-status').val();
    searchObj.tagId = $('#select-tag').val();

    if ($('.orderby.active use').length > 0) {
        searchObj.orderByColmun = $('.orderby.active use').attr('data-order-col');
        searchObj.orderByDesc = $('.orderby.active use').attr('data-order-desc');
    }else {
        searchObj.orderByColmun = 'SortOrder';
        searchObj.orderByDesc = 0;
    }

    console.log(searchObj);
    return searchObj;
}

function getBlockList(pageno) {
    let pagesize = localStorage.getItem('block-page-size');

    if (pagesize == null) {
        pagesize = 25;
        localStorage.setItem('block-page-size', 25);
    }

    let searchObj = getFilterFromDom();

    var filter = '?';

    if (searchObj.tagId !== '-1') {
        filter += 'tagId='+ searchObj.tagId + '&';
    }

    if (searchObj.blockName) {
        filter += 'blockName='+ searchObj.blockName + '&';
    }

    if (searchObj.status !== '-1') {
        filter += 'status='+ searchObj.status + '&';
    }

    let obj = transDateRange(searchObj.modifiedAtFrom, searchObj.modifiedAtTo, "Last Modified");
    if(obj.dStart != undefined && obj.dEnd != undefined) {

        filter += 'modifiedAtFrom='+ obj.dStart + '&';
        filter += 'modifiedAtTo='+ obj.dEnd + '&';
    }else if (obj.dStart == undefined && obj.dEnd == undefined) {
        ;
    }else {
        return false;
    }

    filter += 'orderBy='+ searchObj.orderByColmun + '&';
    filter += 'desc='+ searchObj.orderByDesc + '&';
    
    if (pagesize) {
        filter += 'pageSize='+ pagesize + '&';
    }

    if (pageno) {
        filter += 'pageNo='+ pageno + '&';
    }

    filter = filter.slice(0, filter.length - 1);

    loading();

    $('#block-list').empty();
    $('#block-head-list').empty();
    $('.pagination').empty();
    $("#block-head-cbx").prop('checked', false);

    $.ajax({
        type: requestURL.getBlockTagRel.method,
        contentType: "application/json",
        url: requestURL.getBlockTagRel.url + filter,
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
                if (!result.data[i].status) {
                    result.data[i].status = 'Inactive';
                } else {
                   result.data[i].status = 'Active';
                }
            }

            $("#tmpl-block-list").tmpl({list: result.data}).appendTo("#block-list");
            $("#tmpl-block-head-list").tmpl().appendTo("#block-head-list");

            // 时间原因没有做分页 2017-12-8
            $('.orderby').each(function() {
                if ($(this).find('.use-icon').data('order-col') == searchObj.orderByColmun) {
                    if (searchObj.orderByDesc == 1) {
                        $(this).find('use').attr('xlink:href', '#icon-desc');
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
            getCountWithFilter(filter, pagesize, pageno);
        }, error: function(result, resp, par) {
            toashWithCloseBtn(result.responseJSON.message);
            finishLoading();

            if (result.status == 401) {
                window.location.href = '../../../login'
            }
        }
    });
}

function getCountWithFilter(filter, pagesize, pageno){
    $.ajax({
        type: requestURL.getBlocksCount.method,
        contentType: "application/json",
        url: requestURL.getBlocksCount.url + filter,
        data: {},
        dataType: 'json',
        beforeSend: function(request) {
            request.setRequestHeader("token", token);
        },
        success: function(result) {
            updatePagination(pagesize, pageno, Math.ceil(result.data/pagesize), pageAction);
            $('#page-size').val(localStorage.getItem('block-page-size'));
            $('#page-size').material_select();
            $('#page-size').change(function() {
                localStorage.setItem('block-page-size', $(this).val());
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
    getBlockList(pageno);
}

function initActionEvent() {

    $("#block-head-cbx").change(function() {
        if ($(this).prop('checked') === true) {
            $(".block-list .page-block [type='checkbox']").prop('checked', true);
        } else {
            $(".block-list .page-block [type='checkbox']").prop('checked', false);
        }
    })

    // 时间原因，没有做排序 2017-12-8
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
        let current = $('.pagination .active.pagination-index').data('pageno') + 1;
        getBlockList(current);
    })
}

function updateBlock(action) {
  let msg = '';
  let status = 0;
  if(action == 'activate') {
    msg = 'Activate Block Success';
    status = 1;
  } else {
    msg = 'De-Activate Block Success';
    status = 0;
  }

  let blockList = [];
  let count = 0;
  $('.block-list .item input[type=checkbox]').each(function(idx, item) {
      if ($(item).prop('checked')) {
          let param = {};
          param.blockId = $(item).data('block-id');
          param.status = status;
          blockList.push(param);
          count++;
      }
  })

  if (count == 0) {
      Materialize.toast('Please select at least one block', 3000);
      return;
  }

  console.log(blockList);

  loading();
  $.ajax({
      type: requestURL.activateBlock.method,
      contentType: "application/json",
      url: requestURL.activateBlock.url,
      data: JSON.stringify(blockList),
      dataType: 'json',
      beforeSend: function(request) {
          request.setRequestHeader("token", token);
      },
      success: function(result) {
          Materialize.toast(msg, 3000);
          // 获取当前页数
          let current = $('.pagination .active.pagination-index').data('pageno') + 1;
          getBlockList(current);
          finishLoading();
      }, error: function(result, resp, par) {
          toashWithCloseBtn(result.responseJSON.message);
          finishLoading();
          if (result.status == 401) {
              window.location.href = '../../../login';
          }
      }
  });
}

function initEvent() {
    $('.sale-datepicker').pickadate({
        selectMonths: true,
        selectYears: 15,
        today: 'Today',
        clear: 'Clear',
        close: 'Close',
        format: 'yyyy-mm-dd',
        closeOnSelect: false // Close upon selecting a date,
    });

    $('#search-btn').click(function() {
        getBlockList(1);
    })

    $('#cancel-btn').click(function() {
        $('#select-tag').val('-1');
        $('#select-status').val('-1');
        $('#text-blockName').val('');
        $('#text-blockName').trigger('blur');

        $('#text-new-blockName').val('');
        $('#text-new-blockName').trigger('blur');

        initDate();
        $('select').material_select();

        $('.orderby .icon use').attr('xlink:href', '#icon-paixu-copy');
        $('.orderby').removeClass('active');
        getBlockList(1);
    })


    $('#category-menu-input input, #category-menu-input span.triangle').click(function(e) {
        $('#category-menu .multi-menu').css('min-width', $('#category-input').width() + 'px');
        $('#category-menu').show();
        e.stopPropagation();
    });

    $('html').click(function() {
        $('#category-menu').hide();
    })

    $('#activate-btn').click(function() {
        updateBlock('activate');
    });

    $('#de-activate-btn').click(function() {
        updateBlock('de-activate');
    });

    $('#new-block-btn').click(function() {
        // $('#new-block').openModal({
        //     dismissible: false,
        //     opacity: .5,
        //     inDuration: 300,
        //     outDuration: 200,
        //     startingTop: '4%',
        //     endingTop: '10%'
        // });
        window.open('./detail.html?new=1&blockId=abc');
    });

    $('#new-block .model-yes').click(function() {
        loading();
        
        let param = {};
        param.status = 0;
        param.blockName = $('#text-new-blockName').val();
        if(param.blockName == "") {
            toashWithCloseBtn("Please enter the block name!");
            finishLoading();
            return;
        }
        param.enabled = 1;
        //掉后台接口
        $.ajax({
            type: requestURL.createBlock.method,
            contentType: "application/json",
            url: requestURL.createBlock.url,
            data: JSON.stringify(param),
            dataType: 'json',
            beforeSend: function(request) {
                request.setRequestHeader("token", token);
            },
            success: function(result) {
                Materialize.toast("Create Block Success!", 3000);
                // 获取当前页数
                //let current = $('.pagination .active.pagination-index').data('pageno') + 1;
                $('#new-block').closeModal();
                getBlockList(1);
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

    $('#new-block .model-no').click(function() {
        let oriProductTag = $('#select-product-tag').data('origin-product-id');        
        $('#new-block').closeModal();
    });
}
