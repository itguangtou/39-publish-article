package com.itguangtou.controller;

import com.itguangtou.handler.Result;
import com.itguangtou.pojo.vo.SystemSettingsVO;
import com.itguangtou.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;

    @GetMapping
    public Result<SystemSettingsVO> getSettings() {
        return Result.success(settingsService.getSettings());
    }

    @PutMapping
    public Result<SystemSettingsVO> updateSettings(
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String intro,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar) {
        return Result.success(settingsService.updateSettings(nickname, intro, avatar));
    }
}
