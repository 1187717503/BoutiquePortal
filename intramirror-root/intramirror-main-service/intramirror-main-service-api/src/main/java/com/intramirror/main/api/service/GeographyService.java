package com.intramirror.main.api.service;


import com.intramirror.main.api.model.Geography;

import java.util.List;
import java.util.Map;

public interface GeographyService {

    List<Map<String,Object>> getGeographyList();

    Geography getGeographyById(Long geographyId);

    List<Geography> getGeographyGroupList();
}
