/**
 * Created by Likun on 2017/7/15.
 */

import axios from 'axios'
import router from '../router'
import store from '../store'
const service = axios.create({
  // 基础url前缀
  //baseURL: `http://localhost:8081/` //本地
  //baseURL: 'http://192.168.31.250:8081/'
  //baseURL: 'http://test.admin.intramirror.com:8085/' //测试环境
  //baseURL: 'http://staging.admin.intramirror.com:8085/' //预发布环境
  //baseURL: 'http://admin2.intramirror.com:8085/' //正式环境
  //baseURL:'http://sha.staging.admin2.intramirror.com:8096/'
  //baseURL: 'http://sh.admin2.intramirror.com/'
  baseURL:process.env.BASE_URL
});

//http request 拦截器
service.interceptors.request.use(
  config => {
    if (store.getters.token) {
      config.headers['token'] = store.getters.token
    }
    return config;
  },
  err => {
    return Promise.reject(err);
  });
service.interceptors.response.use(
  response => {
    return response;
  },
  error => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          router.replace({
            path: '/login'
          })
      }
    }
    return Promise.reject(error.response.data)
  }
);

export default service;
