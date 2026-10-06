package com.itguangtou.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itguangtou.enums.ResultCodeEnum;
import com.itguangtou.exception.BusinessException;
import com.itguangtou.mapper.ArticleImageMapper;
import com.itguangtou.mapper.ArticleMapper;
import com.itguangtou.pojo.dto.ImageMetaItem;
import com.itguangtou.pojo.entity.Article;
import com.itguangtou.pojo.entity.ArticleImage;
import com.itguangtou.pojo.vo.ArticleDetailVO;
import com.itguangtou.pojo.vo.ArticleVO;
import com.itguangtou.pojo.vo.AuthorVO;
import com.itguangtou.pojo.vo.SystemSettingsVO;
import com.itguangtou.service.ArticleService;
import com.itguangtou.service.ImageStorageService;
import com.itguangtou.utils.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;
    private final ArticleImageMapper articleImageMapper;
    private final ImageStorageService imageStorageService;
    private final SettingsServiceImpl settingsService;

    @Override
    public IPage<ArticleVO> pageList(long current, long size, String keyword) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .like(StringUtils.hasText(keyword), Article::getTitle, keyword)
                .orderByDesc(Article::getCreatedAt);
        Page<Article> page = articleMapper.selectPage(new Page<>(current, size), wrapper);
        List<Long> articleIds = page.getRecords().stream().map(Article::getId).toList();
        Map<Long, List<String>> imageMap = loadImagePaths(articleIds);
        return page.convert(article -> toVO(article, imageMap.getOrDefault(article.getId(), List.of())));
    }

    @Override
    public ArticleDetailVO getBySlug(String slug) {
        Article article = articleMapper.selectOne(new LambdaQueryWrapper<Article>()
                .eq(Article::getSlug, slug));
        if (article == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND);
        }
        List<String> images = loadImagePaths(List.of(article.getId())).getOrDefault(article.getId(), List.of())
                .stream()
                .map(imageStorageService::toPublicUrl)
                .toList();
        AuthorVO author = toAuthorVO();

        ArticleDetailVO vo = new ArticleDetailVO();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setSlug(article.getSlug());
        vo.setPromotionLink("/article/" + article.getSlug());
        vo.setImages(images);
        vo.setCreatedAt(article.getCreatedAt());
        vo.setAuthor(author);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleVO create(String title, MultipartFile[] images) {
        validateTitle(title);
        validateCreateImages(images);

        Article article = new Article();
        article.setTitle(title.trim());
        article.setSlug(generateUniqueSlug());
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());
        articleMapper.insert(article);

        List<String> paths = saveImages(article.getId(), images);
        saveImageRecords(article.getId(), paths);
        return toVO(article, paths);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleVO update(Long id, String title, String imageMeta, MultipartFile[] images) {
        Article article = requireArticle(id);
        validateTitle(title);

        List<ArticleImage> existingImages = articleImageMapper.selectList(new LambdaQueryWrapper<ArticleImage>()
                .eq(ArticleImage::getArticleId, id)
                .orderByAsc(ArticleImage::getSortOrder));
        Set<String> existingPaths = existingImages.stream()
                .map(ArticleImage::getImagePath)
                .collect(Collectors.toSet());

        List<String> finalPaths = resolveFinalPaths(id, imageMeta, images, existingPaths);
        if (finalPaths.isEmpty()) {
            throw new BusinessException("请上传至少一张正文图片");
        }

        Set<String> keepPaths = new HashSet<>(finalPaths);
        for (String oldPath : existingPaths) {
            if (!keepPaths.contains(oldPath)) {
                imageStorageService.deleteByRelativePath(oldPath);
            }
        }

        articleImageMapper.delete(new LambdaQueryWrapper<ArticleImage>().eq(ArticleImage::getArticleId, id));
        saveImageRecords(id, finalPaths);

        article.setTitle(title.trim());
        article.setUpdatedAt(LocalDateTime.now());
        articleMapper.updateById(article);
        return toVO(article, finalPaths);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        requireArticle(id);
        articleImageMapper.delete(new LambdaQueryWrapper<ArticleImage>().eq(ArticleImage::getArticleId, id));
        articleMapper.deleteById(id);
        imageStorageService.deleteArticleDir(id);
    }

    private Article requireArticle(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND);
        }
        return article;
    }

    private void validateTitle(String title) {
        if (!StringUtils.hasText(title)) {
            throw new BusinessException("标题不能为空");
        }
    }

    private void validateCreateImages(MultipartFile[] images) {
        if (images == null || images.length == 0) {
            throw new BusinessException("请上传至少一张正文图片");
        }
        boolean hasFile = false;
        for (MultipartFile image : images) {
            if (image != null && !image.isEmpty()) {
                hasFile = true;
                break;
            }
        }
        if (!hasFile) {
            throw new BusinessException("请上传至少一张正文图片");
        }
    }

    private String generateUniqueSlug() {
        for (int i = 0; i < 10; i++) {
            String slug = SlugUtils.generateSlug();
            Long count = articleMapper.selectCount(new LambdaQueryWrapper<Article>().eq(Article::getSlug, slug));
            if (count == 0) {
                return slug;
            }
        }
        throw new BusinessException("生成推广标识失败，请重试");
    }

    private List<String> saveImages(Long articleId, MultipartFile[] images) {
        List<String> paths = new ArrayList<>();
        for (MultipartFile image : images) {
            if (image == null || image.isEmpty()) {
                continue;
            }
            paths.add(imageStorageService.saveArticleImage(articleId, image));
        }
        return paths;
    }

    private void saveImageRecords(Long articleId, List<String> paths) {
        for (int i = 0; i < paths.size(); i++) {
            ArticleImage record = new ArticleImage();
            record.setArticleId(articleId);
            record.setImagePath(paths.get(i));
            record.setSortOrder(i);
            record.setCreatedAt(LocalDateTime.now());
            articleImageMapper.insert(record);
        }
    }

    private List<String> resolveFinalPaths(Long articleId, String imageMeta, MultipartFile[] images, Set<String> existingPaths) {
        if (!StringUtils.hasText(imageMeta)) {
            throw new BusinessException("图片信息不能为空");
        }
        List<ImageMetaItem> metaItems = JSONUtil.toList(imageMeta, ImageMetaItem.class);
        if (metaItems.isEmpty()) {
            throw new BusinessException("请上传至少一张正文图片");
        }

        MultipartFile[] safeImages = images == null ? new MultipartFile[0] : images;
        List<String> finalPaths = new ArrayList<>();
        for (ImageMetaItem item : metaItems) {
            if ("keep".equals(item.getType())) {
                if (!StringUtils.hasText(item.getPath()) || !existingPaths.contains(item.getPath())) {
                    throw new BusinessException("保留的图片无效");
                }
                finalPaths.add(item.getPath());
            } else if ("new".equals(item.getType())) {
                if (item.getIndex() == null || item.getIndex() < 0 || item.getIndex() >= safeImages.length) {
                    throw new BusinessException("新图片索引无效");
                }
                MultipartFile file = safeImages[item.getIndex()];
                if (file == null || file.isEmpty()) {
                    throw new BusinessException("新图片不能为空");
                }
                finalPaths.add(imageStorageService.saveArticleImage(articleId, file));
            } else {
                throw new BusinessException("图片元数据格式错误");
            }
        }
        return finalPaths;
    }

    private Map<Long, List<String>> loadImagePaths(List<Long> articleIds) {
        if (articleIds.isEmpty()) {
            return Map.of();
        }
        List<ArticleImage> images = articleImageMapper.selectList(new LambdaQueryWrapper<ArticleImage>()
                .in(ArticleImage::getArticleId, articleIds)
                .orderByAsc(ArticleImage::getSortOrder));
        return images.stream().collect(Collectors.groupingBy(
                ArticleImage::getArticleId,
                Collectors.mapping(ArticleImage::getImagePath, Collectors.toList())
        ));
    }

    private AuthorVO toAuthorVO() {
        SystemSettingsVO settings = settingsService.toVO(settingsService.requireSetting());
        AuthorVO author = new AuthorVO();
        author.setAvatar(settings.getAvatar());
        author.setNickname(settings.getNickname());
        author.setIntro(settings.getIntro());
        return author;
    }

    private ArticleVO toVO(Article article, List<String> paths) {
        ArticleVO vo = new ArticleVO();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setSlug(article.getSlug());
        vo.setPromotionLink("/article/" + article.getSlug());
        vo.setCreatedAt(article.getCreatedAt());
        List<String> publicUrls = paths.stream().map(imageStorageService::toPublicUrl).toList();
        vo.setImages(publicUrls);
        vo.setCoverUrl(publicUrls.isEmpty() ? null : publicUrls.get(0));
        return vo;
    }
}
