<template>
  <div>
    <div class="head-search">
      <div class="input-field">
        <i class="mdi mdi-calendar prefix"></i>
        <input id="date" type="text" class="validate">
        <label for="date">Created Date</label>
      </div>
      <div class="input-field">
        <select id="Boutique">
          <option v-for="item in vendorList" v-bind:value="item.vendor_id+','+item.vendor_name">{{item.vendor_name}}</option>
        </select>
        <label>Boutique</label>
      </div>

      <div class="input-field">
        <select id="Api">
          <option v-for="item in apiList" :value="item.api_end_point_id+','+item.name">{{item.name}}</option>
        </select>
        <label>Api</label>
      </div>

      <div class="input-field">
        <select id="ErrorType">
          <option v-for="item in apiErrorTypeList" :value="item.api_error_type_id+','+item.error_type_name">
            {{item.error_type_name}}
          </option>
        </select>
        <label>Error Type</label>
      </div>

      <div class="input-field">
        <select id="DataField">
          <option v-for="item in apiFieldList" :value="item.data_field">
            {{item.data_field}}
          </option>
        </select>
        <label>Data Field</label>
      </div>

      <div class="input-field">
        <select id="BoutiqueData">
          <option v-for="item in apiContentList" :value="item.boutiqueDataId+','+item.boutique_data">
            {{item.boutique_data}}
          </option>
        </select>
        <label>Boutique Data</label>
      </div>

      <div class="input-field">
        <input id="BrandID" type="text" class="validate" v-model="searchData.brandId">
        <label for="BrandID">Brand ID</label>
      </div>

      <div class="input-field">
        <input id="Color" type="text" class="validate" v-model="searchData.color">
        <label for="Color">Color</label>
      </div>
      <button class="waves-effect waves-light btn" style="background-color:#871b55 !important" @click="searchList">SEARCH</button>
      <button class="waves-effect waves-light btn" style="background-color:#9b9b9b !important"  @click="resetLoad">RESET</button>
    </div>
    <div class="search-tag">
      <div v-for="(item,index) in searchArr" v-if="item.name">
        <span>{{item.name}}</span>
        <i class="mdi mdi-close-circle" @click="delSearch(item.id)"></i>
      </div>
    </div>
    <div class="main-cent">
      <div class="head-btn">
        <button class="waves-effect waves-light btn" @click="AllProcess">RE-PROCESS</button>
        <button class="waves-effect waves-light btn" @click="AllRemove">REMOVE</button>
      </div>
      <v-app style="min-height:inherit;">
        <v-card>
          <v-data-table
            v-model="selected"
            v-bind:headers="tabHead"
            v-bind:pagination.sync="pagination"
            selected-key="error_id"
            hide-actions
            select-all
            :items="items"
            class="sort"
          >
            <template slot="items" scope="props">
              <tr :active="props.selected">
                <td>
                  <v-checkbox
                    primary
                    hide-details
                    :input-value="props.selected"
                    @click="props.selected = !props.selected"
                  ></v-checkbox>
                </td>
                <td class="text-xs-center"
                    @click="ShowError(props.item.api_error_processing_id,props.item.error_id,props.item.vendor_name)"><span
                  style="width:80px;
    overflow: hidden;
    display: inline-block;
    white-space: nowrap;
    cursor: pointer;
    text-overflow: ellipsis;
}">{{ props.item.error_id }}</span><i sclass="mdi mdi-dots-vertical"></i></td>
                <td class="text-xs-center">{{ props.item.vendor_name }}</td>
                <td class="text-xs-center">{{ props.item.name }}</td>
                <td class="text-xs-center">{{ props.item.brand_id }}</td>
                <td class="text-xs-center">{{ props.item.color_code }}</td>
                <td class="text-xs-center">{{ props.item.boutique_id }}</td>
                <td class="text-xs-center">{{ props.item.error_type_name }}</td>
                <td class="text-xs-center">{{ props.item.data_field}}</td>
                <td class="text-xs-center">{{ props.item.boutique_data}}</td>
                <td class="text-xs-center">{{ props.item.create_time}}</td>
                <td class="text-xs-center">{{ props.item.process_time}}</td>
              </tr>
            </template>
          </v-data-table>
          <div class="foot-page">
            <div class="page">
              <span>Page: </span>
              <multiselect :options="pageLen" @select="selectPageNum($event)" :value="pageNum"
                           :show-labels="false"></multiselect>
            </div>
            <div class="rows-page">
              <span>Rows per page: </span>
              <multiselect v-model="pagination.rowsPerPage" :options="pageSizeArr" @select="selectPageSize($event)"
                           :value="pageSize"
                           :show-labels="false"></multiselect>
            </div>
            <div class="event-page">
              <span>{{pageNum}} - {{pageSize}} of {{totalRow}}</span>
              <i class="mdi mdi-chevron-left" @click="prevPage"></i>
              <i class="mdi mdi-chevron-right" @click="nextPage"></i>
            </div>
          </div>
        </v-card>
      </v-app>
    </div>

    <div class="alert-error" v-if="errorAlert">
      <p class="tit">API Message from Boutique</p>
      <p class="error-info">
        <span>Error ID: {{errorDate.errorId}} </span>
        <span>Boutique: {{errorDate.Boutique}} </span>
      </p>
      <div class="error-text">
        <pre> {{errorDate.errorText}}</pre>
      </div>
      <div class="foot-btn">
        <span @click="AllProcess">RE-PROCESS</span>
        <span @click="showShade=false;errorAlert=false;selected=[]">OK</span>
      </div>
    </div>

    <shade v-if="showShade"></shade>
    <loading v-if="isLoading"></loading>
  </div>
