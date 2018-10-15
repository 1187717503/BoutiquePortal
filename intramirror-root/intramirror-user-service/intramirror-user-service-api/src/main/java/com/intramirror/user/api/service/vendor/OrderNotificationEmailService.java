package com.intramirror.user.api.service.vendor;

import com.intramirror.user.api.model.OrderNotificationEmail;

import java.util.List;

public interface OrderNotificationEmailService {

    List<OrderNotificationEmail> selectByVendorId(Long vendorId);

    void modify(OrderNotificationEmail orderNotificationEmail);

    void create(OrderNotificationEmail orderNotificationEmail);
}
