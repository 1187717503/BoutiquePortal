<template>
  <div>
    <div class="main-box">
      <nav-bar></nav-bar>
      <div class="btn-pack">
        <router-link class="waves-effect waves-light btn" :to="{name:'PackOrder',params: {id:'new' }}">
          <i class="mdi mdi-package-down left"></i>
          PACK
        </router-link>
        <div class="input-field">
          <label>Sort By</label>
          <select>
            <option v-for="item in sortData" :value="item.id">{{item.name}}</option>
          </select>
        </div>
      </div>
      <div class="row-list">
        <div class="list" v-for="item in $store.state.order.list">
          <div class="pic">
            <img
              :src="item.cover_img,'?x-oss-process=image/resize,m_fill,w_57,h_73,limit_0/auto-orient,0/quality,q_90' | ImgArr">
          </div>
          <div class="info">
            <div class="product-id">
              <div>
                <p>{{item.brandID}}</p>
                <span>Product ID</span>
              </div>
              <div>
                <p>{{item.colorCode}}</p>
                <span>Color</span>
              </div>
              <div>
                <p>{{item.size}}</p>
                <span>Size</span>
              </div>
              <div>
                <p>{{item.order_line_num}}</p>
                <span>Order Line No.</span>
              </div>
              <div>
                <p>{{item.created_at | formDate}}</p>
                <span>Order Date</span>
              </div>
              <div>
                <p>{{item.confirmed_at | formDate}}</p>
                <span>Confirm Date</span>
              </div>
              <div>
                <p>{{item.est_ship_date | formDate}}</p>
                <span>Estimated Ship Date</span>
              </div>
              <div>
                <p>{{item.geography_name}}</p>
                <span>Destination</span>
              </div>
            </div>
          </div>
          <div class="btns">
            <div v-if="!item.oeComments || item.oeStatus!==1">
              <i class="mdi mdi-printer" @click="rePrintOrder(item.order_line_num)"></i>
              <p>PRINT ORDER</p>
            </div>
          </div>
        </div>
      </div>
      <foot-bar></foot-bar>
    </div>
    <loading v-if="isLoading"></loading>
  </div>
</template>

<script>
  import {mapMutations} from 'vuex'
  import navBar from './nav_bar.vue'
  import footBar from './foot_bar.vue'
  import loading from '../component/loading.vue'
  import {} from '../../api/order'
  export default {
    data(){
      return {
        isLoading: true,
        listData: [],

        sortData: [
          {id: 'order_line_num', name: 'Order Line No.'},
          {id: 'created_at', name: 'Order Date'},
          {id: 'confirmed_at', name: 'Confirm Date'},
          {id: 'est_ship_date', name: 'Estimated Ship Date'},
          {id: 'geography_name', name: 'Destination'}
        ]
      }
    },
    mounted(){
      $('select').material_select();//初始化select
      $('.btn-pack .input-field select').on('change', (e) => {
        this.loadList(e.target.value)
      });
      this.loadList('order_line_num');
    },
    methods: {
      ...mapMutations([
        'GET_ORDER'
      ]),
      rePrintOrder(num) {
        var rePrintPage = window.open('/confirmprint2?num=' + num + '&sta=2');
      },
      loadList(sort){
        let data = {
          'status': '2',
          'sortByName': sort
        };
        this.isLoading = true;
        this.$store.dispatch('getOrderList', data).then(() => {
          this.isLoading = false
        })
      }
    },
    components: {
      'navBar': navBar,
      'footBar': footBar,
      loading
    }
  }
</script>
<style lang="less">
  .main-box {
    .btn-pack {
      .input-field {

        input.select-dropdown {
          border-bottom: 2px solid #00a6ce;
          line-height: 30px;
          height: 30px;
        }
        span {
          color: #00a6ce;
          top: 10px;
        }
      }
    }
  }
</style>
<style lang="less" scoped>
  .main-box {
    width: 100%;
    background-color: #FAFAFA;
    box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
    border-left: 1px solid #9b9b9b;
    border-right: 1px solid #9b9b9b;
    .btn-pack {
      padding: 15px 44px 15px 44px;
      border-bottom: 1px solid #9b9b9b;
      position: relative;
      .btn {
        width: 227.48px;
        line-height: 41.07px;
        border-radius: 2px;
        background-color: #871B55;
        margin-top: 6px;
        box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
        i {
          position: relative;
          left: 45px;
          line-height: 37px;
        }
      }
      .input-field {
        display: inline-block;
        position: absolute;
        right: 30px;
        width: 180px;
        label {
          position: absolute;
          top: -14px;
          left: 0;
          font-size: 12px;
        }
      }
    }
    .row-list {
      .list {
        border-bottom: 1px solid #9b9b9b;
        height: 96px;
        padding: 12px 0;
        display: flex;
      }
      .pic {
        width: 60px;
        margin-left: 20px;
      }
      .info {
        margin-top: 12px;
        width: 100%;
        padding-left: 26px;
        .product-id {
          margin-left: -10px;
          display: flex;
          div {
            text-align: center;
            width: 9%;
            p {
              font-size: 16px;
              overflow: hidden;
              white-space: nowrap;
              text-overflow: ellipsis;
            }
            span {
              font-size: 12px;
            }
          }
          div:nth-child(2), div:nth-child(3) {
            width: 5%;
          }
          div:nth-child(4), div:nth-child(5), div:nth-child(6), div:nth-child(7), div:nth-child(8) {
            width: 16%;
          }
        }
        .name {
          font-size: 18px;
          line-height: 55px;
        }
        .price {
          font-size: 12px;
          span {
            margin-left: 18px;
          }
        }
      }
      .btns {
        margin-left: 10px;
        div {
          width: 78px;
          display: inline-block;
          text-align: center;
          margin-top: 8px;
          margin-right: 30px;
          &:nth-child(3) {
            margin-right: 0;
            i {
              background: #9E2976;
            }
          }
        }
        .exception {
          i {
            background: none;
            box-shadow: none;
            color: #ff212d;
            font-size: 30px;
          }
        }
        i {
          width: 31px;
          height: 31px;
          display: inline-block;
          border-radius: 31px;
          background: #00A6CE;
          box-shadow: 0 1px 6px 0 rgba(0, 0, 0, 0.24);
          line-height: 31px;
          cursor: pointer;
          color: #fff;
          font-size: 18px;
        }
        p {
          font-size: 10px;
          margin-top: 10px;
        }
      }
    }
  }
</style>
