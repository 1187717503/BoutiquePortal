/**
 * Created by Likun on 2017/8/2.
 * Email 1350612656@qq.com
 */
import HTTP from '../http'

export function getOrderCount() { // 获取头部Num
  return HTTP({
    url: 'order/getOrderCount.htm',
  })
}

export function getOrderList(data) { // 获取列表
  return HTTP({
    url: 'order/getOrderList.htm',
    method: 'post',
    data: data
  })
}
export function getOrderDetail(data) { //打印订单
  return HTTP({
    url: 'order/getOrderDetail.htm',
    method: 'post',
    data: data
  })
}

export function orderRefund(data) { //取消订单
  return HTTP({
    url: 'order/orderRefund.htm',
    method: 'post',
    data: data
  })
}

export function updateOrderStatus(data) { //更改订单状态
  return HTTP({
    url: 'order/updateOrderStatus.htm',
    method: 'post',
    data: data
  })
}

export function getBarcode() { //生成箱子code
  return HTTP({
    url: 'container/getBarcode.htm'
  })
}

export function saveContainerApi(data) { //新建箱子
  return HTTP({
    url: 'container/saveContainer.htm',
    method: 'post',
    data: data
  })
}

export function updateContainerBybarcode(data) { //修改箱子
  return HTTP({
    url: 'container/updateContainerBybarcode.htm',
    method: 'post',
    data: data
  })
}

export function packingCheckOrder(data) { //往箱子里添加商品
  return HTTP({
    url: 'order/packingCheckOrder.htm',
    method: 'post',
    data: data
  })
}

export function deleteContainerById(data) { //删除箱子
  return HTTP({
    url: 'container/deleteContainerById.htm',
    method: 'post',
    data: data
  })
}

export function deletePackingCheckOrder(data) { //删除箱子里的商品
  return HTTP({
    url: 'order/deletePackingCheckOrder.htm',
    method: 'post',
    data: data
  })
}

export function printBarcode(data) { //生成条形码
  return HTTP({
    url: 'container/printBarcode.htm',
    method: 'post',
    data: data
  })
}

export function getReadyToShipCartonList(data) { //获取to shop 列表
  return HTTP({
    url: 'orderShip/getReadyToShipCartonList.htm',
    method: 'post',
    data: data
  })
}

export function getShipmentInfo(data) { //获取to shop 详情列表
  return HTTP({
    url: 'orderShip/getShipmentInfo.htm',
    method: 'post',
    data: data
  })
}

export function getShippedList(data) { //获取shipped列表
  return HTTP({
    url: 'shipped/getShippedList.htm',
    method: 'post',
    data: data
  })
}

export function getGeography(data) { //初始化地区
  return HTTP({
    url: 'orderShip/getGeography.htm',
    method: 'post',
    data: data
  })
}

export function updateShipmentById(data) { //修改Shipment状态
  return HTTP({
    url: 'shipment/updateShipmentById.htm',
    method: 'post',
    data: data
  })
}

export function addInvoice(data) { //addInvoice
  return HTTP({
    url: 'invoice/addInvoice.htm',
    method: 'post',
    data: data
  })
}

export function printPackingList(data) { //printPackingList
  return HTTP({
    url: 'orderShip/printPackingList.htm',
    method: 'post',
    data: data
  })
}

export function printShipmentInfo(data) { //获取打印Shipment数据
  return HTTP({
    url: 'orderShip/printShipmentInfo.htm',
    method: 'post',
    data: data
  })
}

export function newShipment(data) { //newShipment
  return HTTP({
    url: 'shipment/newShipment.htm',
    method: 'post',
    data: data
  })
}

export function updateContainerStatus(data) { //修改箱子状态
  return HTTP({
    url: 'container/updateContainerStatus.htm',
    method: 'post',
    data: data
  })
}

export function getShipmentNo(data) { //获取箱子信息
  return HTTP({
    url: 'container/getShipmentNo.htm',
    method: 'post',
    data: data
  })
}

export function confirmCheckOrder(data) {
  return HTTP({
    url: 'order/confirmCheckOrder.htm',
    method: 'post',
    data: data
  })
}

export function getContainerBybarcode(data) {
  return HTTP({
    url: 'container/getContainerBybarcode.htm',
    method: 'post',
    data: data
  })
}

export function getPackOrderList(data) {
  return HTTP({
    url: 'order/getPackOrderList.htm',
    method: 'post',
    data: data
  })
}

export function saveUserComment(data) {
  return HTTP({
    url: 'order/saveUserComment.htm',
    method: 'post',
    data: data
  })
}

export function getAllByShipmentId(data) {
  return HTTP({
    url: 'container/getAllByShipmentId.htm',
    method: 'post',
    data: data
  })
}

export function updateAllContainer(data) {
  return HTTP({
    url: 'container/updateAllContainer.htm',
    method: 'post',
    data: data
  })
}

export function getExceptionType() {
  return HTTP({
    url: 'order/getExceptionType.htm'
  })
}
