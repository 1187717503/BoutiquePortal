<template>
  <div>
    <div class="main-box">
      <nav-bar></nav-bar>
      <div class="content">
        <div class="head-search">
          <div class="input-field">
            <label>Date Type</label>
            <select>
              <option value="0">Order Date</option>
              <option value="1">Shipped Date</option>
            </select>
          </div>
          <div class="input-field">
            <i class="mdi mdi-calendar prefix"></i>
            <input id="date" type="text" class="validate">
            <label for="date" class="">Date Range</label>
          </div>
          <div class="input-field">
            <input id="orderline" type="text" class="validate" v-model="postData.orderLine">
            <label for="orderline" class="">Order Line No.</label>
          </div>
          <div class="input-field">
            <input id="productid" type="text" class="validate" v-model="postData.productId">
            <label for="productid" class="">Product ID</label>
          </div>
          <div class="input-field">
            <input id="colorcode" type="text" class="validate" v-model="postData.colorCode">
            <label for="colorcode" class="">Color Code</label>
          </div>
          <div class="waves-effect waves-light btn" @click="getList('s')"><i class="mdi mdi-magnify left"></i>SEARCH
          </div>
          <div class="waves-effect waves-light btn" @click="resetList">RESET</div>
        </div>
        <div class="list">
          <div class="tit">
            <div>Order Date</div>
            <div>Order Line No.</div>
            <div>Shipped Date</div>
            <div>Shipment No.</div>
            <div>Status</div>
            <div>Geography</div>
            <div></div>
            <div>Brand</div>
            <div>Product ID</div>
            <div>Color</div>
            <div> Size</div>
            <!--<div>Retail<br>Price</div>-->
            <!--<div>Purchase<br>Price</div>-->
          </div>
          <div class="li" v-for="item in listData.result">
            <div>{{item.order_date | dateTime}}</div>
            <div>{{item.order_line_num}}</div>
            <div>{{item.shipped_date | dateTime}}</div>
            <div>{{item.shipment_no}}</div>
            <div>
              <span v-if="item.status===1">Open</span>
              <span v-else-if="item.status===2">Closed</span>
              <span v-else-if="item.status===3">Shipped</span>
              <span v-else-if="item.status===4">Delivered</span>
            </div>
            <div>{{item.geography_name}}</div>
            <div>
              <img :src="item.cover_img,'?x-oss-process=image/resize,m_fill,w_50,h_70,limit_0/auto-orient,0/quality,q_90' | ImgArr" alt="">
            </div>
            <div>
              <span>{{item.brandName}}</span>
            </div>
            <div>2824KDI</div>
            <div>{{item.colorCode}}</div>
            <div>{{item.size}}</div>
            <!--<div>€{{item.price | PriceForm}}</div>-->
            <!--<div>€{{item.in_price | PriceForm}}</div>-->
          </div>
        </div>
        <div class="foot-page">
          <div class="page">
            <span>Page: </span>
            <div class="input-field">
              <select>
                <option :value="i" v-for="i in pageLen">{{i}}</option>
              </select>
            </div>
          </div>
          <div class="rows-page">
            <span>Rows per page: </span>
            <div class="input-field">
              <select>
                <option v-for="item in rowsPage" :value="item">{{item}}</option>
              </select>
            </div>
          </div>
          <div class="event-page">
            <span>{{pageNum}} - {{pageSize}} of {{listData.total}}</span>
            <i class="mdi mdi-chevron-left" @click="prevPage"></i>
            <i class="mdi mdi-chevron-right" @click="nextPage"></i>
          </div>
        </div>
      </div>
      <foot-bar></foot-bar>
    </div>
    <loading v-if="isLoad"></loading>
  </div>
</template>

