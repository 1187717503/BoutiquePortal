
function loading() {
    $('.load-data-holder').toggleClass("active");
    $('body').addClass('hidden-scroll');
}

function finishLoading() {
    $('.load-data-holder').toggleClass("active");
    $('body').removeClass('hidden-scroll');
}

function toashWithCloseBtn(message) {
    $('.close-toast').unbind("click");
    Materialize.toast('<span>' + message + '</span><a class="btn close-toast" href="#">Close<a>', 1800000, 'egToast-warn');
    $('.close-toast').click(function() {
        $(this).parent().remove();
    });
}

function updatePagination(pagesize, pageno, totalsize, pageAction, param) {
    $('.pagination').empty();

    let pageinfo = {}
    pageinfo.totalPage = totalsize;
    pageinfo.currPage = pageno;
    pageinfo.list = [];
    let listData = pageinfo.list;

	console.log('pagesize=%d,pageno=%d,totalsize=%d.',pagesize, pageno, totalsize);

    if (totalsize == 0) {
        return;
    }

    if (pageno > totalsize + 1) {
        pageno = totalsize + 1;
    }

    if (pageno < 0) {
        pageno = 0;
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
        if (4 - totalsize + pageno > 4) {
            listData.push({"no": totalsize + 1});
            listData[4 - totalsize + pageno].active = 1;
        } else {
            listData[4 - totalsize + pageno].active = 1;
        }
        
    } else {
        listData.push({"no": pageno - 2});
        listData.push({"no": pageno - 1});
        listData.push({"no": pageno, "active": 1});
        listData.push({"no": pageno + 1});
        listData.push({"no": pageno + 2});
    }

    console.log(pageinfo);
    
    $('#tmpl-pagination').tmpl({page: pageinfo}).appendTo('.pagination');

    $('#page-size').val(localStorage.getItem('product-page-size'));
    $('#page-size').material_select();

    $('#page-size').change(function() {
        localStorage.setItem('product-page-size', $(this).val());
        pageAction(1, param);
    }) 

    $('.pagination-index').click(function() {
        pageAction($(this).data('pageno') + 1, param);
    })
}