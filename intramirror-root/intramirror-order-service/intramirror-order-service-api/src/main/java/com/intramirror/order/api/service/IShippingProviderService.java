/**
 *
 */
package com.intramirror.order.api.service;

import com.intramirror.order.api.model.ShippingProvider;

/**
 * @author yuan
 */
public interface IShippingProviderService {

    ShippingProvider getShippingProviderByShipmentId(Long shipmentId);

    ShippingProvider getShippingProviderById(Long id);

    /**
     * 获取买手店发往中国大陆的第一段物流供应商
     * @param vendorId
     * @return
     */
    ShippingProvider getShippingProviderByVendorId(Long vendorId);
}
