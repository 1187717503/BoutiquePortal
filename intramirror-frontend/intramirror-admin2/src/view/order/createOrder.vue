<template>
  <div>
    <div class="head-box">
      <div class="buyer">
        <h3 class="tit">Buyer:</h3>
        <input type="text" placeholder="Name" v-model="buyer.name">
        <input type="text" placeholder="Phone" maxlength="11" v-model="buyer.phone">
        <input type="text" placeholder="Wechat" v-model="buyer.wechat">
      </div>
      <div class="shipto">
        <h3 class="tit">Ship To:</h3>
        <div v-if="!isAddress">
          <div class="region" @click="selectGeography(1)">
            <p>China Mainland</p>
            <span>Geography</span>
          </div>
          <div class="region" @click="selectGeography(2)">
            <p>HongKong/Macau</p>
            <span>Geography</span>
          </div>
          <div class="region" @click="selectGeography(3)">
            <p>European Union</p>
            <span>Geography</span>
          </div>
        </div>
        <div class="address-info" v-else="isAddress">
          <div v-if="seleseAddressData.type===1">
            <p><span>{{chinaAddress.info.name}}</span><span>{{chinaAddress.info.phone}}</span></p>
            <p>
              <span>{{chinaAddress.info.province}}</span><span>{{chinaAddress.info.city}}</span><span>{{chinaAddress.info.district}}</span><span>{{chinaAddress.info.code}}</span>
            </p>
            <p>{{chinaAddress.info.address}} <i class="mdi mdi-pencil"
                                                @click="selectGeography(seleseAddressData.type)"></i></p>
          </div>
          <div v-else-if="seleseAddressData.type===2">
            <p><span>{{macauAddress.info.name}}</span><span>{{macauAddress.info.phone}}</span></p>
            <p><span>{{macauAddress.info.province}}</span><span>{{macauAddress.info.distrinct}}</span></p>
            <p>{{macauAddress.info.address}} <i class="mdi mdi-pencil"
                                                @click="selectGeography(seleseAddressData.type)"></i></p>
          </div>
          <div v-else-if="seleseAddressData.type===3">
            <p><span>{{europeanAddress.info.name}}</span><span>{{europeanAddress.info.phone}}</span></p>
            <p>
              <span>{{europeanAddress.info.country}}</span><span>{{europeanAddress.info.province}}</span><span>{{europeanAddress.info.city}}</span><span>{{europeanAddress.info.code}}</span>
            </p>
            <p>{{europeanAddress.info.address}} <i class="mdi mdi-pencil"
                                                   @click="selectGeography(seleseAddressData.type)"></i></p>
          </div>
          <div>
            <p>{{areaText}}<i class="mdi mdi-pencil" @click="isAddress=false"></i></p>
          </div>
        </div>
      </div>
    </div>
    <div class="min-center">
      <div class="add-button" @click="addProduct">
        ADD PRODUCT
      </div>

      <div class="list">
        <div class="head">
          <div></div>
          <div>Boutique ID</div>
          <div>Designer ID</div>
          <div>Color</div>
          <div>Size</div>
          <div>Brand</div>
          <div>Boutique</div>
          <div>Retail<br>Price</div>
          <div>Boutique<br>Price</div>
          <div>Discount</div>
          <div>Sale<br>Price</div>
          <div></div>
        </div>
        <div class="li" v-for="(item,index) in listData">
          <div><img
            :src="item.product.coverImg | ImgArr('?x-oss-process=image/resize,m_fill,w_50,h_64,limit_0/auto-orient,0/quality,q_90')"
            alt=""></div>
          <div>
            {{item.product.productCode}}
          </div>
          <div>
            {{item.productPropertyMap.brandID}}
          </div>
          <div>
            {{item.productPropertyMap.colorCode}}
          </div>
          <div>
            {{item.sizeMap.value}}
          </div>
          <div>
            {{item.brand.englishName}}
          </div>
          <div>
            {{item.vendor.vendorName}}
          </div>
          <div>
            €{{item.sku.price | PriceForm}}
          </div>
          <div>
            €{{item.sku.inPrice | PriceForm}}
          </div>
          <div>
            <input type="number" :value="item.shopProductSku.salePrice/item.sku.price | formDisc"
                   @change="salePrice(item.sku.price,index,$event)">off
          </div>
          <div>
            €{{item.shopProductSku.salePrice | PriceForm}}
          </div>
          <div>
            <i class="mdi mdi-close" @click="delProduct(index)"></i>
          </div>
        </div>
      </div>

      <div class="foot-price">
        <div class="left">
          <div class="input">
            <input type="number" placeholder="Paid" @change="coDeposit($event)" style="width:150px" v-model="paid">
            <input type="text" placeholder="Buyer Name" style="width:197px" v-model="BuyerName">
            <div class="input-field PaymentType" style="width:189px;display:inline-block;">
              <select id="PaymentType">
                <option v-for="item in PaymentType" :value="item.id">{{item.name}}</option>
              </select>
            </div>
            <div class="input-field datetime" style="width:200px;display:inline-block;">
              <i class="mdi mdi-calendar prefix active"></i>
              <input id="date" type="text" class="validate active" v-model="Paidat">
              <label for="date" class="active">Paid at</label>
            </div>
          </div>

          <p>Balance Due: <span>¥{{footPrice.balancedue | PriceForm}}</span></p>
        </div>
        <div class="right">
          <div class="flex">
            <div>Subtotal:</div>
            <div>€{{footPrice.subtotal | PriceForm}}</div>
          </div>
          <div class="flex">
            <div>Shipping:</div>
            <div>€{{footPrice.shipping | PriceForm}}</div>
          </div>
          <div class="flex">
            <div>VAT:</div>
            <div>€{{footPrice.vat | PriceForm}}</div>
          </div>
          <div class="flex">
            <div>Grand Total:</div>
            <div>€{{footPrice.subtotal + footPrice.shipping + footPrice.vat | PriceForm}}</div>
          </div>
          <div class="flex">
            <div>Grand Total (¥ 1:{{footPrice.rate}}):</div>
            <div>¥{{(footPrice.subtotal + footPrice.shipping + footPrice.vat) * footPrice.rate | PriceForm}}</div>
          </div>
        </div>
      </div>
    </div>
    <div class="foot-btn">
      <div class="waves-effect waves-light btn" @click="submitData">
        SUBMIT
      </div>
      <div class="waves-effect waves-light btn" @click="showShade=true;isClose=true">
        CANCEL
      </div>
    </div>

    <div class="geography-layer" v-show="geographyLayer.show" :class="{european:geographyLayer.european}">
      <h3 class="tit">Enter Ship-to Address:</h3>
      <div class="china-form" v-show="geographyLayer.china">
        <input type="text" placeholder="Name" style="width:219px" v-model="chinaAddress.info.name">
        <input type="text" placeholder="Phone" style="width:210px;margin-left:6px" maxlength="11"
               v-model="chinaAddress.info.phone">
        <input type="text" placeholder="Zip Code" style="width:140px;margin-left:6px" v-model="chinaAddress.info.code">
        <div class="input-field" style="width:161px">
          <select id="chinaProvince">
            <option v-for="item in chinaAddress.province" :value="item.address_province_id+','+item.name">{{item.name}}</option>
          </select>
        </div>
        <div class="input-field" style="width:189px">
          <select id="chinaCity">
            <option v-for="item in chinaAddress.city" :value="item.address_city_id+','+item.name">{{item.name}}</option>
          </select>
        </div>
        <div class="input-field" style="width:210px;margin-right:0">
          <select id="chinaZone">
            <option v-for="item in chinaAddress.zone" :value="item.name">{{item.name}}</option>
          </select>
        </div>
        <input type="text" placeholder="Address" v-model="chinaAddress.info.address">
      </div>

      <div class="china-form" v-show="geographyLayer.macau">
        <input type="text" placeholder="Name" style="width:219px" v-model="macauAddress.info.name">
        <input type="text" placeholder="Phone" style="width:210px;margin-left:6px" maxlength="11"
               v-model="macauAddress.info.phone">
        <div class="input-field" style="width:150px;">
          <select id="macauProvince">
            <option v-for="item in macauAddress.province" :value="item.name+','+item.address_country_id">{{item.name}}
            </option>
          </select>
        </div>
        <input type="text" placeholder="Distrinct" style="width:250px" v-model="macauAddress.info.distrinct">
        <input type="text" placeholder="Address" v-model="macauAddress.info.address">
      </div>

      <div class="china-form" v-show="geographyLayer.european">
        <input type="text" placeholder="Name" style="width:219px" v-model="europeanAddress.info.name">
        <input type="text" placeholder="Phone" style="width:197px;margin-left:6px" maxlength="11"
               v-model="europeanAddress.info.phone">
        <div class="input-field" style="width:424px">
          <select id="europeanProvince">
            <option v-for="item in europeanAddress.province" :value="item.name+','+item.address_country_id">{{item.name}}</option>
          </select>
        </div>
        <input type="text" placeholder="Province" style="width:216px" v-model="europeanAddress.info.province">
        <input type="text" placeholder="City" style="width:223px;margin-left:6px" v-model="europeanAddress.info.city">
        <input type="text" placeholder="Zip Code" style="width:197px" v-model="europeanAddress.info.code">
        <input type="text" placeholder="Address" v-model="europeanAddress.info.address">
      </div>
      <div class="btns">
        <span @click="addressCancel">CANCEL</span>
        <span @click="addressSave">SAVE</span>
      </div>
    </div>

    <div class="addproduct-layer" v-if="addproductDate.show">
      <h3 class="tit">Add Product</h3>
      <div class="product-cent">
        <div class="left-list">
          <div class="head-search">
            <i class="mdi mdi-magnify"></i>
            <input type="text" placeholder="Designer ID" v-model="searcImport.id" @keypress="searchList">
            <input type="text" placeholder="Color Code" v-model="searcImport.color" @keypress="searchList">
          </div>
          <div class="head-tit">
            <span>Designer ID</span>
            <span>Color</span>
            <span>Boutique</span>
            <span>Boutique Price</span>
            <span>Discount</span>
          </div>
          <div class="list">
            <div class="li" v-for="item in addproductDate.list" @click="getPicStock(item,$event)">
              <span>{{item.BrandID}}</span>
              <span>{{item.ColorCode}}</span>
              <span>{{item.vendor_name}}</span>
              <span>€{{item.in_price | PriceForm}}</span>
              <span>{{item.discount}}</span>
            </div>
          </div>
        </div>

        <div class="right-info">
          <swiper class="banner-swiper" :options="swiperOption" v-if="addproductDate.banner.length>0">
            <swiper-slide v-for="(item,index) in addproductDate.banner" :key="index">
              <img :src="item" alt="">
            </swiper-slide>
            <div class="swiper-button-prev" slot="button-prev">
              <i class="mdi mdi-chevron-left"></i>
            </div>
            <div class="swiper-button-next" slot="button-next">
              <i class="mdi mdi-chevron-right"></i>
            </div>
          </swiper>
          <div class="head-tit" v-if="addproductDate.stock.length>0">
            <span>Size</span>
            <span>Avail Stock</span>
          </div>
          <div class="spec-box" v-if="addproductDate.stock.length>0">
            <div class="li" v-for="item in addproductDate.stock" @click="selectStock(item,$event)">
              <span>{{item.value}}</span>
              <span>{{item.store}}</span>
            </div>
          </div>
        </div>
        <div class="btns">
          <span @click="cancelSeach">CANCEL</span>
          <span @click="addSelectList">ADD</span>
        </div>
        <div class="loading" v-if="seachLoading">
          <div class="line-spin-fade-loader">
            <div></div>
            <div></div>
            <div></div>
            <div></div>
            <div></div>
            <div></div>
            <div></div>
            <div></div>
          </div>
        </div>
      </div>
    </div>

    <div class="close-layer" v-if="isClose">
      <h3>Are you sure to discard the changes?</h3>
      <p>The draft of your order will be deleted. You<br>can not undo this action. </p>
      <div class="btns">
        <span @click="showShade=false;isClose=false;">BACK</span>
        <span onclick="window.location.reload()">DISCARD</span>
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
  import shade from '../component/shade.vue'
  import loading from '../component/loading.vue'
  import {swiper, swiperSlide} from 'vue-awesome-swiper'
  import {
    get_product,
    get_address,
    product_get_skuStore,
    get_product_detail,
    get_fee,
    input_create_order
  } from '../../api/CreateOrder'
  import flatpickr from 'flatpickr'

  export default {
    data() {
      return {
        searcImport: {
          id: '',
          color: ''
        },
        showShade: false,
        isLoading: false,
        isAddress: false,
        PaymentType: [
          {id: 1, name: 'Wechat'},
          {id: 3, name: "Alipay"},
          {id: 2, name: 'Bank card'}
        ],
        payType: 1,
        Paidat: '',
        BuyerName: '',
        paid: '',
        addproductDate: {
          show: false,
          list: [],
          banner: [],
          stock: [],
          add: {
            pid: '',
            sid: '',
            uid: '',
            tid: '',
            vid: ''
          }
        },
        imAlert: {
          show: false,
          text: ''
        },
        listData: [],
        geographyLayer: {
          show: false,
          china: false,
          macau: false,
          european: false
        },
        swiperOption: {
          nextButton: '.swiper-button-next',
          prevButton: '.swiper-button-prev',
          slidesPerView: 1,
          paginationClickable: true,
          spaceBetween: 30,
          loop: true
        },
        seleseAddressData: {
          type: 0,
          gid: '',
          cid: '',
          pid: '',
          yid: '',
          did: ''
        },
        chinaAddress: {
          province: [],
          city: [],
          zone: [],
          info: {
            name: '',
            phone: '',
            code: '',
            province: '',
            city: '',
            district: '',
            address: ''
          }
        },
        areaText: '',
        macauAddress: {
          province: [],
          info: {
            name: '',
            phone: '',
            province: '',
            distrinct: '',
            address: ''
          }
        },
        europeanAddress: {
          province: [],
          info: {
            name: '',
            phone: '',
            country: '',
            province: '',
            city: '',
            code: '',
            address: ''
          }
        },
        buyer: {
          name: '',
          phone: '',
          wechat: ''
        },
        postPriceInfo: {
          product_ids: [],
          geography_id: "",
          country_id: "",
          shop_product_sku_ids: [],
          category_id: []
        },
        footPrice: {
          subtotal: 0,
          shippingList: [],
          shipping: 0,
          vatList: [],
          vat: 0,
          rate: 1,
          balancedue: 0
        },
        seachLoading: false,
        isClose: false
      }
    },
    watch: {
      listData: {
        handler(val) {
          this.footPrice.subtotal = 0;
          for (let item in val) {
            this.footPrice.subtotal += val[item].shopProductSku.salePrice
          }
          this.coDeposit()
        },
        deep: true
      },
      isAddress(val) {
        if (!val) {
          this.chinaAddress = {
            province: [],
            city: [],
            zone: [],
            info: {
              name: '',
              phone: '',
              code: '',
              province: '',
              city: '',
              district: '',
              address: ''
            }
          };
          this.macauAddress = {
            province: [],
            info: {
              name: '',
              phone: '',
              province: '',
              distrinct: '',
              address: ''
            }
          };
          this.europeanAddress = {
            province: [],
            info: {
              name: '',
              phone: '',
              country: '',
              province: '',
              city: '',
              code: '',
              address: ''
            }
          };
          this.geographyLayer = {
            china: false,
            macau: false,
            european: false
          };
        }
      }
    },
    mounted() {
      flatpickr('#date', {
        dateFormat: 'd/m/Y',
      });
      let tDate = new Date();
      let y = tDate.getFullYear();
      let m = tDate.getMonth() + 1;
      let d = tDate.getDate();
      this.Paidat = d + '/' + m + '/' + y;

      $('select').material_select();
      $('#chinaProvince').change((e) => {
        let val = e.target.value.split(',');
        this.seleseAddressData.pid = val[0];
        this.loadAddress(this.seleseAddressData);
        this.chinaAddress.info.province = val[1]
      });
      $('#chinaCity').change((e) => {
        let val = e.target.value.split(',');
        this.seleseAddressData.yid = val[0];
        this.loadAddress(this.seleseAddressData);
        this.chinaAddress.info.city = val[1]
      });
      $('#chinaZone').change((e) => {
        this.chinaAddress.info.district = e.target.value
      });
      $('#macauProvince').change((e) => {
        let val = e.target.value.split(',');
        this.macauAddress.info.province = val[0];
        this.postPriceInfo.country_id = val[1];
      });
      $('#europeanProvince').change((e) => {
        let val = e.target.value.split(',');
        this.europeanAddress.info.country = val[0];
        this.postPriceInfo.country_id = val[1];
      });
      $('#PaymentType').change((e) => {
        this.payType = e.target.value
      })
    },
    methods: {
      //添加地址事件 1大陆 2港澳 3欧盟
      selectGeography(type) {
        switch (type) {
          case 1:
            this.geographyLayer.china = true;
            this.seleseAddressData = {
              gid: '',
              cid: 2,
            };
            this.areaText = '中国大陆';
            this.postPriceInfo.country_id = 2;
            this.loadAddress(this.seleseAddressData);
            break;
          case 2:
            this.geographyLayer.macau = true;
            this.seleseAddressData = {
              gid: 2,
              cid: '',
            };
            this.areaText = '香港澳门';
            this.loadAddress(this.seleseAddressData);
            break;
          case 3:
            this.geographyLayer.european = true;
            this.seleseAddressData = {
              gid: 3,
              cid: '',
            };
            this.areaText = '欧盟';
            this.loadAddress(this.seleseAddressData);
            break;
        }
        this.seleseAddressData.type = type;
        this.postPriceInfo.geography_id = type;
        this.showShade = true;
        this.geographyLayer.show = true;
      },
      //关闭选择地址
      addressCancel() {
        this.geographyLayer = {
          show: false,
          china: false,
          macau: false,
          european: false
        };
        this.showShade = false;
      },
      //选择地址初始化接口
      loadAddress(data) {
        let postData = {
          "geography_id": parseInt(data.gid),
          "country_id": parseInt(data.cid),
          "province_id": parseInt(data.pid),
          "city_id": parseInt(data.yid),
          "district_id": parseInt(data.did)
        };
        get_address(postData).then(res => {
          switch (this.seleseAddressData.type) {
            case 1:
              this.chinaAddress.province = res.data.data.provinceMapList;
              this.chinaAddress.province.unshift({name: 'Province'});
              if (res.data.data.cityMapList) {
                this.chinaAddress.city = res.data.data.cityMapList;
                this.chinaAddress.city.unshift({name: 'City'})
              } else {
                this.chinaAddress.city.push({name: 'City'})
              }
              if (res.data.data.districtMapList) {
                this.chinaAddress.zone = res.data.data.districtMapList;
                this.chinaAddress.zone.unshift({name: 'District'})
              } else {
                this.chinaAddress.zone.push({name: 'District'})
              }
              break;
            case 2:
              this.macauAddress.province = res.data.data.countryMapList;
              this.macauAddress.info.province = res.data.data.countryMapList[0].name;
              this.postPriceInfo.country_id = res.data.data.countryMapList[0].address_country_id;
              break;
            case 3:
              this.europeanAddress.province = res.data.data.countryMapList;
              this.europeanAddress.province.unshift({name: 'Country'});
              break;
          }
          this.seleseFand();
        })

      },
      //保存选择的地址
      addressSave() {
        switch (this.seleseAddressData.type) {
          case 1:
            if (this.chinaAddress.info.name === '') {
              this.imAlertfun('请输入Name')
            } else if (this.chinaAddress.info.phone === '') {
              this.imAlertfun('请输入Phone')
            } else if (this.chinaAddress.info.code === '') {
              this.imAlertfun('请输入Zip Code')
            } else if (this.chinaAddress.info.province === '' || this.chinaAddress.info.province === 'Province') {
              this.imAlertfun('请选择Province')
            } else if (this.chinaAddress.info.city === '' || this.chinaAddress.info.province === 'City') {
              this.imAlertfun('请选择City')
            } else if (this.chinaAddress.info.district === '' || this.chinaAddress.info.province === 'District') {
              this.imAlertfun('请选择District')
            } else if (this.chinaAddress.info.address === '') {
              this.imAlertfun('请输入Address')
            } else {
              this.isAddress = true;
              this.showShade = false;
              this.geographyLayer.show = false;
            }
            break;
          case 2:
            if (this.macauAddress.info.name === '') {
              this.imAlertfun('请输入Name')
            } else if (this.macauAddress.info.phone === '') {
              this.imAlertfun('请输入Phone')
            } else if (this.macauAddress.info.distrinct === '') {
              this.imAlertfun('请输入Distrinct')
            } else if (this.macauAddress.info.address === '') {
              this.imAlertfun('请输入Address')
            } else {
              this.isAddress = true;
              this.showShade = false;
              this.geographyLayer.show = false;
            }
            break;
          case 3:
            if (this.europeanAddress.info.name === '') {
              this.imAlertfun('请输入Name')
            } else if (this.europeanAddress.info.phone === '') {
              this.imAlertfun('请输入Phone')
            } else if (this.europeanAddress.info.country === '') {
              this.imAlertfun('请选择Country')
            } else if (this.europeanAddress.info.province === '') {
              this.imAlertfun('请输入Province')
            } else if (this.europeanAddress.info.city === '') {
              this.imAlertfun('请输入City')
            } else if (this.europeanAddress.info.code === '') {
              this.imAlertfun('请输入Code')
            } else if (this.europeanAddress.info.address === '') {
              this.imAlertfun('请输入Address')
            } else {
              this.isAddress = true;
              this.showShade = false;
              this.geographyLayer.show = false;
            }
            break;
        }
        this.delFee();
      },
      //添加商品
      addProduct() {
        if (!this.isAddress) {
          Materialize.toast('请先选择地址！', 4000);
          return false
        }
        this.addproductDate.show = true;
        this.showShade = true
      },
      //搜索列表
      searchList(e) {
        if (e.charCode !== 13) {
          return false
        }
        $('.addproduct-layer .product-cent .left-list .list .li').removeClass('active');
        let data = {
          "brandID": this.searcImport.id,
          "colorCode": this.searcImport.color
        };
        this.seachLoading = true;
        this.addproductDate.stock = [];
        this.addproductDate.banner = [];
        get_product(data).then(res => {
          if (res.data.status === 1) {
            this.addproductDate.list = res.data.data.mapList;
            this.seachLoading = false;
            if (this.addproductDate.list.length <= 0) {
              this.imAlertfun('没有搜索到商品！');
            }
          }
        })
      },
      getPicStock(item, e) {
        for (let i = 0; i < e.target.parentNode.parentNode.childNodes.length; i++) {
          e.target.parentNode.parentNode.childNodes[i].classList.remove('active')
        }
        e.target.parentNode.classList.add('active');
        this.addproductDate.add = {
          pid: item.product_id,
          sid: item.shop_product_id,
          uid: '',
          tid: '',
          vid: item.shop_category_id
        };
        let data = {
          shop_product_id: item.shop_product_id
        };
        this.seachLoading = true;
        product_get_skuStore(data).then(res => {
          if (res.data.status === 1) {
            this.addproductDate.stock = res.data.data.mapList;
            let pic = res.data.data.shopProduct.coverpic;
            pic = pic.replace('[', '');
            pic = pic.replace(']', '');
            pic = pic.split(',');
            for (let i in pic) {
              pic[i] = pic[i].replace('"', '').replace('"', '') + '?x-oss-process=image/resize,m_fill,w_257,h_328,limit_0/auto-orient,0/quality,q_90'
            }
            this.addproductDate.banner = pic;
            $('.addproduct-layer .product-cent .right-info .spec-box .li').removeClass('active');
            this.seachLoading = false;
          }
        })
      },
      //选择规格
      selectStock(item, e) {
        if (item.store <= 0) {
          this.imAlertfun('请选择有库存的商品规格！');
          return false;
        }
        for (let i = 0; i < e.target.parentNode.parentNode.childNodes.length; i++) {
          e.target.parentNode.parentNode.childNodes[i].classList.remove('active')
        }
        e.target.parentNode.classList.add('active');
        this.addproductDate.add.uid = item.sku_id;
        this.addproductDate.add.tid = item.shop_product_sku_id;
      },
      //关闭搜索框
      cancelSeach() {
        this.showShade = false;
        this.addproductDate = {
          show: false,
          list: [],
          banner: [],
          stock: [],
          add: {
            pid: '',
            sid: '',
            uid: '',
            tid: '',
            vid: ''
          }
        };
        this.searcImport = {
          id: '',
          color: ''
        };
      },
      //保存选择的商品
      addSelectList() {
        if (this.addproductDate.add.pid === '') {
          this.imAlertfun('请选择商品');
          return false;
        } else if (this.addproductDate.add.uid === '') {
          this.imAlertfun('请选择规格');
          return false;
        }
        let data = {
          product_id: this.addproductDate.add.pid,
          shop_product_id: this.addproductDate.add.sid,
          sku_id: this.addproductDate.add.uid,
          shop_product_sku_id: this.addproductDate.add.tid
        };
        get_product_detail(data).then(res => {
          if (res.data.status === 1) {
            this.listData.push(res.data.data);
            this.postPriceInfo.product_ids.push(this.addproductDate.add.pid);
            this.postPriceInfo.shop_product_sku_ids.push(this.addproductDate.add.tid);
            this.postPriceInfo.category_id.push(this.addproductDate.add.vid);
            this.delFee();
            this.cancelSeach();
          }
        })
      },
      //修改折扣价格
      salePrice(price, index, event) {
        let val = parseFloat(100 - event.target.value);
        this.$set(this.listData[index].shopProductSku, 'salePrice', price * (val / 100));
        $('.foot-price .left input').val('')
      },
      //删除添加的商品
      delProduct(index) {
        this.listData.splice(index, 1);
        this.postPriceInfo.product_ids.splice(index, 1);
        this.postPriceInfo.shop_product_sku_ids.splice(index, 1);
        this.postPriceInfo.category_id.splice(index, 1);
        $('.foot-price .left input').val('');
        this.delFee();
      },
      //获取运费增值税
      delFee() {
        let data = {
          product_ids: this.postPriceInfo.product_ids.join(',') === '' ? 0 : this.postPriceInfo.product_ids.join(','),
          geography_id: this.postPriceInfo.geography_id,
          country_id: this.postPriceInfo.country_id,
          shop_product_sku_ids: this.postPriceInfo.shop_product_sku_ids.join(',') === '' ? 0 : this.postPriceInfo.shop_product_sku_ids.join(','),
          category_ids: this.postPriceInfo.category_id.join(',') === '' ? 0 : this.postPriceInfo.category_id.join(',')
        };
        get_fee(data).then(res => {
          if (res.data.status === 1) {
            this.footPrice.rate = res.data.data.rate;
            this.footPrice.shippingList = res.data.data.shipFeeListMap;
            this.footPrice.shipping = 0;
            for (let item in this.footPrice.shippingList) {
              this.footPrice.shipping += this.footPrice.shippingList[item].eurFee
            }
            this.footPrice.vatList = res.data.data.taxFeeMapList;
            this.footPrice.vat = 0;
            for (let item in this.footPrice.vatList) {
              this.footPrice.vat += this.footPrice.vatList[item].taxFees
            }
            this.coDeposit()
          }
        });
      },
      //计算Deposit
      coDeposit(event) {
        try {
          let val = parseFloat(event.target.value === '' ? '0' : event.target.value);
          val = val < 1 ? '' : val;
          this.paid = val;
          let pri = ((this.footPrice.subtotal + this.footPrice.shipping + this.footPrice.vat) * this.footPrice.rate) - val;
          this.footPrice.balancedue = pri < 0 ? 0 : pri;
        } catch (err) {
          this.footPrice.balancedue = (this.footPrice.subtotal + this.footPrice.shipping + this.footPrice.vat) * this.footPrice.rate;
        }
      },
      //提交数据
      submitData() {
        if (this.buyer.name === '' || this.buyer.phone === '' || this.buyer.wechat === '') {
          Materialize.toast('请输入买家信息！', 4000);
          return false
        } else if (!this.isAddress) {
          Materialize.toast('请选择卖家地址！', 4000);
          return false
        } else if (this.listData.length <= 0) {
          Materialize.toast('请至少添加一件商品！', 4000);
          return false
        } else if (this.paid === '') {
          Materialize.toast('请输入Paid！', 4000);
          return false
        } else if (this.BuyerName === '') {
          Materialize.toast('请输入Buyer Name！', 4000);
          return false
        } else {
          let addressInfo = {
            user_rec_name: "",
            user_rec_country: "",
            user_rec_province: "",
            user_rec_city: "",
            user_rec_area: "",
            user_rec_addr: "",
            user_rec_code: "",
            user_rec_mobile: ""
          };
          let listInfo = [];
          switch (this.seleseAddressData.type) {
            case 1:
              addressInfo = {
                user_rec_name: this.chinaAddress.info.name,
                user_rec_country: "中国大陆",
                user_rec_province: this.chinaAddress.info.province,
                user_rec_city: this.chinaAddress.info.city,
                user_rec_area: this.chinaAddress.info.district,
                user_rec_addr: this.chinaAddress.info.address,
                user_rec_code: this.chinaAddress.info.code,
                user_rec_mobile: this.chinaAddress.info.phone
              };
              break;
            case 2:
              addressInfo = {
                user_rec_name: this.macauAddress.info.name,
                user_rec_country: this.macauAddress.info.province,
                user_rec_province: this.macauAddress.info.distrinct,
                user_rec_city: 0,
                user_rec_area: 0,
                user_rec_addr: this.macauAddress.info.address,
                user_rec_code: 0,
                user_rec_mobile: this.macauAddress.info.phone
              };
              break;
            case 3:
              addressInfo = {
                user_rec_name: this.europeanAddress.info.name,
                user_rec_country: this.europeanAddress.info.country,
                user_rec_province: this.europeanAddress.info.province,
                user_rec_city: this.europeanAddress.info.city,
                user_rec_area: 0,
                user_rec_addr: this.europeanAddress.info.address,
                user_rec_code: this.europeanAddress.info.code,
                user_rec_mobile: this.europeanAddress.info.phone
              };
              break;
          }
          for (let item in this.listData) {
            let data = {
              shop_product_sku_id: this.listData[item].shopProductSku.shopProductSkuId,
              in_price: this.listData[item].sku.inPrice,
              sale_price: this.listData[item].shopProductSku.salePrice,
              quantity: 1,
              shipping_fee: this.footPrice.shippingList[item].eurFee,
              tax_fee: this.footPrice.vatList[item].taxFees,
              tax_id: this.footPrice.vatList[item].tax_id
            };
            listInfo.push(data)
          }
          let data = {
            buyerName: this.buyer.name,
            buyerPhone: this.buyer.phone,
            buyerWechat: this.buyer.wechat,
            geographyId: this.seleseAddressData.type,
            totalShipFee: this.footPrice.shipping,
            totalTaxFee: this.footPrice.vat,
            subtotalPrice: this.footPrice.subtotal,
            balanceDue: this.footPrice.balancedue,
            shipToMap: addressInfo,
            checkoutListStr: listInfo,
            paymentMethod: this.payType,
            amt: this.paid,
            paidAt: this.Paidat,
            paymentBuyerName: this.BuyerName
          };
          input_create_order(data).then(res => {
            if (res.data.status === 1) {
              Materialize.toast('提交成功！', 4000);
              setTimeout(() => {
                window.location.reload()
              }, 4000)
            } else {
              Materialize.toast('提交失败！', 4000);
            }
          });
        }
      },
      //下拉框初始化
      seleseFand() {
        setTimeout(() => {
          $('select').material_select();
        }, 0)
      },
      //Im Alert
      imAlertfun(prompt) {
        this.imAlert.text = prompt;
        this.imAlert.show = true;
        setTimeout(() => {
          this.imAlert.show = false;
        }, 3000)
      }
    },
    filters: {
      formDisc(v) {
        let pri = parseFloat(100 - v * 100);
        return parseInt(pri)
      }
    },
    components: {
      shade,
      loading,
      swiper,
      swiperSlide
    }
  }
