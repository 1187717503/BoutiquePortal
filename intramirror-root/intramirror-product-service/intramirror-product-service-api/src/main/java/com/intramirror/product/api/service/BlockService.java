package com.intramirror.product.api.service;

import com.intramirror.product.api.model.Block;

/**
 * Created on 2017/11/17.
 *
 * @author YouFeng.Zhu
 */
public interface BlockService {
    int insert(Block record);

    int updateByBlockId(Block record);

}
