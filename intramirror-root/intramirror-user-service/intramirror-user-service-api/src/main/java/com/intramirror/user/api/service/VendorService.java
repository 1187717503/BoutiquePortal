package com.intramirror.user.api.service;


import com.intramirror.user.api.model.Vendor;
import com.intramirror.user.api.model.VendorApplication;

public interface VendorService {

    VendorApplication getVendorApplicationByUserId(Long userId) throws Exception;

    Vendor getVendorByUserId(Long userId) throws Exception;

}
