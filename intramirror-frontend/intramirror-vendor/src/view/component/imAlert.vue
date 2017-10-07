<template>
  <div id="card-alert" class="card red im-alert" v-if="data.show">
    <div class="card-content white-text">
      <p>{{data.text}}</p>
    </div>
    <button type="button" class="close white-text">
      <span @click="data.show = false">×</span>
    </button>
  </div>
</template>

<script>
  export default {
    data(){
      return {}
    },
    watch: {
      data(e){
        if (e.show) {
          setTimeout(() => {
            e.show = false;
          }, 4000)
        }
      }
    },
    props: ['data']
  }
</script>

<style lang="less" scoped>
  .im-alert {
    position: fixed;
    top: 30px;
    left: 50%;
    width: 300px;
    margin-left: -300px/2;
    z-index: 9;
  }
</style>
