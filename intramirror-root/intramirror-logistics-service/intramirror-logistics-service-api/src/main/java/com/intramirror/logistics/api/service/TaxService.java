package com.intramirror.logistics.api.service;

import com.intramirror.logistics.api.model.Tax;

public interface TaxService {
    Tax getTaxByAddressCountryId(Long addressCountryId);
}
