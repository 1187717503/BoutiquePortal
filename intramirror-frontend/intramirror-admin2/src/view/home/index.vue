<template>
  <div class="base-box">
    <div class="head-bar">
      <span class="mdi mdi-menu icon" @click="showNav"></span>
      <p v-wechat-title="$route.meta.title">{{$route.meta.title}}</p>
    </div>
    <div class="left-nav" :class="{'show-nav':navShow}">
      <div class="user-info">
        <div class="pic">
          <img :src='userInfo.userImage' alt="">
        </div>
        <p class="email">{{userInfo.email}}<i class="mdi mdi-menu-down" @click="showBtn"></i></p>
        <button class="waves-effect waves-light btn" v-if="btnShow" @click="logout">Sign out</button>
      </div>
      <div class="nav-list">
        <div class="item">
          <i class="mdi mdi-tag"></i>
          <span>IM Pricing Rule</span>
          <div class="tow-nav">
            <router-link to="/admin/default" active-class="hover">Default Rule</router-link>
            <router-link to="/admin/active" active-class="hover">Active Rule</router-link>
            <router-link to="/admin/pending" active-class="hover">Pending Rule</router-link>
          </div>
        </div>
        <div class="item">
          <i class="mdi mdi-tag"></i>
          <span>Boutique Pricing Rule</span>
          <div class="tow-nav">
            <router-link to="/vendor/active" active-class="hover">Active Rule</router-link>
            <router-link to="/vendor/pending" active-class="hover">Pending Rule</router-link>
          </div>
        </div>
        <div class="item">
          <router-link to="/error" active-class="hover">
            <i class="mdi mdi-tag"></i>
            <span>API Error Processing</span>
          </router-link>
        </div>
        <div class="item">
          <i class="mdi mdi-tag"></i>
          <span>Order Management</span>
          <div class="tow-nav">
            <router-link to="/create/order" active-class="hover">Create Order</router-link>
          </div>
        </div>
        <div class="item">
          <i class="mdi mdi-tag"></i>
          <span>Merchandise Management</span>
          <div class="tow-nav">
            <router-link to="/merchandise/product" active-class="hover">Product Management</router-link>
          </div>
        </div>
      </div>
    </div>
    <router-view class="main"></router-view>
    <div class="shade" :class="{'show-shade':navShow}" v-if="navShow" @click="showNav"></div>
  </div>
</template>

<script>

  export default {
    data() {
      return {
        navShow: false,
        btnShow: false,
        userInfo: ''
      }
    },
    watch: {
      // 如果路由有变化，会再次执行该方法
      '$route': 'loadPage'
    },
    mounted: function () {
      this.$store.dispatch('getUser').then(res => {
        if (res.data.status === 1) {
          this.userInfo = res.data.user
        } else {
          Materialize.toast('获取用户信息出错', 4000);
        }
      })
    },
    methods: {
      logout() {
        localStorage.removeItem('token');
        sessionStorage.removeItem("token");
        this.$router.push({path: '/login'})
      },
      showNav() {
        this.navShow = !this.navShow;
        this.btnShow = false;
      },
      showBtn() {
        this.btnShow = !this.btnShow;
      },
      loadPage() {
        this.navShow = false;
        this.btnShow = false;
      }
    }
  }
</script>

<style lang="less" scoped>
  .base-box {
    .head-bar {
      position: relative;
      width: 100%;
      height: 64px;
      padding: 0 20px;
      background-color: #fff;
      line-height: 64px;
      box-shadow: 0 0 4px 0 rgba(0, 0, 0, 0.12), 0 4px 4px 0 rgba(0, 0, 0, 0.24);
      z-index: 2;
      .icon {
        font-size: 22px;
        cursor: pointer;
        color: #871B55;
        float: left;
      }
      p {
        float: left;
        font-size: 20px;
        font-weight: 500;
        margin-left: 27px;
        color: #871B55;
        color: rgba(0, 0, 0, 0.87);
      }

    }
    .left-nav {
      position: fixed;
      top: 0;
      left: 0;
      width: 260px;
      height: 100%;
      z-index: 100;
      background: #fff;
      transition-duration: 200ms;
      box-shadow: 0 0 16px 0 rgba(0, 0, 0, 0.18), 0 16px 16px 0 rgba(0, 0, 0, 0.24);
      transform: translate3d(-(260+16px), 0, 0);
      .user-info {
        width: 100%;
        height: 144px;
        background-color: #871B55;
        padding: 24px 0 0 24px;
        .pic {
          width: 56px;
          height: 56px;
          background: #E0E0E0;
          border-radius: 56px;
          overflow: hidden;
        }
        .email {
          margin-top: 28px;
          font-size: 13px;
          color: #fff;
          i {
            float: right;
            padding-right: 23px;
            cursor: pointer;
            margin-top: -4px;
            font-size: 18px;
          }
        }
        button {
          background: #fff;
          color: #000;
          margin-top: -46px;
          width: 180px;
        }
      }
    }
    .nav-list {
      .item {
        width: 260px;
        display: inline-block;
        height: 60px;
        line-height: 60px;
        color: #4A4A4A;
        font-weight: 500;
        font-size: 15px;
        position: relative;
        i {
          font-size: 20px;
          float: left;
          margin-left: 20px;
          color: #4A4A4A;
        }
        span {
          margin-left: 34px;
          float: left;
          color: #4A4A4A;
        }
        .tow-nav {
          width: 100%;
          position: relative;
          top: 0;
          left: 0;
          line-height: 40px;
          font-size: 13px;
          a {
            width: 100%;
            display: inline-block;
            text-indent: 100px;
            color: rgba(0, 0, 0, 0.87);
          }
          .hover {
            color: #00B0FF;
          }
        }
      }
      .hover {
        color: #00B0FF;
        i {
          color: #00B0FF;
        }
        span {
          color: #00B0FF;
        }
      }
    }
    .show-nav {
      transform: translate3d(0, 0, 0);
    }
    .shade {
      position: absolute;
      left: 0;
      right: 0;
      bottom: 0;
      top: 0;
      z-index: 1;
      background: rgba(0, 0, 0, .6);
      opacity: 0;
      transition-duration: 200ms;
    }
    .show-shade {
      opacity: 1;
      z-index: 99;
    }
    .main {
      padding: 14px;
    }
  }
</style>
