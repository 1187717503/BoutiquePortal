<template>
  <div>
    <div class="select-boutique">
      <div class="input-field">
        <multiselect track-by="vendor_name" label="vendor_name" placeholder="Boutique" :options="allVendor" :show-labels="false" @select="getBoutiqueid($event)" :allow-empty="false" :value="allVendor[0]"></multiselect>
      </div>
      <button v-if="tableBar.length === 0" class="waves-effect waves-light btn" @click="CREATENEWRULE">CREATE NEW RULE
      </button>
      <div class="head-search" v-if="tableBar.length !== 0">
        <i class="mdi mdi-magnify"></i>
        <input type="text" placeholder="Brand" @change="searchBrand">
        <span class="mdi mdi-close" @click="searchBrand"></span>
      </div>
    </div>
    <p v-if="tableBar.length === 0" class="noFound">No IM Pricing Rule Found!</p>
    <div v-if="tableBar.length !== 0">
      <div class="table-bar">
        <div class="item">
          <div class="lists" v-for="(i,index) in tableBar" :class="{hover:i.price_change_rule_id===priceId||priceId===0}" @click="setTablebar(i.price_change_rule_id)">
            {{i.name}}
            <div>
              <p v-for="j in i.season_codes">{{j}}</p>
            </div>
          </div>
        </div>
      </div>
      <div class="main-center">
        <div class="head-btn">
          <div class="waves-effect waves-light btn" @click="showAddbrand=true;showShade=true;getaddBran(priceId,'');listBandright=[];addBandright=[]">
            ADD BRAND
          </div>
          <div class="waves-effect waves-light btn" @click="showSeason=true;showShade=true;">COPY TO NEW</div>
          <div class="waves-effect waves-light btn" @click="delChangeRule">DELETE SEASON</div>
        </div>
        <div class="head-name">
          <p v-for="title in headerTitles.value" :key="title.name" :style="{width:(100*title.size)/headerTitles.totalSize + '%'}">
            {{title.name}}
          </p>
        </div>
        <v-app class="vapp">
          <v-card class="datalist">
            <v-data-table v-bind:headers="headers" v-bind:pagination.sync="pagination" v-bind:items="items">
              <template slot="items" scope="props">
                <tr v-bind:class="props.item.english_name">
                  <td style="width: 200px;">
                    {{ props.item.english_name }}
                  </td>
                  <td class="text-xs-center" v-for="(headerItem,index) in headers" :key="headerItem.value" v-if="index!=0">
                    <v-edit-dialog lazy>{{ props.item[headerItem.value] | setTableval}}%
                      <v-text-field slot="input" label="Edit" single-line v-bind:value="props.item[headerItem.value] | setTableval" v-on:change="ediClothing($event,props.index,props.item.brand_id,index)"></v-text-field>
                    </v-edit-dialog>
                  </td>
                  <td>
                    <span class="mdi mdi-close" v-if="props.item.english_name!=='Default'" @click="delBrand(props.item.brand_id)"></span>
                  </td>
                </tr>
              </template>
              <template slot="pageText" scope="{ pageStart, pageStop}">
                From {{ pageStart }} to {{ pageStop }} of {{items.length}}
              </template>
            </v-data-table>

            <div class="im-foot-page">
              <select-page v-model="pagination.page" :length="Math.ceil(this.items.length / pagination.rowsPerPage)"></select-page>
            </div>
          </v-card>
        </v-app>
      </div>
      <div class="brand-category">
        <div class="head-input">
          <div class="input-field">
            <multiselect track-by="english_name" style="z-index:1;" label="english_name" :max-height="400" placeholder="Brand" :options="selectBrands" @select="getBrandid($event)" :show-labels="false"></multiselect>
          </div>
          <div class="input-field select-tow">
            <div class="multiselect">
              <div class="multiselect__select"></div>
              <input type="text" readonly placeholder="Category" class="multiselect__input" @focus="isMultistep = true" :value="categoryName" @blur="hideMultistep">
            </div>
            <div class="multistep" v-if="isMultistep">
              <div v-for="item in selectCategorys">
                {{item.name}}
                <div v-for="i in item.children">－{{i.name}}
                  <div v-for="j in i.children" @click="setCategory($event,j.name,j.categoryId)">－－{{j.name}}</div>
                </div>
              </div>
            </div>
          </div>
          <div class="input-field">
            <input type="text" class="validate" placeholder="10%" v-model="addPercentage">
          </div>
          <button class="waves-effect waves-light btn" @click="addBrandCateort($event)">ADD</button>
        </div>
        <div class="tag" v-for="item in brandList">
          <div>
            {{item.english_name}}　{{item.c1Name}} > {{item.c2Name}} > {{item.cName}}　{{item.discount_percentage | setTableval}}% Off
            <span class="mdi mdi-close" @click="delBrandCateort(item)"></span>
          </div>
        </div>
      </div>
      <div class="foot-row">
        <div class="product-group">
          <div class="head-input">
            <div class="input-field">
              <multiselect track-by="name" style="z-index:2" label="name" placeholder="Product Group" @select="getProductGroup($event)" :options="selectProductGroup" :show-labels="false"></multiselect>
            </div>
            <div class="input-field">
              <input type="text" class="validate" placeholder="10%" v-model="addProductGroupVal">
            </div>
            <button class="waves-effect waves-light btn" @click="addPriceChangeRuleGroup($event)">ADD</button>
          </div>
          <div class="tag" v-for="item in productGrouplist">
            <div>{{item.name}}　{{item.discount_percentage | setTableval }}% Off
              <span class="mdi mdi-close" @click="delPriceChangeRuleGroup(item)"></span>
            </div>
          </div>
        </div>
        <div class="brand-id">
          <div class="head-input">
            <div class="input-field">
              <input type="text" class="validate" placeholder="Boutique Code" v-model="BrandIDColorCodetext">
            </div>
            <div class="input-field">
              <input type="text" class="validate" placeholder="10%" v-model="BrandIDColorCodeNum">
            </div>
            <button class="waves-effect waves-light btn" @click="addBrandIDColorCode">ADD</button>
          </div>
          <div class="tag" v-for="item in BrandIDColorCode">
            <div>{{item.boutique_id}}　{{item.discount_percentage | setTableval}}% Off
              <span class="mdi mdi-close" @click="delBrandIDColorCode(item)"></span>
            </div>
          </div>
        </div>
      </div>
      <div class="foot-btn">
        <div class="input-field">
          <i class="mdi mdi-calendar prefix"></i>
          <input id="seleDate" type="text" class="validate" placeholder="Date" @focus="setEffectiveDate" :value="RuleDate">
          <label for="icon_prefix" class="active">Effective Date</label>
        </div>
        <button class="waves-effect waves-light btn" @click="saveDate">SAVE</button>
        <button class="waves-effect waves-light btn">CANCEL</button>
        <input type="file" @change="getFile($event)" class="file-input" />
        <button class="waves-effect waves-light btn button-no-padding" @click="uploadFile">UPLOAD EXCL</button>
        <button class="waves-effect waves-light btn button-no-padding" @click="downloadFile">DOWNLOAD EXCL</button>
        <button class="waves-effect waves-light btn active-button" @click="activeNow">ACTIVE NOW</button>
      </div>
    </div>

    <div class="layer-copy" v-if="showCopy">
      <p class="tit">Create New Boutique Pricing Rule</p>
      <div class="head-input">
      </div>
      <div class="radio-list">
        <p v-for="(i,index) in pricingRulelist">
          <input class="with-gap" type="radio" :id="'test'+index" name="Pricing" />
          <label :for="'test'+index" @click="selectPricingRule(index)">{{i.name}}
            <b>{{i.text}}</b>
          </label>
        </p>
      </div>
      <div class="foot-btn">
        <p @click="cancelCopy">CANCEL</p>
        <p @click="createPricingRule">CREATE</p>
      </div>
    </div>

    <div class="layer-addbrand" v-if="showAddbrand">
      <div class="search">
        <p class="tit">Add a Brand</p>
        <div class="input-field">
          <i class="mdi mdi-magnify prefix"></i>
          <input id="icon_prefix3" type="text" class="validate" @change="searchBandleft($event)">
          <label for="icon_prefix3" class="">Brand</label>
        </div>
        <div class="switch">
          <label>
            <input type="checkbox">
            <span class="lever"></span>
          </label>
          Search All Brands
        </div>
      </div>
      <div class="list-cent">
        <div class="line">
          <div class="tit">
            <div>
              <label for="filled-in-box">Brand</label>
            </div>
          </div>

          <div class="rows" v-for="(item,index) in addBandleft">
            <div>
              <input type="checkbox" class="filled-in" :id="'filled-in-box'+index">
              <label :for="'filled-in-box'+index" @click.stop="addBandrightarr($event,item)">{{item.english_name}}</label>
            </div>
          </div>
        </div>
        <div class="line">
          <div class="mdi mdi-chevron-double-right" @click.stop.prevent="addBand($event)"></div>
          <div class="mdi mdi-chevron-double-left" @click="delBand($event)"></div>
        </div>
        <div class="line">
          <div class="tit">
            <div>
              <input type="checkbox" class="filled-in" id="filled-in-box">
              <label for="filled-in-box" @click="selectAllBandright($event)">Brand</label>
            </div>
          </div>
          <div class="rows" v-for="item in listBandright">
            <div>
              <input type="checkbox" class="filled-in" :id="item.brand_id">
              <label :for="item.brand_id" @click="delBandrightarr($event,item)">{{item.english_name}}</label>
            </div>
          </div>
        </div>
      </div>
      <div class="foot-btn">
        <span @click="showAddbrand=false;showShade=false;">CANCEL</span>
        <span @click="addBrand">ADD</span>
      </div>
    </div>

    <div class="layer-season" v-if="showSeason">
      <p class="tit">Select Season</p>
      <div class="list-cent">
        <div v-for="(item,index) in copyData">
          <input type="checkbox" class="filled-in" :id="'chech-'+index">
          <label :for="'chech-'+index" data-check="false" @click="SelectSeason($event,item)">{{item.season_code}}</label>
        </div>
      </div>
      <div class="foot-btn">
        <span @click="showSeason=false;showShade=false;">CANCEL</span>
        <span @click="createSeason(coptyNewType)">CREATE</span>
      </div>
    </div>

    <shade v-if="showShade"></shade>
    <loading v-if="isLoading"></loading>
    <div id="card-alert" class="card red im-alert" v-show="imAlert.show">
      <div class="card-content white-text">
        <p>{{imAlert.text}}</p>
      </div>
      <button type="button" class="close white-text">
        <span @click="imAlert.show=false">×</span>
      </button>
    </div>
  </div>
