<template>
  <div style="background:#fff;font-family:Roboto-Regular;" >
    <p style="text-align:center;font-size:20px;padding:80px 0 50px 0">Questo foglio d'ordine accompagna sempre il <br> prodotto confermato insieme al packing fino alla <br> destinazione al cliente finale</p>
    <table width="100%" cellpadding="0" cellspacing="0" border="0">
      <tr>
        <td style="padding-left:24px;font-size:16px">Order Date</td>
        <td></td>
      </tr>
      <tr>
        <td style="padding-left:24px;font-size:20px">{{printData.created_at | formDate}}</td>
        <td style="font-size:30px">{{printData.geography_name}}</td>
      </tr>
      <tr>
        <td style="padding-left:24px;font-size:16px;padding-top:37px">Order Number</td>
      </tr>
      <tr>
        <td style="padding-left:24px;font-size:47px">{{printData.order_line_num}}</td>
      </tr>
      <tr>
        <td>
          <img style="padding-left:10px;margin-top:10px" :src="apiUrl+'image/getImage.htm?message='+printData.order_line_num+'&type=0&width=2'" alt="">
        </td>
      </tr>
    </table>
    <table width="100%"
           style="border-collapse:collapse;border-top:1px solid #d3d3d3;border-bottom:1px solid #d3d3d3;margin-top:32px">
      <tr style="font-size:12px">
        <td style="padding-top:28px;padding-left:37px">Brand</td>
        <td colspan="2" style="padding-top:28px">Name</td>
      </tr>
      <tr style="font-size:20px">
        <td style="padding-left:37px">{{printData.brandName}}</td>
        <td>{{printData.name}}</td>
        <td rowspan="4">
          <img style="position:relative;top:30px;" :src="printData.cover_img,'?x-oss-process=image/resize,w_125,h_159' | ImgArr" alt="">
        </td>
      </tr>
      <tr style="font-size:12px">
        <td style="padding-top:40px;padding-left:37px">Product ID-Color</td>
        <td colspan="2" style="padding-top:28px">Size</td>
      </tr>
      <tr style="font-size:20px">
        <td style="padding-left:37px">{{printData.brandID}}-{{printData.colorCode}}</td>
        <td colspan="2">{{printData.size}}</td>
      </tr>

      <tr style="font-size:12px">
        <td style="padding-top:40px;padding-left:37px">Boutique ID</td>
      </tr>
      <tr style="font-size:20px">
        <td colspan="3" style="padding-left:37px;padding-bottom:90px">{{printData.ProductCode}}</td>
      </tr>

    </table>
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

