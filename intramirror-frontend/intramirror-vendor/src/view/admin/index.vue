<template>
  <div>
    <div class="main-box">
      <nav-bar></nav-bar>
      <div class="list">
        <div class="row" v-for="item in $store.state.order.list">
          <div class="pic">
            <img
              :src="item.cover_img,'?x-oss-process=image/resize,m_fill,w_98,h_125,limit_0/auto-orient,0/quality,q_90' | ImgArr">
          </div>
          <div class="info">
            <div class="text">
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
            </div>
            <p class="name">{{item.brandName}}　{{item.name}}</p>
            <p class="price">
              Retail Price:<span>€ {{item.price | PriceForm}}</span><span>Purchase Price:</span><span>€ {{item.in_price | PriceForm}}　exld. VAT</span><span
              style="margin-left:20px;">{{item.supply_price_discount}}</span>
            </p>
          </div>
          <div class="info2">
            <div class="orderline">
              <p>{{item.order_line_num}}</p>
              <span>Order LIne No.</span>
            </div>
            <div class="date">
              <p>{{item.created_at | formDate}}</p>
              <span>Order Date</span>
            </div>
          </div>
          <div class="btns">
            <div v-if="!item.oeComments || item.oeStatus!==1">
              <i class="mdi mdi-printer" @click="printOrder(1,item.order_line_num)"></i>
              <p>PRINT ORDER</p>
            </div>
            <div v-if="!item.oeComments || item.oeStatus!==1">
              <i class="mdi mdi-check" @click="confirmOrder(item)"></i>
              <p>CONFIRM</p>
            </div>
            <div v-if="!item.oeComments || item.oeStatus!==1">
              <i class="mdi mdi-window-close"
                 @click="cancelOrder(item.logistics_product_id,item.order_num,item.vendor_id)"></i>
              <p>CANCEL</p>
            </div>
            <div class="exception" v-if="item.oeComments && item.oeStatus===1">
              <i class="mdi mdi-alert-decagram"
                 @click="exceptionAlert(item.oeDescription,item.oeCreatedAt,item.oeCreatedByuser,item.oeComments,item.oeStatus,item.oeModified,item.Resolved,item.oeResolution)"></i>
              <p>EXCEPTION</p>
            </div>

          </div>
        </div>
      </div>
      <foot-bar></foot-bar>
    </div>
    <div class="ConfirmOrder" v-if="showPopup.configm">
      <h3>Confirm Order</h3>
      <div class="barcode">
        <div class="input-field">
          <i class="mdi mdi-barcode-scan prefix"></i>
          <input id="code" type="text" class="validate" v-model="confirmBarCode">
          <label for="code">Please scan or type PRODUCT barcode</label>
        </div>
        <p class="or">OR</p>
        <div class="product-input">
          <div class="input-field">
            <input id="productID" type="text" class="validate" v-model="confirmBrandId">
            <label for="productID">Please enter Product ID</label>
          </div>
          <div class="input-field">
            <input id="ColorCode" type="text" class="validate" v-model="confirmColorCode">
            <label for="ColorCode">Please enter Color Code</label>
          </div>

          <div class="input-field">
            <i class="mdi mdi-calendar prefix"></i>
            <input id="seleDate" type="text" class="validate" @focus="setEffectiveDate">
            <label for="seleDate">Estimated Ship Date</label>
          </div>
        </div>
        <div class="btns">
          <span @click="cancelLayer('configm')">CANCEL</span>
          <span @click="updateOrder">SAVE</span>
        </div>
      </div>
    </div>
    <div class="sucess-order" v-if="showPopup.sucessorder">
      <h3>Sucessfully Confirmed Order!</h3>
      <p class="please-make-sur">Now, please make sure you have: </p>
      <p class="checkbox">
        <i class="mdi mdi-checkbox-marked-outline"></i>
        <span>Deduct stock from your Store POS</span>
      </p>
      <p class="checkbox">
        <i class="mdi mdi-checkbox-marked-outline"></i>
        <span>Print Order sheet</span>
      </p>
      <p class="checkbox">
        <i class="mdi mdi-checkbox-marked-outline"></i>
        <span>Put aside the product with Order sheet</span>
      </p>
      <p class="ok" @click="cancelLayer('sucessorder')">OK</p>
    </div>
    <div class="failed-order" v-if="showPopup.cancel">
      <h3>Failed to Confirm Order</h3>
      <p class="text">
        The Product you entered does NOT match with Order, please try again!
      </p>
      <div class="btns">
        <span @click="cancelLayer('cancel')">Cancel</span>
        <span @click="showPopup.cancel = false;showPopup.configm = true;">TRY AGAIN</span>
      </div>
    </div>
    <div class="cancel-rder" v-if="showPopup.iscancel">
      <h3>Tell us why you cancel this order?</h3>
      <div class="input-field select">
        <label>Select a reason</label>
        <select id="cancelType">
          <option v-for="item in selectReason" :value="item.order_exception_type_id">{{item.description}}</option>
        </select>
      </div>
      <div class="input-field text">
        <input id="comment" type="text" class="validate" v-model="cancelDataText.text">
        <label for="comment">Please provide a comment</label>
      </div>

      <div class="btns">
        <span @click="showPopup.iscancel=false;showShade=false">Cancel</span>
        <span @click="yescancelOrder">Submit</span>
      </div>
    </div>

    <div class="exception-info" v-if="exceptionInfo.show">
      <h3>Order Exception</h3>
      <div class="exc-center">
        <div class="li">
          <div>Exception Type:</div>
          <div>{{exceptionInfo.info.a}}</div>
        </div>
        <div class="li">
          <div>Created at:</div>
          <div>{{exceptionInfo.info.b | dateTime}}</div>
        </div>
        <div class="li">
          <div>Created by:</div>
          <div>{{exceptionInfo.info.c}}</div>
        </div>
        <div class="li">
          <div>Comments:</div>
          <div>{{exceptionInfo.info.d}}</div>
        </div>


        <div class="li" style="margin-top:60px">
          <div>Status:</div>
          <div>{{exceptionInfo.info.e | formStatus}}</div>
        </div>
        <div class="li">
          <div>Resolved at:</div>
          <div>{{exceptionInfo.info.f | dateTime}}</div>
        </div>

        <div class="li">
          <div>Resolved by:</div>
          <div>{{exceptionInfo.info.g}}</div>
        </div>
        <div class="li">
          <div>Resolution:</div>
          <div>{{exceptionInfo.info.h}}</div>
        </div>
      </div>
      <div class="btns" @click="exceptionInfo.show=false;showShade=false;exceptionInfo.info=[]">
        ok
      </div>
    </div>

    <im-alert :data="imAlert"></im-alert>
    <shade v-if="showShade"></shade>
    <loading v-if="isLoading"></loading>
  </div>
