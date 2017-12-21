/**
 * Created by Likun on 2017/6/27.
 */

import {ajaxLogin, userInfo} from '@/api/login'
import {getOrderCount} from '../../api/order'

const user = {
  state: {
    token: localStorage.token ? localStorage.token : sessionStorage.token,
    orderNum: {
      comfirmed: 0,
      pack: 0,
      peady: 0,
      shipped: 0,
    }
  },
  mutations: {
    SET_TOKEN(state, data){
      state.token = data
    },
    SET_ORDER_NUM(state, data){
      state.orderNum.comfirmed = data.comfirmed;
      state.orderNum.pack = data.pack;
      state.orderNum.peady = data.readyToship;
      state.orderNum.shipped = data.shipped;
    }
  },
  actions: {
    login({commit}, data){
      return new Promise((resolve, reject) => {
        ajaxLogin(data.username, data.password).then(res => {
          if (res.data.status === 1) {
            console.log(data)
            if (data.remember) {
              localStorage.token = res.data.token;
            } else {
              sessionStorage.token = res.data.token;
              localStorage.removeItem('token');
            }
          }
          resolve(res)
        }).catch(err => {
          reject(err)
        })
      })
    },
    setToken({commit}, token){
      commit('SET_TOKEN', token);
    },
    getUser(){
      return new Promise((resolve, reject) => {
        userInfo().then(res => {
          resolve(res)
        }).catch(err => {
          reject(err)
        })
      })
    },
    getOrderNum(){
      return new Promise((resolve) => {
        getOrderCount().then(res => {
          resolve(res)
        })
      })
    }
  }
};

export default user;
