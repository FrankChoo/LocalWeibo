package app.demo.weibotestdemo.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 99538 on 2017/6/17.
 */

public class DateUtils {

    private static DateUtils sInstance;
    private String mNowDate;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;


    public static DateUtils getInstance() {
        if (sInstance == null) {
            synchronized(DateUtils.class) {
                if(sInstance == null) {
                    sInstance = new DateUtils();
                }
            }
        }
        return sInstance;
    }

    private DateUtils() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        mNowDate = sdf.format(date);
        mYear = Integer.parseInt(mNowDate.substring(0, 4));
        mMonth = Integer.parseInt(mNowDate.substring(5, 7));
        mDay = Integer.parseInt(mNowDate.substring(8, 10));
        mHour = Integer.parseInt(mNowDate.substring(11, 13));
        mMinute = Integer.parseInt(mNowDate.substring(14, 16));
    }

    public String getNowDate() {
        return mNowDate;
    }


    /**
     * @param currentDate 该参数为用户传入的精确的日期数据, 通过与当前的日期进行对比, 得出更为人性化的日期数据
     */
    public String formatDate(String currentDate) {
        String formatDate = "";
        int year = Integer.parseInt(currentDate.substring(0, 4));
        int month = Integer.parseInt(currentDate.substring(5, 7));
        int day = Integer.parseInt(currentDate.substring(8, 10));
        String hour = currentDate.substring(11, 13);
        String minute = currentDate.substring(14, 16);
        if (mYear == year && mMonth == month) {
            if (mDay - day == 0) {
                formatDate = "今天" + " " + hour + ":" + minute;
            } else if (mDay - day == 1) {
                formatDate = "昨天" + " " + hour + ":" + minute;
            }
        } else {
            formatDate = year + "-" + month + "-" + day;
        }
        return formatDate;
    }

}
