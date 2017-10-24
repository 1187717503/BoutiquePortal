<template>
  <div>
    <div class="head-icon">
      <span class="mdi mdi-arrow-left" onclick="window.history.go(-1)"></span>
    </div>
    <div class="head-info">
      <div class="line">
        <div class="info">
          <p>{{listData.shipmentInfo.shipment_no}}</p>
          <p>{{listData.shipmentInfo.ship_to_geography}}</p>
        </div>
        <p class="info">Carton: {{listData.shipmentInfo.carton_qty}}</p>
        <div class="input">
          <p class="input-field">
            <input id="Invoice" type="text" class="validate" v-model="listData.shipmentInfo.invoice_num">
            <label for="Invoice" :class="{active:listData.shipmentInfo.invoice_num}">Invoice No.</label>
          </p>
          <p class="input-field">
            <i class="mdi mdi-calendar prefix"></i>
            <input id="date" type="text" class="validate" v-model="listData.shipmentInfo.invoice_date">
            <label for="date" class="active">Invoice Date</label>
          </p>
        </div>
        <div class="status">
          <p>{{shipmentStatus}}</p>
          <div class="icon" v-if="listData.shipmentInfo.status===2"
               @click="updateState(listData.shipmentInfo.shipment_id,1)">
            <span class="mdi mdi-package-variant"></span><i>REOPEN</i>
          </div>
        </div>
      </div>
      <div class="btns">
        <div @click="printInvoice(listData.shipmentInfo)">
          <span class="mdi mdi-printer"></span>
          <p>INVOICE</p>
        </div>
        <div @click="printPacking(listData.shipmentInfo)">
          <span class="mdi mdi-printer"></span>
          <p>PACKING
            LIST</p>
        </div>
        <div>
          <span class="mdi mdi-printer"></span>
          <p>AWB</p>
        </div>
        <div @click="updateState(listData.shipmentInfo.shipment_id,3)">
          <span class="mdi mdi-truck"></span>
          <p>SHIP</p>
        </div>
      </div>
    </div>
    <div class="ship-box">
      <div class="list" v-for="(item,index) in listData.cartonList">
        <div class="top-tit">
          <div>{{index + 1}} of {{listData.cartonList.length}}</div>
          <div>{{item.barcode}}</div>
          <div>Qty: {{item.order_qty}}</div>
          <div>{{item.width}} cm X {{item.length}} cm X {{item.height}} cm</div>
          <div>
            <span class="mdi mdi-unfold-more-horizontal" @click="showList($event)"></span>
          </div>
        </div>
        <div class="list-box">
          <div class="head-tit">
            <div></div>
            <div>Product ID</div>
            <div>Color</div>
            <div>Size</div>
            <div>Brand</div>
            <div>Name</div>
            <div>Retail<br>Price</div>
            <div>Sale<br>Price</div>
            <div>Order Line No.</div>
            <div>Order Date</div>
          </div>
          <div class="li" v-for="i in item.orderList">
            <div>
              <img
                :src="i.cover_img,'?x-oss-process=image/resize,m_fill,w_98,h_125' | ImgArr"
                alt="">
            </div>
            <div>{{i.brandID}}</div>
            <div>{{i.colorCode}}</div>
            <div>{{i.size}}</div>
            <div>{{i.brandName}}</div>
            <div>{{i.name}}</div>
            <div>€{{i.price | PriceForm}}</div>
            <div>€{{i.in_price | PriceForm}}</div>
            <div>{{i.order_line_num}}</div>
            <div>{{i.updated_at | formDate}}</div>
          </div>
        </div>

      </div>
    </div>
    <div class="boxlist-layer" v-if="isBoxlist.show">
      <h3 class="tit">Please provide carton dimension:</h3>
      <div class="table">
        <div class="head">
          <div>Carton# </div>
          <div>Length (cm)</div>
          <div>Width (cm)</div>
          <div>Height (cm)</div>
        </div>
        <div class="list">
          <div class="li" v-for="item in boxInfoList">
            <div :data-id="item.container_id">{{item.barcode}}</div>
            <div>
              <input type="text" :value="item.length===0?'':item.length" placeholder="Length">
            </div>
            <div>
              <input type="text" :value="item.width===0?'':item.width" placeholder="Width">
            </div>
            <div>
              <input type="text" :value="item.height===0?'':item.height" placeholder="Height">
            </div>
          </div>
        </div>
      </div>
      <div class="btns">
        <span @click="isBoxlist.show=false;isBoxlist.info=[]">Cancel</span>
        <span @click="saveBoxlist">Save</span>
      </div>
    </div>
    <shade v-if="isBoxlist.show"></shade>
    <!--打印订单-->
    <div id="invoiceList">
      <div
        style="margin: 0 auto;width:100%;background: #fff;font-family: Roboto-Bold, Arial, sans-serif, border-collapse;">
        <!--<table width="100%" cellpadding="0" cellspacing="0" border="0">
          <tr style="height:120px">
            <th align="left">
              <img
                src="http://static-front.oss-cn-shanghai.aliyuncs.com/logo/284198222588302119.jpg?x-oss-process=image/resize,w_230"
                alt="logo">
            </th>
            <th style="font-size:20px;color: #000000;letter-spacing: 2.2px;text-align: right;padding-right:44px;">
              {{printInvoiceData.ShipVendorName}}
            </th>
          </tr>
        </table>-->
        <table width="100%" cellpadding="0" cellspacing="0" border="0"
               style="border-collapse: collapse;border-top: 1px solid #d3d3d3;border-bottom: 1px solid #d3d3d3;">
          <tr style="	font-size:16px;font-weight:500;">
            <td style="padding-left:24px;padding-top:17px;width:50%">Invoice From</td>
            <td style="padding-top:17px;width:50%">Shipping From</td>
          </tr>
          <tr style="	font-size:12px;">
            <td style="padding-left:24px;padding-top:8px;width:50%">
              {{printInvoiceData.ShipCompanyName}}<br>
              {{printInvoiceData.ShipFrom}}<br>
              VAT Number: {{printInvoiceData.VATNumber}}<br>
              <br>
              Date of Invoice: {{printInvoiceData.InvoiceDate}}<br>
              Invoice Number: {{printInvoiceData.InvoiceNumber}}<br>
            </td>
            <td style="vertical-align:top;padding-top:8px;width:50%">
              {{printInvoiceData.ShipCompanyName}}<br>
              {{printInvoiceData.ShipFrom}}<br>
            </td>
          </tr>
          <tr style="	font-size:16px;font-weight:500;">
            <td style="padding-left:24px;padding-top:17px;width:50%">Invoice To</td>
            <td style="padding-top:17px;width:50%">Deliver To</td>
          </tr>
          <tr style="	font-size:12px;">
            <td style="padding-left:24px;padding-top:8px;width:50%;padding-bottom:15px">
              {{printInvoiceData.InvoiceName}}<br>
              {{printInvoiceData.InvoiceTo}}<br>
            </td>
            <td style="vertical-align:top;padding-top:8px;width:50%;padding-bottom:15px">
              <span v-if="printInvoiceData.DeliverTo && printInvoiceData.DeliverTo.transferConsignee">
                {{printInvoiceData.DeliverTo.transferConsignee}}<br>
                <br>
                {{printInvoiceData.DeliverTo.addrCountry}}<br>
                {{printInvoiceData.DeliverTo.addrCity}}
              </span>
              <span v-if="printInvoiceData.DeliverTo && printInvoiceData.DeliverTo.consignee">
                {{printInvoiceData.DeliverTo.consignee}}<br>
                {{printInvoiceData.DeliverTo.shipToCountry}}<br>
                <br>
                {{printInvoiceData.DeliverTo.shipToProvince}} {{printInvoiceData.DeliverTo.shipToDistrict}} {{printInvoiceData.DeliverTo.shipToCity}}<br>
                {{printInvoiceData.DeliverTo.shipToAddr}}
              </span>
            </td>
          </tr>
        </table>
        <table width="100%" cellpadding="0" cellspacing="0" border="0"
               style="border-collapse: collapse;border-bottom: 1px solid #d3d3d3;">
          <tr style="font-size:14px;border-bottom: 1px solid #d3d3d3;">
            <td style="padding-left:16px;padding-top:26px">Order No.</td>
            <td style="padding-top:26px">Product Description</td>
            <td style="padding-top:26px">Composition</td>
            <td style="padding-top:26px">Made In</td>
            <td style="position:relative;top:-10px;padding-top:26px;width:80px;text-align: right;padding-right:20px">
              Purchase<br>Price
            </td>
          </tr>
          <tr style="line-height:30px;font-size:12px;border-bottom: 1px solid #d3d3d3;"
              v-for="item in printInvoiceData.cartonList">
            <td style="padding-left:16px;">{{item.order_line_num}}</td>
            <td>{{item.brandName}}　{{item.categoryName}}<br>{{item.brandID}}/{{item.colorCode}}/{{item.size}}</td>
            <td>{{item.Composition}}</td>
            <td>{{item.MadeIn}}</td>
            <td style="text-align: right;padding-right:20px">€{{item.in_price | PriceForm}}</td>
          </tr>
          <tr style="border-bottom:1px solid #d3d3d3">
            <td colspan="4" style="text-align: right;padding:10px 0">Total: {{printInvoiceData.all_qty}}</td>
            <td colspan="1" style="text-align: right;padding-right:30px;">€ {{printInvoiceData.allTotal | PriceForm}}
            </td>
          </tr>
          <tr style="border-bottom:1px solid #d3d3d3;">
            <td colspan="4" style="padding:10px 0;text-align:right">VAT: </td>
            <td style="padding:10px 0;text-align:right;padding-right:30px;" colspan="1">
              € {{printInvoiceData.VAT | PriceForm}}
            </td>
          </tr>
          <tr>
            <td colspan="4" style="padding:10px 0;text-align:right">Grand total: </td>
            <td style="padding:10px 0;text-align:right;padding-right:30px;" colspan="1">
              € {{printInvoiceData.GrandTotal | PriceForm}}
            </td>
          </tr>
        </table>
        <p style="margin-top:40px">Shipment exempt from VAT - IVA non imponibile Art. 8 1°C L/A DPR 633/72</p>
      </div>
    </div>
    <!--加载动画-->
    <loading v-if="isLoad"></loading>
  </div>
