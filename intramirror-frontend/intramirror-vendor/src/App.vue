<template>
  <div id="webapp">
    <div id="loader-wrapper">
      <div id="loader"></div>
      <div class="loader-section section-left"></div>
      <div class="loader-section section-right"></div>
    </div>
    <router-view></router-view>
    <div v-wechat-title="$route.meta.title"></div>
  </div>
</template>

<script>
  export default {
    name: 'app',
    data(){
      return {}
    },
    created: function () {
      setTimeout(function () {
        $('body').addClass('loaded');
      }, 200);
    }
  }
</script>

<style lang="less">
  @import "assets/css/materialize.css";
  @import "assets/css/style.css";
  @import "assets/css/reset.css";
  @import "assets/mdi/css/materialdesignicons.css";
  .select-wrapper input.select-dropdown{
    border-bottom:2px solid #00a6ce;
    line-height:36px;
    height:36px;
  }
  .select-wrapper span.caret{
    color: #00a6ce;
  }
  input[type=text]{
    border-bottom:2px solid #00A6CE;
    line-height:36px;
    height:36px;
  }
  input[type=text]:focus:not([readonly]){
    border-bottom:2px solid #00A6CE;
    box-shadow:none;
  }
  .input-field label{
    left:0;
    font-size: 16px;
  }
  input[type=text]:focus:not([readonly]) + label{
    color: #4a4a4a;
  }
  input[type=text].valid{
    border-bottom:2px solid #00A6CE;
    box-shadow:none;
  }
  .input-field .prefix.active{
    color: #000;
  }
</style>