</template>

<script>
  import {mapMutations} from 'vuex'
  import navBar from './nav_bar.vue'
  import footBar from './foot_bar.vue'
  import shade from '../component/shade.vue'
  import loading from '../component/loading.vue'
  import imAlert from '../component/imAlert.vue'
  import {
    getOrderDetail,
    confirmCheckOrder,
    orderRefund,
    updateOrderStatus,
    saveUserComment,
    getExceptionType
  } from '../../api/order'

  export default {
    data() {
      return {
        showShade: false,//遮罩是否显示
        selectReason: [],
        cancelDataText: {
          type: 1,
          text: ''
        },
        showPopup: {//弹出框是否显示
          configm: false,
          sucessorder: false,
          cancel: false,
          iscancel: false
        },
        orderLineNum: null,
        imAlert: {
          show: false,
          text: ''
        },
        cancelData: {
          pid: null,
          num: null,
          vid: null
        },
        logisticsProductId: null,
        confirmBarCode: null,
        confirmBrandId: null,
        confirmColorCode: null,
        confirmDate: null,
        exceptionInfo: {
          show: false,
          info: {}
        },
        isLoading: true,
        printData: {//打印的参数
        },
        listDate: []
      }
    },
    mounted() {
      this.loadList();
      this.loadReason()
    },
    methods: {
      ...mapMutations([
        'SET_ORDER_NUM',
        'GET_ORDER'
      ]),
      updateOrderNum() {
        this.$store.dispatch('getOrderNum').then(res => {

          this.SET_ORDER_NUM(res.data.data);
        })
      },
      setEffectiveDate() {
        let date = new Date();
        let year = date.getFullYear();
        let month = date.getMonth();
        let dates = date.getDate();
        let _this = this;
        $('#seleDate').pickadate({
          monthsShort: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12],
          selectMonths: true,
          selectYears: 15,
          min: new Date(year, month, dates),
          format: 'dd/mmm/yyyy',

          onSet: function () {
            _this.confirmDate = $('#seleDate').val();
          }
        });
      },
//        请求列表
      loadList() {
        let data = {
          'status': '1'
        };
        this.$store.dispatch('getOrderList', data).then(() => {
          this.isLoading = false
        })
      },
      loadReason() {
        getExceptionType().then(res => {
          if (res.data.status === 1) {
            this.selectReason = res.data.data;
            setTimeout(() => {
              $('select').material_select();//初始化select
            }, 100);
          }
        })
      },
      confirmOrder(item) {
        if (item.skip_confirm === 1) {
          this.showPopup.configm = false;
          let data = {
            "logisticsProductId": item.logistics_product_id,
            "brandId": item.brandID,
            "colorCode": item.colorCode
          };

          confirmCheckOrder(data).then(res => {
            if (res.data.status === 1) {
              this.showPopup.configm = false;
              this.showPopup.sucessorder = true;
              this.updateStatus(item.logistics_product_id);
            } 
          });
          
        } else {
          this.showPopup.configm = true;
        }
        
        this.showShade = true;
        this.logisticsProductId = item.logistics_product_id;
        this.orderLineNum = item.order_line_num;
      },
      updateOrder() {

        if (this.confirmBarCode === null && this.confirmBrandId === null) {
          this.imAlert = {
            show: true,
            text: 'Please enter a'
          };
          return false
        }
        let data = {
          "logisticsProductId": this.logisticsProductId,
          "barCode": this.confirmBarCode,
          "brandId": this.confirmBrandId,
          "colorCode": this.confirmColorCode,
          "estShipDate": this.confirmDate
        };
        confirmCheckOrder(data).then(res => {
          if (res.data.status === 1) {
            this.showPopup.configm = false;
            this.showPopup.sucessorder = true;
            this.updateStatus(this.logisticsProductId);
            this.confirmBarCode = null;
            this.confirmBrandId = null;
            this.confirmColorCode = null;
            this.confirmDate = null;
          } else {
            this.showPopup.configm = false;
            this.confirmBarCode = null;
            this.confirmBrandId = null;
            this.confirmColorCode = null;
            this.confirmDate = null;
            this.showPopup.cancel = true;
            this.showShade = true;
          }
        })
      },
      printOrder(sta, num) {
        window.open('/#/confirmprint?num=' + num + '&sta=' + sta);
      },
      cancelOrder(pid, num, vid) {
        this.showPopup.iscancel = true;
        this.showShade = true;
        this.cancelData = {
          pid: pid,
          num: num,
          vid: vid
        };
        setTimeout(() => {
          $('select').material_select();//初始化select
          $('#cancelType').change((e) => {
              this.cancelDataText.type = e.target.value;
          });
        }, 0);
      },
      exceptionAlert(a, b, c, d, e, f, g, h) {
        let data = {
          a: a,
          b: b,
          c: c,
          d: d,
          e: e,
          f: f,
          g: g,
          h: h
        };
        this.exceptionInfo.info = data;
        this.showShade = true;
        this.exceptionInfo.show = true;
      },
      yescancelOrder() {
        if (this.cancelDataText.text === '') {
          this.imAlert = {
            show: true,
            text: 'Please enter a'
          };
          return false
        }

        let data = {
          logistics_product_id: this.cancelData.pid,
          comments: this.cancelDataText.text,
          order_exception_type_id: this.cancelDataText.type,
        };
        saveUserComment(data).then(res => {
          if (res.data.status === 1) {
            Materialize.toast('取消成功', 4000);
            this.loadList();
          } else {
            Materialize.toast('取消失败', 4000);
          }
          this.showPopup.iscancel = false;
          this.showShade = false;
        })
//        orderRefund(data).then(res => {
//          if (res.data.status === 1) {
//            Materialize.toast('取消成功', 4000);
//          } else {
//            Materialize.toast('取消失败', 4000);
//          }
//          this.showPopup.iscancel = false;
//          this.showShade = false;
//        })
      },
      cancelLayer(type) {
        switch (type) {
          case 'configm':
            this.showPopup.configm = false;
            this.logisticsProductId = null;
            this.confirmBarCode = null;
            this.confirmBrandId = null;
            this.confirmColorCode = null;
            this.confirmDate = null;
            break;
          case 'sucessorder':
            window.open('/#/confirmprint2?num='+this.orderLineNum+'&sta=2');
            this.showPopup.sucessorder = false;
            this.loadList();
            break;
          case 'cancel':
            this.showPopup.cancel = false;
            this.showPopup.configm = false;
            break;
        }
        this.showShade = false;
      },
      updateStatus(id) {
        let data = {
          logisProductId: id,
          status: 2,
        };
        updateOrderStatus(data).then(res => {
          if (res.data.status === 1) {
            this.updateOrderNum();
            this.loadList();
          }
        })
      }
    },
    components: {
      'navBar': navBar,
      'footBar': footBar,
      'shade': shade,
      loading,
      imAlert
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
      },
      formStatus(s) {
        let str = '';
        switch (s) {
          case 1:
            str = 'Exception submit';
            break;
          case 2:
            str = '';
            break;
          case 3:
            str = '';
            break;
        }
        return str;
      }
    }
  }
