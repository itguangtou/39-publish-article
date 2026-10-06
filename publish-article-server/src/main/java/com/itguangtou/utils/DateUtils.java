package com.itguangtou.utils;

import cn.hutool.core.date.DateUtil;

import java.util.Date;

public final class DateUtils {

    private DateUtils() {
    }

    public static String format(Date date) {
        return DateUtil.formatDateTime(date);
    }
}
