<template>
  <div class="foot-bar">
    <router-link class="item" :to="{name:'Confirm'}" active-class="hover">
    </router-link>
    <router-link class="item" :to="{name:'Pack'}" active-class="hover">
    </router-link>
    <router-link class="item" :to="{name:'Ready'}" active-class="hover">
    </router-link>
    <router-link class="item" :to="{name:'Shipped'}" active-class="hover">
    </router-link>
  </div>
</template>

<script>
  export default {}
</script>

<style lang="less">
  .foot-bar {
    width: 100%;
    height: 46px;
    position: relative;
    left:-1px;
    clear:both;
    .item {
      width: 26%;
      height: 46px;
      position: absolute;
      background: url("./icon/foot_bg.png") no-repeat;
      background-size: 100% 46px;
      z-index: 1;
      &:nth-child(2) {
        left: 25%;
        z-index: 1;
      }
      &:nth-child(3) {
        left: 25*2%;
        z-index: 1;
      }
      &:nth-child(4) {
        width: 25%;
        left: 25*3%;
        z-index: 1;
      }
    }
    .item.hover {
      background: url("./icon/foot_bghover.png") no-repeat;
      background-size: 100% 46px;
      z-index:2;
    }
  }
</style>
