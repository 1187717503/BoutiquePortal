<template>
  <div>
    <p class="nav-breadcrumbs">Order Processing <i class="mdi mdi-chevron-right"></i> Pack Order</p>
    <div class="screen" v-if="!Processing">
      <div class="left-bar">
        <span>1</span>
        <i></i>
        <span style="background-color: #9b9b9b;">2</span>
      </div>
      <div class="scan-carton">
        <p class="tit">SCAN CARTON</p>
        <div class="field">
          <div class="input-field">
            <i class="mdi mdi-barcode-scan prefix"></i>
            <input id="icon_prefix3" type="text" class="validate" @change="getBybarcode">
            <label for="icon_prefix3">Please scan or type CARTON barcode</label>
          </div>
          <button class="waves-effect waves-light btn cate" @click="openGeography('open')">
            <i class="mdi mdi-package-variant left"></i>
            CREATE NEW CARTON
          </button>
        </div>
      </div>
      <p class="scan-products">
        SCAN ORDER LINE NO.
      </p>
      <div class="waves-effect waves-light btn back" onclick="window.history.go(-1)">BACK</div>
    </div>

    <div class="screen processing" v-if="Processing">
      <div class="left-bar">
        <span>1</span>
        <i></i>
        <span>2</span>
        <i id="bar2"></i>
        <span class="mdi mdi-check left-icon"></span>
      </div>
      <div class="scan-carton">
        <div class="info">
          <p class="tit">{{cartonData.barcode}}</p>
          <div class="input-field">
            <input id="Length" type="text" class="validate" v-model="cartonData.length">
            <label for="Length" :class="{active:cartonData.length!==null}">Length (CM)</label>
          </div>
          <span class="mdi mdi-window-close"></span>
          <div class="input-field">
            <input id="Width" type="text" class="validate" v-model="cartonData.width">
            <label for="Width" :class="{active:cartonData.length!==null}">Width (CM)</label>
          </div>
          <span class="mdi mdi-window-close"></span>
          <div class="input-field">
            <input id="Height" type="text" class="validate" v-model="cartonData.height">
            <label for="Height" :class="{active:cartonData.length!==null}">Height (CM)</label>
          </div>
          <p class="carton">{{shipmentData.currentSort}} of {{shipmentData.count}} Carton</p>
        </div>
        <div class="text">
          <span v-if="shipmentData.shipmentNo!==null">Shipment: {{shipmentData.shipmentNo}} </span>
          <span>{{cartonData.shipToGeography}}</span>
          <!--<span>{{cartonData.address}}</span>-->
        </div>
        <div class="rig-btn">
          <div>
            <i class="mdi mdi-printer" @click="printLabel"></i>
            <p>CARTON<br>LABEL</p>
          </div>
          <div v-if="enterType!=='new'">
            <i class="mdi mdi-package-variant" @click="OrderProcessing('new',true,true,true)"></i>
            <p>NEW<br>CARTON</p>
          </div>
          <div v-if="enterType!=='new'">
            <i class="mdi mdi-window-close" @click="deleteBox(cartonData.containerId)"></i>
            <p>DELETE<br>CARTON</p>
          </div>
        </div>
      </div>
      <div class="products">
        <p class="tit">SCAN ORDER LINE NO.</p>
        <div class="head-search">
          <div class="input-field">
            <i class="mdi mdi-barcode-scan prefix"></i>
            <input id="barcode" type="text" class="validate" v-model="valOrderLineNo"
                   @change="scanBarcode">
            <label for="barcode">Please scan or type ORDER LINE NO. barcode</label>
          </div>
        </div>
        <div class="list-box" v-if="productData.length>=1">
          <div class="head-tit">
            <div></div>
            <div>Product ID</div>
            <div>Color</div>
            <div>Size</div>
            <div>Brand</div>
            <div>Name</div>
            <div>Retail<br>Price</div>
            <div>Purchase<br>Price</div>
            <div>Order Line No.</div>
            <div>Order Date</div>
          </div>
          <div class="li" v-for="item in productData">
            <div>
              <img
                :src="item.cover_img,'?x-oss-process=image/resize,m_fill,w_98,h_125' | ImgArr"
                alt="">
            </div>
            <div>{{item.brandID}}</div>
            <div>{{item.colorCode}}</div>
            <div>{{item.size}}</div>
            <div>{{item.brandName}}</div>
            <div>{{item.name}}</div>
            <div>€{{item.price | PriceForm}}</div>
            <div>€{{item.in_price | PriceForm}}</div>
            <div>{{item.order_line_num}}</div>
            <div>{{item.created_at | formDate}}</div>
            <div>
              <i class="mdi mdi-window-close" @click="deletePacking(item.logistics_product_id)"></i>
            </div>
          </div>
        </div>
      </div>
      <div class="foot-btn">
        <button class="waves-effect waves-light btn" @click="saveContainer">SAVE</button>
        <button class="waves-effect waves-light btn" @click="OrderProcessing('get',false)">CANCEL</button>
      </div>
    </div>


    <!--错误提示弹框-->
    <div class="error-prompt" v-if="errorPrompt.show">
      <h3>This Order cannot be packed into this carton. </h3>
      <p>{{errorPrompt.text}}</p>
      <span @click="errorPrompt.show=false;showShade=false">Ok</span>
    </div>
    <!--选择打印的地区-->
    <div class="select-geography" v-if="isNewCase">
      <p class="tit">Select Ship-to Geography</p>
      <div class="list">
        <p v-for="(item,index) in geographyData">
          <input class="with-gap" name="geography" :value="item.english_name" type="radio" :id="'geography'+index"
                 v-model="selectGeography">
          <label :for="'geography'+index">{{item.english_name}}</label>
        </p>
      </div>
      <div class="btns">
        <span @click="openGeography('close')">CANCEL</span>
        <span @click="OrderProcessing('new')">SAVE</span>
      </div>
    </div>
    <!--选择-->
    <div class="selectShipment" v-if="selectShipment">
      <p class="tit">Select a Shipment</p>
      <div class="sele">
        <p v-for="item in shipmentData">
          <input class="with-gap" name="Shipment" type="radio" :value="item.shipment_id" @change="selectShipmentid"
                 :id="item.shipment_id"/>
          <label :for="item.shipment_id">{{item.shipment_no}}　　　{{item.ship_to_geography}}</label>
        </p>
      </div>
      <div class="btns">
        <button class="waves-effect waves-light btn"
                @click="selectShipment=false;showShade=false;cartonData.shipmentId=0;valOrderLineNo=''">CANCEL
        </button>
        <button class="waves-effect waves-light btn" @click="setShipmentid">SAVE</button>
      </div>
    </div>
    <!--弹出框-->
    <im-alert :data="imAlert"></im-alert>
    <!--加载动画-->
    <loading v-if="isLoading"></loading>
    <!--遮罩层-->
    <shade v-if="showShade"></shade>
  </div>
