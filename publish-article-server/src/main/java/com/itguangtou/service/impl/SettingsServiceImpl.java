package com.itguangtou.service.impl;

import com.itguangtou.exception.BusinessException;
import com.itguangtou.mapper.SystemSettingMapper;
import com.itguangtou.pojo.entity.SystemSetting;
import com.itguangtou.pojo.vo.SystemSettingsVO;
import com.itguangtou.service.ImageStorageService;
import com.itguangtou.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SettingsServiceImpl implements SettingsService {

    private static final long DEFAULT_SETTING_ID = 1L;

    private final SystemSettingMapper systemSettingMapper;
    private final ImageStorageService imageStorageService;

    @Override
    public SystemSettingsVO getSettings() {
        return toVO(requireSetting());
    }

    @Override
    public SystemSettingsVO updateSettings(String nickname, String intro, MultipartFile avatar) {
        SystemSetting setting = requireSetting();
        if (StringUtils.hasText(nickname)) {
            setting.setNickname(nickname.trim());
        }
        if (intro != null) {
            setting.setIntro(intro.trim());
        }
        if (avatar != null && !avatar.isEmpty()) {
            String oldPath = setting.getAvatarPath();
            String newPath = imageStorageService.saveAvatar(avatar);
            setting.setAvatarPath(newPath);
            if (StringUtils.hasText(oldPath) && !oldPath.equals(newPath)) {
                imageStorageService.deleteByRelativePath(oldPath);
            }
        }
        systemSettingMapper.updateById(setting);
        return toVO(setting);
    }

    public SystemSetting requireSetting() {
        SystemSetting setting = systemSettingMapper.selectById(DEFAULT_SETTING_ID);
        if (setting == null) {
            throw new BusinessException("系统设置不存在");
        }
        return setting;
    }

    public SystemSettingsVO toVO(SystemSetting setting) {
        SystemSettingsVO vo = new SystemSettingsVO();
        vo.setNickname(setting.getNickname());
        vo.setIntro(setting.getIntro());
        if (StringUtils.hasText(setting.getAvatarPath())) {
            vo.setAvatar(imageStorageService.toPublicUrl(setting.getAvatarPath()));
        } else {
            vo.setAvatar("/avatar-default.svg");
        }
        return vo;
    }
}
