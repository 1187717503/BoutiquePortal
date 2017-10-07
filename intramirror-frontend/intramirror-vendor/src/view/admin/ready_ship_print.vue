<template>
  <div id="packingList">
    <div
      style="margin: 0 auto;width:100%;background: #fff;font-family: Roboto-Bold, Arial, sans-serif, border-collapse;height:1170px;"
      v-for="item in printPackingData.cartonList">
      <table width="100%" cellpadding="0" cellspacing="0" border="0">
        <tr style="height:120px">
          <th align="left">
            <img
              src="http://static-front.oss-cn-shanghai.aliyuncs.com/logo/284198222588302119.jpg?x-oss-process=image/resize,w_230"
              alt="logo">
          </th>
          <th style="font-size:20px;color: #000000;letter-spacing: 2.2px;text-align: right;padding-right:44px;">
            Packing List
          </th>
        </tr>
      </table>
      <table width="100%" cellpadding="0" cellspacing="0" border="0"
             style="border-collapse: collapse;border-top: 1px solid #d3d3d3;border-bottom: 1px solid #d3d3d3;">
        <tr style="font-size:12px;">
          <td colspan="2" style="padding-left:24px;padding-top:17px;">Carton Number</td>
        </tr>
        <tr style="font-size:20px;letter-spacing:1px;">
          <td style="padding-left:24px;padding-top:5px;">{{item.barcode}}</td>
          <td style="text-align: right;padding-right:44px;">{{item.ship_to_geography}}</td>
        </tr>
        <tr>
          <td><img
            :src="apiUrl+'image/getImage.htm?message='+item.barcode+'&type=0'"
            style="margin-left:16px;margin-top:8px;margin-bottom:16px;" alt=""></td>
          <td style="text-align: right;padding-right:44px;">
            Measure:　{{item.width}} x {{item.length}} x {{item.height}} CM
          </td>
        </tr>
      </table>
      <table width="100%" cellpadding="0" cellspacing="0" border="0"
             style="border-collapse: collapse;border-bottom: 1px solid #d3d3d3;">
        <tr style="font-size:14px;border-bottom: 1px solid #d3d3d3;">
          <td style="padding-left:16px;padding-top:26px">Order No.</td>
          <!--<td style="padding-top:26px">Brand</td>-->
          <td style="padding-top:26px">Product Description</td>
          <td style="padding-top:26px">Product ID</td>
          <td style="padding-top:26px	">Color</td>
          <td style="padding-top:26px">Size</td>
          <td style="padding-top:26px">Qty</td>
          <!--<td style="position:relative;top:-10px;padding-top:26px;width:80px;text-align: right;padding-right:20px">-->
            <!--Retail<br>Price-->
          <!--</td>-->
          <!--<td style="position:relative;top:-10px;padding-top:26px;width:80px;text-align: right;padding-right:20px">-->
            <!--Purchase<br>Price-->
          <!--</td>-->
          <!--<td style="padding-top:26px;width:80px;text-align: right;padding-right:30px;">Total</td>-->
        </tr>
        <tr style="line-height:30px;font-size:12px;border-bottom: 1px solid #d3d3d3;" v-for="i in item.orderList">
          <td style="padding-left:16px;">{{i.order_line_num}}</td>
          <!--<td>{{i.brandName}}</td>-->
          <td>{{i.categoryName}}</td>
          <td>{{i.brandID}}</td>
          <td>{{i.colorCode}}</td>
          <td>{{i.size}}</td>
          <td>{{i.amount}}</td>
          <!--<td style="text-align: right;padding-right:20px">€{{i.price | PriceForm}}</td>-->
          <!--<td style="text-align: right;padding-right:20px">€{{i.in_price | PriceForm}}</td>-->
          <!--<td style="text-align: right;padding-right:30px;">€{{i.Total | PriceForm}}</td>-->
        </tr>
        <tr>
          <td colspan="6" style="text-align: right;padding:10px 30px 10px 0">Total:{{item.allTotal}}</td>
        </tr>
      </table>
    </div>
    <loading v-if="isLoading"></loading>
  </div>
</template>

<script>
  import loading from '../component/loading.vue'
  import {printPackingList} from '../../api/order'
  export default {
    data(){
      return {
        printPackingData:[],
        sid: this.$route.query.sid,
        isLoading: true,
        apiUrl:process.env.BASE_URL
      }
    },
    mounted(){
        this.printPacking();
    },
    methods: {
      printPacking(){
        let data = {
          "status": "7",
          "shipment_id": this.sid
        };
        this.isLoad = true;
        printPackingList(data).then(res => {
          if (res.data.status === 1) {
            this.printPackingData = res.data.data;
            setTimeout(()=>{
              this.isImgLoad(() => {
                this.isLoading = false;
                setTimeout(() => {
                  window.print();
                  window.close();
                }, 100);
              })
            },100);

          }
        })
      },
      isImgLoad(callback){
        let t_img;
        let isLoad = true;
        // 查找所有打印图，迭代处理
        $('img').each(function () {
          // 找到为0就将isLoad设为false，并退出each
          if (this.height === 0) {
            isLoad = false;
            return false;
          }
        });
        // 为true，没有发现为0的。加载完毕
        if (isLoad) {
          clearTimeout(t_img); // 清除定时器
          // 回调函数
          callback();
          // 为false，因为找到了没有加载完成的图，将调用定时器递归
        } else {
          isLoad = true;
          t_img = setTimeout(() => {
            this.isImgLoad(callback); // 递归扫描
          }, 500); // 我这里设置的是500毫秒就扫描一次，可以自己调整
        }
      }
    },
    components: {
      loading
    }
  }
</script>

<style>

</style>
