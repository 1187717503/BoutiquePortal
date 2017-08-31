package com.intramirror.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dingyifan on 2017/8/31.
 */
public class DateUtils {

    public static String getStrDate(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }
}