</template>

<script>
import shade from "../component/shade.vue";
import loading from "../component/loading.vue";
import selectPage from "../component/selectPage.vue";
import Multiselect from "vue-multiselect";
import {
  queryRuleByBrandZero,
  queryRuleByNotHasSesaon,
  queryRuleByGroup,
  queryRuleByProduct,
  queryRuleByHasSeason,
  productGroup,
  selectActiveBrands,
  selectActiveCategorys,
  searchBrandZero,
  queryNotRuleByBrand,
  queryRuleByBrandOne,
  queryAllVendor,
  initPriceChangeRule,
  updatePriceChangeRuleCategoryBrand,
  deletePriceChangeRuleCategoryBrandBatch,
  createPriceChangeRuleCategoryBrandBatch,
  createPriceChangeRuleCategoryBrand,
  deletePriceChangeRuleCategoryBrand,
  createPriceChangeRuleProduct,
  deletePriceChangeRuleGroup,
  activeVendor,
  pengingVendor,
  deletePriceChangeRuleProduct,
  createPriceChangeRuleProductGroup,
  copyseasonVendor,
  deletePriceChangeRule,
  updatePriceChangeRule,
  getRuleDate,
  copyRule,
  downFileApi,
  uploadFileApi,
  boutiqueActiveRefresh
} from "../../api/pricingrule";

