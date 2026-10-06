package com.itguangtou.constant;

import java.util.List;

public final class FileConstants {

    private FileConstants() {
    }

    public static final List<String> ALLOWED_IMAGE_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif", "webp");
    public static final long MAX_FILE_SIZE = 20 * 1024 * 1024L;
}
