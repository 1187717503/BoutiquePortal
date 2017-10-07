<template>
  <div>
    <div class="main-box">
      <nav-bar></nav-bar>
      <div class="screen">
        <div class="screening">

          <div id="sortBy" class="input-field">
            <label>Sort By</label>
            <select>
              <option v-for="item in SortBy" :value="item.val">{{item.name}}</option>
            </select>
          </div>
          <div id="status" class="input-field">
            <label>Shipment Status</label>
            <select>
              <option v-for="item in ShipmentStatus" :value="item.val">{{item.name}}</option>
            </select>
          </div>
          <div id="geography" class="input-field">
            <label>Geography</label>
            <select>
              <option v-for="item in Geography" :value="item.english_name">{{item.english_name}}</option>
            </select>
          </div>
        </div>
        <div class="list-box">
          <div class="line" v-for="item in listData">
            <div class="nugsp">
              <div>
                <i class="mdi mdi-dots-horizontal" @click="clickShow($event)"></i>
              </div>
              <div>
                <p>{{item.shipment_no}}</p>
                <span>Shipment No.</span>
              </div>
              <div>
                <p>{{item.shipment_status === '1' ? 'OPEN' : 'CLOSED'}}</p>
                <span>Status</span>
              </div>
              <div>
                <p>{{item.carton_qty}}</p>
                <span>Carton Qty</span>
              </div>
              <div>
                <p>{{item.product_qty}}</p>
                <span>Product Qty</span>
              </div>
              <div>
                <p>{{item.ship_to_geography}}</p>
                <span>Destination</span>
              </div>
              <div>
                <p v-if="item.shipment_status==='1'"
                   @click="updateShipment(item.shipment_id,item.shipment_status)">
                  <i class="mdi mdi-printer"></i>
                  <em style="top:0;">PRINT<br>DOCUMENTS</em>
                </p>
                <p v-if="item.shipment_status>='2'" @click="updateShipment(item.shipment_id,item.shipment_status)">
                  <i class="mdi mdi-truck"></i>
                  <em>SHIP</em>
                </p>
                <!--<p>-->
                <!--<i class="mdi mdi-package-variant"></i>-->
                <!--<em>REOPEN</em>-->
                <!--</p>-->
                <p v-if="item.shipment_status>='2'" @click="jumpEdit(item.shipment_id)">
                    <i class="mdi mdi-box-cutter"></i>
                    <em>EDIT</em>
                </p>
              </div>
            </div>
            <div class="rectangle">
              <div class="rows" v-for="(i,index) in item.cartonList">
                <div class="cent">
                  <div>
                    {{index + 1}} of {{item.cartonList.length}}
                  </div>
                  <div>
                    <p>{{i.barcode}}</p>
                    <span>Carton No.</span>
                  </div>
                  <div>
                    <p>{{i.product_qty}}</p>
                    <span>Product Qty</span>
                  </div>
                  <div>
                    <p>{{i.shipment_status === 1 ? 'OPEN' : 'Sealed'}}</p>
                    <span>Status</span>
                  </div>
                  <div>
                    <p v-if="i.shipment_status===1 && item.cartonList.length>1" @click="newShipments(i.container_id,i.shipment_id)">
                      <i class="mdi mdi-truck"></i>
                      <em style="top:0;">NEW<br>SHIPMENT</em>
                    </p>
                    <p v-if="i.shipment_status<3" @click="editShipment(i.container_id,i.shipment_status,i.barcode)">
                      <i class="mdi mdi-box-cutter"></i>
                      <em>EDIT</em>
                    </p>
                  </div>
                </div>
              </div>
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
  import navBar from './nav_bar.vue'
  import footBar from './foot_bar.vue'
  import loading from '../component/loading.vue'
  import {getReadyToShipCartonList, getGeography, updateShipmentById,updateContainerStatus, newShipment} from '../../api/order'
  export default {
    data(){
      return {
        isLoading: false,
        listData: [],
        SortBy: [
          {'val': 'shipment_status', 'name': 'Shipment Status'},
          {'val': 'geography_name', 'name': 'Geography'},
          {'val': 'shipment_no', 'name': 'Shipment No.'}
        ],
        ShipmentStatus: [
          {'val': '', 'name': 'All'},
          {'val': 1, 'name': 'Open'},
          {'val': 2, 'name': 'Closed'}
        ],
        Geography: [],
        selectData: {
          sortBy: '',
          status: '',
          geography: ''
        }
      }
    },
    mounted(){
      $('select').material_select();//初始化select
      this.selectData.sortBy = 'shipment_status';
      this.getList();
      this.loadGeography();
      $('#sortBy select').change((e) => {
        this.selectData.sortBy = e.target.value;
        this.getList();
      });
      $('#status select').change((e) => {
        this.selectData.status = e.target.value;
        this.getList();
      });
      $('#geography select').change((e) => {
        if (e.target.value === 'All') {
          this.selectData.geography = ''
        } else {
          this.selectData.geography = e.target.value;
        }
        this.getList();
      })

    },
    methods: {
      clickShow(e){
        if (e.target.classList.value === 'mdi mdi-dots-vertical') {
          e.target.classList.value = 'mdi mdi-dots-horizontal';
          e.target.parentNode.parentNode.parentNode.children[1].style.display = 'none';
        } else {
          e.target.classList.value = 'mdi mdi-dots-vertical';
          e.target.parentNode.parentNode.parentNode.children[1].style.display = 'block';
        }
      },
      newShipments(cid, sid){
        let data = {
          "container_id": cid,
          "shipmentId": sid
        };
        newShipment(data).then(res => {
          if (res.data.status === 1) {
            Materialize.toast('New success', 4000);
            this.getList();
          } else {
            Materialize.toast('A new failure', 4000);
          }
        })
      },
      getList(){
        let data = {
          "sortByName": this.selectData.sortBy,
          "shipmentStatus": this.selectData.status,
          "ship_to_geography": this.selectData.geography
        };
        this.isLoading = true;
        getReadyToShipCartonList(data).then(res => {
          if (res.data.status === 1) {
            this.listData = res.data.data
          }
          this.isLoading = false;
        })
      },
      loadGeography(){
        getGeography().then(res => {
          if (res.data.status === 1) {
            this.Geography = res.data.data;
            this.Geography.unshift({'english_name': 'All'});
            setTimeout(() => {
              $('select').material_select();//初始化select
            }, 100);
          }
        })
      },
      updateShipment(id, sta){
        if (sta === '1') {
          this.$router.push({name: 'ReadyShip', params: {id: id}});
        } else if (sta === '2') {
          let data = {
            "shipmentId": id,
            "status": 3
          };
          this.isLoading = true;
          updateShipmentById(data).then(res => {
            if (res.data.status === 1) {
              this.$router.push({name: 'Shipped'})
            } else {
              Materialize.toast('Request error', 2000);
            }
            this.isLoading = false;
          })
        } else {
          this.$router.push({name: 'ReadyShip', params: {id: id}})
        }
      },
      editShipment(cid, sta, code){
        if (sta === 1) {
          this.$router.push({name: 'PackOrder', params: {id: code}})
        }else if(sta===2){
          let data = {
            "containerId": cid,
            "status": 1
          };
          updateContainerStatus(data).then(res=>{
            if (res.data.status === 1) {
              this.$router.push({name: 'PackOrder', params: {id: code}})
            } else {
              Materialize.toast('Failure to modify status', 2000);
            }
          })
        }
      },
      jumpEdit(id){
          this.$router.push({name:'ReadyShip',params:{id:id}});
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
  .screen {
    .screening {
      .input-field {
        label {
          font-size: 12px;
          position: absolute;
          top: -8px;
          left: 0;
        }
        span {
          color: #00A6CE;
        }
        input.select-dropdown {
          border-bottom: 2px solid #00A6CE;
          height: 40px;
          line-height: 40px;
        }
      }
    }
  }
</style>
<style lang="less" scoped>
  .main-box {
    width: 100%;
    background-color: #FAFAFA;
  }

  .screen {
    .screening {
      height: 67px;
      display: flex;
      .input-field {
        width: 168px;
        margin-left: 40px;
        font-size: 16px;
      }
    }
    .list-box {
      background: #E4E4E4;
      .line {
      }
      .nugsp {
        border: 1px solid #979797;
        margin-top: -1px;
        height: 62px;
        display: flex;
        position: relative;
        div {
          text-align: center;
          p {
            font-size: 18px;
            margin-top: 9px;
            line-height: normal;
          }
          a {
            color: #000;
          }
          span {
            font-size: 12px;
          }
        }
        div:nth-child(1) {
          line-height: 58px;
          margin: 0 16px;
          font-size: 22px;
          i {
            cursor: pointer;
          }
        }
        div:nth-child(2) {
          width: 140px;
        }
        div:nth-child(3) {
          width: 140px;
        }
        div:nth-child(4) {
          width: 140px;
        }
        div:nth-child(5) {
          width: 140px;
        }
        div:nth-child(6) {
          width: 140px;
        }
        div:nth-child(7) {
          position: absolute;
          right: 10px;
          p {
            margin-top: 16px;
            display: inline-block;
            margin-right: 20px;
            cursor: pointer;
          }
          i {
            height: 31px;
            width: 31px;
            background-color: #871B55;
            box-shadow: 0 1px 6px 0 rgba(0, 0, 0, 0.24);
            color: #fff;
            border-radius: 31px;
            line-height: 28px;
            float: left;
          }
          em {
            font-style: normal;
            font-size: 10px;
            display: inline-block;
            text-align: left;
            float: left;
            position: relative;
            top: 10px;
            left: 4px;
          }
        }

      }
      .rectangle {
        display: none;
        .rows {
          border-bottom: 1px solid #979797;
          .cent {
            margin-left: 180px;
            background: #fff;
            height: 62px;
            display: flex;
            position: relative;
            &:before {
              content: ' ';
              display: inline-block;
              width: 1px;
              height: 62px;
              border-left: 1px solid #979797;
              position: relative;
              left: -82px;
            }
            &:after {
              content: ' ';
              display: inline-block;
              width: 82px;
              height: 62px;
              border-top: 1px solid #979797;
              position: absolute;
              left: -82px;
              top: 30px;
            }
            div {
              text-align: center;
              p {
                font-size: 18px;
                line-height: normal;
                margin-top: 11px;
              }
              span {
                font-size: 12px;
              }
              &:nth-child(1) {
                width: 180px;
                font-size: 18px;
                line-height: 62px;
              }
              &:nth-child(2) {
                width: 150px;
              }
              &:nth-child(3) {
                width: 150px;
              }
              &:nth-child(4) {
                width: 150px;
              }
              &:nth-child(5) {
                position: absolute;
                right: 63px;
                p {
                  margin-top: 16px;
                  display: inline-block;
                  margin-right: 20px;
                  cursor: pointer;
                }
                a {
                  color: #000;
                }
                i {
                  height: 31px;
                  width: 31px;
                  background-color: #871B55;
                  box-shadow: 0 1px 6px 0 rgba(0, 0, 0, 0.24);
                  color: #fff;
                  border-radius: 31px;
                  line-height: 28px;
                  float: left;
                }
                em {
                  font-style: normal;
                  font-size: 10px;
                  display: inline-block;
                  text-align: left;
                  float: left;
                  position: relative;
                  top: 10px;
                  left: 4px;
                }
              }
            }
          }
          &:last-of-type {
            .cent {
              &:before {
                height: 30px;
              }
            }
          }
        }
      }
    }
  }
</style>

