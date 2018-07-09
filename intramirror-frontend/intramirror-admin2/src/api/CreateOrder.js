import HTTP from '../http'

export function get_product(data) {
  return HTTP({
    url: 'order/get_product.htm',
    method: 'post',
    data: data
  })
}

export function get_address(data) {
  return HTTP({
    url: 'order/get_address.htm',
    method: 'post',
    data: data
  })
}

export function product_get_skuStore(data) {
  return HTTP({
    url: 'order/product_get_skuStore.htm',
    method: 'post',
    data: data
  })
}

export function get_product_detail(data) {
  return HTTP({
    url: 'order/get_product_detail.htm',
    method: 'post',
    data: data
  })
}

export function get_fee(data) {
  return HTTP({
    url: 'order/get_fee.htm',
    method: 'post',
    data: data
  })
}

export function input_create_order(data) {
  return HTTP({
    url: 'order/input_create_order.htm',
    method: 'post',
    data: data
  })
}
