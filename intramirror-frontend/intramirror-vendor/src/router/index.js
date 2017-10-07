import Vue from 'vue'
import Router from 'vue-router'

import Login from '../view/login/index.vue'
import HomeView from '../view/home/index.vue'
import Admin from '../view/admin/index.vue'
import Pack from '../view/admin/pack.vue'
import PackOrder from '../view/admin/pack_order.vue'
import ConfirmPrint2 from '../view/admin/confirm_print.vue'

import ConfirmPrint from '../view/admin/confirm_order_print.vue'
import PackPrint from '../view/admin/pack_order_print.vue'
import ReadyShipPrint from '../view/admin/ready_ship_print.vue'

import Ready from '../view/admin/ready.vue'
import ReadyShip from '../view/admin/ready_ship.vue'

import Shipped from '../view/admin/shipped.vue'
import Notfound from '../view/404.vue'

Vue.use(Router);

const routes = [
  {
    path: '/',
    redirect: 'admin/confirm'
  },
  {
    path: '/confirmprint',
    name: 'ConfirmPrint',
    component: ConfirmPrint
  },
  {
    path: '/confirmprint2',
    name: 'ConfirmPrint2',
    component: ConfirmPrint2
  },


  {
    path: '/packprint',
    name: 'PackPrint',
    component: PackPrint
  },

  {
    path: '/readyshipprint',
    name: 'ReadyShipPrint',
    component: ReadyShipPrint
  },
  {
    path: '/',
    component: HomeView,
    children: [
      {
        path: 'admin/confirm',
        name: 'Confirm',
        meta: {
          title: 'Order Processing - Confirm Order'
        },
        component: Admin
      },

      {
        path: 'admin/pack',
        name: 'Pack',
        meta: {
          title: 'Order Processing - Pack Order'
        },
        component: Pack
      },
      {
        path: 'admin/packorder/:id',
        name: 'PackOrder',
        meta: {
          title: 'Pack Order'
        },
        component: PackOrder
      },
      {
        path: 'admin/ready',
        name: 'Ready',
        meta: {
          title: 'Order Processing - Ready to Ship'
        },
        component: Ready
      },
      {
        path: 'admin/readyship/:id',
        name: 'ReadyShip',
        meta: {
          title: 'Ready to Ship'
        },
        component: ReadyShip
      },
      {
        path: 'admin/shipped',
        name: 'Shipped',
        meta: {
          title: 'Order Processing - Shipped'
        },
        component: Shipped
      },
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
