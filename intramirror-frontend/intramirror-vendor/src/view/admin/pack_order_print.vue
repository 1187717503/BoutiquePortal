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
          <th style="font-size:20px;color: #000000;letter-spacing: 2.2px;text-align: right;padding-right:44px;">
            Carton Label
          </th>
        </tr>
      </table>

      <table width="100%" cellpadding="0" cellspacing="0" border="0"
             style="border-collapse: collapse;border-top: 1px solid #d3d3d3;border-bottom: 1px solid #d3d3d3;">
        <tr style="font-size:12px;">
          <td colspan="2" style="padding-left:24px;padding-top:17px;">Carton Number</td>
        </tr>
        <tr style="font-size:20px">
          <td style="padding-left:24px;padding-top:5px;">{{cartonData.barcode}}</td>
          <td style="text-align: right;padding-right:44px;">{{cartonData.shipToGeography}}</td>
        </tr>
        <tr>
          <td><img
            :src="apiUrl+'image/getImage.htm?message='+cartonData.barcode+'&type=0'"
            style="margin-left:16px;margin-top:8px;" alt=""></td>
          <td style="text-align: right;padding-right:44px;">
            Measure:　{{cartonData.length==='null'?0:cartonData.length}} x {{cartonData.width==='null'?0:cartonData.width}} x {{cartonData.height==='null'?0:cartonData.height}} CM
          </td>
        </tr>
        <tr>
          <td colspan="2" style="text-align: right;font-size: 20px;padding:20px 44px 36px 0">_______ of _________</td>
        </tr>
      </table>
    </div>
    <loading v-if="isLoading"></loading>
  </div>
</template>

<script>
  import loading from '../component/loading.vue'
  import {printBarcode} from '../../api/order'
  export default {
    data(){
      return {
        cartonData: {
          length: this.$route.query.length,
          width: this.$route.query.width,
          height: this.$route.query.height,
          barcode: this.$route.query.barcode,
          shipToGeography: this.$route.query.shipToGeography
        },
        isLoading: true,
        apiUrl:process.env.BASE_URL
      }
    },
    created(){
      this.getCodePic(this.cartonData.barcode);
    },
    methods: {
      getCodePic(code){
        let data = {
          barCode: code
        };
        this.isLoading = true;
        printBarcode(data).then(res => {
          if (res.data.status === 1) {
            this.barCodePic = res.data.data;
            this.isImgLoad(() => {
              this.isLoading = false;
              setTimeout(() => {
                window.print();
                window.close();
              }, 100);
            })
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
