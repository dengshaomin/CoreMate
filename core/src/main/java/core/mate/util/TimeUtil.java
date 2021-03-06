package core.mate.util;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.text.format.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 本类是封装了对时间操作的静态工具类
 *
 * @author DrkCore
 * @since 2015年7月18日23:43:03
 */
public final class TimeUtil extends DateUtils {

    private TimeUtil() {
    }

	/* 静态常量 */

    /**
     * 后天之后
     */
    private static final int DAYS_LATER = 4;
    /**
     * 后天
     */
    private static final int DAY_AFTER_TOMORROW = 3;
    /**
     * 明天
     */
    private static final int DAY_TOMORROW = 2;
    /**
     * 今天
     */
    private static final int DAY_TODAY = 1;
    /**
     * 未知时间
     */
    private static final int DAY_UNKNOWN = 0;
    /**
     * 昨天
     */
    private static final int DAY_YESTERDAY = -1;
    /**
     * 前天
     */
    private static final int DAY_BEFORE_YESTERDAY = -2;
    /**
     * 前天之前
     */
    private static final int DAYS_FARTHER = -3;

	/* 判断时间 */

    private static Locale locale = Locale.getDefault();

    public static Locale getLocale() {
        return locale;
    }

    public static void setLocale(Locale locale) {
        TimeUtil.locale = locale;
    }

    private volatile static LruCache<String, DateFormat> dateFormatLruCache;

    public static DateFormat getDateFormat(String pattern) {
        if (dateFormatLruCache == null) {
            synchronized (TimeUtil.class) {
                if (dateFormatLruCache == null) {
                    dateFormatLruCache = new LruCache<>(4);
                }
            }
        }
        DateFormat format = dateFormatLruCache.get(pattern);
        if (format == null) {
            format = new SimpleDateFormat(pattern, locale);
            dateFormatLruCache.put(pattern, format);
        }
        return format;
    }

    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm";

    /**
     * 使用{@link #DEFAULT_PATTERN}样式来格式化时间
     *
     * @param time
     * @return
     */
    public static String format(long time) {
        return format(time, null);
    }

    /**
     * 按照时间格式给出一个时间的描述。比如“2014年8月17日 09:50”。
     * 默认使用{@link Locale#getDefault()}作为时区。
     *
     * @param time
     * @param pattern 时间的格式。<br>
     *                如果为null，则默认使用{@link #DEFAULT_PATTERN}
     * @return
     */
    public static String format(long time, @Nullable String pattern) {
        pattern = !TextUtil.isEmpty(pattern) ? pattern : DEFAULT_PATTERN;
        return getDateFormat(pattern).format(new Date(time));
    }

    /**
     * 给出一个时间的描述。比如“昨天 12:30”。
     * 当time不在前天与后天之，则默认使用“yyyy-MM-dd”格式初始化时间描述。
     *
     * @param time
     * @return 时间的描述
     */
    public static String formatRecent(long time) {
        long currentTime = System.currentTimeMillis();

        // 获取结尾的 小时和分钟
        int whatDay = whatDay(new Date(time), new Date(currentTime));
        switch (whatDay) {
            case DAY_TODAY:
                return "今天 " + format(time, "HH:mm");

            case DAY_YESTERDAY:
                return "昨天 " + format(time, "HH:mm");

            case DAY_TOMORROW:
                return "明天 " + format(time, "HH:mm");

            case DAY_BEFORE_YESTERDAY:
                return "前天 " + format(time, "HH:mm");

            case DAY_AFTER_TOMORROW:
                return "后天 " + format(time, "HH:mm");

            default:
                return format(time, "yyyy-MM-dd");
        }
    }