</template>

<script>
  import shade from '../component/shade.vue'
  import loading from '../component/loading.vue'
  import imAlert from '../component/imAlert.vue'
  import {
    getContainerBybarcode,
    getPackOrderList,
    getBarcode,
    saveContainerApi,
    updateContainerBybarcode,
    packingCheckOrder,
    deleteContainerById,
    deletePackingCheckOrder,
    printBarcode,
    getGeography,
    getShipmentNo
  } from '../../api/order'

  export default {
    data() {
      return {
        Processing: false,
        selectShipment: false,
        showShade: false,
        isLoading: false,
        isNewCase: false,
        barCodePic: null,
        enterType: null,
        productData: [],
        errorPrompt: {
          show: false,
          text: ''
        },
        shipmentData: [],
        valOrderLineNo: null,
        imAlert: {
          show: false,
          text: ''
        },
        selectGeography: '',
        geographyData: [],
        cartonData: {
          barcode: null,
          length: null,
          width: null,
          height: null,
          shipmentId: null,
          shipToGeography: null,
          containerId: null
        }
      }
    },
    mounted() {
      if (this.$route.params.id !== 'new') {
        this.getBybarcode(this.$route.params.id)
      }
    },
    methods: {
      //根据barcode查询当前各个状态的箱子
      getBybarcode(e) {
        let codeVal = '';
        try {
          codeVal = e.target.value
        }
        catch (err) {
          codeVal = e
        }
        let data = {
          "barcode": codeVal,
          "status": 1
        };
        this.isLoading = true;
        getContainerBybarcode(data).then(res => {
          if (res.data.status === 1 && res.data.data !== null) {
            let data = res.data.data;
            this.cartonData = {
              barcode: data.barcode,
              length: data.length === 0 ? null : data.length,
              width: data.width === 0 ? null : data.width,
              height: data.height === 0 ? null : data.height,
              shipmentId: data.shipmentId,
              shipToGeography: data.shipToGeography,
              containerId: data.containerId
            };
            this.OrderProcessing('get', data.containerId, true);
          } else {
            this.isLoading = false;
            Materialize.toast('Data failure', 4000);
          }
        });
        getShipmentNo(data).then(res => {
          if (res.data.status === 1) {
            this.shipmentData = res.data.data
          }
        })
      },
      getOrderList(id) {
        let data = {
          "status": "7",
          "containerId": id
        };
        this.isLoading = true;
        getPackOrderList(data).then(res => {
          if (res.data.status === 1) {
            this.productData = res.data.data;
            setTimeout(function () {
              let th = $('.products').height();
              $('#bar2').css('height', th + 5 + 'px')
            }, 1);
          }
          this.isLoading = false;
        })
      },
      openGeography(type) {
        if (type === 'open') {
          this.loadGeography();
        } else {
          this.isNewCase = false;
          this.showShade = false;
        }
      },
//        save
      saveContainer() {
        let dataAll = {
          length: this.cartonData.length,
          width: this.cartonData.width,
          height: this.cartonData.height,
          barcode: this.cartonData.barcode,
          shipToGeography: this.cartonData.shipToGeography
        };
        if (this.enterType === 'get') {
          let postData = {
            length: dataAll.length,
            width: dataAll.width,
            height: dataAll.height,
            barcode: dataAll.barcode,
          };
          updateContainerBybarcode(postData).then(res => {
            if (res.data.status === 1) {
              Materialize.toast('Save success', 2000);
              setTimeout(() => {
                this.$router.push({name: 'Pack'})
              }, 1000);
            }
          });
        } else if (this.enterType === 'new') {
          let postData = {
            barcode: dataAll.barcode,
            shipToGeography: dataAll.shipToGeography,
          };
          saveContainerApi(postData).then(res => {
            if (res.data.status === 1) {
              this.selectGeography = '';
              this.getBybarcode(this.cartonData.barcode);
              Materialize.toast('New success', 4000);
            }
          });
        }

      },
//        显示pack order
      OrderProcessing(type, id, open, cent) {
        if (type === 'get') {
          if (open) {
            this.getOrderList(id);
          }
          this.Processing = open;
          this.enterType = 'get';
        } else if (type === 'new') {
          this.Processing = open;
          if (this.selectGeography === '' && !cent) {
            this.isNewCase = true;
            this.showShade = true;
            this.imAlert = {
              show: true,
              text: 'Please select large area'
            };
            return
          } else if (this.selectGeography === '' && cent) {
            this.loadGeography(); 
            this.isNewCase = true;
            this.showShade = true;
            return
          }
          this.newCase();
          this.enterType = 'new';
          this.productData = [];
        }
      },
//      获取箱子码
      newCase() {
        this.openGeography('close');
        this.isLoading = true;
        getBarcode().then(res => {
          if (res.data.status === 1) {
            this.cartonData.barcode = res.data.data;
            this.saveContainer();
          }
          this.isLoading = false;
        });
        this.cartonData = {
          length: null,
          width: null,
          height: null,
          shipmentId: null,
          shipToGeography: this.selectGeography,
          containerId: null
        };
        this.Processing = !this.Processing;
        if (this.Processing) {
          setTimeout(function () {
            let th = $('.products').height();
            $('#bar2').css('height', th + 5 + 'px')
          }, 1);
        }
      },
      scanBarcode() {
        if (this.valOrderLineNo !== null && this.valOrderLineNo !== '') {
          this.verifyGoods(this.valOrderLineNo);
        }
      },
//      删除箱子
      deleteBox(code) {
        if (code !== null && code !== '') {
          let data = {
            containerId: code
          };
          deleteContainerById(data).then(res => {
            if (res.data.status === 1) {
              Materialize.toast('Delete the success', 4000);
              this.OrderProcessing('get', false)
            }
          })
        }
      },
//      删除箱子里的商品
      deletePacking(pid) {
        let data = {
          "logistics_product_id": pid,
          "container_id": this.cartonData.containerId
        };
        deletePackingCheckOrder(data).then(res => {
          if (res.data.status === 1) {
            Materialize.toast('Delete the success', 4000);
            this.getOrderList(this.cartonData.containerId)
          } else {
            Materialize.toast('Delete failed', 4000);
          }
        })
      },
//      验证商品
      verifyGoods(num) {
        let _this = this;
        let data = {
          "orderLineNum": num,
          "status": "2",
          "containerId": this.cartonData.containerId,
          "shipment_id": this.cartonData.shipmentId === 0 ? '' : this.cartonData.shipmentId
        };
        this.isLoading = true;
        packingCheckOrder(data).then(res => {
          if (res.data.status === 1) {
            switch (res.data.infoMap.statusType) {
              case  0: //0 校验
                this.showShade = true;
                this.errorPrompt = {
                  show: true,
                  text: res.data.msg
                };
                break;
              case  2: //2返回shipment列表
                _this.selectBox(res.data.data);
                break;
              case  3:// 3箱子为空时(需要刷新箱子信息)
                _this.getBybarcode(_this.cartonData.barcode);
                break;
              case  4: //4不为空时,直接存入箱子
                _this.getOrderList(_this.cartonData.containerId);
                break;
            }
            if (res.data.infoMap.code && res.data.infoMap.code === 1001) {
              this.showShade = true;
              this.errorPrompt = {
                show: true,
                text: res.data.msg
              }
            }
          } else {
            this.showShade = true;
            this.errorPrompt = {
              show: true,
              text: res.data.msg
            }
          }
          this.isLoading = false;
          if (res.data.infoMap.statusType !== 2) {
            this.valOrderLineNo = '';
          }
        });
      },
//      获取条形码图片
      selectBox(data) {
        this.shipmentData = data;
        this.selectShipment = true;
        this.showShade = true;
      },
      selectShipmentid(e) {
        this.cartonData.shipmentId = e.target.value;
      },
      setShipmentid() {
        this.selectShipment = false;
        this.showShade = false;
        this.verifyGoods(this.valOrderLineNo);
        this.valOrderLineNo = '';
      },
      loadGeography() {
        this.isLoading = true;
        getGeography().then(res => {
          if (res.data.status === 1) {
            this.geographyData = res.data.data;
            this.isNewCase = true;
            this.showShade = true;
          }
          this.isLoading = false;
        })
      },
//      打印标签
      printLabel() {
        /*if (this.cartonData.length === null || this.cartonData.length === '') {
          Materialize.toast('Please enter the length', 4000);
          return
        }
        if (this.cartonData.width === null || this.cartonData.width === '') {
          Materialize.toast('Please enter the width', 4000);
          return
        }
        if (this.cartonData.height === null || this.cartonData.height === '') {
          Materialize.toast('Please enter the height', 4000);
          return
        }*/
        this.saveContainer();
        window.open('/#/packprint?barcode=' + this.cartonData.barcode + '&length=' + this.cartonData.length + '&width=' + this.cartonData.width + '&height=' + this.cartonData.height + '&shipToGeography=' + this.cartonData.shipToGeography);
      }
    },
    components: {
      'shade': shade,
      loading,
      imAlert
    }
  }
