<template>
  <div class="im-page">
    <p @click="item===1?showPage = false:showPage = true"><span>Page:</span>{{value}} <i class="mdi mdi-menu-down"></i>
    </p>
    <ul v-if="showPage">
      <li v-for="i in item" v-on:click.prevent="$emit('input', i);showPage = false">{{i}}</li>
    </ul>
  </div>
</template>

<script>
  export default{
    data(){
      return {
        showPage: false
      }
    },
    watch: {
      showPage(val){
        if (val) {
          document.body.onclick = ((event) => {
            if (event.srcElement.localName !== 'li' && event.srcElement.localName !== 'p' && event.srcElement.localName !== 'i') {
              this.showPage = false
            }
          })
        }
      }
    },
    props: {
      length: {
        type: Number,
        default: 0
      },
      value: {
        type: Number,
        default: 0
      }
    },
    computed: {
      item(){
        return this.length
      }
    }
  }
</script>

<style lang="less" scoped>
  .im-page {
    width: 80px;
    font-size: 16px;
    text-align: center;
    cursor: pointer;
    position: relative;
    p {
      line-height: 32px;
      position: relative;
      font-size: 12px;
      color: rgba(0, 0, 0, .54);
      i {
        position: absolute;
        right: 6px;
        font-size: 23px;
      }
      span {
        position: absolute;
        left: 0;
      }
    }
    ul {
      width: 80px;
      position: absolute;
      top: 0;
      background: #fff;
      z-index: 6;
      max-height: 200px;
      overflow: auto;
      box-shadow: 0 5px 5px -3px rgba(0, 0, 0, .2), 0 8px 10px 1px rgba(0, 0, 0, .14), 0 3px 14px 2px rgba(0, 0, 0, .12);
      li {
        line-height: 36px;
        background: #fff;
        &:hover {
          background: #eee;
        }
      }
    }
  }
</style>