export default {
  data() {
    return {
      update_files: "",
      showShade: false, //是否显示遮罩
      showSeason: false,
      showAddbrand: false,
      showCopy: false,
      isLoading: false,
      pagination: {},
      isMultistep: false,
      boutiqueVendorid: null,
      tableBar: [],
      priceId: 0,
      categoryName: "",
      selectBrands: [],
      selectCategorys: [],
      copyData: [],
      productGrouplist: [],
      BrandIDColorCode: [],
      productGroupselect: [],
      addBandleft: [],
      brandList: [],
      allVendor: [],
      allvendorId: null,
      pricingRule: null,
      imAlert: {
        show: false,
        text: ""
      },
      arrSelectSeason: [],

      pricingRulelist: [
        // { name: "Copy Active", text: "Boutique Pricing Rule" },
        { name: "Create From Blank Pricing Rule", text: "" }
      ],
      headers: [],
      items: [],
      addBandright: [],
      listBandright: [],
      dellistBandright: [],
      addBrandid: null,
      addCategoryid: null,
      addPercentage: null,
      discount: null,
      BrandIDColorCodetext: null,
      BrandIDColorCodeNum: null,
      selectProductGroup: [],
      addProductGroupid: null,
      addProductGroupVal: null,
      coptyNewType: "COPY",
      RuleDate: ""
    };
  },
  watch: {
    priceId(val) {
      this.getqueryRuleByGroup(val);
      this.getqueryRuleByProduct(val);
      this.getaddBran(val, "");
      this.getBrandlist(val);
    },
    boutiqueVendorid(val) {
      this.getTablenav(val);
    },
    showShade(val) {
      if (val) {
        $("html").css("overflow", "hidden");
      } else {
        $("html").css("overflow", "");
      }
    }
  },
  mounted() {
    this.isLoading = true;
    queryAllVendor().then(res => {
      if (res.data.status === 1) {
        this.allVendor = res.data.data;
        this.boutiqueVendorid = this.allVendor[0].vendor_id;
        this.getTablenav(this.boutiqueVendorid);
      }
    });
    selectActiveBrands(2).then(res => {
      //获取Brands
      this.selectBrands = res.data.data;
    });
    selectActiveCategorys().then(res => {
      //获取Categorys
      const tempData = _.filter(res.data.data, item => {
        if (item.categoryId == 1499 || item.categoryId == 1568) {
          return false;
        }
        return true;
      });
      this.selectCategorys = tempData;
      this.isLoading = false;
      const list = [
        {
          text: "",
          value: "",
          align: "center",
          sortable: false
        }
      ];
      const headerTitles = [];
      for (let item in this.selectCategorys) {
        //          this.selectCategorys[item].children.sort(function (a, b) {
        //            return b.name < a.name
        //          });
        for (let i in this.selectCategorys[item].children) {
          list.push({
            text: this.selectCategorys[item].children[i].name,
            value: this.selectCategorys[item].children[i].categoryId,
            align: "center",
            sortable: false
          });
        }
        headerTitles.push({
          name: this.selectCategorys[item].name,
          size: this.selectCategorys[item].children.length
        });
      }
      this.headers = list;
      let headerSize = 0;
      headerTitles.forEach(item => {
        headerSize = headerSize + item.size;
      });
      this.headerTitles = {
        totalSize: headerSize,
        value: headerTitles
      };
    });
  },
  methods: {
    setEffectiveDate() {
      let date = new Date();
      this.year = date.getFullYear();
      this.month = date.getMonth();
      this.date = date.getDate();
      $("#seleDate").pickadate({
        monthsShort: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12],
        selectMonths: true,
        selectYears: 15,
        min: new Date(this.year, this.month, this.date + 1),
        format: "dd/mmm/yyyy"
      });
    },
    saveDate() {
      let dateVal = $("#seleDate").val();
      if (dateVal === "") {
        Materialize.toast("请选择时间", 4000);
        return false;
      }
      let data = {
        price_change_rule_id: this.priceId,
        valid_from: dateVal,
        price_type: 1,
        vendorId: this.boutiqueVendorid
      };
      updatePriceChangeRule(data, 2).then(res => {
        if (res.data.status === 1) {
          Materialize.toast("保存成功", 4000);
        } else {
          Materialize.toast(res.data.info, 4000);
        }
      });
    },
    delChangeRule() {
      if (!confirm("确定删除吗？")) {
        return false;
      }
      let data = {
        price_change_rule_id: this.priceId
      };
      deletePriceChangeRule(data).then(res => {
        if (res.data.status === 1) {
          Materialize.toast("删除成功", 4000);
          this.getTablenav(this.boutiqueVendorid);
        } else {
          Materialize.toast(res.data.info, 4000);
        }
      });
    },
    getProductGroup(item) {
      this.addProductGroupid = item.productGroupId;
    },
    addPriceChangeRuleGroup(event) {
      let inputDom = event.target.parentNode.getElementsByTagName("input");
      if (this.addProductGroupid === null) {
        Materialize.toast("请选择Product Group", 4000);
        return false;
      } else if (this.addProductGroupVal === null) {
        Materialize.toast("请输入折扣", 4000);
        return false;
      }
      if (!this.retDiscount(this.addProductGroupVal)) {
        return false;
      }
      this.isLoading = true;
      let data = {
        price_change_rule_product_group: {
          price_change_rule_id: this.priceId,
          product_group_id: this.addProductGroupid,
          discount_percentage: this.addProductGroupVal
        }
      };
      createPriceChangeRuleProductGroup(data).then(res => {
        if (res.data.status === 1) {
          Materialize.toast("添加成功", 4000);
          this.getqueryRuleByGroup(this.priceId);
          this.addProductGroupVal = null;
          for (let i = 0; i < inputDom.length; i++) {
            inputDom[i].value = null;
          }
        } else {
          Materialize.toast(res.data.info, 4000);
        }
        this.isLoading = false;
      });
    },
    addBrandIDColorCode() {
      if (this.BrandIDColorCodetext === null) {
        Materialize.toast("请输入Brand ID & Color Code", 4000);
        return false;
      } else if (this.BrandIDColorCodeNum === null) {
        Materialize.toast("请输入折扣", 4000);
        return false;
      }
      if (!this.retDiscount(this.BrandIDColorCodeNum)) {
        return false;
      }
      this.isLoading = true;
      let data = {
        product_code: this.BrandIDColorCodetext,
        price_change_rule_id: this.priceId,
        discount_percentage: parseInt(this.BrandIDColorCodeNum)
      };
      createPriceChangeRuleProduct(data).then(res => {
        if (res.data.status === 1) {
          Materialize.toast("添加成功", 4000);
          this.getqueryRuleByProduct(this.priceId);

          this.BrandIDColorCodetext = null;
          this.BrandIDColorCodeNum = null;
        } else {
          Materialize.toast(res.data.info, 4000);
        }
        this.isLoading = false;
      });
    },
    delBrandIDColorCode(item) {
      this.isLoading = true;
      let data = {
        price_change_rule_product_id: item.price_change_rule_product_id
      };
      deletePriceChangeRuleProduct(data).then(res => {
        if (res.data.status === 1) {
          Materialize.toast("删除成功", 4000);
          this.getqueryRuleByProduct(this.priceId);
        } else {
          Materialize.toast(res.data.info, 4000);
        }
        this.isLoading = false;
      });
    },
    getSesaon(val) {
      queryRuleByNotHasSesaon(val, 1, 2).then(res => {
        //COPY TO NEW data
        this.copyData = res.data.data;
      });
    },
    getTablenav(val) {
      this.getSesaon(val);

      productGroup(val).then(res => {
        if (res.data.status === 1) {
          this.selectProductGroup = res.data.productGroupList;
        }
      });
      queryRuleByHasSeason(1, val, 1, 2).then(res => {
        //获取head tab
        this.tableBar = res.data.data;
        if (this.tableBar.length !== 0) {
          let contain = false;
          this.tableBar.forEach(tabBarItem => {
            if (tabBarItem.price_change_rule_id == this.priceId) {
              contain = true;
            }
          });

          if (contain === true) {
            this.setTablebar(this.priceId);
          } else {
            this.setTablebar(this.tableBar[0].price_change_rule_id);
          }
        }
      });
    },
    CREATENEWRULE() {
      this.showCopy = true;
      this.showShade = true;
    },
    setCategory(event, val, id) {
      //设置三级选择框
      this.categoryName = val;
      this.addCategoryid = id;
      this.isMultistep = false;
    },
    hideMultistep() {
      //关闭三级选择框
      setTimeout(() => {
        this.isMultistep = false;
      }, 300);
    },
    setTablebar(id) {
      //设置选择的head tab
      this.getTable(id);
      this.priceId = id;
      getRuleDate(id).then(res => {
        if (res.data.status === 1) {
          this.RuleDate = res.data.data.validFromStr;
          this.pagination.page = 1;
        }
      });
    },
    getTable(id) {
      this.isLoading = true;
      queryRuleByBrandZero(id).then(res => {
        //获取table列表
        if (res.data.status === 1) {
          this.items = res.data.data;
          setTimeout(() => {
            this.isLoading = false;
          }, 300);
        } else {
          this.items = [];
        }
      });
    },
    getqueryRuleByGroup(id) {
      queryRuleByGroup(id).then(res => {
        this.productGrouplist = res.data.data;
      });
    },
    getqueryRuleByProduct(id) {
      queryRuleByProduct(id).then(res => {
        this.BrandIDColorCode = res.data.data;
      });
    },
    searchBrand(e) {
      if (e.type === "click") {
        searchBrandZero(this.priceId, "").then(res => {
          this.items = res.data.data;
        });
        e.target.parentNode.children[1].value = "";
      } else {
        searchBrandZero(this.priceId, e.target.value).then(res => {
          this.items = res.data.data;
        });
      }
    },
    getaddBran(id, val) {
      queryNotRuleByBrand(id, val).then(res => {
        this.addBandleft = res.data.data;
      });
    },
    getBrandlist(id) {
      queryRuleByBrandOne(id).then(res => {
        this.brandList = res.data.data;
      });
    },
    searchBandleft(event) {
      let lDom =
        event.target.parentNode.parentNode.parentNode.children[2].children[0]
          .children;
      for (let i = 0; i < lDom.length; i++) {
        if (lDom[i].className === "rows") {
          lDom[i].children[0].children[0].checked = false;
        }
      }
      this.getaddBran(this.priceId, event.target.value);
    },
    getBoutiqueid(event) {
      this.boutiqueVendorid = event.vendor_id;
      this.getTable(this.boutiqueVendorid);
    },
    retDiscount(val) {
      let reg = new RegExp("^(\\d|[1-9]\\d|100)$");
      if (!reg.test(val)) {
        Materialize.toast("请输入0-100的整数", 4000);
        return false;
      } else {
        return true;
      }
    },
    //      编辑
    ediClothing(event, index, brandid, categoryid) {
      if (!this.retDiscount(event)) {
        return false;
      }
      let data = {
        price_change_rule_category_brand: {
          price_change_rule_id: this.priceId,
          brand_id: brandid,
          category_id: this.headers[categoryid].value,
          level: 2,
          discount_percentage: parseInt(event)
        }
      };
      updatePriceChangeRuleCategoryBrand(data).then(res => {
        if (res.data.status === 1) {
          Materialize.toast("修改成功", 4000);
          this.getTable(this.priceId);
        } else {
          Materialize.toast(res.data.info, 4000);
        }
      });
    },
    delBrand(id) {
      this.isLoading = true;
      let data = {
        price_change_rule_id: this.priceId,
        price_change_rule_category_brand_list: [id]
      };
      deletePriceChangeRuleCategoryBrandBatch(data).then(res => {
        if (res.data.status === 1) {
          Materialize.toast("删除成功", 4000);
          this.getTable(this.priceId);
        } else {
          Materialize.toast(res.data.info, 4000);
        }
        this.isLoading = false;
      });
    },
    //      创建
    addBandrightarr(event, item) {
      let isCheck = event.target.parentNode.childNodes[0].checked;
      if (!isCheck) {
        this.addBandright.push(item);
        return false;
      } else {
        for (let i = 0; i < this.addBandright.length; i++) {
          if (this.addBandright[i].brand_id === item.brand_id) {
            this.addBandright.splice(i, 1);
          }
        }
      }
    },
    selectAllBandright(event) {
      let isCheck = event.target.parentNode.childNodes[0].checked;
      if (!isCheck) {
        let checkDom = event.target.parentNode.parentNode.parentNode.children;

        for (let i = 0; i < checkDom.length; i++) {
          if (checkDom[i].className === "rows") {
            checkDom[i].children[0].children[0].checked = true;
          }
        }
      } else {
        let checkDom = event.target.parentNode.parentNode.parentNode.children;
        for (let i = 0; i < checkDom.length; i++) {
          if (checkDom[i].className === "rows") {
            checkDom[i].children[0].children[0].checked = false;
          }
        }
      }
    },
    delBandrightarr(event, item) {
      let isCheck = event.target.parentNode.childNodes[0].checked;
      if (!isCheck) {
        this.dellistBandright.push(item);
      } else {
        for (let i = 0; i < this.dellistBandright.length; i++) {
          if (this.dellistBandright[i].brand_id === item.brand_id) {
            this.dellistBandright.splice(i, 1);
          }
        }
      }
    },
    addBand(event) {
      let lDom = event.target.parentNode.parentNode.children[0].children;
      for (let i = 0; i < lDom.length; i++) {
        if (lDom[i].className === "rows") {
          lDom[i].children[0].children[0].checked = false;
        }
      }
      //        大坑写法 this.listBandright = this.addBandright
      //        正确写法
      this.listBandright = Object.assign(
        {},
        this.listBandright,
        this.addBandright
      );
      //        去重
      let ret = [];
      for (let i in this.listBandright) {
        if (ret.indexOf(this.listBandright[i]) === -1) {
          ret.push(this.listBandright[i]);
        }
      }
      this.listBandright = ret;
    },
    delBand(event) {
      let checkDom = event.target.parentNode.parentNode.children[2].children;
      if (
        event.target.parentNode.parentNode.children[2].children[0].children[0]
          .children[0].checked
      ) {
        this.listBandright = [];
      } else {
        for (let i = 0; i < checkDom.length; i++) {
          if (checkDom[i].children[0].children[0].checked) {
            for (let k in this.dellistBandright) {
              for (let j in this.listBandright) {
                if (
                  this.dellistBandright[k].brand_id ===
                  this.listBandright[j].brand_id
                ) {
                  this.listBandright.splice(j, 1);
                }
              }
            }
          }
          checkDom[i].children[0].children[0].checked = false;
        }
      }
      event.target.parentNode.parentNode.children[2].children[0].children[0].children[0].checked = false;
      this.addBandright = [];
      this.dellistBandright = [];
    },
    imAlertfun(prompt) {
      this.imAlert.text = prompt;
      this.imAlert.show = true;

      setTimeout(() => {
        this.imAlert.show = false;
      }, 4000);
    },
    addBrand() {
      if (this.listBandright.length === 0) {
        if (!this.imAlert.show) {
          this.imAlertfun("Please add brand name");
        }

        return false;
      }
      let id = [];
      for (let i in this.listBandright) {
        id.push(this.listBandright[i].brand_id);
      }
      let data = {
        price_change_rule_id: this.priceId,
        price_change_rule_category_brand_list: id
      };
      this.showAddbrand = false;
      this.showShade = false;
      this.isLoading = true;
      createPriceChangeRuleCategoryBrandBatch(data).then(res => {
        if (res.data.status === 1) {
          this.isLoading = false;
          this.getTable(this.priceId);
        }
      });
    },
    getBrandid(item) {
      this.addBrandid = item.brand_id;
    },
    addBrandCateort(event) {
      if (this.addBrandid === null) {
        Materialize.toast("请选择Brand", 4000);
        return false;
      } else if (this.addCategoryid === null) {
        Materialize.toast("请选择Category", 4000);
        return false;
      } else if (this.addPercentage === null) {
        Materialize.toast("请输入折扣", 4000);
        return false;
      }
      if (!this.retDiscount(this.addPercentage)) {
        return false;
      }
      this.isLoading = true;
      let data = {
        price_change_rule_category_brand: {
          price_change_rule_id: this.priceId,
          brand_id: this.addBrandid,
          category_id: this.addCategoryid,
          level: 3,
          discount_percentage: parseInt(this.addPercentage)
        }
      };
      createPriceChangeRuleCategoryBrand(data).then(res => {
        this.isLoading = false;
        if (res.data.status === 1) {
          Materialize.toast("添加成功", 4000);
          this.addBrandid = null;
          this.addCategoryid = null;
          this.addPercentage = null;
          this.getBrandlist(this.priceId);
          this.categoryName = null;
          let inputDom = event.target.parentNode.getElementsByTagName("input");
          for (let i = 0; i < inputDom.length; i++) {
            inputDom[i].value = null;
          }
        } else {
          Materialize.toast(res.data.info, 4000);
        }
      });
    },
    delBrandCateort(item) {
      this.isLoading = true;
      let data = {
        price_change_rule_category_brand_id:
          item.price_change_rule_category_brand_id
      };
      deletePriceChangeRuleCategoryBrand(data).then(res => {
        if (res.data.status === 1) {
          Materialize.toast("删除成功", 4000);
          this.getBrandlist(this.priceId);
        } else {
          Materialize.toast(res.data.info, 4000);
        }
        this.isLoading = false;
      });
    },
    delPriceChangeRuleGroup(item) {
      this.isLoading = true;
      let data = {
        price_change_rule_group_id: item.price_change_rule_group_id
      };
      deletePriceChangeRuleGroup(data).then(res => {
        if (res.data.status === 1) {
          Materialize.toast("删除成功", 4000);
          this.getqueryRuleByGroup(this.priceId);
        } else {
          Materialize.toast(res.data.info, 4000);
        }
        this.isLoading = false;
      });
    },
    selectVendor(e) {
      this.allvendorId = e.vendor_id;
    },
    selectPricingRule(index) {
      // switch (index) {
      //   case 0:
      //     this.pricingRule = index;
      //     break;
      //   case 1:
      this.pricingRule = 1;
      //     break;
      // }
    },
    cancelCopy() {
      //        if (this.tableBar.length === 0) {
      //          this.showCopy = true;
      //          this.showShade = true;
      //        }
      this.showCopy = false;
      this.showShade = false;
    },
    createPricingRule() {
      if (this.pricingRule === null) {
        if (!this.imAlert.show) {
          this.imAlertfun("Please select the Pricing Rule");
        }
        return false;
      }
      if (this.pricingRule === 0) {
        copyRule(this.boutiqueVendorid, 1).then(res => {
          if (res.data.status === 1) {
            this.getTablenav(this.boutiqueVendorid);
            this.showCopy = false;
            this.showShade = false;
          } else {
            this.showCopy = true;
            this.showShade = true;
            if (!this.imAlert.show) {
              this.imAlertfun(res.data.infoMap.info);
            }
          }
          this.pricingRule = null;
        });
      }
      if (this.pricingRule === 1) {
        this.showCopy = false;
        this.showSeason = true;
        this.coptyNewType = "CREATE";
        this.getSesaon(0);
        this.pricingRule = null;
      } else {
        this.showCopy = false;
        this.showShade = false;
      }
    },
    SelectSeason(event, item) {
      let isCheck = event.target.parentNode.childNodes[0].checked;
      if (!isCheck) {
        this.arrSelectSeason.push(item.season_code);
      } else {
        for (let i = 0; i < this.arrSelectSeason.length; i++) {
          if (this.arrSelectSeason[i] === item.season_code) {
            this.arrSelectSeason.splice(i, 1);
          }
        }
      }
    },
    createSeason(type) {
      if (type === "CREATE") {
        let data = {
          name: this.arrSelectSeason.toString(),
          price_type: 1,
          status: 1,
          vendorId: this.boutiqueVendorid,
          price_change_rule_season_group_List: this.arrSelectSeason
        };
        this.showSeason = false;
        this.showShade = false;
        this.isLoading = true;
        initPriceChangeRule(data, 2).then(res => {
          if (res.data.status === 1) {
            this.getTablenav(this.boutiqueVendorid);
          } else {
            if (!this.imAlert.show) {
              this.imAlertfun(res.data.info);
            }
          }
          this.coptyNewType = "COPY";
          this.arrSelectSeason = [];
        });
      } else if (type === "COPY") {
        this.showSeason = false;
        this.showShade = false;
        this.isLoading = true;
        copyseasonVendor(
          this.priceId,
          this.arrSelectSeason.join(","),
          this.boutiqueVendorid,
          1
        ).then(res => {
          if (res.data.status === 1) {
            this.getTablenav(this.boutiqueVendorid);
          } else {
            if (!this.imAlert.show) {
              this.imAlertfun(res.data.infoMap.info);
            }
          }
          this.arrSelectSeason = [];
        });
      }
    },
    uploadFile() {
      if (this.update_files) {
        let formData = new FormData();
        formData.append("file", this.update_files);
        uploadFileApi(this.priceId, formData)
          .then(() => {
            Materialize.toast("文件上传成功！", 4000);
            this.getTablenav(this.boutiqueVendorid);
            this.getTable(this.boutiqueVendorid);
          })
          .catch(error => {
            Materialize.toast(error.detail, 4000);
          });
      } else {
        Materialize.toast("请选择文件上传！", 4000);
      }
    },
    downloadFile() {
      downFileApi(this.priceId);
    },
    getFile(event) {
      this.update_files = event.target.files[0];
    },
    activeNow() {
      this.isLoading = true;
      boutiqueActiveRefresh(this.priceId).then(result => {
        this.getTablenav(this.boutiqueVendorid);
        this.getTable(this.boutiqueVendorid);
        this.isLoading = false;
      });
    }
  },
  filters: {
    setTableval(val) {
      if (val === undefined) {
        return 0;
      } else {
        return 100 - parseInt(val);
      }
    }
  },
  components: {
    shade: shade,
    loading: loading,
    Multiselect,
    selectPage: selectPage
  }
};
</script>
<style lang="less" scoped>
@import "../../../node_modules/vue-multiselect/dist/vue-multiselect.min.css";

