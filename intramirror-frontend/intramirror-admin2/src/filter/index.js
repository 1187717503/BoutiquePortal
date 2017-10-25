/**
 * Created by Likun on 2017/7/30.
 * Email 1350612656@qq.com
 * export function name (val...){}
 */

export function ImgArr(e, v) { //获取字符串数组的第一张图片
  if (e) {
    e = e.replace('[', '');
    e = e.replace(']', '');
    e = e.split(',');
    return e[0].replace('"', '').replace('"', '') + v;
  }
}

export function PriceForm(str) { //格式化金额
  if (!str) {
    return 0
  }
  let newStr = "";
  let count = 0;
  str = str.toString();
  if (str.indexOf(".") === -1) {
    for (let i = str.length - 1; i >= 0; i--) {
      if (count % 3 === 0 && count !== 0) {
        newStr = str.charAt(i) + "," + newStr;
      } else {
        newStr = str.charAt(i) + newStr;
      }
      count++;
    }
    str = newStr + ".00"; //自动补小数点后两位
    return str
  }
  else {
    for (let i = str.indexOf(".") - 1; i >= 0; i--) {
      if (count % 3 === 0 && count !== 0) {
        newStr = str.charAt(i) + "," + newStr;
      } else {
        newStr = str.charAt(i) + newStr; //逐个字符相接起来
      }
      count++;
    }
    str = newStr + (str + "00").substr((str + "00").indexOf("."), 3);
    return str
  }
}
export function formDate(date) { //格式化日期
  if (!date) {
    return '　'
  }
  let newDate = new Date(date);
  let month = [
    'January',
    'February',
    'March',
    'April',
    'May',
    'June',
    'July',
    'August',
    'September',
    'October',
    'November',
    'December'
  ];
  let Y = newDate.getFullYear(); //年
  let M = month[newDate.getMonth()]; //月
  let D = newDate.getDate(); //日
  let H = newDate.getHours(); //时
  let T = newDate.getMinutes(); //分
  return D + ' ' + M + ' ' + Y + ' ' + H + ':' + T
}
