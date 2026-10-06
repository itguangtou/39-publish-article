package com.itguangtou.utils;

import cn.hutool.core.util.RandomUtil;

public final class SlugUtils {

    private static final String CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";

    private SlugUtils() {
    }

    public static String generateSlug() {
        return RandomUtil.randomString(CHARS, 8);
    }
}
