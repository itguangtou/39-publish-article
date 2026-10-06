package com.itguangtou.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("system_setting")
public class SystemSetting {

    @TableId
    private Long id;
    private String avatarPath;
    private String nickname;
    private String intro;
    private LocalDateTime updatedAt;
}
