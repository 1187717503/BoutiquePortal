package com.intramirror.common.help;

import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/8/15.
 */
public interface IPageService {

    List<Map<String,Object>> getResult(Map<String,Object> params);

}
