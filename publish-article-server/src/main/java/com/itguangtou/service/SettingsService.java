package com.itguangtou.service;

import com.itguangtou.pojo.vo.SystemSettingsVO;
import org.springframework.web.multipart.MultipartFile;

public interface SettingsService {

    SystemSettingsVO getSettings();

    SystemSettingsVO updateSettings(String nickname, String intro, MultipartFile avatar);
}