    /**
     * 判断judgeTime相对于relativeTime的时间，比如说今天，昨天，或者前天
     *
     * @param judgeTime    需要判断的时间，比如昨天。
     * @param relativeTime 相对的时间，比如今天。如果为null的话，将默认设置为今天
     * @return 时间的标号。请参见：
     * <li>{@link #DAY_AFTER_TOMORROW}
     * <li>
     * {@link #DAY_BEFORE_YESTERDAY}
     * <li>
     * {@link #DAY_TODAY}
     * <li>
     * {@link #DAY_TOMORROW}
     * <li>
     * {@link #DAY_YESTERDAY}
     * <li>
     * {@link #DAYS_FARTHER}
     * <li>
     * {@link #DAYS_LATER}
     * <li>{@link #DAY_UNKNOWN}
     */
    private static int whatDay(Date judgeTime, Date relativeTime) {
        if (relativeTime == null) {
            relativeTime = new Date(System.currentTimeMillis());
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", locale);
        String relativeTimeStr = format.format(relativeTime);
        // 获取今天0点的时间
        Date relativeDay;
        try {
            relativeDay = format.parse(relativeTimeStr);

            // 用judgeTime减去相对时间的那天的0点。
            long millis = judgeTime.getTime() - relativeDay.getTime();

            long oneDayMillis = DateUtils.DAY_IN_MILLIS;
            if (millis >= 0) {
                // 今天0点之后的时间
                if (millis < oneDayMillis) {
                    // 1天之内
                    return DAY_TODAY;
                } else if (millis >= oneDayMillis && millis < 2 * oneDayMillis) {
                    // 1~2天之内
                    return DAY_TOMORROW;
                } else if (millis >= 2 * oneDayMillis && millis < 3 * oneDayMillis) {
                    // 2~3之内
                    return DAY_AFTER_TOMORROW;
                } else {
                    return DAYS_LATER;
                }
            } else {
                // 今天0点之前的时间
                millis = -millis;
                if (millis < oneDayMillis) {
                    // 之前1天之内
                    return DAY_YESTERDAY;
                } else if (millis >= oneDayMillis && millis < 2 * oneDayMillis) {
                    // 之前1~2天之内
                    return DAY_BEFORE_YESTERDAY;
                } else {
                    return DAYS_FARTHER;
                }
            }

        } catch (ParseException e) {
            LogUtil.e(e);
            // 报错则默认是unknown
            return DAY_UNKNOWN;
        }
    }

    /**
     * 将指定格式的时间字符串转为日期对象。
     *
     * @param pattern
     * @param formattedStr
     * @return
     * @throws ParseException
     */
    @SuppressLint("SimpleDateFormat")
    public static Date parseDate(String pattern, String formattedStr) throws ParseException {
        return getDateFormat(pattern).parse(formattedStr);
    }

    /**
     * 将指定格式的时间字符串转为毫秒数。具体实现请参阅{@link #parseDate(String, String)}
     *
     * @param pattern
     * @param formattedStr
     * @return
     * @throws ParseException
     */
    public static long parse(String pattern, String formattedStr) throws ParseException {
        return parseDate(pattern, formattedStr).getTime();
    }

	/*获取时间*/

    /**
     * 获取某个时间点所在当天的零点时间戳
     *
     * @param time
     * @return
     */
    public static long getDayStart(long time) {
        //取格林威治零点，然后按照时区偏移
        long dayStart = time / DAY_IN_MILLIS * DAY_IN_MILLIS - getTimeZoneMillisOffset();
        if (time - dayStart >= DAY_IN_MILLIS) {//补正被整取法抹去的偏差
            dayStart += DAY_IN_MILLIS;
        }
        return dayStart;
    }

    /**
     * 获取今天0点的毫秒数。
     *
     * @return
     */
    public static long getTodayStart() {
        return getDayStart(System.currentTimeMillis());
    }

    /**
     * 获取时间点所在的周一零点时间戳
     *
     * @param time
     * @return
     */
    public static long getWeekStart(long time) {
        //周数取整再按时区偏移
        long weekStart = time / WEEK_IN_MILLIS * WEEK_IN_MILLIS - getTimeZoneMillisOffset();
        weekStart -= 3 * DAY_IN_MILLIS;// 1970.1.1 那天是星期四，因而之前有三天，这里减去三天的偏移量算出周一的零点

        //由于1970.1.1 那天是星期四并且因为JAVA的除法是整除，在周四零点之前的时间都会被抹去，
        // 使得算出的结果其实是上一周的周一零点，这里偏移回来。
        if (time - weekStart >= WEEK_IN_MILLIS) {
            weekStart += WEEK_IN_MILLIS;
        }

        return weekStart;
    }

    /**
     * 获取本周周一的零点毫秒数。
     *
     * @return
     */
    public static long getThisMondayStart() {
        return getWeekStart(System.currentTimeMillis());
    }

    /**
     * 获取相对格林威治的毫秒时差，具体请参阅{@link TimeZone#getRawOffset()}。
     *
     * @return
     */
    public static int getTimeZoneMillisOffset() {
        return TimeZone.getDefault().getRawOffset();
    }
}
