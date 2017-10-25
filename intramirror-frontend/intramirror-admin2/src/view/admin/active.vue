<template>
  <div>
    <div class="select-boutique">
      <div class="input-field">
        <multiselect track-by="vendor_name"
                     label="vendor_name"
                     placeholder="Boutique" :options="allVendor" :show-labels="false"
                     @select="getBoutiqueid($event)" :allow-empty="false" :value="allVendor[0]"></multiselect>
      </div>
      <p class="tit">Effective Date: {{RuleDate}}</p>
      <div class="head-search" v-if="tableBar.length !== 0">
        <i class="mdi mdi-magnify"></i>
        <input type="text" placeholder="Search Brand" @change="searchBrand">
        <span class="mdi mdi-close" @click="searchBrand"></span>
      </div>
    </div>
    <div v-if="tableBar.length !== 0">
      <div class="table-bar">
        <div class="item">
          <div class="lists" v-for="item in tableBar" :class="{hover:item.price_change_rule_id===priceId||priceId===0}"
               @click="setTablebar(item.price_change_rule_id)">{{item.name}}
            <div>
              <p v-for="i in item.season_codes">{{i}}</p>
            </div>
          </div>
        </div>
      </div>
      <div class="main-center">
        <div class="head-name">
          <p>Men</p>
          <p>Women</p>
        </div>
        <v-app class="vapp" style="margin-top:0">
          <v-card class="datalist">
            <v-data-table
              v-bind:headers="headers"
              v-bind:pagination.sync="pagination"
              v-bind:items="items">
              <template slot="items" scope="props">
                <tr v-bind:class="props.item.english_name">
                  <td>
                    {{ props.item.english_name }}
                  </td>
                  <td class="text-xs-center">{{ props.item[headers[1].value] | setTableval}}</td>
                  <td class="text-xs-center">{{ props.item[headers[2].value] | setTableval}}</td>
                  <td class="text-xs-center">{{ props.item[headers[3].value] | setTableval}}</td>
                  <td class="text-xs-center">{{ props.item[headers[4].value] | setTableval}}</td>
                  <td class="text-xs-center">{{ props.item[headers[5].value] | setTableval}}</td>
                  <td class="text-xs-center">{{ props.item[headers[6].value] | setTableval}}</td>
                  <td class="text-xs-center">
                    <div class="text-xs-center">{{ props.item[headers[7].value] | setTableval}}</div>
                  </td>
                  <td class="text-xs-center">{{ props.item[headers[8].value] | setTableval}}</td>
                </tr>
              </template>
              <template slot="pageText" scope="{ pageStart, pageStop }">
                From {{ pageStart }} to {{ pageStop }} of {{items.length}}
              </template>
            </v-data-table>
            <div class="im-foot-page">
              <select-page v-model="pagination.page"
                           :length="Math.ceil(this.items.length / pagination.rowsPerPage)"></select-page>
            </div>
          </v-card>
        </v-app>
      </div>
      <div class="brand-category">
        <p class="tit">Exception - Brand & Category</p>
        <div class="tag" v-for="item in brandList">
          <div>{{item.c1Name}} > {{item.c2Name}} > {{item.cName}}　{{item.discount_percentage | setTableval}} Off</div>
        </div>
      </div>

      <div class="foot-row">
        <div class="product-group">
          <p class="tit">Exception - Prorduct Group</p>
          <div class="tag" v-for="item in productGrouplist">
            <div>{{item.name}}　{{item.discount_percentage | setTableval}} Off</div>
          </div>
        </div>
        <div class="brand-id">
          <p class="tit">Exception - Prorduct</p>
          <div class="tag" v-for="item in BrandIDColorCode">
            <div>{{item.boutique_id}}　{{item.discount_percentage | setTableval}} Off</div>
          </div>
        </div>
      </div>
    </div>
    <loading v-if="isLoading"></loading>
  </div>
</template>

