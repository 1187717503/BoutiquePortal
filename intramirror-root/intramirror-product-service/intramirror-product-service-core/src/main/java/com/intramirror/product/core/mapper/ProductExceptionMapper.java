/**
 *
 */
package com.intramirror.product.core.mapper;

import java.util.List;
import java.util.Map;

/**
 * @author Shang jian
 */
public interface ProductExceptionMapper {

    int saveProductException(Map<String, Object> map);

    int updateProductException(Map<String, Object> map);

    List<Map<String, Object>> selectByProductAndSkuId(Map<String, Object> map);
}
