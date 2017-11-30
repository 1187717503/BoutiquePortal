/*return value: 
 *  1:全不空且合法
 *  2:全空
 *  -1:不合法
 */
function checkNumRange(nMin, nMax, sFiledName){

    var rInt100 = /^(100|\d{1,2})$/;
    var rInt = /^-?[1-9]\d*$/;

    let nResult = -1;
    //全空 or 全不空
    if (nMin && nMax) {
        //数字类型
        if ($.isNumeric(nMin) && $.isNumeric(nMax)) {
            //右值 > 左值
            nMax = parseInt(nMax);
            nMin = parseInt(nMin);
            if (nMax >= nMin) {
                // discount是0~100
                if (sFiledName == "Boutique" || sFiledName == "IM") {
                    if(rInt100.test(nMax) && rInt100.test(nMin)) {
                        nResult = 1;
                    }else {
                        toashWithCloseBtn("The value of "+ sFiledName +" should be a positive integer and between 0 ~ 100.");
                        nResult = -1;
                    }
                } else if (sFiledName == "Stock") {
                    if(rInt.test(nMax) && rInt.test(nMin)) {
                        nResult = 1;
                    }else {
                        toashWithCloseBtn("The value of "+ sFiledName +" should be positive integer.");
                        nResult = -1;
                    }
                } else {
                    nResult = 1;
                }
            }else {
                toashWithCloseBtn("The max value should be bigger than min value.");
                nResult = -1;
            }
        }else {
            toashWithCloseBtn("The value of "+ sFiledName +" should be number.");
            nResult = -1;
        }
    } else if (nMin == "" && nMax == "") {
        nResult = 2;
    } else {
        toashWithCloseBtn("All the field of "+ sFiledName +" should be fulfilled.");
        nResult = -1;
    }
    return nResult;
}

/*
 * 日期格式转换 
 */
format = function date2str(x, y) {
    var z = {
        M: x.getMonth() + 1,
        d: x.getDate(),
        h: x.getHours(),
        m: x.getMinutes(),
        s: x.getSeconds()
    };
    y = y.replace(/(M+|d+|h+|m+|s+)/g, function(v) {
        return ((v.length > 1 ? "0" : "") + eval('z.' + v.slice(-1))).slice(-2)
    });

    return y.replace(/(y+)/g, function(v) {
        return x.getFullYear().toString().slice(-v.length)
    });
}

function transDateRange(dStart, dEnd, sFiledName) {
    let obj = {};
    if (dStart && dEnd) {
        if(dStart > dEnd) {
            toashWithCloseBtn("For the "+ sFiledName +", the start date should be smaller than end date.");
        } else {
            obj.dStart = Date.parse(dStart + ' 00:00:00') / 1000;
            obj.dEnd = Date.parse(dEnd + ' 23:59:59')  / 1000;
            console.log(dStart + ' 00:00:00');
            console.log(dEnd + ' 23:59:59');
        }
    } else if (dStart == "" && dEnd == "") {
        //时间要取到月
        // let curDate = format(new Date(), 'yyyy-MM-dd') + ' 23:59:59';
        // console.log("curDate: " + curDate);

        // let endDate = new Date(curDate);
        // obj.dEnd = Date.parse(endDate) / 1000;

        // endDate.setMonth(endDate.getMonth() - 1);
        // let startDate = format(endDate, 'yyyy-MM-dd') + ' 00:00:00';
        // console.log("startDate: " + startDate);

        // obj.dStart = Date.parse(startDate) / 1000;
    } else {
        toashWithCloseBtn("All the field of "+ sFiledName +" should be fulfilled.");
    }
    return obj;
}

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

    $('.pagination-index').click(function() {
        pageAction($(this).data('pageno') + 1, param);
    })
}