.head-search {
  position: relative;
  z-index: 6;
  width: 300px;
  height: 48px;
  margin-left: 60px;
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
    border-bottom: 2px solid #871b55;
    font-size: 20px;
    &:hover {
      border-bottom: 2px solid #871b55;
      box-shadow: none;
    }
    &:focus {
      border-bottom: 2px solid #871b55;
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

.noFound {
  color: #4a4a4a;
  font-size: 16px;
  margin-top: 20px;
}

.select-boutique {
  display: flex;
  color: rgba(0, 0, 0, 0.38);
  button {
    height: 36px;
    margin-left: 16px;
    background: #871b55;
  }
}

.table-bar {
  margin-top: 30px;
  .item {
    display: flex;
    .lists {
      width: 264.93px;
      color: #4a4a4a;
      line-height: 37.91px;
      text-align: center;
      background-color: #cbedf4;
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
        content: " ";
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
        content: " ";
        position: absolute;
        display: inline-block;
        width: 50px;
        height: 50px;
        background: #eee;
        top: -25px;
        right: -25px;
        transform: rotate(45deg);
      }
      div {
        position: absolute;
        font-size: 14px;
        padding: 0 12px;
        bottom: 6px;
        left: 133px;
        border-radius: 2px;
        background-color: #ffffff;
        box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12),
          0 2px 2px 0 rgba(0, 0, 0, 0.24);
        font-weight: normal;
        display: none;
        z-index: 2;
      }
    }
    .hover {
      background: #00a6ce;
      color: #fff;
      div {
        color: rgba(0, 0, 0, 0.87);
      }
    }
  }
}

