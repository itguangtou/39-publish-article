package com.itguangtou.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itguangtou.pojo.vo.ArticleDetailVO;
import com.itguangtou.pojo.vo.ArticleVO;
import org.springframework.web.multipart.MultipartFile;

public interface ArticleService {

    IPage<ArticleVO> pageList(long current, long size, String keyword);

    ArticleDetailVO getBySlug(String slug);

    ArticleVO create(String title, MultipartFile[] images);

    ArticleVO update(Long id, String title, String imageMeta, MultipartFile[] images);

    void delete(Long id);
}