</script>
<style lang="less" scoped>
  .nav-breadcrumbs {
    font-size: 20px;
    font-weight: 500;
    color: #871B55;
    line-height: 24px;
    margin-left: 10px;
  }

  .input-field {
    label {
      left: 0;
    }
    input {
      border-bottom: 2px solid #00A6CE;
      height: 40px;
      &:focus {
        border-bottom: 2px solid #00A6CE;
        box-shadow: none;
      }
    }
  }

  .screen {
    margin-top: 31px;
    overflow: hidden;
    .left-bar {
      width: 36px;
      margin-right: 20px;
      float: left;
      span {
        height: 36px;
        width: 36px;
        background-color: #9E2976;
        border-radius: 36px;
        display: block;
        text-align: center;
        line-height: 36px;
        font-size: 20px;
        font-weight: 500;
        color: #fff;
      }
      i {
        width: 1px;
        height: 100px;
        border-left: 2px solid #9b9b9b;
        display: block;
        margin: 10px 0 10px 18px;
      }
    }
    .scan-carton {
      float: left;
      width: 95%;
      height: 117.1px;
      background-color: #FAFAFA;
      box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
      .tit {
        color: rgba(0, 0, 0, 0.87);
        font-size: 20px;
        font-weight: 500;
        margin: 12.6px 0 0 25.27px;
      }
      .field {
        margin-left: 16px;
        .input-field {
          width: 330px;
          float: left;
          i {
            font-size: 24px;
            top: 10px;
            left: 12px;
          }
        }
        .cate {
          margin: 18px 0 0 120px;
          background-color: #9E2976;
          width: 212.4px;
          height: 36px;
          padding: 0;
          box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
          i {
            margin-right: 0;
            position: relative;
            left: 12px;
          }
        }
      }
    }
    .scan-products {
      font-size: 20px;
      color: rgba(0, 0, 0, 0.87);
      font-weight: 500;
      clear: both;
      position: relative;
      left: 56px;
      top: -30px;

    }
    .back {
      position: relative;
      left: 56px;
      height: 36px;
      width: 112.3px;
      border-radius: 2px;
      background-color: #9B9B9B;
      box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
    }
  }

  .processing {
    .left-bar .left-icon {
      background: #9b9b9b
    }
    .scan-carton {
      position: relative;
      height: 137.6px;
      .info {
        .tit {
          margin: 25.6px 90px 0 24.27px;
          float: left;
        }
        .input-field {
          width: 90px;
          float: left;
        }
        span {
          float: left;
          margin: 30px 20px 0 20px;
          color: rgba(0, 0, 0, 0.38);
        }
        .carton {
          font-size: 18px;
          color: #000;
          float: left;
          margin: 30px 0 0 60px;
        }
      }
      .text {
        clear: both;
        padding: 12px 0 0 24px;
        font-size: 18px;
        color: #000;
        span {
          margin-right: 66px;
        }
      }
      .rig-btn {
        position: absolute;
        right: 30px;
        top: 36px;
        div {
          float: left;
          text-align: center;
          margin-left: 67px;
        }
        div:nth-child(3) i {
          background: #9E2976;
        }
        i {
          height: 31px;
          width: 31px;
          background-color: #871B55;
          box-shadow: 0 1px 6px 0 rgba(0, 0, 0, 0.24);
          border-radius: 31px;
          color: #fff;
          line-height: 29px;
          cursor: pointer;
        }
        p {
          font-size: 10px;
          margin-top: 6px;
        }
      }
    }
    .products {
      width: 95%;
      padding: 15px 24px;
      float: left;
      margin-top: 14px;
      background-color: #FAFAFA;
      box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
      .tit {
        font-size: 20px;
        font-weight: 500;
      }
      .head-search {
        span {
          float: left;
          font-size: 16px;
          margin: 30px 45px 0;
        }
        .input-field {
          i {
            font-size: 22px;
            top: 10px;
            left: 12px;
          }
          &:nth-child(1) {
            width: 400px;
          }
        }
      }
      .list-box {
        clear: both;
        .head-tit {
          display: flex;
          border-bottom: 1px solid #E0E0E0;
          color: #9B9B9B;
          padding: 8px 0;

          div {
            text-align: center;
            font-size: 13px;
          }
          div:nth-child(1) {
            width: 51px;
            margin: 0 0 0 22px;
          }
          div:nth-child(2) {
            width: 110px;
          }
          div:nth-child(3) {
            width: 32+17px;
          }
          div:nth-child(4) {
            width: 30+22px;
          }
          div:nth-child(5) {
            width: 35+40px;
          }
          div:nth-child(6) {
            width: 130px;
          }
          div:nth-child(7) {
            width: 72+23px;
            margin-top: -12px;
          }
          div:nth-child(8) {
            width: 72px;
            margin-top: -12px;
          }
          div:nth-child(9) {
            width: 93+72px;
          }
          div:nth-child(10) {
            width: 110px;
          }
        }
        .li {
          position: relative;
          display: flex;
          font-size: 12px;
          text-align: center;
          border-bottom: 1px solid #E0E0E0;
          div {
            height: 75px;
            line-height: 75px;
            white-space: nowrap;
            text-overflow: ellipsis;
            overflow: hidden;
          }
          div:nth-child(1) {
            width: 51px;
            margin: 0 0 0 22px;
            padding-top: 5px;
          }
          div:nth-child(2) {
            width: 110px;
          }
          div:nth-child(3) {
            width: 32+17px;
          }
          div:nth-child(4) {
            width: 30+22px;
          }
          div:nth-child(5) {
            width: 35+40px;
          }
          div:nth-child(6) {
            width: 130px;
          }
          div:nth-child(7) {
            width: 72+23px;
            text-align: right;
            padding-right: 30px;
          }
          div:nth-child(8) {
            width: 72px;
            text-align: right;
          }
          div:nth-child(9) {
            width: 93+72px;
          }
          div:nth-child(10) {
            width: 110px;
          }
          div:nth-child(11) {
            margin-left: 120px;
            i {
              position: relative;
              top: 4px;
              height: 31px;
              width: 31px;
              border-radius: 31px;
              line-height: 28px;
              color: #333;
              font-size: 20px;
              cursor: pointer;
            }
            i:hover {
              color: #fff;
              background-color: #9E2976;
              box-shadow: 0 1px 6px 0 rgba(0, 0, 0, 0.24);
            }
          }
        }
        .li:hover {
          background: #f5f5f5;
        }
      }
    }
    .foot-btn {
      float: left;
      margin-top: 34px;
      .btn {
        width: 112.3px;
        font-size: 14px;
        height: 36px;
        box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
        &:nth-child(1) {
          background: #871B55;
        }
        &:nth-child(2) {
          background: #9B9B9B;
          margin-left: 27.7px;
        }
      }
    }
  }

  .selectShipment {
    position: fixed;
    z-index: 7;
    height: 291.62px;
    width: 449.25px;
    border-radius: 2px;
    background-color: #FFFFFF;
    box-shadow: 0 0 24px 0 rgba(0, 0, 0, 0.22), 0 24px 24px 0 rgba(0, 0, 0, 0.3);
    top: 50%;
    left: 50%;
    margin: -291.62/2px 0 0 -449.25/2px;
    padding: 21px 24px;
    .tit {
      font-size: 20px;
      font-weight: 500;
    }
    .sele {
      height: 170px;
      overflow-y: scroll;
      p {
        margin-top: 20px;
      }
      label {
        font-size: 18px;
        color: rgba(0, 0, 0, 0.87);
        &:before {
          border-color: #871B55;
        }
        &:after {
          border-color: #871B55;
          background: #871B55;
        }
      }
    }
    .btns {
      position: absolute;
      right: 20px;
      bottom: 20px;
      .btn {
        width: 112.3px;
        height: 36px;
        border-radius: 2px;
        box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
        &:nth-child(1) {
          margin-right: 28px;
          background: #9B9B9B;
        }
        &:nth-child(2) {
          background: #871B55;
        }
      }
    }
  }

  .select-geography {
    width: 449.25px;
    min-height: 291.62px;
    max-height: 600px;
    border-radius: 2px;
    left: 50%;
    top: 50%;
    background-color: #FFFFFF;
    box-shadow: 0 0 24px 0 rgba(0, 0, 0, 0.22), 0 24px 24px 0 rgba(0, 0, 0, 0.3);
    position: fixed;
    margin: -291.62px/2 0 0 -449.25px/2;
    padding: 21px 24px;
    z-index: 7;
    .tit {
      color: rgba(0, 0, 0, 0.87);
      font-size: 20px;
      font-weight: 500;
    }
    .list {
      margin-top: 39px;
      p {
        margin-bottom: 25px;
      }
    }
    .btns {
      position: absolute;
      bottom: 16px;
      right: 26px;
      font-size: 14px;
      color: #00A6CE;
      span {
        margin-left: 20px;
        cursor: pointer;
      }
    }
    label {
      color: #000;
      font-size: 18px;
    }
    label:before {
      border-color: rgba(0, 0, 0, .52);
      width: 20px;
      height: 20px;
      margin-top: 2px;
    }
    [type="radio"].with-gap:checked + label:before {
      border-color: #871B55;

    }
    [type="radio"].with-gap:checked + label:after {
      background: #871B55;
      border-color: #871B55;
      width: 20px;
      height: 20px;
      margin-top: 2px;
    }
  }

  .error-prompt {
    position: fixed;
    top: 50%;
    left: 50%;
    z-index: 7;
    margin: -176px/2 0 0 -503.64px/2;
    height: 176px;
    width: 503.64px;
    border-radius: 2px;
    background-color: #FFFFFF;
    box-shadow: 0 0 24px 0 rgba(0, 0, 0, 0.22), 0 24px 24px 0 rgba(0, 0, 0, 0.3);
    padding: 24px;
    h3 {
      font-size: 20px;
      font-weight: 500;
    }
    p {
      font-size: 16px;
      color: rgba(0, 0, 0, 0.54);
      margin-top: 12px;
    }
    span {
      position: absolute;
      right: 34px;
      bottom: 17px;
      font-size: 14px;
      font-weight: 500;
      color: #00B0FF;
      cursor: pointer;
    }

  }

  #printBox {
    display: none;
  }
</style>
