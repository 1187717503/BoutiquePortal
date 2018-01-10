/**
 * Created by Likun on 2017/8/24.
 * Email 1350612656@qq.com
 */
import HTTP from '../http'

let adminUrl = process.env.ERROR_URL;
let adminUrl2 = process.env.ERROR_URL2;

export function getApiErrorPage(data) {
  if (data.Date) {
    data.Date = data.Date.split('~')
  } else {
    data.Date = ['', '']
  }

  return HTTP({
    baseURL: adminUrl,
    url: 'errormessage/getApiErrorPage?startDate=' + data.Date[0] + '&endDate=' + data.Date[1] + '&boutiqueIds=' + data.boutique + '&apis=' + data.api + '&errorTypes=' + data.errorType + '&apiField=' + data.apiField + '&boutiqueDataId=' + data.boutiqueDataId + '&brandIds=' + data.brandId + '&colors=' + data.color + '&pageNumber=' + data.pageNum + '&pageSize=' + data.pageSize + '&param=' + data.sortType + '&num=' + data.sortNum,
    method: 'post'
  })
}

export function searchApiError(data) {
  return HTTP({
    baseURL: adminUrl,
    url: 'errormessage/searchApiError?vendorId=' + (data.vid?data.vid:'') + '&apiEndPointId=' + (data.pid?data.pid:'') + '&apiErrorTypeId=' + (data.etp?data.etp:'')+ '&apiField=' + (data.field?data.field:''),
    method: 'post'
  })
}

export function selectShowArr(id) {
  return HTTP({
    baseURL: adminUrl2,
    url: 'errormessage/select?api_error_processing_id=' + id
  })
}

export function processing(data) {
  return HTTP({
    baseURL: adminUrl2,
    url: 'errormessage/processing?data=' + data
  })
}

export function remove(data) {
  return HTTP({
    baseURL: adminUrl,
    url: 'errormessage/remove?data=' + data,
    method: 'post'
  })
}