.main-center {
  width: 100%;
  background-color: #fafafa;
  box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
  .application {
    background: white;
    margin-top: 0 !important;
  }
  .head-btn {
    padding: 14px 0 0 23px;
    .btn {
      width: 149.87px;
      height: 36px;
      border-radius: 2px;
      background-color: #871b55;
      box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
      padding: 0;
      &:nth-child(3) {
        float: right;
        margin-right: 23px;
        background-color: #9b9b9b;
      }
    }
  }
  .head-name {
    border-bottom: 1px solid #4a4a4a;
    line-height: 36px;
    height: 36px;
    display: block;
    overflow: hidden;
    z-index: 1;
    padding-left: 200px;
    padding-right: 100px;
    p {
      text-align: center;
      font-size: 16px;
      line-height: 36px;
      height: 36px;
      font-weight: bold;
      float: left;
      width: 25%;
    }
  }
  .table__overflow {
    overflow-x: visible;
    display: inline-block;
  }
  .datalist {
    margin-top: 0;
  }
}

.brand-category {
  width: 100%;
  background-color: #fafafa;
  box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
  padding: 24px;
  margin: 22px 0;
  position: relative;
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
      background-color: #871b55;
      box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
    }
  }
}

.foot-row,
.brand-category {
  .tag {
    display: inline-block;
    font-size: 14px;
    background-color: rgba(153, 153, 153, 0.2);
    line-height: 32px;
    padding: 0 8px 0 16px;
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
      background-color: #9b9b9b;
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
  position: relative;
  .product-group,
  .brand-id {
    background-color: #fafafa;
    box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
    padding: 24px;
    width: 49.5%;
    .head-input {
      display: flex;
      .input-field {
        width: 267px;
        margin-right: 20px;
        margin-top: -10px;
        &:nth-child(2) {
          width: 40px;
        }
      }
      button {
        height: 36px;
        width: 93px;
        border-radius: 2px;
        background-color: #871b55;
        box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12),
          0 2px 2px 0 rgba(0, 0, 0, 0.24);
      }
    }
  }
  .brand-id {
    margin-left: 1%;
  }
}