</template>

<script type="text/javascript">
  import flatpickr from 'flatpickr'
  import shade from '../component/shade.vue'
  import '../../../node_modules/flatpickr/dist/flatpickr.css'
  import '../../assets/js/jQuery.print' //打印插件
  import {
    getShipmentInfo,
    updateShipmentById,
    printShipmentInfo,
    addInvoice,
    printPackingList,
    getAllByShipmentId,
    updateAllContainer
  } from '../../api/order'
  import loading from '../component/loading.vue'

  export default {
    data() {
      return {
        sid: this.$route.params.id,
        isLoad: true,
        saveType: 0,
        isBoxlist: {
          show: false,
          info: []
        },
        boxInfoList: [],
        listData: {
          'shipmentInfo': [{
            'shipment_no': null,
            'carton_qty': null,
            'ship_to_geography': null
          }]
        },
        shipmentStatus: '',
        printInvoiceData: {
          DeliverTo: {
            name: ''
          }
        }
        ,
        printPackingData: []
      }
    },
    mounted() {
      this.getList();
      flatpickr('#date', {
        dateFormat: "d/m/Y",
        defaultDate: 'today'
      });
      this.getAllBoxinfo(this.sid)
    },
    methods: {
      showList(e) {
        if (e.target.className === 'mdi mdi-unfold-more-horizontal') {
          e.target.className = 'mdi mdi-unfold-less-horizontal';
          e.target.parentNode.parentNode.parentNode.children[1].style.display = 'block'
        } else {
          e.target.className = 'mdi mdi-unfold-more-horizontal';
          e.target.parentNode.parentNode.parentNode.children[1].style.display = 'none'
        }
      },
      getAllBoxinfo(id) {
        let data = {
          shipmentId: id
        };
        getAllByShipmentId(data).then(res => {
          this.boxInfoList = res.data.data
        })
      },
      saveBoxlist() {
        let data = {
          containerList: []
        };
        let inputDom = $('.boxlist-layer input');
        let isNull = true;
        for (let i = 0; i < inputDom.length; i++) {
          if (inputDom[i].value === '') {
            inputDom[i].parentNode.classList.add('active');
            isNull = false;
          } else {
            inputDom[i].parentNode.classList.remove('active');
          }
        }
        let liDom = $('.boxlist-layer .li');
        for (let i = 0; i < liDom.length; i++) {
          let cid = liDom[i].childNodes[0].dataset.id;
          let len = liDom[i].childNodes[2].childNodes[0].value;
          let wid = liDom[i].childNodes[4].childNodes[0].value;
          let hen = liDom[i].childNodes[6].childNodes[0].value;
          let obj = {
            "container_id": cid,
            "height": hen,
            "width": wid,
            "length": len
          };
          data.containerList.push(obj);
        }

        if (!isNull) {
          return false;
        }

        updateAllContainer(data).then(res => {
          if (res.data.status === 1) {
            let info = this.isBoxlist.info;
            if (this.saveType === 1) {
              this.printShip(info);
            } else {
              this.isBoxlist = {
                show: false,
                info: []
              };
              this.getList();
            }
          }
        });
      },
      printShip(info) {
        if (info.status <= '1') {
          let data = {
            "shipmentId": info.shipment_id,
            "status": 2
          };
          updateShipmentById(data).then(res => {
            if (res.data.status === 1) {
              this.isBoxlist = {
                show: false,
                info: []
              };
              Materialize.toast('Status change success', 4000);
              this.getList();
              window.open('/readyshipprint?sid=' + this.sid);
            } else {
              Materialize.toast('Status change failed', 4000);
              return false
            }
          })
        } else {
          window.open('/readyshipprint?sid=' + this.sid);
        }
      },
      getList() {
        let data = {
          "status": "7",
          "shipment_id": this.sid
        };
        this.isLoad = true;
        getShipmentInfo(data).then(res => {
          if (res.data.status === 1) {
            this.listData = res.data.data
            switch (res.data.data.shipmentInfo.status) {
              case 1:
                this.shipmentStatus = 'Open';
                break;
              case 2:
                this.shipmentStatus = 'Closed';
                break;
              case 3:
                this.shipmentStatus = 'Shipped';
                break;
              case 4:
                this.shipmentStatus = 'Delivered';
                break
            }
          } else {
            Materialize.toast('获取数据出错', 2000);
            setTimeout(() => {
              window.history.go(-1)
            }, 2000)
          }
          this.isLoad = false;
        })
      },
      printInvoice(info) {
        let isW = true;
        for (let i in this.listData.cartonList) {
          if (this.listData.cartonList[i].height === '0' || this.listData.cartonList[i].length === '0' || this.listData.cartonList[i].width === '0') {
            isW = false;
          }
        }
        if (!this.listData.shipmentInfo.invoice_num || this.listData.shipmentInfo.invoice_num === '') {
          Materialize.toast('Please enter Invoice No.', 4000);
          return false
        } else if (!this.listData.shipmentInfo.invoice_date || this.listData.shipmentInfo.invoice_date === '') {
          Materialize.toast('Please select Invoice Date', 4000);
          return false
        } else if (!isW) {
          this.isBoxlist = {
            show: true,
            info: []
          };
          this.saveType = 2;
          return false;
        }
        if (info.status <= '1') {
          let data = {
            "shipmentId": info.shipment_id,
            "status": 2
          };
          updateShipmentById(data).then(res => {
            if (res.data.status === 1) {
              Materialize.toast('Status change success', 4000);
              this.getList();
            } else {
              Materialize.toast('Status change failed', 4000);
              return false
            }
          })
        }
        let data = {
          "shipmentId": this.sid,
          "invoiceNo": this.listData.shipmentInfo.invoice_num,
          "invoiceDate": this.listData.shipmentInfo.invoice_date
        };
        this.isLoad = true;
        addInvoice(data).then(res => {
          if (res.data.status == 1) {
            let data = {
              "status": "7",
              "shipment_id": this.sid
            };
            printShipmentInfo(data).then(res => {
              if (res.data.status === 1) {
                this.printInvoiceData = res.data.data;
                setTimeout(() => {
                  $('#invoiceList').show();
                  $('#invoiceList').print();
                  $('#invoiceList').hide();
                  this.isLoad = false;
                }, 1000)
              }
            });
          }
        });
      },
      printPacking(info) {
        if (info.status > 1) {
          this.printShip(info)
        } else {
          this.saveType = 1;
          this.isBoxlist = {
            show: true,
            info: info
          };
        }
      },
      updateState(id, sta) {
        let data = {
          "shipmentId": id,
          "status": sta
        };
        updateShipmentById(data).then(res => {
          if (res.data.status === 1) {
            if (sta === 1) {
              this.$router.push({name: 'Ready'})
            } else {
              this.$router.push({name: 'Shipped'})
            }
          } else {
            Materialize.toast('Failure to modify status', 2000);
          }
          this.isLoading = false;
        })
      }
    },
    components: {
      loading,
      shade
    }
  }
