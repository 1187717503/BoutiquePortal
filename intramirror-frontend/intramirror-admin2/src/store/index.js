/**
 * Created by Likun on 2017/6/27.
 */
import Vue from 'vue'
import Vuex from 'vuex'
import user from './modules/user'
import main from './modules/main'
import getters from './getters'
Vue.use(Vuex);
const store = new Vuex.Store({
  modules: {
    user,
    main
  },
  getters
});
export default store
