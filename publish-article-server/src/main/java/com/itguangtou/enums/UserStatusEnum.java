package com.itguangtou.enums;

import lombok.Getter;

@Getter
public enum UserStatusEnum {
    DISABLED(0, "禁用"),
    NORMAL(1, "正常");

    private final int code;
    private final String desc;

    UserStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
