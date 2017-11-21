package com.intramirror.product.api.service.content;

import java.util.List;
import java.util.Map;

/**
 * Created on 2017/11/21.
 *
 * @author YouFeng.Zhu
 */
public interface ContentManagementService {
    List<Map<String, Object>> listTagProductInfo(Long tagId);
}
