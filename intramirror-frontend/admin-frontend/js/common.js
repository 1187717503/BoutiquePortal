/*return value: 
 *  1:全不空且合法
 *  2:全空
 *  -1:不合法
 */
function checkNumRange(nMin, nMax, sFiledName){

    var rInt100 = /^(100|\d{1,2})$/;
    var rInt = /^-?\d+$/;
    var ruFloat = /^\d+(\.\d+)?$/;

    let nResult = -1;
    //全空 or 全不空
    if (nMin && nMax) {
        //数字类型
        if ($.isNumeric(nMin) && $.isNumeric(nMax)) {
            //右值 > 左值

            if(sFiledName == "Price"){
                nMax = parseFloat(nMax);
                nMin = parseFloat(nMin);
            }else{
                nMax = parseInt(nMax);
                nMin = parseInt(nMin);
            }
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
                } else if (sFiledName == "Price") {
                    if(ruFloat.test(nMax) && ruFloat.test(nMin)) {
                        nResult = 1;
                    }else {
                        toashWithCloseBtn("The value of "+ sFiledName +" should be positive float.");
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
            // only add one value to let it know it's an error.
            obj.dStart = Date.parse('0000-00-00 00:00:00') / 1000;
        } else {
            obj.dStart = Date.parse(dStart + ' 00:00:00') / 1000;
            obj.dEnd = Date.parse(dEnd + ' 23:59:59')  / 1000;
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
        // only add one value to let it know it's an error.
        obj.dStart = Date.parse('0000-00-00 00:00:00') / 1000;
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

    if (pageno <= 4 ) {
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


function Base64() {  

    // private property
    _keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

    // public method for encoding  
    this.encode = function (input) {
        var output = "";
        var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
        var i = 0;
        input = _utf8_encode(input);
        while (i < input.length) {
            chr1 = input.charCodeAt(i++);
            chr2 = input.charCodeAt(i++);
            chr3 = input.charCodeAt(i++);
            enc1 = chr1 >> 2;
            enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
            enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
            enc4 = chr3 & 63;
            if (isNaN(chr2)) {
                enc3 = enc4 = 64;
            } else if (isNaN(chr3)) {
                enc4 = 64;
            }
            output = output +
            _keyStr.charAt(enc1) + _keyStr.charAt(enc2) +
            _keyStr.charAt(enc3) + _keyStr.charAt(enc4);
        }
        return output;
    }

    // public method for decoding
    this.decode = function (input) {  
        var output = "";
        var chr1, chr2, chr3;
        var enc1, enc2, enc3, enc4;
        var i = 0;
        input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");
        while (i < input.length) {
            enc1 = _keyStr.indexOf(input.charAt(i++));
            enc2 = _keyStr.indexOf(input.charAt(i++));
            enc3 = _keyStr.indexOf(input.charAt(i++));
            enc4 = _keyStr.indexOf(input.charAt(i++));
            chr1 = (enc1 << 2) | (enc2 >> 4);
            chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
            chr3 = ((enc3 & 3) << 6) | enc4;
            output = output + String.fromCharCode(chr1);
            if (enc3 != 64) {
                output = output + String.fromCharCode(chr2);
            }
            if (enc4 != 64) {
                output = output + String.fromCharCode(chr3);
            }
        }
        output = _utf8_decode(output);
        return output;
    }

    // private method for UTF-8 encoding
    _utf8_encode = function (string) {
        string = string.replace(/\r\n/g,"\n");
        var utftext = "";
        for (var n = 0; n < string.length; n++) {
            var c = string.charCodeAt(n);
            if (c < 128) {
                utftext += String.fromCharCode(c);
            } else if((c > 127) && (c < 2048)) {
                utftext += String.fromCharCode((c >> 6) | 192);
                utftext += String.fromCharCode((c & 63) | 128);
            } else {
                utftext += String.fromCharCode((c >> 12) | 224);
                utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                utftext += String.fromCharCode((c & 63) | 128);
            }
        }
        return utftext;
    }

    // private method for UTF-8 decoding
    _utf8_decode = function (utftext) {
        var string = "";
        var i = 0;
        var c = c1 = c2 = 0;
        while ( i < utftext.length ) {
            c = utftext.charCodeAt(i);
            if (c < 128) {
                string += String.fromCharCode(c);
                i++;
            } else if((c > 191) && (c < 224)) {
                c2 = utftext.charCodeAt(i+1);
                string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
                i += 2;
            } else {
                c2 = utftext.charCodeAt(i+1);  
                c3 = utftext.charCodeAt(i+2);  
                string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
                i += 3;
            }
        }
        return string;
    }
}