<script>
  import selectPage from '../component/selectPage.vue'
  import loading from '../component/loading.vue'
  import Multiselect from 'vue-multiselect'
  import {
    searchBrandZero,
    queryRuleByProduct,
    queryRuleByHasSeason,
    selectActiveCategorys,
    queryRuleByBrandZero,
    queryRuleByGroup,
    queryRuleByBrandOne,
    productGroup,
    getRuleDate,
    queryRuleVendor
  } from '../../api/pricingrule'

  export default {
    data() {
      return {
        priceId: 0,
        tableBar: [],
        items: [],
        boutiqueData: [],
        BrandIDColorCode: [],
        productGrouplist: [],
        pagination: {},
        isLoading: false,
        brandList: [],
        boutiqueVendorid: null,
        headers: [
          {text: 'Brand', align: 'left', value: 'english_name', sortable: false},
          {text: 'Clothing', value: 'calories', align: 'center', sortable: false},
          {text: 'Shoes', value: 'fat', align: 'center', sortable: false},
          {text: 'Bags', value: 'carbs', align: 'center', sortable: false},
          {text: 'Accessory', value: 'protein', align: 'center', sortable: false},
          {text: 'Clothings', value: 'sodium', align: 'center', sortable: false},
          {text: 'Shoess', value: 'calcium', align: 'center', sortable: false},
          {text: 'Bagss', value: 'iron', align: 'center', sortable: false},
          {text: 'Accessorys', value: 'men', align: 'center', sortable: false}
        ],
        selectProductGroup: [],
        allVendor: [],
        RuleDate: null

      }
    },
    watch: {
      priceId(val){
        this.getTable(val)
        this.getqueryRuleByProduct(val)
        this.getqueryRuleByGroup(val)
        this.getBrandlist(val)
      },
      boutiqueVendorid(val){
        this.getTablenav(val)
      }
    },
    mounted() {
      this.isLoading = true;
      queryRuleVendor(3).then(res => {
        if (res.data.status === 1) {
          this.allVendor = res.data.data;
          this.boutiqueVendorid = this.allVendor[0].vendor_id;
          this.getTablenav(this.boutiqueVendorid)
        }
      });

      selectActiveCategorys().then(res => { //获取Categorys
        this.selectCategorys = res.data.data
        this.isLoading = false;
        //设置table head标题
        let j = 0;
        for (let item in this.selectCategorys) {
          for (let i in this.selectCategorys[item].children) {
            j++
            this.headers[j].text = this.selectCategorys[item].children[i].name
            this.headers[j].value = this.selectCategorys[item].children[i].categoryId
          }
        }
        let head2Val = this.headers[2];
        let head3Val = this.headers[3];
        this.headers[2] = head3Val;
        this.headers[3] = head2Val
      });
    },
    methods: {
      getBoutiqueid(event){
        this.boutiqueVendorid = event.vendor_id;
        this.getTable(this.boutiqueVendorid)
      },
      setTablebar(id){ //设置选择的head tab
        this.priceId = id;
        getRuleDate(id).then(res => {
          if (res.data.status === 1) {
            this.RuleDate = res.data.data.validFromStr
          }
        });
      },
      getTable(id){
        this.isLoading = true;
        queryRuleByBrandZero(id).then(res => { //获取table列表
          if (res.data.status === 1) {
            this.items = res.data.data;
            setTimeout(() => {
              this.isLoading = false;
            }, 300)
          } else {
            this.items = []
          }
        })
      },
      getTablenav(val){
        //console.log(1)
        productGroup(val).then(res => {
          if (res.data.status === 1) {
            this.selectProductGroup = res.data.productGroupList
          }
        })
        queryRuleByHasSeason(2, val, 3).then(res => { //获取head tab
          this.tableBar = res.data.data;
          if (this.tableBar.length === 0) {
            this.showCopy = true;
            this.showShade = true;
            this.priceId = 0;
            return false
          } else {
            this.setTablebar(this.tableBar[0].price_change_rule_id)
          }
        })
      },
      getBrandlist(id){
        queryRuleByBrandOne(id).then(res => {
          this.brandList = res.data.data
        })
      },
      getqueryRuleByGroup(id){
        queryRuleByGroup(id).then(res => {
          this.productGrouplist = res.data.data
        })
      },
      getqueryRuleByProduct(id){
        queryRuleByProduct(id).then(res => {
          this.BrandIDColorCode = res.data.data
        })
      },
      searchBrand(e){
        if (e.type === 'click') {
          searchBrandZero(this.priceId, '').then(res => {
            this.items = res.data.data;
          })
          e.target.parentNode.children[1].value = ''
        } else {
          searchBrandZero(this.priceId, e.target.value).then(res => {
            this.items = res.data.data;
          })
        }
      }
    },
    filters: {
      setTableval(val){
        if (val === undefined) {
          return 0 + '%'
        } else {
          return 100 - parseInt(val) + '%'
        }
      }
    },
    components: {
      Multiselect,
      selectPage,
      loading
    }
  }
</script>
<style lang="less">
  @import "../../assets/css/googlefont.css";
  @import "../../../node_modules/vuetify/dist/vuetify.min.css";
  @import "../../../node_modules/vue-multiselect/dist/vue-multiselect.min.css";

  .multiselect__tags {
    background: none;
    border: none;
    padding: 0;
    input {
      border-bottom: 2px solid #871B55;
      margin: 0;
      &:hover {
        border-bottom: 2px solid #871B55;
      }
    }
  }

  .multiselect--active {
    z-index: 7;
  }

  .vapp {
    min-height: inherit;
    margin-top: 37px;
    input {
      &:hover {
        border-bottom: 1px solid #9e9e9e;
      }
      &:focus:not([readonly]) {
        border-bottom: 1px solid #9e9e9e;
        box-shadow: none;
      }
    }
    .mdi-close {
      width: 20px;
      height: 20px;
      text-align: center;
      font-size: 18px;
      line-height: 18px;
      cursor: pointer;
      &:hover {

        border-radius: 20px;
        background: #871B55;

        color: #fff;
      }
    }
    .btn {
      padding: 0;
    }
  }

  .datalist {
    width: 100%;
  }

  .input-field {
    width: 267px;
    margin: 0;
    input.select-dropdown {
      border-bottom: 2px solid #871B55;
      margin: 0;
    }
    input.validate {
      border-bottom: 2px solid #871B55;
      margin: 0;
      &:hover {
        box-shadow: none;
        border-bottom: 2px solid #871B55;
      }
      &:focus {
        box-shadow: none;
        border-bottom: 2px solid #871B55;
      }
    }
  }

  .foot-btn {
    .input-field {
      input.validate {
        margin-left: 40px;
      }
      .prefix ~ label {
        margin-left: 30px;
      }
    }
  }

