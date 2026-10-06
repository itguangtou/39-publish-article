package com.itguangtou.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {

    String saveArticleImage(Long articleId, MultipartFile file);

    String saveAvatar(MultipartFile file);

    void deleteByRelativePath(String relativePath);

    void deleteArticleDir(Long articleId);

    String toPublicUrl(String relativePath);
}