<script>
  import loading from '../component/loading.vue'
  import navBar from './nav_bar.vue'
  import footBar from './foot_bar.vue'
  import flatpickr from 'flatpickr'
  import {getShippedList} from '../../api/order'

  export default {
    data() {
      return {
        rowsPage: [
          50, 100, 150
        ],
        isLoad: false,
        pageNum: 1,
        pageSize: 50,
        pageLen: 1,
        postData: {
          orderLine: '',
          productId: '',
          colorCode: '',
          startDate: '',
          endDate: '',
          dateType: '0'
        },
        listData: {
          result: [],
          total: 0
        }
      }
    },
    mounted() {
      let _this = this;
      $('select').material_select();//初始化select
      $('.head-search select').change((e) => {
        this.postData.dateType = e.target.value;
      });
      $('.rows-page select').change((e) => {
        this.pageSize = e.target.value;
        this.getList();
      });
      $('.page select').change((e) => {
        this.pageNum = e.target.value;
        this.getList();
      });
      flatpickr('#date', {
        dateFormat: "d/m/Y",
        mode: "range",
        onClose(e, s) {
          if (e.length < 2) {
            return;
          }
          this.input.value = s.replace('to', '~');
          let star = new Date(e[0]);
          let end = new Date(e[1]);
          _this.postData.startDate = star.getFullYear() + '-' + parseInt(star.getMonth() + 1) + '-' + star.getDate() + ' 00:00:00';
          _this.postData.endDate = end.getFullYear() + '-' + parseInt(end.getMonth() + 1) + '-' + end.getDate() + ' 23:59:59';
        }
      });
      this.getList();
    },
    methods: {
      resetList() {
        this.postData = {
          orderLine: '',
          productId: '',
          colorCode: '',
          startDate: '',
          endDate: '',
          dateType: ''
        };
        this.getList();
      },
      getList(type) {
        if (type === 's') {
          this.pageNum = 1;
        }
        let data = {
          orderLineNo: this.postData.orderLine,
          brandID: this.postData.productId,
          colorCode: this.postData.colorCode,
          startDate: this.postData.startDate,
          endDate: this.postData.endDate,
          dateType: this.postData.endDate !== '' ? this.postData.dateType : '',
          pageNum: this.pageNum,
          pageSize: this.pageSize
        };
        this.isLoad = true;
        getShippedList(data).then(res => {
          if (res.data.status === 1) {
            this.listData = res.data.data;
            this.pageLen = Math.ceil(res.data.data.total / this.pageSize) ? Math.ceil(res.data.data.total / this.pageSize) : '1';
            setTimeout(() => {
              $('select').material_select();//初始化select
            }, 100);
          } else {
            Materialize.toast('获取数据出错', 2000);
          }
          this.isLoad = false;
        })
      },
      prevPage() {
        if (this.pageNum > 1) {
          this.pageNum--;
          this.getList();
        }
      },
      nextPage() {
        if (this.pageNum < this.listData.sumPage) {
          this.pageNum++;
          this.getList();
        }
      }
    },
    filters: {
      dateTime(date) {
        if (date) {
          let newDate = new Date(date);
          let Y = newDate.getFullYear(); //年
          let M = newDate.getMonth() + 1 < 10 ? '0' + parseInt(newDate.getMonth() + 1) : newDate.getMonth() + 1; //月
          let D = newDate.getDate() < 10 ? '0' + newDate.getDate() : newDate.getDate(); //日
          return D + '/' + M + '/' + Y
        } else {
          return ''
        }
      }
    },
    components: {
      'navBar': navBar,
      'footBar': footBar,
      loading
    }
  }