</style>
<style lang="less" scoped>
  .head-search {
    position: relative;
    z-index: 6;
    width: 300px;
    height: 48px;
    i {
      width: 48px;
      line-height: 48px;
      text-align: center;
      font-size: 26px;
      color: #777;
      float: left;
    }
    input {
      float: left;
      width: 300-48px;
      line-height: 48px;
      border-bottom: 2px solid #871B55;
      font-size: 20px;
      &:hover {
        border-bottom: 2px solid #871B55;
        box-shadow: none;
      }
      &:focus {
        border-bottom: 2px solid #871B55;
        box-shadow: none;
      }
    }
    span {
      width: 30px;
      height: 30px;
      display: inline-block;
      background: #ddd;
      text-align: center;
      line-height: 26px;
      border-radius: 50%;
      font-size: 18px;
      cursor: pointer;
      position: absolute;
      right: 0;
      color: #777;
      top: 8px;
    }
  }

  .select-boutique {
    display: flex;
    color: rgba(0, 0, 0, 0.38);
    position: relative;
    z-index: 1;
    .tit {
      font-size: 20px;
      font-weight: 500;
      color: rgba(0, 0, 0, 0.87);
      margin: 16px 0 0 20px;
    }
  }

  .table-bar {
    margin-top: 30px;
    .item {
      display: flex;
      .lists {
        width: 264.93px;
        color: #4A4A4A;
        line-height: 37.91px;
        text-align: center;
        background-color: #CBEDF4;
        font-size: 20px;
        font-weight: bold;
        position: relative;
        cursor: pointer;
        &:hover {
          & div {
            display: block;
          }
        }
        &:before {
          content: ' ';
          position: absolute;
          display: inline-block;
          width: 50px;
          height: 50px;
          background: #eee;
          top: -25px;
          left: -25px;
          transform: rotate(45deg);
        }
        &:after {
          content: ' ';
          position: absolute;
          display: inline-block;
          width: 50px;
          height: 50px;
          background: #eee;
          top: -25px;
          right: -25px;
          transform: rotate(45deg);
        }
        span {
          position: absolute;
          right: 34px;
        }
        div {
          position: absolute;
          font-size: 14px;
          width: 65.59px;
          bottom: 6px;
          left: 133px;
          border-radius: 2px;
          background-color: #FFFFFF;
          box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
          font-weight: normal;
          display: none;
          z-index: 2;
        }
      }
      .hover {
        background: #00A6CE;
        color: #fff;
        div {
          color: rgba(0, 0, 0, 0.87);
        }
      }
    }
  }

  .main-center {
    width: 100%;
    background-color: #FAFAFA;
    box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
    .head-name {
      width: 81%;
      border-bottom: 1px solid #4a4a4a;
      line-height: 36px;
      height: 36px;
      display: flex;
      float: right;
      position: relative;
      z-index: 1;
      p {
        flex: 1;
        text-align: center;
        font-size: 16px;
        font-weight: bold;
      }
    }
  }

  .brand-category {
    width: 100%;
    background-color: #FAFAFA;
    box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
    padding: 24px;
    margin: 22px 0;
    .head-input {
      display: flex;
      .input-field {
        width: 267px;
        margin-right: 20px;
        margin-top: -10px;
        &:nth-child(3) {
          width: 40px;
        }
      }
      button {
        height: 36px;
        width: 93px;
        border-radius: 2px;
        background-color: #871B55;
        box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
      }
    }
  }

  .foot-row, .brand-category {
    .tit {
      font-size: 20px;
      font-weight: 500;
      margin-bottom: 20px;
    }
    .tag {
      display: inline-block;
      font-size: 14px;
      background-color: rgba(153, 153, 153, 0.2);
      line-height: 32px;
      padding: 0 16px;
      border-radius: 32px;
      color: rgba(0, 0, 0, 0.54);
      margin: 14px 13px 0 0;
      span {
        position: relative;
        top: 2px;
        margin-left: 10px;
        height: 20px;
        width: 20px;
        border-radius: 20px;
        background-color: #9B9B9B;
        line-height: 19px;
        text-align: center;
        color: #fff;
        font-size: 15px;
        cursor: pointer;
      }
    }
  }

  .foot-row {
    width: 100%;
    display: flex;
    .product-group, .brand-id {
      background-color: #FAFAFA;
      box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
      padding: 24px;
      width: 49.5%;
    }
    .brand-id {
      margin-left: 1%;
    }
  }

  .im-foot-page {
    position: absolute;
    bottom: 10px;
    right: 440px;
  }
</style>