</template>

<script>
import shade from "../component/shade.vue";
import loading from "../component/loading.vue";
import flatpickr from "flatpickr";
import Multiselect from "vue-multiselect";
import {
  getApiErrorPage,
  searchApiError,
  selectShowArr,
  processing,
  remove
} from "../../api/error";

export default {
  data() {
    return {
      searchData: {
        Date: "",
        boutique: {
          id: "",
          name: ""
        },
        api: {
          id: "",
          name: ""
        },
        errorType: {
          id: "",
          name: ""
        },
        brandId: "",
        color: ""
      },
      searchArr: [],
      pageSize: 25,
      pageSizeArr: [25, 50, 100],
      pageNum: 1,
      pageLen: [],
      sortType: "createDate",
      sortNum: 1,
      totalRow: 0,
      showShade: false,
      isLoading: false,
      vendorList: [
        {
          vendor_id: "",
          vendor_name: "All"
        }
      ],
      apiList: [
        {
          api_end_point_id: "",
          name: "All"
        }
      ],
      apiErrorTypeList: [
        {
          api_error_type_id: "",
          error_type_name: "All"
        }
      ],

      apiFieldList: [
        {
          data_field: "All"
        }
      ],

      apiContentList: [
        {
          boutiqueDataId: "",
          boutique_data: "All"
        }
      ],

      pagination: {},
      selected: [],
      errorAlert: false,
      errorDate: {
        errorId: "1",
        Boutique: "2",
        errorText: "3"
      },
      tabHead: [
        {
          text: "Error ID",
          align: "center",
          sortable: false,
          value: "error_id"
        },
        {
          text: "Boutique",
          value: "vendor_name",
          align: "center",
          sortable: false
        },
        { text: "API", value: "name", align: "center", sortable: false },
        {
          text: "Brand ID",
          value: "brand_id",
          align: "center",
          sortable: false
        },
        {
          text: "Color",
          value: "color_code",
          align: "center",
          sortable: false
        },
        {
          text: "Boutique ID",
          value: "BoutiqueID",
          align: "center",
          sortable: false
        },
        {
          text: "Error Type",
          value: "error_type_name",
          align: "center",
          sortable: false
        },
        {
          text: "Data Field",
          value: "data_field",
          align: "center",
          sortable: false
        },
        {
          text: "Boutique Data",
          value: "boutique_data",
          align: "center",
          sortable: false
        },
        {
          text: "Create DateTime",
          value: "create_time",
          align: "center",
          sortable: false
        },
        {
          text: "Process DateTime",
          value: "process_time",
          align: "center",
          sortable: false
        }
      ],
      items: []
    };
  },
  watch: {},
  mounted() {
    $("select").material_select();
    let _this = this;
    flatpickr("#date", {
      dateFormat: "d/m/Y",
      mode: "range",
      onClose(e, s) {
        if (e.length < 2) {
          return;
        }
        this.input.value = s.replace("to", "~");
        let star = new Date(e[0]);
        let end = new Date(e[1]);
        _this.searchData.Date =
          star.getFullYear() +
          "-" +
          parseInt(star.getMonth() + 1) +
          "-" +
          star.getDate() +
          " 00:00:00" +
          "~" +
          end.getFullYear() +
          "-" +
          parseInt(end.getMonth() + 1) +
          "-" +
          end.getDate() +
          " 23:59:59";
      }
    });
    this.loadBoutique();
    this.loadList();
    $("#Boutique").change(e => {
      let data = e.target.value.split(",");
      if (
        data[0] === "undefined" ||
        data[0] === "" ||
        data[1] === "undefined"
      ) {
        this.searchData.boutique = undefined;
        this.searchData.api = undefined;
        this.searchData.errorType = undefined;
        this.searchData.apiField = undefined;
        this.searchData.boutiqueDataId = undefined;
      } else {
        this.searchData.boutique = {
          id: data[0],
          name: data[1]
        };
        this.searchData.api = undefined;
        this.searchData.errorType = undefined;
        this.searchData.apiField = undefined;
        this.searchData.boutiqueDataId = undefined;
      }
      if (this.searchData.boutique) {
        this.loadBoutique(data[0]);
      } else {
        this.loadBoutique();
      }
      console.log(this.searchData);
    });

    $("#Api").change(e => {
      let data = e.target.value.split(",");
      if (
        data[0] === "undefined" ||
        data[0] === "" ||
        data[1] === "undefined"
      ) {
        this.searchData.api = undefined;
        this.searchData.errorType = undefined;
        this.searchData.apiField = undefined;
        this.searchData.boutiqueDataId = undefined;
      } else {
        this.searchData.api = {
          id: data[0],
          name: data[1]
        };
        this.searchData.errorType = undefined;
        this.searchData.apiField = undefined;
        this.searchData.boutiqueDataId = undefined;
      }

      if (this.searchData.boutique && this.searchData.api) {
        this.loadBoutique(this.searchData.boutique.id, data[0]);
      } else {
        if (this.searchData.boutique) {
          this.loadBoutique(this.searchData.boutique.id);
        }
      }
      console.log(this.searchData);
    });
    $("#ErrorType").change(e => {
      let data = e.target.value.split(",");
      if (
        data[0] === "undefined" ||
        data[0] === "" ||
        data[1] === "undefined"
      ) {
        this.searchData.errorType = undefined;
        this.searchData.apiField = undefined;
        this.searchData.boutiqueDataId = undefined;
      } else {
        this.searchData.errorType = {
          id: data[0],
          name: data[1]
        };
        this.searchData.apiField = undefined;
        this.searchData.boutiqueDataId = undefined;
      }

      if (this.searchData.api && this.searchData.errorType) {
        this.loadBoutique(
          this.searchData.boutique.id,
          this.searchData.api.id,
          data[0]
        );
      } else {
        if (this.searchData.api) {
          this.loadBoutique(this.searchData.api.id);
        }
      }
      console.log(this.searchData);
    });

    $("#DataField").change(e => {
      let data = e.target.value;
      if (data === "All") {
        this.searchData.apiField = undefined;
        this.searchData.boutiqueDataId = undefined;
      } else {
        this.searchData.apiField = data;
        this.searchData.boutiqueDataId = undefined;
      }
      if (this.searchData.errorType && this.searchData.apiField) {
        this.loadBoutique(
          this.searchData.boutique.id,
          this.searchData.api.id,
          this.searchData.errorType.id,
          data
        );
      } else {
        if (this.searchData.errorType) {
          this.loadBoutique(
            this.searchData.boutique.id,
            this.searchData.api.id,
            this.searchData.errorType.id
          );
        }
      }
      console.log(this.searchData);
    });

    $("#BoutiqueData").change(e => {
      let data = e.target.value.indexOf(",");
      if (data < 0) {
        this.searchData.boutiqueDataId = undefined;
      } else {
        this.searchData.boutiqueDataId = {
          id: e.target.value.substring(0, data),
          name: String(e.target.value.substring(data + 1))
        };
      }
      console.log(this.searchData.boutiqueDataId);
    });

    let isClick = [
      false,
      false,
      false,
      false,
      false,
      false,
      false,
      false,
      false,
      false,
      false
    ];

    $(".sort thead .column")
      .eq(0)
      .on("click", () => {
        this.delDom();
        this.sortType = "errorId";
        if (isClick[0]) {
          this.sortNum = 0;
          $(".sort thead .column")
            .eq(0)
            .append(
              "<i aria-hidden='true' class='material-icons icon'>arrow_upward</i>"
            );
          $(".sort thead .column")
            .eq(0)
            .addClass("sortable active asc desc");
          isClick[0] = false;
        } else {
          this.sortNum = 1;
          $(".sort thead .column")
            .eq(0)
            .append(
              "<i aria-hidden='true' class='material-icons icon'>arrow_upward</i>"
            );
          $(".sort thead .column")
            .eq(0)
            .addClass("sortable active asc");
          isClick[0] = true;
        }
        this.loadList();
      });

    $(".sort thead .column")
      .eq(1)
      .on("click", () => {
        this.delDom();
        this.sortType = "boutique";
        if (isClick[1]) {
          this.sortNum = 0;
          $(".sort thead .column")
            .eq(1)
            .append(
              "<i aria-hidden='true' class='material-icons icon'>arrow_upward</i>"
            );
          $(".sort thead .column")
            .eq(1)
            .addClass("sortable active asc desc");
          isClick[1] = false;
        } else {
          this.sortNum = 1;
          $(".sort thead .column")
            .eq(1)
            .append(
              "<i aria-hidden='true' class='material-icons icon'>arrow_upward</i>"
            );
          $(".sort thead .column")
            .eq(1)
            .addClass("sortable active asc");
          isClick[1] = true;
        }
        this.loadList();
      });

    $(".sort thead .column")
      .eq(2)
      .on("click", () => {
        this.delDom();
        this.sortType = "api";
        if (isClick[2]) {
          this.sortNum = 0;
          $(".sort thead .column")
            .eq(2)
            .append(
              "<i aria-hidden='true' class='material-icons icon'>arrow_upward</i>"
            );
          $(".sort thead .column")
            .eq(2)
            .addClass("sortable active asc desc");
          isClick[2] = false;
        } else {
          this.sortNum = 1;
          $(".sort thead .column")
            .eq(2)
            .append(
              "<i aria-hidden='true' class='material-icons icon'>arrow_upward</i>"
            );
          $(".sort thead .column")
            .eq(2)
            .addClass("sortable active asc");
          isClick[2] = true;
        }
        this.loadList();
      });

    $(".sort thead .column")
      .eq(3)
      .on("click", () => {
        this.delDom();
        this.sortType = "brandId";
        if (isClick[3]) {
          this.sortNum = 0;
          $(".sort thead .column")
            .eq(3)
            .append(
              "<i aria-hidden='true' class='material-icons icon'>arrow_upward</i>"
            );
          $(".sort thead .column")
            .eq(3)
            .addClass("sortable active asc desc");
          isClick[3] = false;
        } else {
          this.sortNum = 1;
          $(".sort thead .column")
            .eq(3)
            .append(
              "<i aria-hidden='true' class='material-icons icon'>arrow_upward</i>"
            );
          $(".sort thead .column")
            .eq(3)
            .addClass("sortable active asc");
          isClick[3] = true;
        }
        this.loadList();
      });

    $(".sort thead .column")
      .eq(4)
      .on("click", () => {
        this.delDom();
        this.sortType = "color";
        if (isClick[4]) {
          this.sortNum = 0;
          $(".sort thead .column")
            .eq(4)
            .append(
              "<i aria-hidden='true' class='material-icons icon'>arrow_upward</i>"
            );
          $(".sort thead .column")
            .eq(4)
            .addClass("sortable active asc desc");
          isClick[4] = false;
        } else {
          this.sortNum = 1;
          $(".sort thead .column")
            .eq(4)
            .append(
              "<i aria-hidden='true' class='material-icons icon'>arrow_upward</i>"
            );
          $(".sort thead .column")
            .eq(4)
            .addClass("sortable active asc");
          isClick[4] = true;
        }
        this.loadList();
      });

    $(".sort thead .column")
      .eq(5)
      .on("click", () => {
        this.delDom();
        this.sortType = "boutiqueId";
        if (isClick[5]) {
          this.sortNum = 0;
          $(".sort thead .column")
            .eq(5)
            .append(
              "<i aria-hidden='true' class='material-icons icon'>arrow_upward</i>"
            );
          $(".sort thead .column")
            .eq(5)
            .addClass("sortable active asc desc");
          isClick[5] = false;
        } else {
          this.sortNum = 1;
          $(".sort thead .column")
            .eq(5)
            .append(
              "<i aria-hidden='true' class='material-icons icon'>arrow_upward</i>"
            );
          $(".sort thead .column")
            .eq(5)
            .addClass("sortable active asc");
          isClick[5] = true;
        }
        this.loadList();
      });

    $(".sort thead .column")
      .eq(6)
      .on("click", () => {
        this.delDom();
        this.sortType = "errorType";
        if (isClick[6]) {
          this.sortNum = 0;
          $(".sort thead .column")
            .eq(6)
            .append(
              "<i aria-hidden='true' class='material-icons icon'>arrow_upward</i>"
            );
          $(".sort thead .column")
            .eq(6)
            .addClass("sortable active asc desc");
          isClick[6] = false;
        } else {
          this.sortNum = 1;
          $(".sort thead .column")
            .eq(6)
            .append(
              "<i aria-hidden='true' class='material-icons icon'>arrow_upward</i>"
            );
          $(".sort thead .column")
            .eq(6)
            .addClass("sortable active asc");
          isClick[6] = true;
        }
        this.loadList();
      });

    $(".sort thead .column")
      .eq(7)
      .on("click", () => {
        this.delDom();
        this.sortType = "dateField";
        if (isClick[7]) {
          this.sortNum = 0;
          $(".sort thead .column")
            .eq(7)
            .append(
              "<i aria-hidden='true' class='material-icons icon'>arrow_upward</i>"
            );
          $(".sort thead .column")
            .eq(7)
            .addClass("sortable active asc desc");
          isClick[7] = false;
        } else {
          this.sortNum = 1;
          $(".sort thead .column")
            .eq(7)
            .append(
              "<i aria-hidden='true' class='material-icons icon'>arrow_upward</i>"
            );
          $(".sort thead .column")
            .eq(7)
            .addClass("sortable active asc");
          isClick[7] = true;
        }
        this.loadList();
      });

    $(".sort thead .column")
      .eq(8)
      .on("click", () => {
        this.delDom();
        this.sortType = "boutiqueDate";
        if (isClick[8]) {
          this.sortNum = 0;
          $(".sort thead .column")
            .eq(8)
            .append(
              "<i aria-hidden='true' class='material-icons icon'>arrow_upward</i>"
            );
          $(".sort thead .column")
            .eq(8)
            .addClass("sortable active asc desc");
          isClick[8] = false;
        } else {
          this.sortNum = 1;
          $(".sort thead .column")
            .eq(8)
            .append(
              "<i aria-hidden='true' class='material-icons icon'>arrow_upward</i>"
            );
          $(".sort thead .column")
            .eq(8)
            .addClass("sortable active asc");
          isClick[8] = true;
        }
        this.loadList();
      });

    $(".sort thead .column")
      .eq(9)
      .on("click", () => {
        this.delDom();
        this.sortType = "createDate";
        if (isClick[9]) {
          this.sortNum = 0;
          $(".sort thead .column")
            .eq(9)
            .append(
              "<i aria-hidden='true' class='material-icons icon'>arrow_upward</i>"
            );
          $(".sort thead .column")
            .eq(9)
            .addClass("sortable active asc desc");
          isClick[9] = false;
        } else {
          this.sortNum = 1;
          $(".sort thead .column")
            .eq(9)
            .append(
              "<i aria-hidden='true' class='material-icons icon'>arrow_upward</i>"
            );
          $(".sort thead .column")
            .eq(9)
            .addClass("sortable active asc");
          isClick[9] = true;
        }
        this.loadList();
      });
    $(".sort thead .column")
      .eq(10)
      .on("click", () => {
        this.delDom();
        this.sortType = "processDateTime";
        if (isClick[10]) {
          this.sortNum = 0;
          $(".sort thead .column")
            .eq(10)
            .append(
              "<i aria-hidden='true' class='material-icons icon'>arrow_upward</i>"
            );
          $(".sort thead .column")
            .eq(10)
            .addClass("sortable active asc desc");
          isClick[10] = false;
        } else {
          this.sortNum = 1;
          $(".sort thead .column")
            .eq(10)
            .append(
              "<i aria-hidden='true' class='material-icons icon'>arrow_upward</i>"
            );
          $(".sort thead .column")
            .eq(10)
            .addClass("sortable active asc");
          isClick[10] = true;
        }
        this.loadList();
      });
  },
  methods: {
    delDom() {
      let sortDom = $(".sort thead .column");
      for (let i = 0; i < sortDom.length; i++) {
        $(".sort thead .column")
          .eq(i)
          .find("i")
          .empty();
        $(".sort thead .column")
          .eq(i)
          .removeClass("sortable active desc asc");
      }
    },
    sortList(item) {
      console.log(item);
    },
    loadList() {
      this.isLoading = true;
      let data = {
        Date: this.searchData.Date,
        boutique: this.searchData.boutique ? this.searchData.boutique.id : "",
        api: this.searchData.api ? this.searchData.api.id : "",
        errorType: this.searchData.errorType
          ? this.searchData.errorType.id
          : "",
        brandId: this.searchData.brandId,
        color: this.searchData.color,
        apiField: this.searchData.apiField ? this.searchData.apiField : "",
        boutiqueDataId: this.searchData.boutiqueDataId
          ? this.searchData.boutiqueDataId.id
          : "",
        pageSize: this.pageSize,
        pageNum: this.pageNum,
        sortType: this.sortType,
        sortNum: this.sortNum
      };
      getApiErrorPage(data).then(res => {
        this.items = res.data.apiErrorPage.list;
        this.pageLen = [];
        for (let i = 1; i <= res.data.apiErrorPage.totalPage; i++) {
          this.pageLen.push(i);
        }
        this.isLoading = false;
        this.totalRow = res.data.apiErrorPage.totalRow;
      });
    },
    resetLoad() {
      this.searchData = {
        Date: "",
        boutique: {
          id: "",
          name: ""
        },
        api: {
          id: "",
          name: ""
        },
        errorType: {
          id: "",
          name: ""
        },
        apiField: "",
        boutiqueDataId: {
          id: "",
          name: ""
        },
        brandId: "",
        color: ""
      };
      this.vendorList = [
        {
          vendor_id: "",
          vendor_name: "All"
        }
      ];
      this.apiList = [
        {
          api_end_point_id: "",
          name: "All"
        }
      ];
      this.apiErrorTypeList = [
        {
          api_error_type_id: "",
          error_type_name: "All"
        }
      ];

      this.apiFieldList = [
        {
          data_field: "All"
        }
      ];

      this.apiContentList = [
        {
          boutiqueDataId: "",
          boutique_data: "All"
        }
      ];

      this.loadBoutique();
      this.searchArr = [];
      this.loadList();
    },
    delSearch(id) {
      this.searchArr.splice(id, 1);
      switch (id) {
        case 0:
          this.searchData.Date = "";
          $("#date")[0].value = "";
          this.loadList();
          break;
        case 1:
          this.searchData.boutique = {
            id: "",
            name: ""
          };
          this.loadList();
          break;
        case 2:
          this.searchData.api = {
            id: "",
            name: ""
          };
          this.loadList();
          break;
        case 3:
          this.searchData.errorType = {
            id: "",
            name: ""
          };
          this.loadList();
          break;
        case 4:
          this.searchData.apiField = "";
          this.loadList();
          break;
        case 5:
          this.searchData.boutiqueDataId = {
            id: "",
            name: ""
          };
          this.loadList();
          break;
        case 6:
          this.searchData.brandId = "";
          this.loadList();
          break;
        case 7:
          this.searchData.color = "";
          this.loadList();
          break;
      }
    },
    selectPageNum(e) {
      this.pageNum = e;
      this.loadList();
    },
    selectPageSize(e) {
      this.pageSize = e;
      this.loadList();
    },
    prevPage() {
      if (this.pageNum > 1) {
        this.pageNum--;
        this.loadList();
      }
    },
    nextPage() {
      if (this.pageLen.length - 1 > this.pageNum) {
        this.pageNum++;
        this.loadList();
      }
    },
    loadBoutique(vid, pid, etp, field) {
      debugger;
      let data = {
        vid: "",
        pid: "",
        etp: "",
        field: ""
      };
      if (vid && vid !== undefined) {
        data.vid = vid;
      }
      if (pid && pid != undefined) {
        data.pid = pid;
        data.vid = vid;
      }
      if (etp && etp != undefined) {
        data.pid = pid;
        data.vid = vid;
        data.etp = etp;
      }
      if (field && field != undefined) {
        data.pid = pid;
        data.vid = vid;
        data.etp = etp;
        data.field = field;
      }
      console.log(data);
      this.isLoading = true;
      searchApiError(data).then(res => {
        console.log(res);
        if (res.data.vendorList) {
          this.vendorList = res.data.vendorList;
          this.vendorList.unshift({ vendor_id: "", vendor_name: "All" });
        } else {
          this.vendorList = [{ vendor_id: "", vendor_name: "All" }];
        }
        if (res.data.apiList) {
          this.apiList = res.data.apiList;
          this.apiList.unshift({ api_end_point_id: "", name: "All" });
        } else {
          this.apiList = [{ api_end_point_id: "", name: "All" }];
        }

        if (res.data.apiErrorTypeList) {
          this.apiErrorTypeList = res.data.apiErrorTypeList;
          this.apiErrorTypeList.unshift({
            api_error_type_id: "",
            error_type_name: "All"
          });
        } else {
          this.apiErrorTypeList = [
            { api_error_type_id: "", error_type_name: "All" }
          ];
        }

        if (res.data.apiFieldList) {
          this.apiFieldList = res.data.apiFieldList;
          this.apiFieldList.unshift({ data_field: "All" });
        } else {
          this.apiFieldList = [{ data_field: "All" }];
        }

        if (res.data.apiContentList) {
          this.apiContentList = res.data.apiContentList;
          this.apiContentList.unshift({
            boutiqueDataId: "",
            boutique_data: "All"
          });
        } else {
          this.apiContentList = [{ boutiqueDataId: "", boutique_data: "All" }];
        }

        setTimeout(() => {
          $("select").material_select();
          this.isLoading = false;
        }, 100);
      });
    },
    searchList() {
      this.searchArr = [];
      this.searchArr.push(
        { id: 0, name: this.searchData.Date },
        {
          id: 1,
          name: this.searchData.boutique ? this.searchData.boutique.name : ""
        },
        {
          id: 2,
          name: this.searchData.api ? this.searchData.api.name : ""
        },
        {
          id: 3,
          name: this.searchData.errorType ? this.searchData.errorType.name : ""
        },
        { id: 4, name: this.searchData.apiField },
        {
          id: 5,
          name: this.searchData.boutiqueDataId
            ? this.searchData.boutiqueDataId.name
            : ""
        },
        { id: 6, name: this.searchData.brandId },
        {
          id: 7,
          name: this.searchData.color
        }
      );
      console.log(this.searchArr);
      this.pageNum = 1;
      this.loadList();
    },
    AllProcess() {
      if (this.selected.length <= 0) {
        Materialize.toast("请至少选择一个", 4000);
        return;
      }
      let data = [];
      for (let i in this.selected) {
        data.push(this.selected[i].api_error_processing_id);
      }
      this.isLoading = true;
      processing(data).then(res => {
        this.showShade = false;
        this.isLoading = false;
        this.errorAlert = false;
        if (res.data.status === 1) {
          Materialize.toast("处理成功", 4000);
          this.loadList();
        } else {
          Materialize.toast("处理失败", 4000);
        }
        this.selected = [];
      });
    },
    AllRemove() {
      if (this.selected.length <= 0) {
        Materialize.toast("请至少选择一个", 4000);
        return;
      }
      let data = [];
      for (let i in this.selected) {
        data.push(this.selected[i].api_error_processing_id);
      }
      this.isLoading = true;
      remove(data).then(res => {
        this.isLoading = false;
        if (res.data.status === 1) {
          Materialize.toast("处理成功", 4000);
          this.loadList();
        } else {
          Materialize.toast("处理失败", 4000);
        }
        this.selected = [];
      });
    },
    ShowError(aid, eid, name) {
      this.isLoading = true;
      this.selected = [];
      selectShowArr(aid).then(res => {
        this.isLoading = false;
        this.errorDate = {
          errorId: eid,
          Boutique: name,
          errorText: res.data
        };
        console.log(this.errorDate);
        this.errorDate.errorText = this.formatJson(this.errorDate.errorText);
        this.selected.push({ api_error_processing_id: aid });
        this.showShade = true;
        this.errorAlert = true;
      });
    },
    formatJson(json, options) {
      if (json === undefined) {
        return;
      }
      let reg = null,
        formatted = "",
        pad = 0,
        PADDING = "    ";
      options = options || {};
      options.newlineAfterColonIfBeforeBraceOrBracket =
        options.newlineAfterColonIfBeforeBraceOrBracket === true ? true : false;
      options.spaceAfterColon =
        options.spaceAfterColon === false ? false : true;
      if (typeof json !== "string") {
        json = JSON.stringify(json);
      } else {
        json = JSON.parse(json);
        json = JSON.stringify(json);
      }
      reg = /([\{\}])/g;
      json = json.replace(reg, "\r\n$1\r\n");
      reg = /([\[\]])/g;
      json = json.replace(reg, "\r\n$1\r\n");
      reg = /(\,)/g;
      json = json.replace(reg, "$1\r\n");
      reg = /(\r\n\r\n)/g;
      json = json.replace(reg, "\r\n");
      reg = /\r\n\,/g;
      json = json.replace(reg, ",");
      if (!options.newlineAfterColonIfBeforeBraceOrBracket) {
        reg = /\:\r\n\{/g;
        json = json.replace(reg, ":{");
        reg = /\:\r\n\[/g;
        json = json.replace(reg, ":[");
      }
      if (options.spaceAfterColon) {
        reg = /\:/g;
        json = json.replace(reg, ":");
      }
      json.split("\r\n").forEach(function(node, index) {
        let i = 0,
          indent = 0,
          padding = "";
        if (node.match(/\{$/) || node.match(/\[$/)) {
          indent = 1;
        } else if (node.match(/\}/) || node.match(/\]/)) {
          if (pad !== 0) {
            pad -= 1;
          }
        } else {
          indent = 0;
        }
        for (i = 0; i < pad; i++) {
          padding += PADDING;
        }
        formatted += padding + node + "\r\n";
        pad += indent;
      });
      return formatted;
    }
  },
  components: {
    Multiselect,
    shade,
    loading
  }
};
</script>
<style lang="less">
@import "../../../node_modules/flatpickr/dist/flatpickr.min.css";
@import "../../assets/css/googlefont.css";
@import "../../../node_modules/vuetify/dist/vuetify.min.css";

.btn {
  padding: 0;
}

.head-search .select-wrapper input.select-dropdown {
  border-bottom: 2px solid #871b55;
}

.toast {
  position: relative;
}
</style>
<style lang="less" scoped>
@import "../../../node_modules/vue-multiselect/dist/vue-multiselect.min.css";

input[type="text"] {
  border-bottom: 2px solid #871b55;
  font-size: 16px;
}

input[type="text"].valid {
  box-shadow: none;
}

.input-field .prefix.active {
  color: #4a4a4a;
}

.input-field {
  margin-top: 1rem;
  margin-bottom: 1rem;
}

.input-field label {
  left: 0;
}

input[type="text"]:focus:not([readonly]),
input[type="text"].invalid {
  border-bottom: 2px solid #871b55;
  box-shadow: none;
  & + label {
    color: rgba(0, 0, 0, 0.38);
    left: 0;
  }
}

.head-search {
  display: flex;
  .input-field {
    margin-right: 8px;
    &:nth-child(1) {
      width: 250px;
      [readonly="readonly"] {
        color: #000;
        margin-left: 3rem;
      }
    }
    &:nth-child(2) {
      width: 145px;
    }
    &:nth-child(3) {
      width: 125px;
    }
    &:nth-child(4) {
      width: 177px;
    }
    &:nth-child(5) {
      width: 152px;
    }
    &:nth-child(6) {
      width: 84px;
    }
  }
  .btn {
    height: 36px;
    width: 93px;
    text-align: center;
    line-height: 36px;
    border-radius: 2px;
    box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
    margin-top: 16px;
    &:nth-child(7) {
      background-color: #871b55;
      margin-left: 22px;
    }
    &:nth-child(8) {
      background-color: #9e9e9e;
      margin-left: 16px;
    }
  }
}

.search-tag {
  display: flex;
  flex-wrap: wrap;
  div {
    height: 32px;
    line-height: 32px;
    border-radius: 16px;
    background-color: rgba(153, 153, 153, 0.2);
    padding: 0 16px;
    color: rgba(0, 0, 0, 0.54);
    margin: 0 20px 10px 0;
    span {
      float: left;
    }
    i {
      float: right;
      font-size: 20px;
      color: #9b9b9b;
      position: relative;
      top: -1px;
      left: 5px;
      cursor: pointer;
    }
  }
}

.main-cent {
  width: 100%;
  background-color: #fafafa;
  box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
  .head-btn {
    position: relative;
    padding: 24px;
    button {
      height: 36px;
      width: 122.29px;
      border-radius: 2px;
      background-color: #871b55;
      box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.12), 0 2px 2px 0 rgba(0, 0, 0, 0.24);
      &:nth-child(2) {
        position: absolute;
        right: 24px;
        background-color: #9b9b9b;
      }
    }
  }
}

.alert-error {
  height: 582.63px;
  width: 572.5px;
  position: fixed;
  left: 50%;
  top: 50%;
  z-index: 7;
  padding: 24px;
  border-radius: 2px;
  background-color: #ffffff;
  box-shadow: 0 0 24px 0 rgba(0, 0, 0, 0.22), 0 24px 24px 0 rgba(0, 0, 0, 0.3);
  margin: -582.63px/2 0 0 -572.5px/2;
  .tit {
    font-size: 20px;
  }
  .error-info {
    font-size: 16px;
    color: rgba(0, 0, 0, 0.54);
    margin: 20px 0;
    span {
      margin-right: 30px;
    }
  }
  .error-text pre {
    width: 100%;
    height: 400px;
    overflow: auto;
  }
  .error-text pre::-webkit-scrollbar {
    width: 10px;
    height: 10px;
  }

  .error-text pre::-webkit-scrollbar-thumb {
    background-color: #c1c1c1;
    border-radius: 5px;
  }

  .error-text pre:hover::-webkit-scrollbar-thumb {
    background-color: #959595;
  }

  .error-text pre::-webkit-scrollbar-thumb:hover {
    background-color: #959595;
  }

  .error-text pre::-webkit-scrollbar-thumb:active {
    background-color: #757575;
  }
  .foot-btn {
    position: absolute;
    bottom: 17px;
    color: #871b55;
    right: 35px;
    span {
      margin-left: 48px;
      cursor: pointer;
    }
  }
}

.foot-page {
  display: flex;
  float: right;
  margin-right: 20px;
  padding: 16px 0 30px 0;
  .input-field {
    width: 70px;
  }
  span {
    font-size: 14px;
    position: relative;
    top: 25px;
    margin-right: 6px;
    color: #909090;
  }
  .page {
    display: flex;
  }
  .rows-page {
    display: flex;
    margin-left: 30px;
  }
  .event-page {
    margin-left: 30px;
    display: flex;
    color: #909090;
    i {
      position: relative;
      top: 16px;
      font-size: 22px;
      cursor: pointer;
      &:hover {
        color: #000;
      }
      &:nth-child(3) {
        margin-left: 20px;
      }
    }
  }
}
</style>
<style lang="less">
.multiselect {
  width: 100px;
  .multiselect__tags {
    background: none;
    border: none;
  }
  .multiselect__select {
    top: 13px;
  }
}

.main-cent {
  table.table tbody td:first-child,
  table.table tbody td:not(:first-child),
  table.table tbody th:first-child,
  table.table tbody th:not(:first-child),
  table.table thead td:first-child,
  table.table thead td:not(:first-child),
  table.table thead th:first-child,
  table.table thead th:not(:first-child) {
    padding: 0 10px;
  }
}

.head-search .input-field .dropdown-content {
  width: inherit !important;
}
</style>
