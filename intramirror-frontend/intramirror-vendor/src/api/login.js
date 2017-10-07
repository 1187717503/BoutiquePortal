/**
 * Created by Likun on 2017/6/28.
 */

import HTTP from '../http'
//登录
export function ajaxLogin(username, password) {
  let data = {
    email: username,
    userPwd: password
  };
  return HTTP({
    url: 'login/do_login.htm',
    method: 'post',
    data: data
  });
}
//获取用户信息
export function userInfo() {
  return HTTP({
    url: 'user/getUser.htm',
    method: 'post'
  });
}
