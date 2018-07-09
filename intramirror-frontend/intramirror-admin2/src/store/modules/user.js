/**
 * Created by Likun on 2017/6/27.
 */

import {ajaxLogin, userInfo} from '@/api/login'

const user = {
  state: {
    token: localStorage.token ? localStorage.token : sessionStorage.token
  },
  mutations: {},
  actions: {
    login({commit}, data){
      return new Promise((resolve, reject) => {
        ajaxLogin(data.username, data.password).then(res => {
          if (res.data.status === 1) {
            if (data.remember) {
              localStorage.token = res.data.token;
            } else {
              sessionStorage.token = res.data.token;
            }
          }
          resolve(res)
        }).catch(err => {
          reject(err)
        })
      })
    },
    getUser({commit}){
      return new Promise((resolve, reject) => {
        userInfo().then(res => {
          resolve(res)
        }).catch(err => {
          reject(err)
        })
      })
    }
  }
};

export default user;
