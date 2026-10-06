package com.itguangtou.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itguangtou.handler.PageResult;
import com.itguangtou.handler.Result;
import com.itguangtou.pojo.vo.ArticleDetailVO;
import com.itguangtou.pojo.vo.ArticleVO;
import com.itguangtou.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    public PageResult<ArticleVO> list(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword) {
        IPage<ArticleVO> page = articleService.pageList(current, size, keyword);
        return PageResult.of(page);
    }

    @GetMapping("/slug/{slug}")
    public Result<ArticleDetailVO> getBySlug(@PathVariable String slug) {
        return Result.success(articleService.getBySlug(slug));
    }

    @PostMapping
    public Result<ArticleVO> create(
            @RequestParam String title,
            @RequestParam("images") MultipartFile[] images) {
        return Result.success(articleService.create(title, images));
    }

    @PutMapping("/{id}")
    public Result<ArticleVO> update(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String imageMeta,
            @RequestParam(value = "images", required = false) MultipartFile[] images) {
        return Result.success(articleService.update(id, title, imageMeta, images));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        articleService.delete(id);
        return Result.success(null);
    }
}
