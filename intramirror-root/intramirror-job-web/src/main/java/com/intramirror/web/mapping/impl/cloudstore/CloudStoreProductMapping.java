package com.intramirror.web.mapping.impl.cloudstore;

import com.intramirror.web.mapping.api.IProductMapping;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import pk.shoplus.model.ProductEDSManagement;

import java.util.Map;

@Service(value = "cloudStoreProductMapping")
public class CloudStoreProductMapping implements IProductMapping {

    private static Logger logger = Logger.getLogger(CloudStoreProductMapping.class);

    @Override
    public ProductEDSManagement.ProductOptions mapping(Map<String,Object> bodyDataMap) {

        return null;
    }
}
