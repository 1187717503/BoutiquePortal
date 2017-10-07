<template>
  <div id="printBox">
    <div
      style="margin: 0 auto;width:100%;background: #fff;font-family: Roboto-Bold, Arial, sans-serif, border-collapse;">
      <table width="100%" cellpadding="0" cellspacing="0" border="0">
        <tr style="height:120px">
          <th align="left">
            <img
              src="http://static-front.oss-cn-shanghai.aliyuncs.com/logo/284198222588302119.jpg?x-oss-process=image/resize,w_230"
              alt="logo">
          </th>
          <th style="font-size:16px;color: #000000;letter-spacing: 2.2px;">{{printData.vendor_name}}</th>
        </tr>
      </table>
      <table width="100%"
             style="border-collapse:collapse;border-top:1px solid #d3d3d3;border-bottom:1px solid #d3d3d3">
        <tr style="font-size:12px;">
          <td style="padding:16px 0 0 24px"><span
            style="transform:scale(0.833333);display:inline-block;margin-left:-6px">Order LIne No.</span></td>
          <td style="padding:16px 0 0 0;width:160px"><span
            style="transform:scale(0.833333);display:inline-block;margin-left:-6px">Order Date</span></td>
        </tr>
        <tr style="font-size:14px;letter-spacing: 1.09px;">
          <td style="padding:0 0 0 24px">{{printData.order_line_num}}</td>
          <td>{{printData.created_at | formDate}}</td>
        </tr>
        <tr>
          <td style="width:400px">
            <img :src="apiUrl+'image/getImage.htm?message='+printData.order_line_num+'&type=0'"
                 style="width:200px;margin:10px 0 16px 15px">
          </td>
          <td style="	font-size: 20px;">
            {{printData.geography_name}}
          </td>
        </tr>
      </table>
      <table width="100%" style="border-collapse:collapse;border-bottom:1px solid #d3d3d3;font-size:12px">
        <tr>
          <td rowspan="3" style="width:146px">
            <img
              :src="printData.cover_img,'?x-oss-process=image/resize,w_125,h_159' | ImgArr"
              style="padding:20px">
          </td>
          <td style="width:90px">
            <span style="transform:scale(0.833333);display:inline-block;margin-left:-4px;margin-top:6px">Brand</span>
            <p style="font-size:14px;margin:4px 0 0 0">{{printData.brandName}}</p>
          </td>
          <td colspan="3">
            <span style="transform:scale(0.833333);display:inline-block">Name</span>
            <p style="font-size:14px;margin:4px 0 0 0">{{printData.name}}</p>
          </td>
        </tr>
        <tr>
          <td style="width:120px">
              <span
                style="transform:scale(0.833333);display:inline-block;margin-left:-6px;margin-top:6px">Product ID</span>
            <p style="font-size:14px;margin:4px 0 0 0">{{printData.brandID}}</p>
          </td>
          <td>
            <span style="transform:scale(0.833333);display:inline-block;">Color</span>
            <p style="font-size:14px;margin:4px 0 0 0">{{printData.colorCode}}</p>
          </td>
          <td>
            <span style="transform:scale(0.833333);display:inline-block;">Size</span>
            <p style="font-size:14px;margin:4px 0 0 0">{{printData.size}}</p>
          </td>
          <td>
            <span style="transform:scale(0.833333);display:inline-block;margin-left:-6px">Boutique ID</span>
            <p style="font-size:14px;margin:4px 0 0 0">{{printData.vendor_id}}</p>
          </td>
        </tr>
        <tr>
          <td>
            <span style="transform:scale(0.833333);display:inline-block;margin-left:-6px">Retail Price</span>
            <p style="font-size:14px;margin:4px 0 0 0">€ {{printData.price | PriceForm}}</p>
          </td>
          <td>
            <span style="transform:scale(0.833333);display:inline-block;margin-left:-6px">Purchase Price</span>
            <p style="font-size:14px;margin:4px 0 0 0">€ {{printData.in_price | PriceForm}}</p>
          </td>
          <td>
            <span style="transform:scale(0.833333);display:inline-block;margin-left:-6px">Discount</span>
            <p style="font-size:14px;margin:4px 0 0 0">{{printData.supply_price_discount}} off</p>
          </td>
        </tr>
        <tr>
          <td colspan="5">
            <img v-if="printData.sku_code !=='#'&& printData.sku_code!== undefined"
                 :src="apiUrl+'image/getImage.htm?message='+printData.sku_code+'&type=1'" alt=""
                 style="width:167px;margin:10px 0 16px 24px">
          </td>
        </tr>
      </table>
    </div>
    <loading v-if="isLoading"></loading>
  </div>
</template>

<script>
  import loading from '../component/loading.vue'
  import {getOrderDetail} from '../../api/order'
  export default {
    data(){
      return {
        printData: [],
        isLoading: true,
        apiUrl:process.env.BASE_URL
      }
    },
    created(){
      this.printOrder(this.$route.query.sta, this.$route.query.num);
    },
    methods: {
      printOrder(sta, num){
        let data = {
          "status": sta,
          "orderNumber": num
        };
        this.isLoading = true;
        getOrderDetail(data).then(res => {
          if (res.data.status === 1) {
            this.printData = res.data.data;
            this.isImgLoad(() => {
              this.isLoading = false;
              setTimeout(() => {
                window.print();
                window.close();
              }, 1000);
            })
          } else {
            Materialize.toast(res.data.msg, 4000);
            this.isLoading = false
          }
        });
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

<style lang="css" media="print">
  @page 
  {
      size:  auto;   /* auto is the initial value */
      margin: 0mm;  /* this affects the margin in the printer settings */
  }

</style>
