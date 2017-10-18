<template>
  <div id="webapp">
    <div id="loader-wrapper">
      <div id="loader"></div>
      <div class="loader-section section-left"></div>
      <div class="loader-section section-right"></div>
    </div>
    <router-view></router-view>
  </div>
</template>

<script>
  export default {
    name: 'app',
    data() {
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
</style>