</script>
<style lang="less" scoped>
  input[type=text][readonly="readonly"] {
    color: #000;
    border-bottom: 2px solid #00A6CE;
  }

  input[type=text].invalid {
    box-shadow: none;
  }

  input[type=text][readonly="readonly"] + label {
    color: #9e9e9e;
  }

  .main-box {
    width: 100%;
    background-color: #f9f9f9;
    border-left: 1px solid #979797;
    border-right: 1px solid #979797;
    .content {
      background: #f9f9f9;
      padding: 0 16px;
    }
    .head-search {
      display: flex;
      div {
        margin-right: 16px;
      }
      div:nth-child(1) {
        width: 136px;
        label {
          top: -10px;
          left: 0;
          color: #4A4A4A;
          font-size: 12px;
        }
      }
      div:nth-child(2) {
        width: 240px;
        i {
          left: 10px;
          top: 3px;
        }
      }
      div:nth-child(3), div:nth-child(4) {
        width: 186px;
      }
      div:nth-child(5) {
        width: 93px;
      }
      div:nth-child(6), div:nth-child(7) {
        width: 128px;
        background: #9B9B9B;
        position: relative;
        top: 16px;
      }
      div:nth-child(6) {
        background: #9E2976;
        padding: 0;
        i {
          position: relative;
          left: 16px;
        }
      }
    }
    .list {
      margin-top: 20px;
      .tit {
        display: flex;
        font-size: 15px;
        padding-bottom: 6px;
        font-weight: bold;
        color: #9B9B9B;
        text-align: center;
        div:nth-child(1) {
          width: 90px;
        }
        div:nth-child(2) {
          width: 160px;
        }
        div:nth-child(3) {
          width: 110px;
        }
        div:nth-child(4) {
          width: 110px;
        }
        div:nth-child(5) {
          width: 80px;
        }
        div:nth-child(6) {
          width: 110px;
        }
        div:nth-child(7) {
          width: 50px;
        }
        div:nth-child(8) {
          width: 140px;
        }
        div:nth-child(9) {
          width: 180px;
        }
        div:nth-child(10) {
          width: 80px;
        }
        div:nth-child(11) {
          width: 60px;
        }
        /*div:nth-child(11) {*/
          /*margin-top: -24px;*/
          /*width: 70px;*/
          /*text-align: right;*/
        /*}*/
        /*div:nth-child(12) {*/
          /*margin-top: -24px;*/
          /*width: 70px;*/
          /*text-align: right;*/
        /*}*/
      }
      .li {
        display: flex;
        text-align: center;
        height: 72px;
        line-height: 72px;
        border-top: 1px solid #e0e0e0;
        border-bottom: 1px solid #e0e0e0;
        font-size: 14px;
        div {
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
        div:nth-child(1) {
          width: 90px;
        }
        div:nth-child(2) {
          width: 160px;
        }
        div:nth-child(3) {
          width: 110px;
        }
        div:nth-child(4) {
          width: 110px;
        }
        div:nth-child(5) {
          width: 80px;
        }
        div:nth-child(6) {
          width: 110px;
        }
        div:nth-child(7) {
          width: 50px;
        }
        div:nth-child(8) {
          width: 140px;
        }
        div:nth-child(9) {
          width: 180px;
        }
        div:nth-child(10) {
          width: 80px;
        }
        div:nth-child(11) {
          width: 60px;
        }
        /*div:nth-child(11) {*/
          /*width: 70px;*/
          /*text-align: right;*/
        /*}*/
        /*div:nth-child(12) {*/
          /*width: 70px;*/
          /*text-align: right;*/
        /*}*/
      }
    }
    .foot-page {
      display: flex;
      float: right;
      .input-field {
        width: 70px;
      }
      span {
        font-size: 14px;
        position: relative;
        top: 25px;
        margin-right: 6px;
        color: #909090;
      }
      .page {
        display: flex;
      }
      .rows-page {
        display: flex;
        margin-left: 30px;
      }
      .event-page {
        margin-left: 30px;
        display: flex;
        color: #909090;
        i {
          position: relative;
          top: 16px;
          font-size: 22px;
          cursor: pointer;
          &:hover {
            color: #000;
          }
          &:nth-child(3) {
            margin-left: 20px;
          }
        }

      }
    }
  }
</style>
