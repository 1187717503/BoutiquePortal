package pk.shoplus.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by dingyifan on 2017/8/31.
 */
public class DateUtils {

    public static String getStrDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }

    public static String getStrDate(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static String getTimeByHour(int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - hour);
        return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
    }

    public static String getTimeByMINUTE(int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - minute);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
    }

    public static Date getDateByStr(String format, String date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.parse(date);
    }

    public static String getformatDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    public static String getDateByString(String date) {
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
     * date1 > date2 return true date1日期在后,date2日期在前
     * date1 < date2 return false
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean compareDate(Date date1, Date date2) {
        if (date1.getTime() > date2.getTime() || date1.getTime() == date2.getTime()) {
            System.out.println(date1 + "---" + date2 + "true");
            return true;
        }
        System.out.println(date1 + "---" + date2 + "false");
        return false;
        /*Sun Sep 24 10:54:09 CST 2017---Sun Sep 24 10:54:10 CST 2017
        false*/
    }
}
