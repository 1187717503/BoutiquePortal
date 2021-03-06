package com.intramirror.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang.time.DateFormatUtils;

/**
 * Created by dingyifan on 2017/8/31.
 */
public class DateUtils {

    public static String getStrDate(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }

    public static String getStrDate(Date date,String format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }
    
	public static String getTimeByHour(int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - hour);
        return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
    }

    public static String getTimeByMinute(int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - minute);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
    }

    public static Date getDateByStr(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getTimeByMinuteGMT(int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - minute);
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+08:00").format(calendar.getTime());
    }

    public static void main(String[] args) {
        Calendar start = Calendar.getInstance();

        Calendar end = Calendar.getInstance();
        end.add(Calendar.MINUTE,1);

        String startTime = DateFormatUtils.format(start.getTime(),"yyyy-MM-dd HH:mm:00");
        String endTime = DateFormatUtils.format(end.getTime(),"yyyy-MM-dd HH:mm:00");

        System.out.println(startTime);
        System.out.println(endTime);
    }

    public static String getformatDate(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    public static String getDateByString(String date){
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date formatDate = null;
        try {
            formatDate = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return simpleDateFormat2.format(formatDate);
    }

    /**
     * date1 > date2 return true
     * date1 < date2 return false
     * @param date1
     * @param date2
     * @return
     */
    public static boolean compareDate(Date date1,Date date2){
        System.out.println(date1+"---"+date2);
        if(date1.getTime() > date2.getTime()) {
            return true;
        }
        return false;
        /*Sun Sep 24 10:54:09 CST 2017---Sun Sep 24 10:54:10 CST 2017
        false*/
    }

    public static int getDatePool4Hour(Date fromDate,Date toDate){
        long from = fromDate.getTime();
        long to = toDate.getTime();
        int hours = (int) ((to - from)/(1000 * 60 * 60));
        return hours;
    }
}
