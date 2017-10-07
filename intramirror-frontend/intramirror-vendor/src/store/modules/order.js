/**
 * Created by Likun on 2017/8/8.
 * Email 1350612656@qq.com
 */
import {getOrderList} from '../../api/order'
const order = {
  state: {
    list:[]
  },
  mutations:{
    GET_ORDER(state, data){
      state.list = data
    }
  },
  actions:{
    getOrderList({commit},data){
      return new Promise((resolve) => {
        getOrderList(data).then(res=>{
          commit('GET_ORDER',res.data.data)
          resolve(res)
        })
      })
    }
  }
};
export default order;