</script>
<style lang="less" scoped>
  @import "../../assets/css/loaders.css";
  @import "../../assets/css/swiper-3.4.2.min.css";

  .head-box {
    display: flex;
    input {
      border-bottom: 2px solid #871B55;
      &:focus {
        border-bottom: 2px solid #871B55;
        box-shadow: none;
      }
    }
    .tit {
      color: rgba(0, 0, 0, 0.87);
      font-size: 20px;
      font-weight: 500;
      margin-bottom: 0;
      line-height: normal;
    }
    .buyer {
      width: 409.71px;
      height: 179.64px;
      background-color: #FAFAFA;
      box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
      padding: 24px;
      input {
        &:nth-child(2) {
          width: 145px;
          float: left;
        }
        &:nth-child(3) {
          width: 200px;
          float: right;
        }
      }
    }
    .shipto {
      margin-left: 16px;
      flex: 1;
      height: 180.15px;
      background-color: #FAFAFA;
      box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
      padding: 24px;
      .region {
        height: 103.14px;
        width: 220.16px;
        border-radius: 2px;
        background-color: #fff;
        box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
        float: left;
        margin: 12px 17px 0 0;
        padding: 16px;
        p {
          font-size: 24px;
        }
        span {
          font-size: 14px;
        }
        &:hover {
          background: #9E2976;
          color: #fff;
          cursor: pointer;
        }
        &:last-child {
          margin-right: 0;
        }
      }
    }
    .address-info {
      margin-top: 19px;
      p {
        line-height: 30px;
        span {
          margin-right: 16px;
        }
      }
      div {
        &:nth-child(1) {
          float: left;
        }
        &:nth-child(2) {
          float: right;
          font-size: 24px;
          p {
            margin-top: 28px;
          }
        }
      }
      .mdi {
        font-size: 20px;
        margin-left: 10px;
        cursor: pointer;
      }
    }
  }

  .min-center {
    margin-top: 17px;
    background-color: #FAFAFA;
    box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
    .add-button {
      color: #4A4A4A;
      font-size: 20px;
      font-weight: 500;
      padding: 16px 24px;
      cursor: pointer;
      display: inline-block;
    }
    .list {
      .head {
        display: flex;
        border-top: 1px solid #E0E0E0;
        border-bottom: 1px solid #E0E0E0;
        padding: 30px 0 10px 0;
        font-weight: bold;
        color: #9B9B9B;
        div {
          &:nth-child(8), &:nth-child(9), &:nth-child(11) {
            margin-top: -22px;
          }
        }
      }
      .li {
        display: flex;
        line-height: 65px;
        border-bottom: 1px solid #E0E0E0;
        padding: 6px 0 0 0;
        &:hover {
          background: #F5F5F5;
        }
        div:nth-child(12) i {
          font-size: 16px;
          color: #454545;
          &:hover {
            height: 31px;
            width: 31px;
            border-radius: 31px;
            background-color: #9E2976;
            box-shadow: 0 1px 6px 0 rgba(0, 0, 0, 0.24);
            color: #fff;
            line-height: 31px;
            cursor: pointer;
          }
        }

      }
      .head, .li {
        div {
          width: 90px;
          text-align: center;
          overflow: hidden;
          white-space: nowrap;
          text-overflow: ellipsis;
          &:nth-child(1) {
            width: 120px;
            line-height: normal;
          }
          &:nth-child(2) {
            width: 180px;
            text-align: left;
          }
          &:nth-child(3) {
            width: 100px;
          }

          &:nth-child(7) {
            width: 180px;
          }
          &:nth-child(8) {
            text-align: right;
            width: 100px;
          }
          &:nth-child(9) {
            text-align: right;
            width: 100px;
          }
          &:nth-child(10) {
            text-align: right;
            width: 100px;
            input {
              width: 50px;
              text-align: center;
              border-bottom: 2px solid transparent;
              &:focus {
                border-bottom: 2px solid #871B55;
                box-shadow: none;
              }
            }
          }
          &:nth-child(11) {
            text-align: right;
            width: 100px;
          }
        }
      }
    }
    .foot-price {
      height: 170px;
      padding: 0 24px;
      .left {
        .input {
          width: 800px;
          margin: 20px 0;
          input {
            border-bottom: 2px solid #871B55;
            &:focus {
              border-bottom: 2px solid #871B55;
              box-shadow: none;
            }
          }
          .datetime {
            margin-top: 0;
            i {
              left: 6px;
              top: 10px;
              &.active {
                color: #333;
              }
            }
            label {
              left: 0;
            }
            input {
              margin-left: 3rem;
              color: #333;
            }
            .invalid {
              border-bottom: 2px solid #871B55;
              box-shadow: none;
            }
          }
        }
        p {
          color: #9B9B9B;
          font-weight: bold;
          span {
            color: #000;
            margin-left: 37px;
          }
        }
      }
      .right {
        margin-top: 18px;
        .flex {
          display: flex;
          div {
            line-height: 26px;
            &:nth-child(1) {
              width: 170px;
              text-align: right;
              font-weight: bold;
              color: #9B9B9B;
            }
            &:nth-child(2) {
              width: 110px;
              text-align: right;
            }
          }
          &:nth-child(4), &:nth-child(5) {
            div:nth-child(2) {
              font-weight: bold;
            }
          }
        }
      }
    }
  }

  .foot-btn {
    margin-top: 16px;
    .btn {
      background: #9B9B9B;
      &:nth-child(1) {
        background: #871B55;
        margin-right: 27px;
      }
    }
  }

  .geography-layer {
    position: fixed;
    left: 50%;
    top: 50%;
    height: 342px;
    width: 640px;
    z-index: 7;
    padding: 24px;
    margin: -342px/2 0 0 -620px/2;
    border-radius: 2px;
    background-color: #FFFFFF;
    box-shadow: 0 0 24px 0 rgba(0, 0, 0, 0.22), 0 24px 24px 0 rgba(0, 0, 0, 0.3);
    &.european {
      height: 468.94px;
      margin-top: -468.94px/2;
    }
    .tit {
      font-size: 20px;
      font-weight: 500;
      margin-bottom: 16px;
      line-height: normal;
    }
    .china-form {
      input {
        border-bottom: 2px solid #871B55;
        &:focus {
          border-bottom: 2px solid #871B55;
          box-shadow: none;
        }
      }
      .input-field {
        margin-top: 0;
        float: left;
        margin-right: 15px;
      }
    }
    .btns {
      position: absolute;
      right: 25px;
      bottom: 17px;
      color: #871B55;
      font-size: 14px;
      font-weight: 500;
      span {
        margin-left: 35px;
        cursor: pointer;
      }
    }
  }

  .addproduct-layer {
    position: fixed;
    top: 50%;
    left: 50%;
    margin: -664px/2 0 0 -948.35px/2;
    height: 664px;
    overflow: hidden;
    z-index: 7;
    width: 948.35px;
    padding: 24px;
    border-radius: 2px;
    background-color: #FFFFFF;
    box-shadow: 0 0 24px 0 rgba(0, 0, 0, 0.22), 0 24px 24px 0 rgba(0, 0, 0, 0.3);
    .tit {
      font-size: 20px;
      font-weight: 500;
      margin: 0;
      line-height: normal;
    }
    input {
      border-bottom: 2px solid #871B55;
      &:focus {
        border-bottom: 2px solid #871B55;
        box-shadow: none;
      }
    }
    .product-cent {
      display: flex;
      .left-list {
        width: 600px;
        .head-search {
          display: flex;
          i {
            font-size: 26px;
            margin: 10px 10px 0 0;
          }
          input {
            &:nth-child(2) {
              width: 200px;
            }
            &:nth-child(3) {
              width: 100px;
              margin-left: 10px;
            }
          }
        }
        .head-tit {
          display: flex;
          border-bottom: 1px solid #333;
          line-height: 38px;
          padding: 0 10px;
          font-weight: 500;
          span {
            text-align: left;
            margin-right: 16px;
            &:nth-child(1) {
              width: 170px;
            }
            &:nth-child(2) {
              width: 48px;
            }
            &:nth-child(3) {
              width: 135px;
            }
            &:nth-child(4) {
              width: 120px;
              text-align: right;
            }
            &:nth-child(5) {
              width: 100px;
              text-align: right;
            }
          }
        }
        .list {
          border-right: 1px solid rgba(0, 0, 0, 0.16);
          height: 470px;
          overflow: auto;
          .li {
            padding: 0 10px;
            &.active {
              background: #F5F5F5;
            }
            line-height: 45px;
            border-bottom: 1px solid #E0E0E0;
            display: flex;
            span {
              text-align: left;
              overflow: hidden;
              white-space: nowrap;
              text-overflow: ellipsis;
              margin-right: 16px;
              &:nth-child(1) {
                width: 170px;
              }
              &:nth-child(2) {
                width: 48px;
              }
              &:nth-child(3) {
                width: 135px;
              }
              &:nth-child(4) {
                width: 120px;
                text-align: right;
              }
              &:nth-child(5) {
                width: 100px;
                text-align: right;
              }
            }
          }
        }

      }
      .right-info {
        flex: 1;
        .banner-swiper {
          width: 340px;
          img {
            margin-left: 40px;
          }
          .swiper-button-prev {
            left: 0;
            background: none;
            i {
              position: relative;
              top: -20px;
              font-size: 50px;
              left: 0px;
              color: #9b9b9b;
            }

          }
          .swiper-button-next {
            right: 20px;
            background: none;
            i {
              position: relative;
              top: -20px;
              font-size: 50px;
              right: 10px;
              color: #9b9b9b;
            }

          }
        }
        .head-tit {
          width: 242px;
          margin: 0 auto;
          display: flex;
          line-height: 36px;
          font-weight: 500;
          border-bottom: 1px solid #333;
          span {
            flex: 1;
            text-align: center;
          }
        }
        .spec-box {
          width: 242px;
          height: 192px;
          border-right: 1px solid rgba(0, 0, 0, 0.16);
          margin: 0 auto;
          overflow: auto;
          .li {
            display: flex;
            border-bottom: 1px solid #E0E0E0;
            line-height: 48px;
            &.active {
              background: #F5F5F5;
            }
            span {
              flex: 1;
              text-align: center;
            }
          }
        }
      }
    }
    .btns {
      position: absolute;
      right: 31px;
      bottom: 17px;
      font-size: 14px;
      span {
        margin-left: 36px;
        cursor: pointer;
      }
    }
    .loading {
      position: absolute;
      width: 200px;
      top: 50%;
      left: 50%;
      margin: -100px 0 0 -100px;
      height: 200px;
      text-align: center;
      display: flex;
      justify-content: center;
      align-items: center;
      border-radius: 10px;
      background: rgba(0, 0, 0, .6);
      z-index: 1;
    }
  }

  .close-layer {
    position: fixed;
    top: 50%;
    left: 50%;
    height: 197px;
    width: 417.27px;
    margin: -197px/2 0 0 -417.27px/2;
    border-radius: 2px;
    background-color: #FFFFFF;
    padding: 24px;
    z-index: 7;
    box-shadow: 0 0 24px 0 rgba(0, 0, 0, 0.22), 0 24px 24px 0 rgba(0, 0, 0, 0.3);
    h3 {
      font-size: 20px;
      color: rgba(0, 0, 0, 0.87);
      font-weight: 500;
      line-height: normal;
      margin: 0;
    }
    p {
      color: rgba(0, 0, 0, 0.54);
      margin-top: 20px;
    }
    .btns {
      position: absolute;
      right: 31px;
      bottom: 17px;
      font-size: 14px;
      color: #871B55;
      span {
        margin-left: 36px;
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

</style>
<style lang="less">
  @import "../../../node_modules/flatpickr/dist/flatpickr.min.css";

  .geography-layer .dropdown-content {
    max-height: 350px
  }

  .geography-layer {
    .input-field {
      input {
        border-bottom: 2px solid #871B55
      }
    }
  }

  .PaymentType input.select-dropdown {
    border-bottom: 2px solid #871B55;
  }
</style>