.foot-btn {
  margin: 30px 0 20px 0;
  display: flex;
  button {
    height: 36px;
    width: 135.98px;
    border-radius: 2px;
    background-color: #871b55;
    box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
    &:nth-child(3) {
      background: #9b9b9b;
    }
  }
}

.layer-copy {
  position: fixed;
  top: 50%;
  left: 50%;
  margin: -250px/2 0 0 -598.77px /2;
  height: 200px;
  width: 598.77px;
  border-radius: 2px;
  background-color: #ffffff;
  box-shadow: 0 0 24px 0 rgba(0, 0, 0, 0.22), 0 24px 24px 0 rgba(0, 0, 0, 0.3);
  z-index: 7;
  padding: 24px;
  .tit {
    font-size: 20px;
    font-weight: 500;
    margin-bottom: 20px;
  }
  .head-input {
    position: absolute;
    border-left: 1px solid #979797;
    right: 30px;
    margin-top: 18px;
    /*input-field {*/
    /*width: 267px;*/
    /*}*/
    .adjust {
      padding-left: 30px;
      font-size: 16px;
      position: relative;
      &:before {
        content: "";
        display: inline-block;
        width: 9px;
        height: 1px;
        background: #979797;
        position: absolute;
        left: 0;
        top: 26px;
      }
      span {
        position: relative;
        top: 3px;
      }
      input {
        width: 50px;
        border-bottom: 2px solid #871b55;
        margin-left: 2px;
        &:focus {
          box-shadow: none;
        }
      }
    }
  }
  .radio-list {
    p {
      line-height: 45px;
      padding-bottom: 8px;
      label {
        color: #000;
        &:before {
          border-color: #871b55;
        }
        &:after {
          border-color: #871b55;
          background: #871b55;
        }
      }
    }
  }
  .foot-btn {
    display: flex;
    font-size: 14px;
    font-weight: 500;
    color: #871b55;
    float: right;
    cursor: pointer;
    p {
      margin-left: 26px;
    }
  }
}