</script>
<style>
  .picker__date-display, .picker__weekday-display, .picker__day--selected, .picker__day--selected:hover, .picker--focused .picker__day--selected {
    background: #871B55;
  }

  .picker__close, .picker__today, .picker__day.picker__day--today {
    color: #871B55;
  }
</style>
<style lang="less" scoped>
  input[type=text].valid {
    border: none;
    box-shadow: none;
  }

  .main-box {
    width: 100%;
    background-color: #FAFAFA;
    box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
    border-left: 1px solid #9b9b9b;
    border-right: 1px solid #9b9b9b;
    .list {
      .row {
        height: 150px;
        border-bottom: 1px solid #9b9b9b;
        display: flex;
        margin-bottom: 0;
      }
      .pic {
        margin: 12px 20px;
      }
      .info {
        width: 450px;
        .text {
          margin-left: -10px;
          div {
            width: 240px;
            font-size: 16px;
            text-align: center;
            float: left;
            color: #000000;
            margin-top: 15px;
            p {
              overflow: hidden;
              white-space: nowrap;
              text-overflow: ellipsis;
            }
            &:nth-child(2), &:nth-child(3) {
              width: 60px;
            }
            span {
              font-size: 12px;
            }
          }
        }
        .name {
          overflow: hidden;
          white-space: nowrap;
          text-overflow: ellipsis;
          font-size: 16px;
          line-height: 45px;
          clear: both;
        }
        .price {
          font-size: 12px;
          overflow: hidden;
          white-space: nowrap;
          text-overflow: ellipsis;
          span {
            margin-left: 27px;
          }
        }
      }
      .info2 {
        margin-left: 50px;
        width:180px;
        .orderline {
          text-align: center;
          margin-top: 17px;
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
        .date {
          text-align: center;
          margin-top: 22px;
          p {
            font-size: 16px;
          }
          span {
            font-size: 12px;
          }
        }
      }
      .btns {
        margin-left: 60px;
        div {
          width: 78px;
          display: inline-block;
          text-align: center;
          margin-top: 54px;
          margin-right: 20px;
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

  .ConfirmOrder {
    position: fixed;
    top: 50%;
    left: 50%;
    margin-left: -443.1px/2;
    margin-top: -394.34px/2;
    height: 394.34px;
    width: 443.1px;
    border-radius: 2px;
    background-color: #FFFFFF;
    box-shadow: 0 0 24px 0 rgba(0, 0, 0, 0.22), 0 24px 24px 0 rgba(0, 0, 0, 0.3);
    z-index: 7;
    padding: 24px;
    h3 {
      font-size: 20px;
      font-weight: 500;
      margin-bottom: 30px;
    }
    .barcode {
      .input-field {
        input {
          border-bottom: 2px solid #00A6CE;
        }
        label {
          left: 0;
        }
      }
      .or {
        font-size: 16px;
        margin: 10px 0 0 0;
      }
      .product-input {
        .input-field:nth-child(1), .input-field:nth-child(2) {
          width: 187.55px;
          float: left;
        }
        .input-field:nth-child(2) {
          margin-left: 20px;
        }
        .input-field:nth-child(3) {
          clear: both;
          width: 220px;
          top: 20px;
          .prefix {
            top: 10px;
            color: #4A4A4A;
          }
        }
      }
    }
    .btns {
      position: absolute;
      right: 24px;
      bottom: 24px;
      color: #871B55;
      font-size: 14px;
      span {
        margin-left: 40px;
        cursor: pointer;
      }
    }
  }

  .sucess-order {
    position: fixed;
    top: 50%;
    left: 50%;
    margin: -304.52px/2 0 0 -376px/2;
    height: 304.52px;
    width: 376px;
    border-radius: 2px;
    background-color: #FFFFFF;
    box-shadow: 0 0 24px 0 rgba(0, 0, 0, 0.22), 0 24px 24px 0 rgba(0, 0, 0, 0.3);
    padding: 24px;
    z-index: 7;
    h3 {
      font-size: 20px;
    }
    .please-make-sur {
      font-size: 16px;
      color: rgba(0, 0, 0, 0.54);
      margin: 21px 0;
    }
    .checkbox {
      margin-bottom: 10px;
      i {
        font-size: 22px;
        color: #871B55;
      }
      span {
        position: relative;
        top: -3px;
        left: 4px;
      }
    }
    .ok {
      position: absolute;
      right: 35px;
      bottom: 16px;
      font-size: 14px;
      color: #871B55;
      cursor: pointer;
    }
  }

  .failed-order {
    position: fixed;
    top: 50%;
    left: 50%;
    margin: -197px/2 0 0 -363.67px/2;
    height: 197px;
    width: 363.67px;
    border-radius: 2px;
    background-color: #FFFFFF;
    box-shadow: 0 0 24px 0 rgba(0, 0, 0, 0.22), 0 24px 24px 0 rgba(0, 0, 0, 0.3);
    padding: 24px;
    z-index: 7;
    h3 {
      font-size: 20px;
    }
    .text {
      font-size: 16px;
      color: rgba(0, 0, 0, 0.54);
      margin-top: 21px;
    }
    .btns {
      position: absolute;
      right: 20px;
      bottom: 17px;
      span {
        color: #871B55;
        font-size: 14px;
        font-weight: 500;
        cursor: pointer;
        &:nth-child(1) {
          margin-right: 20px;
        }
      }
    }
  }

  .cancel-rder {
    position: fixed;
    top: 50%;
    left: 50%;
    z-index: 7;
    height: 304.52px;
    width: 501.63px;
    margin-left: -501.63px/2;
    margin-top: -304.52px/2;
    border-radius: 2px;
    background-color: #FFFFFF;
    box-shadow: 0 0 24px 0 rgba(0, 0, 0, 0.22), 0 24px 24px 0 rgba(0, 0, 0, 0.3);
    padding: 24px;
    h3 {
      font-size: 20px;
      font-weight: 500;
      line-height: 28px;
    }
    .input-field.select {
      width: 237px;
      margin-top: 30px;
      label {
        top: -16px;
      }
    }
    .input-field.text {
      margin-top: 30px;
      input.valid {
        border-bottom: 2px solid #00A6CE;
      }
      label {
        top: 0;
      }
      label.active {
        top: 12px;
      }
    }
    .btns {
      position: absolute;
      right: 35px;
      bottom: 17px;
      font-size: 14px;
      color: #871B55;
      cursor: pointer;
      span:nth-child(1) {
        margin-right: 60px;
        font-weight: 500;
      }
    }
  }

  .exception-info {
    position: fixed;
    z-index: 7;
    top: 50%;
    left: 50%;
    padding: 24px;
    margin: -526.58px/2 0 0 -555.01px/2;
    height: 526.58px;
    width: 555.01px;
    border-radius: 2px;
    background-color: #FFFFFF;
    box-shadow: 0 0 24px 0 rgba(0, 0, 0, 0.22), 0 24px 24px 0 rgba(0, 0, 0, 0.3);
    h3 {
      color: rgba(0, 0, 0, 0.87);
      font-size: 20px;
      font-weight: 500;
    }
    .exc-center {
      margin-top: 21px;
      .li {
        display: flex;
        div {
          margin-bottom: 10px;
          &:nth-child(1) {
            width: 143px;
            text-align: right;
            color: rgba(0, 0, 0, 0.54);
          }
          &:nth-child(2) {
            margin-left: 20px;
          }
        }
      }
    }
    .btns {
      position: absolute;
      right: 40px;
      cursor: pointer;
      bottom: 20px;
      color: #871B55;
    }
  }


</style>
