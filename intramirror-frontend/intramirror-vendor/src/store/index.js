/**
 * Created by Likun on 2017/6/27.
 */
import Vue from 'vue'
import Vuex from 'vuex'
import user from './modules/user'
import order from './modules/order'
import getters from './getters'
Vue.use(Vuex);
const store = new Vuex.Store({
  modules: {
    user,
    order
  },
  getters
});
export default store
