<template>
  <div>
    <div class="loading">
      <div class="loader-inner pacman">
        <div v-for="i in 5"></div>
      </div>
    </div>
    <shade></shade>
  </div>
</template>

<script>
  import shade from '../component/shade.vue'
  export default {
    data(){
      return {}
    },
    components: {
      'shade': shade
    }
  }
</script>

<style lang="less">
  @import "../../assets/css/loaders.css";

  .loading {
    position: fixed;
    display: flex;
    width: 200px;
    height: 200px;
    left: 50%;
    top: 50%;
    margin: -200px/2 0 0 -200px/2;
    align-items: center;
    justify-content: center;
    z-index: 7;
  }
</style>
