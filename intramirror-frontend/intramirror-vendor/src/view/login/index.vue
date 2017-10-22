<template>
  <div class="cyan" style="background: rgb(238, 238, 238) !important">
    <div class="row form-box">
      <div class="col s12 z-depth-4 card-panel im-box">
        <form class="login-form">
          <div class="row">
            <div class="input-field col s12 center">
              <img src="../../assets/images/im-logo.png" alt=""
                   class="circle responsive-img valign profile-image-login">
              <p class="center login-form-text">ADMINISTRATOR PORTAL</p>
            </div>
          </div>
          <div class="row margin">
            <div class="input-field col s12">
              <i class="mdi-social-person-outline prefix im-col"></i>
              <input id="username" type="text" v-model="loginForm.username">
              <label for="username" class="center-align">Username</label>
            </div>
          </div>
          <div class="row margin">
            <div class="input-field col s12">
              <i class="mdi-action-lock-outline prefix im-col"></i>
              <input id="password" type="password" v-model="loginForm.password">
              <label for="password">Password</label>
            </div>
          </div>
          <div class="row">
            <div class="input-field col s12 m12 l12  login-text">
              <input type="checkbox" id="remember-me" v-model="loginForm.remember"/>
              <label for="remember-me">Remember me</label>
            </div>
          </div>
          <div class="row">
            <div class="input-field col s12">
              <a @click="login" class="btn waves-effect waves-light col s12 im-bgcol">Login</a>
            </div>
          </div>
        </form>
      </div>
    </div>
    <loading v-if="isLoading"></loading>
  </div>
</template>

<script>
  import loading from '../component/loading.vue'
  import md5 from 'js-md5'

  export default {
    data() {
      return {
        loginForm: {
          username: '',
          password: '',
          remember: false
        },
        isLoading: false
      }
    },
    created: function () {
      setTimeout(function () {
        $('body').addClass('loaded');
      }, 200);
    },
    methods: {
      login() {
        if ($.trim(this.loginForm.username) === '') {
          Materialize.toast('请输入用户名', 4000);
          return false
        } else if ($.trim(this.loginForm.password) === '') {
          Materialize.toast('请输入密码', 4000);
          return false
        } else {

          this.loginForm.password = md5(this.loginForm.password);
          this.isLoading = true;
          this.$store.dispatch('login', this.loginForm).then(res => {
            if (res.data.status === 1) {
              window.location.href="/";
            } else {
              Materialize.toast(this.errorInfo(res.data.status), 4000);
            }
            this.isLoading = false;
          }).catch(err => {
            console.log(err);
          })
        }
      },
      errorInfo(msg) {
        let str = msg;
        switch (msg) {
          case -1007:
            str='The user name you entered doesn\'t exist!';
            break;
          case -1009:
            str='The password you entered is incorrect!';
            break;
        }
        return str;
      }
    },
    components: {
      'loading': loading
    }
  }
</script>
<style lang="less">
  .cyan {
    width: 100%;
    height: 100%;
    position: absolute;
    overflow: hidden;
    .form-box {
      position: relative;
      top: 50%;
      width: 320px;
      margin-top: calc(-451px / 2);
      .im-box {
        padding: 20px;
        .input-field .prefix ~ label {
          margin-left: 3.8rem;
        }
      }
      .im-col {
        color: #871B55;
      }
      .im-bgcol {
        background: #871B55;
      }
    }
  }
</style>
