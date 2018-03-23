import Vue from 'vue'
import Router from 'vue-router'

import Login from '../view/login/index.vue'
import HomeView from '../view/home/index.vue'
import Default from '../view/admin/default.vue'
import Active from '../view/admin/active.vue'
import Pending from '../view/admin/pending.vue'
import ActiveKids from '../view/admin/activeKids.vue'
import PendingKids from '../view/admin/pendingKids.vue'

import VendorActive from '../view/vendor/active.vue'
import VendorPending from '../view/vendor/pending.vue'
import VendorActiveKids from '../view/vendor/activeKids.vue'
import VendorPendingKids from '../view/vendor/pendingKids.vue'

import ApiError from '../view/error/index.vue'

import CreateOrder from '../view/order/createOrder.vue'

import Notfound from '../view/404.vue'

Vue.use(Router);

const routes = [
  {
    path: '/',
    redirect: 'admin/default'
  },
  {
    path: '/',
    component: HomeView,
    children: [
      {
        path: 'admin/active',
        name: 'Active',
        meta: {
          title: 'IM Pricing Rule - Active'
        },
        component: Active
      },
      {
        path: 'admin/pending',
        name: 'Pending',
        meta: {
          title: 'IM Pricing Rule - Pending'
        },
        component: Pending
      },
      {
        path: 'admin/default',
        name: 'Default',
        meta: {
          title: 'IM Pricing Rule - Default'
        },
        component: Default
      },
      ,
      {
        path: 'admin/activeKids',
        name: 'Active Kids',
        meta: {
          title: 'IM Pricing Rule - Active Kids'
        },
        component: ActiveKids
      }
      ,
      {
        path: 'admin/pendingKids',
        name: 'Pending Kids',
        meta: {
          title: 'IM Pricing Rule - Pending Kids'
        },
        component: PendingKids
      },
      {
        path: 'vendor/active',
        name: 'VendorActive',
        meta: {
          title: 'Boutique Pricing Rule - Active'
        },
        component: VendorActive
      },
      {
        path: 'vendor/pending',
        name: 'VendorPending',
        meta: {
          title: 'Boutique Pricing Rule - Pending'
        },
        component: VendorPending
      },
      {
        path: 'vendor/activeKids',
        name: 'VendorActiveKids',
        meta: {
          title: 'Boutique Pricing Rule - Active Kids'
        },
        component: VendorActiveKids
      },
      {
        path: 'vendor/pendingKids',
        name: 'VendorPendingKids',
        meta: {
          title: 'Boutique Pricing Rule - Pending Kids'
        },
        component: VendorPendingKids
      },
      {
        path: 'error',
        name: 'ApiError',
        meta: {
          title: 'API Error Processing'
        },
        component: ApiError
      },
      {
        path: 'create/order',
        name: 'CreateOrder',
        meta: {
          title: 'Order Management - Create Order'
        },
        component: CreateOrder
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    meta: {
      title: 'Login',
      isLogin: true
    },
    component: Login
  },
  {
    path: '*',
    name: 'Notfound',
    meta: {
      title: '404'
    },
    component: Notfound
  },
];
export default new Router({
    mode: 'history',
    routes: routes,
  }
)
