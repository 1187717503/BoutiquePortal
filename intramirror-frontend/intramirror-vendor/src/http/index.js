/**
 * Created by Likun on 2017/7/15.
 */

import axios from 'axios'
import router from '../router'
import store from '../store'


const service = axios.create({
  // 基础url前缀
  //baseURL: `http://localhost:8081/`
  //baseURL: 'http://192.168.31.250:8082/'
  //baseURL:'http://test.vendor.intramirror.com:8087/'
  //baseURL: 'http://sha.staging.boutique.intramirror.com:8097/'
  //baseURL: 'http://boutique.intramirror.com:8087/'
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