.layer-addbrand {
  position: fixed;
  top: 50%;
  left: 50%;
  margin: -650px / 2 0 0 -948.35px /2;
  height: 650px;
  width: 948.35px;
  border-radius: 2px;
  background-color: #ffffff;
  box-shadow: 0 0 24px 0 rgba(0, 0, 0, 0.22), 0 24px 24px 0 rgba(0, 0, 0, 0.3);
  z-index: 7;
  padding: 24px;
  .tit {
    font-size: 20px;
    margin-right: 16px;
    font-weight: 500;
    margin-top: 8px;
  }
  .search {
    display: flex;
    .input-field {
      margin-top: -10px;
      input.validate {
        margin-left: 36px;
      }
      .prefix ~ label {
        margin-left: 28px;
      }
    }
    .switch {
      font-size: 16px;
      margin-top: 2px;
      input[type="checkbox"]:checked + .lever:after {
        background: #871b55;
      }
    }
  }
  .list-cent {
    margin-top: 20px;
    display: flex;
    position: relative;
    .line {
      height: 475px;
      overflow-y: auto;
      margin-top: 30px;
      &::-webkit-scrollbar {
        width: 4px;
        height: 4px;
      }
      &::-webkit-scrollbar {
        width: 4px;
        height: 4px;
      }
      &::-webkit-scrollbar-thumb {
        background-color: #c1c1c1;
        border-radius: 5px;
      }
      &::-webkit-scrollbar-thumb {
        border-radius: 9px;
      }
      &:hover::-webkit-scrollbar-thumb {
        background-color: #959595;
      }
      &::-webkit-scrollbar-thumb:hover {
        background-color: #959595;
      }
      &::-webkit-scrollbar-thumb:active {
        background-color: #757575;
      }
      &:nth-child(1),
      &:nth-child(3) {
        width: 386px;
        border-right: 0.5px solid rgba(0, 0, 0, 0.16);
      }
      &:nth-child(2) {
        width: 124px;
        div {
          height: 36px;
          width: 40px;
          border-radius: 2px;
          background-color: #e4e4e4;
          box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12),
            0 2px 2px 0 rgba(0, 0, 0, 0.24);
          text-align: center;
          line-height: 36px;
          display: block;
          font-size: 22px;
          margin-left: 42px;
          margin-top: 44px;
          cursor: pointer;
          &:nth-child(1) {
            margin-top: 150px;
          }
        }
      }
      .tit {
        border-bottom: 1px solid #4a4a4a;
        font-size: 16px;
        width: 386px;
        position: fixed;
        margin-top: -46px;
        label {
          color: #4a4a4a;
          font-weight: bold;
        }
      }
      .tit,
      .rows {
        display: flex;
        line-height: 45px;
        .filled-in:checked + label:after {
          background: #871b55;
          border-color: #871b55;
        }
        label {
          margin-left: 10px;
          padding-left: 60px;
          line-height: 18px;
          width: inherit;
          height: 18px;
          &:after {
            width: 18px;
            height: 18px;
          }
          &:before {
            height: 11px;
          }
        }
      }
      .rows {
        border-bottom: 1px solid #e0e0e0;
      }
    }
  }
  .foot-btn {
    position: absolute;
    bottom: 0;
    right: 24px;
    span {
      margin-left: 36px;
      font-size: 14px;
      font-weight: 500;
      cursor: pointer;
    }
  }
}

