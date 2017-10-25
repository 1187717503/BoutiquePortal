//
//                            _ooOoo_
//                           o8888888o
//                           88" . "88
//                           (| -_- |)
//                            O\ = /O
//                        ____/`---'\____
//                      .   ' \\| |// `.
//                       / \\||| : |||// \
//                     / _||||| -:- |||||- \
//                       | | \\\ - /// | |
//                     | \_| ''\---/'' | |
//                      \ .-\__ `-` ___/-. /
//                   ___`. .' /--.--\ `. . __
//                ."" '< `.___\_<|>_/___.' >'"".
//               | | : `- \`.;`\ _ /`;.`/ - ` : | |
//                 \ \ `-. \_ __\ /__ _/ .-` / /
//         ======`-.____`-.___\_____/___.-`____.-'======
//                            `=---='
//
//         .............................................
//                  佛祖保佑             永无BUG
//          佛曰:
//                  写字楼里写字间，写字间里程序员；
//                  程序人员写程序，又拿程序换酒钱。
//                  酒醒只在网上坐，酒醉还来网下眠；
//                  酒醉酒醒日复日，网上网下年复年。
//                  但愿老死电脑间，不愿鞠躬老板前；
//                  奔驰宝马贵者趣，公交自行程序员。
//                  别人笑我忒疯癫，我笑自己命太贱；
//                  不见满街漂亮妹，哪个归得程序员？
//
import Vue from 'vue'
import App from './App.vue'
import axios from './http'
import router from './router'
import store from './store'
import Vuetify from 'vuetify'
import * as filters from './filter' //全局过滤器

Vue.use(require('vue-wechat-title'));
//注册filter
Object.keys(filters).forEach(key => {
  Vue.filter(key, filters[key])
});
Vue.use(Vuetify);

Vue.config.productionTip = false;

router.beforeEach((to, from, next) => {
  if (!to.matched.some(record => record.meta.isLogin)) {
    if (store.state.user.token === '' || !store.state.user.token) {
      next({
        path: '/login'
      })
    } else {
      next();
    }
  } else {
    next();
  }

});
/* eslint-disable no-new */
new Vue({
  el: '#webapp',
  router,
  axios,
  store,
  template: '<App/>',
  components: {App}
});
