package com.coolers.aogp.aogp.util;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class BaseUtil {

    public static boolean isEmptyString(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean isEmptyList(List list) {
        return list == null || list.isEmpty();
    }


    /**
     * 获取当前时间距离下一天的剩余秒数
     *
     * @return int, 剩余秒数
     */
    public static int getSurplusTimeToday() {
        LocalTime nowTime = LocalTime.now();
        LocalTime endTime = LocalTime.of(23, 59, 59);
        return (int) nowTime.until(endTime, ChronoUnit.SECONDS);
    }

}
