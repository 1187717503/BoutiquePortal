<template>
  <div>
    <div class="default-page">
      <div class="head-input">
        <div class="input-field">
          <input type="text" id="Boutique" v-model="Boutique">
          <label for="Boutique">Boutique Discount Default</label>
        </div>
        <div class="input-field">
          <input type="text" id="IM" v-model="IM">
          <label for="IM">IM Discount Default</label>
        </div>
      </div>
      <div class="foot-btn">
        <button class="waves-effect waves-light btn" @click="DiscountDefault">SAVE</button>
        <button class="waves-effect waves-light btn" @click="cancelDefault">RESET</button>
      </div>
    </div>
  </div>
</template>

<script>
  import {updateDefaultDiscount, selectDefaultDiscount} from '../../api/pricingrule'
  export default {
    data() {
      return {
        Boutique: null,
        IM: null
      }
    },
    mounted(){
      selectDefaultDiscount().then(res => {
        if (res.data.status === 1) {
          this.Boutique = 100 - parseInt(res.data.data.bDiscount) + '%'
          this.IM = 100 - parseInt(res.data.data.iDiscount) + '%'
        }
      })
      console.log(process.env)
    },
    methods: {
      regDiscount(val){
        let reg = new RegExp("^(\\d|[1-9]\\d|100)$");
        if (!reg.test(val)) {
          Materialize.toast('请输入0-100的整数', 4000);
          return false
        } else {
          return true
        }
      },
      DiscountDefault(){
        if (this.Boutique === null) {
          Materialize.toast('请输入Boutique Discount Default', 4000);
          return false
        } else if (this.IM === null) {
          Materialize.toast('请输入IM Discount Default', 4000);
          return false
        }
        if (!this.regDiscount(this.Boutique) || !this.IM) {
          return false
        }
        let data = {
          "boutique_discount_default": this.Boutique.replace("%", ""),
          "im_discount_default": this.IM.replace("%", "")
        };
        updateDefaultDiscount(data).then(res => {
          if (res.data.status === 1) {
            Materialize.toast('保存成功', 4000);
          } else {
            Materialize.toast('保存失败', 4000);
          }
        })
      },
      cancelDefault(){
        selectDefaultDiscount().then(res => {
          if (res.data.status === 1) {
            this.Boutique = 100 - parseInt(res.data.data.bDiscount) + '%'
            this.IM = 100 - parseInt(res.data.data.iDiscount) + '%'
          }
        })
      }
    },
  }
</script>

<style lang="less">
  .default-page {
    width: 100%;
    background-color: #FAFAFA;
    box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
    padding: 24px;
    .head-input {
      display: flex;
      .input-field {
        width: 200px;
        margin-right: 30px;
        input {
          border-bottom: 2px solid #871B55;
          margin: 0;
          box-shadow: none;
          &:hover {
            border-bottom: 2px solid #871B55;
          }
        }
        label {
          left: 0;
          transform: translateY(-140%);
        }
      }
    }
    .foot-btn {
      margin-top: 35px;
      button {
        height: 36px;
        width: 135.98px;
        border-radius: 2px;
        background-color: #871B55;
        box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
        margin-right: 18px;
        &:nth-child(2) {
          background-color: #9B9B9B;
        }
      }
    }
  }
</style>