.layer-season {
  position: fixed;
  top: 50%;
  left: 50%;
  margin: -538px/2 0 0 -243.13px/2;
  height: 538px;
  width: 243.13px;
  border-radius: 2px;
  background-color: #ffffff;
  box-shadow: 0 0 24px 0 rgba(0, 0, 0, 0.22), 0 24px 24px 0 rgba(0, 0, 0, 0.3);
  z-index: 7;
  padding: 24px;
  .tit {
    font-size: 20px;
    font-weight: 500;
  }
  .list-cent {
    height: 420px;
    overflow: auto;
    &::-webkit-scrollbar {
      width: 4px;
      height: 4px;
    }
    &::-webkit-scrollbar {
      width: 4px;
      height: 4px;
    }
    &::-webkit-scrollbar-thumb {
      background-color: #c1c1c1;
      border-radius: 5px;
    }
    &::-webkit-scrollbar-thumb {
      border-radius: 9px;
    }
    &:hover::-webkit-scrollbar-thumb {
      background-color: #959595;
    }
    &::-webkit-scrollbar-thumb:hover {
      background-color: #959595;
    }
    &::-webkit-scrollbar-thumb:active {
      background-color: #757575;
    }
    div {
      line-height: 45px;
      border-bottom: 1px solid #e0e0e0;
      .filled-in:checked + label:after {
        background: #871b55;
        border-color: #871b55;
      }
      label {
        line-height: 18px;
        &:after {
          width: 18px;
          height: 18px;
        }
        &:before {
          height: 11px;
        }
      }
    }
  }
  .foot-btn {
    position: absolute;
    bottom: 0;
    right: 24px;
    font-size: 14px;
    font-weight: 500;
    span {
      margin-left: 25px;
      cursor: pointer;
    }
  }
}

.im-alert {
  position: fixed;
  top: 30px;
  left: 50%;
  width: 300px;
  margin-left: -300px/2;
  z-index: 9;
}

.im-foot-page {
  position: absolute;
  bottom: 10px;
  right: 440px;
}
</style>
<style lang="less">
@import "../../assets/css/googlefont.css";
@import "../../../node_modules/vuetify/dist/vuetify.min.css";

.toast {
  position: relative;
}

.vapp {
  min-height: inherit;
  .card {
    overflow: inherit;
    margin-top: 0;
  }
  .Default {
    background: #cbedf4;
  }
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
      background: #871b55;

      color: #fff;
    }
  }
  .btn {
    padding: 0;
  }
}

.input-field {
  width: 267px;
  margin: 0;
  input.select-dropdown {
    border-bottom: 2px solid #871b55;
    margin: 0;
  }
  input.validate {
    border-bottom: 2px solid #871b55;
    margin: 0;
    &:hover {
      box-shadow: none;
      border-bottom: 2px solid #871b55;
    }
    &:focus {
      box-shadow: none;
      border-bottom: 2px solid #871b55;
    }
  }
}

.multiselect__tags {
  background: none;
  border: none;
  padding: 0;
  input {
    border-bottom: 2px solid #871b55;
    margin: 0;
    &:hover {
      border-bottom: 2px solid #871b55;
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

.multiselect {
  z-index: 2;
}

.select-tow {
  .multiselect {
  }
  input {
    border-bottom: 2px solid #871b55;
    margin: 0;
    &:hover {
      border-bottom: 2px solid #871b55;
    }
  }
  input[type="text"][readonly="readonly"] {
    border-bottom: 2px solid #871b55;
    color: #000;
  }
}

.multistep {
  position: absolute;
  width: 267px;
  height: 300px;
  overflow: auto;
  background: #fff;
  padding: 10px;
  z-index: 6;
  border: 1px solid #eee;
  div {
    line-height: 36px;
    color: rgba(0, 0, 0, 0.38);
    div {
      div {
        cursor: pointer;
        color: #333;
      }
    }
  }
}

.picker__date-display,
.picker__weekday-display,
.picker__day--selected,
.picker__day--selected:hover,
.picker--focused .picker__day--selected {
  background: #871b55;
}

.picker__close,
.picker__today,
.picker__day.picker__day--today {
  color: #871b55;
}

.small-dialog__content {
  width: 150px;
}

.input-group {
  margin-bottom: 0;
}

.file-input {
  display: inline-flex;
  flex: 0 1 auto;
  height: 40px;
  vertical-align: middle;
  width: 200px;
  position: relative;
  top: 12px;
}

.button-no-padding {
  padding: 0;
}

.active-button {
  position: absolute;
  right: 12px;
}
</style>
