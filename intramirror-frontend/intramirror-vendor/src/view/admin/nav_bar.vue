<template>
  <div class="head-bar">
    <router-link class="item" :to="{name:'Confirm'}" active-class="hover">
      <p>CONFIRM<span>{{$store.state.user.orderNum.comfirmed}}</span></p>
    </router-link>
    <router-link class="item" :to="{name:'Pack'}" active-class="hover">
      <p>PACK<span>{{$store.state.user.orderNum.pack}}</span></p>
    </router-link>
    <router-link class="item" :to="{name:'Ready'}" active-class="hover">
      <p>PEADY to SHIP<span>{{$store.state.user.orderNum.peady}}</span></p>
    </router-link>
    <router-link class="item" :to="{name:'Shipped'}" active-class="hover">
      <p>SHIPPED<span>{{$store.state.user.orderNum.shipped}}</span></p>
    </router-link>
  </div>
</template>

<script>
  import {mapMutations} from 'vuex'
  export default {
    data(){
      return {}
    },
    mounted(){
      this.$store.dispatch('getOrderNum').then(res => {
        this.SET_ORDER_NUM(res.data.data);
      })
    },
    methods: {
      ...mapMutations([
        'SET_ORDER_NUM',
      ]),
    }
  }
</script>

<style lang="less" scoped>
  .head-bar {
    height: 55px;
    line-height: 55px;
    border-bottom: 1px solid #9B9B9B;
    position: relative;
    overflow: hidden;
    left: -1px;
    .item {
      width: 26%;
      position: absolute;
      text-align: center;
      font-size: 17px;
      font-weight: 500;
      color: #9E2976;
      background: url("./icon/head_bg.png") no-repeat;
      background-size: 100% 55px;
      z-index: 1;
      p {
        text-indent: -80px;
      }
      &:nth-child(2) {
        left: 25%;
        z-index: 1;
      }
      &:nth-child(3) {
        left: 25*2%;
        z-index: 1;
      }
      &:nth-child(4) {
        left: 25*3%;
        z-index: 1;
      }
      span {
        font-size: 30px;
        position: absolute;
        margin-left: 90px;
      }
    }
    .item.hover {
      background: url("./icon/head_bghover.png") no-repeat;
      background-size: 100% 55px;
      color: #fff;
      z-index: 2;
    }
  }
</style>
