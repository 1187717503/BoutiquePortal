/**
 * Created by 1 on 2017/7/19.
 */
import HTTP from '../http'
let apiUrl = process.env.API_URL;
export function queryRuleByHasSeason(id, vendor, type, categoryType) {
  return HTTP({
    url: 'rule/select/queryRuleByHasSeason.htm?ruleStatus=' + id + '&vendor_id=' + vendor + '&price_type=' + type + '&categoryType=' + categoryType
  })
}

export function queryRuleVendor(type,categoryType) {
  return HTTP({
    url: 'vendor/select/queryRuleVendor.htm?status=2&price_type='+type + '&categoryType=' + categoryType
  })
}

export function queryRuleByNotHasSesaon(id, type, categoryType) {
  return HTTP({
    url: 'rule/select/queryRuleByNotHasSesaon.htm?ruleStatus=1&vendor_id=' + id + '&price_type=' + type + '&categoryType=' + categoryType
  })
}

export function queryRuleByGroup(id) {
  return HTTP({
    url: 'rule/select/queryRuleByGroup.htm?price_change_rule_id=' + id
  })
}

export function queryRuleByProduct(id) {
  return HTTP({
    url: 'rule/select/queryRuleByProduct.htm?price_change_rule_id=' + id
  })
}

export function queryRuleByBrandZero(id) {
  return HTTP({
    url: 'rule/select/queryRuleByBrandZero.htm?price_change_rule_id=' + id
  })
}

export function selectActiveBrands(categoryType) {
  return HTTP({
    url: 'brand/selectActiveBrands.htm?categoryType='+categoryType
  })
}
export function selectActiveCategorys() {
  return HTTP({
    url: 'category/selectActiveCategorys.htm'
  })
}

export function productGroup(id) {
  return HTTP({
    url: 'productGroup/list.htm',
    method: 'post',
    data: {
      "group_type": 1,
      "vendor_id": id
    }
  })
}
export function searchBrandZero(id, val) {
  return HTTP({
    url: 'rule/select/queryRuleByBrandZero.htm?price_change_rule_id=' + id + '&english_name=' + val
  })
}
export function queryNotRuleByBrand(id, val) {
  return HTTP({
    url: 'rule/select/queryNotRuleByBrand.htm?price_change_rule_id=' + id + '&english_name=' + val
  })
}

export function queryRuleByBrandOne(id) {
  return HTTP({
    url: 'rule/select/queryRuleByBrandOne.htm?price_change_rule_id=' + id
  })
}
export function queryAllVendor() {
  return HTTP({
    url: 'vendor/select/queryAllVendor.htm'
  })
}
//创建
export function initPriceChangeRule(data,categoryType) {
  data.categoryType = categoryType;
  return HTTP({
    url: 'priceChangeRule/initPriceChangeRule.htm',
    method: 'post',
    data: data
  })
}
//修改
export function updatePriceChangeRuleCategoryBrand(data) {
  return HTTP({
    url: 'priceChangeRule/updatePriceChangeRuleCategoryBrand.htm',
    method: 'post',
    data: data
  })
}
//删除

export function deletePriceChangeRuleCategoryBrandBatch(data) {
  return HTTP({
    url: 'priceChangeRule/deletePriceChangeRuleCategoryBrandBatch.htm',
    method: 'post',
    data: data
  })
}
//创建
export function createPriceChangeRuleCategoryBrandBatch(data) {
  return HTTP({
    url: 'priceChangeRule/createPriceChangeRuleCategoryBrandBatch.htm',
    method: 'post',
    data: data
  })
}

//创建
export function createPriceChangeRuleCategoryBrand(data) {
  return HTTP({
    url: 'priceChangeRule/createPriceChangeRuleCategoryBrand.htm',
    method: 'post',
    data: data
  })
}
//删除
export function deletePriceChangeRuleCategoryBrand(data) {
  return HTTP({
    url: 'priceChangeRule/deletePriceChangeRuleCategoryBrand.htm',
    method: 'post',
    data: data
  })
}

//删除
export function deletePriceChangeRuleGroup(data) {
  return HTTP({
    url: 'priceChangeRule/deletePriceChangeRuleGroup.htm',
    method: 'post',
    data: data
  })
}
export function activeVendor(id, val, to) {
  return HTTP({
    url: 'rule/copy/activeVendor.htm?vendor_id=' + id + '&discount=' + val + '&to_vendor_id=' + to
  })
}
export function pengingVendor(val, to) {
  return HTTP({
    url: 'rule/copy/pengingVendor.htm?discount=' + val + '&to_vendor_id=' + to
  })
}

export function createPriceChangeRuleProduct(data) {
  return HTTP({
    url: 'priceChangeRule/createPriceChangeRuleProduct.htm',
    method: 'post',
    data: data
  })
}

export function deletePriceChangeRuleProduct(data) {
  return HTTP({
    url: 'priceChangeRule/deletePriceChangeRuleProduct.htm',
    method: 'post',
    data: data
  })
}

export function createPriceChangeRuleProductGroup(data) {
  return HTTP({
    url: 'priceChangeRule/createPriceChangeRuleProductGroup.htm',
    method: 'post',
    data: data
  })
}

export function copyseasonVendor(ruleid, code, id,type) {
  return HTTP({
    url: 'rule/copy/seasonVendor.htm?price_change_rule_id=' + ruleid + '&seasons=' + code + '&vendor_id=' + id+'&price_type='+type
  })
}

export function deletePriceChangeRule(data) {
  return HTTP({
    url: 'priceChangeRule/deletePriceChangeRule.htm',
    method: 'post',
    data: data
  })
}
export function updatePriceChangeRule(data,categoryType) {
  data.categoryType = categoryType;
  return HTTP({
    url: 'priceChangeRule/updatePriceChangeRule.htm',
    method: 'post',
    data: data
  })
}

export function updateDefaultDiscount(data) {
  return HTTP({
    url: 'priceChangeRule/updateDefaultDiscount.htm',
    method: 'post',
    data: data
  })
}
export function getRuleDate(id) {
  return HTTP({
    url: '/priceChangeRule/select.htm?price_change_rule_id=' + id
  })
}

export function selectDefaultDiscount() {
  return HTTP({
    url: 'priceChangeRule/selectDefaultDiscount.htm'
  })
}
export function copyRule( vendouid, type, discount = 0) {
  return HTTP({
    url: 'rule/copy/copyRule.htm?vendor_id=' + vendouid + '&price_type=' + type + '&discount=' + discount
  })
}

export function changePreviewStatus(price_id,priview_status,categoryType) {
  return HTTP({
    url: 'priceChangeRule/changepreview.htm?price_change_rule_id='+price_id+'&preview_status='+priview_status + '&categoryType='+categoryType
  })
}

export function imActiveRefresh(priceId, categoryType) {
  return HTTP({
    url: 'priceChangeRule/run/im.htm?price_change_rule_id=' + priceId + '&categoryType=' + categoryType
  })
}

export function boutiqueActiveRefresh(priceId) {
  return HTTP({
    url: 'priceChangeRule/run/boutique.htm?price_change_rule_id='+ priceId
  })
}

export function uploadFileApi(price_id,data){
  return HTTP({
    url: 'rule/upload.htm?price_change_rule_id='+price_id,
    data:data,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    method:'post'
  })
}

export function downFileApi(price_id){
  const downloadUrl = process.env.BASE_URL + 'rule/download.htm?price_change_rule_id='+price_id;
  window.open(downloadUrl);
}