</script>

<style lang="less" scoped>
  .head-icon {
    span {
      margin-left: 10px;
      font-size: 26px;
      color: #871B55;
      cursor: pointer;
    }
  }

  .head-info {
    position: relative;
    width: 100%;
    height: 137.6px;
    margin: 10px 0;
    background-color: #FAFAFA;
    box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
    .line {
      display: flex;
      padding-left: 24px;
      .input {
        p:nth-child(2) {
          margin-top: 30px;
          i {
            font-size: 20px;
            top: 2px;
            color: #333;
          }
          input {
            text-indent: 24px;
            color: #000;
          }
          label {
            margin-left: 20px;
            color: #333;
          }
          label.active {
            margin-left: 0;
          }
        }
      }
      .info {
        margin-right: 70px;
        padding-top: 27px;
        font-size: 20px;
        font-weight: 500;
        p:nth-child(2) {
          margin-top: 20px;
        }
      }
      .status {
        margin-top: 30px;
        margin-left: 60px;
        p {
          font-size: 20px;
          font-weight: 500;
        }
        .icon {
          margin-top: 27px;
          span {
            text-align: center;
            display: inline-block;
            height: 31px;
            width: 31px;
            background-color: #871B55;
            box-shadow: 0 1px 6px 0 rgba(0, 0, 0, 0.24);
            border-radius: 31px;
            color: #fff;
            line-height: 28px;
            font-size: 16px;
            cursor: pointer;
          }
          i {
            font-style: normal;
            margin-left: 6px;
            font-size: 12px;
            color: #000;
          }
        }
      }
      &:nth-child(1) {
        input {
          margin: 0;
          border-bottom: 2px solid #00A6CE;
          box-shadow: none;
          width: 147px;
          &:focus {
            border-bottom: 2px solid #00A6CE;
          }
        }
        label {
          left: 0;
        }
      }
    }
    .btns {
      position: absolute;
      top: 40px;
      right: 50px;
      div {
        width: 42px;
        text-align: center;
        float: left;
        margin-left: 70px;
      }
      span {
        display: inline-block;
        height: 31px;
        width: 31px;
        background-color: #871B55;
        box-shadow: 0 1px 6px 0 rgba(0, 0, 0, 0.24);
        border-radius: 31px;
        color: #fff;
        line-height: 28px;
        font-size: 16px;
        cursor: pointer;
      }
      p {
        font-size: 10px;
        margin-top: 4px;
      }
    }
  }

  .ship-box {
    .list {
      width: 100%;
      background-color: #FAFAFA;
      box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
      margin: 5px 0 10px 0;
      .top-tit {
        display: flex;
        line-height: 72px;
        position: relative;
        div {
          &:nth-child(1), &:nth-child(2), &:nth-child(3) {
            margin-left: 24px;
            font-size: 20px;
            font-weight: 500;
            margin-right: 60px;
          }
          &:nth-child(4) {
            font-size: 18px;
            color: #9B9B9B;
          }
          &:nth-child(5) {
            position: absolute;
            right: 60px;
            font-size: 24px;
            span {
              cursor: pointer;
            }
          }
        }
      }
      .list-box {
        clear: both;
        display: none;
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
            width: 130px;
          }
          div:nth-child(3) {
            width: 32+17px;
          }
          div:nth-child(4) {
            width: 30+22px;
          }
          div:nth-child(5) {
            width: 100px;
          }
          div:nth-child(6) {
            width: 180px;
          }
          div:nth-child(7) {
            width: 72+53px;
            margin-top: -22px;
          }
          div:nth-child(8) {
            width: 72px;
            margin-top: -22px;
          }
          div:nth-child(9) {
            width: 93+72px;
          }
          div:nth-child(10) {
            width: 150px;
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
            overflow: hidden;
            text-overflow: ellipsis;
          }
          div:nth-child(1) {
            width: 51px;
            margin: 0 0 0 22px;
            padding-top: 5px;
          }
          div:nth-child(2) {
            width: 130px;
          }
          div:nth-child(3) {
            width: 32+17px;
          }
          div:nth-child(4) {
            width: 30+22px;
          }
          div:nth-child(5) {
            width: 100px;
          }
          div:nth-child(6) {
            width: 180px;
          }
          div:nth-child(7) {
            width: 72+53px;
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
            width: 150px;
          }
        }
      }
    }
  }

  .boxlist-layer {
    position: fixed;
    left: 50%;
    top: 50%;
    margin: -483.19px/2 0 0 -535px/2;
    height: 483.19px;
    width: 535px;
    z-index: 7;
    border-radius: 2px;
    background-color: #FFFFFF;
    padding: 24px;
    box-shadow: 0 0 24px 0 rgba(0, 0, 0, 0.22), 0 24px 24px 0 rgba(0, 0, 0, 0.3);
    .tit {
      font-size: 20px;
      font-weight: 500;
    }
    .table {
      margin-top: 30px;
      .head {
        display: flex;
        font-size: 16px;
        font-weight: bold;
        color: #4A4A4A;
        border-bottom: 1px solid #4A4A4A;
        padding-bottom: 8px;
        div {
          flex: 1;
          text-align: center;
          &:nth-child(1) {
            text-align: left;
            text-indent: 14px;
          }
        }
      }
      .list {
        height: 320px;
        overflow: auto;
        .li {
          display: flex;
          text-align: center;
          line-height: 46px;
          font-size: 16px;
          font-weight: 300;
          border-bottom: 1px solid #E0E0E0;
          div {
            flex: 1;
            input {
              width: 60px;
              text-align: center;
              margin: 0;
              border-bottom: 2px solid #00A6CE;
            }
          }
          .active {
            input {
              border-bottom: 2px solid #D0021B;
            }
            position: relative;
            &:after {
              width: 121.75px;
              position: absolute;
              content: 'Cant be blank';
              font-size: 12px;
              display: block;
              top: 32px;
            }
          }
        }
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
      }
    }
  }

  #packingList, #invoiceList {
    display: none;
  }
</style>
