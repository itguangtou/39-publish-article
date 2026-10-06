package com.itguangtou.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.itguangtou.config.StorageProperties;
import com.itguangtou.constant.FileConstants;
import com.itguangtou.exception.BusinessException;
import com.itguangtou.service.ImageStorageService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageStorageServiceImpl implements ImageStorageService {

    private final StorageProperties storageProperties;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    private Path basePath;

    @PostConstruct
    public void init() {
        ImageIO.scanForPlugins();
        basePath = resolveBasePath(storageProperties.getBaseDir());
        log.info("图片存储目录: {}", basePath);
        FileUtil.mkdir(basePath.toFile());
        FileUtil.mkdir(basePath.resolve("articles").toFile());
        FileUtil.mkdir(basePath.resolve("avatar").toFile());
    }

    /**
     * 优先向上查找项目根目录下的 data/，避免 IDE 与 mvn 启动时工作目录不同导致文件写到错误位置。
     */
    private Path resolveBasePath(String configuredDir) {
        Path configured = Paths.get(configuredDir);
        if (configured.isAbsolute()) {
            return configured.normalize();
        }
        Path cwd = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        Path current = cwd;
        for (int depth = 0; depth < 4 && current != null; depth++) {
            Path dataDir = current.resolve("data");
            if (Files.isDirectory(dataDir)) {
                return dataDir.normalize();
            }
            current = current.getParent();
        }
        return cwd.resolve(configured).toAbsolutePath().normalize();
    }

    @Override
    public String saveArticleImage(Long articleId, MultipartFile file) {
        validateImage(file);
        String relativePath = "articles/" + articleId + "/" + IdUtil.simpleUUID() + ".webp";
        writeWebp(file, basePath.resolve(relativePath));
        return relativePath;
    }

    @Override
    public String saveAvatar(MultipartFile file) {
        validateImage(file);
        String relativePath = "avatar/avatar.webp";
        writeWebp(file, basePath.resolve(relativePath));
        return relativePath;
    }

    @Override
    public void deleteByRelativePath(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return;
        }
        Path target = resolveSafePath(relativePath);
        if (Files.exists(target)) {
            try {
                Files.delete(target);
            } catch (IOException e) {
                log.warn("删除图片失败: {}", relativePath, e);
            }
        }
    }

    @Override
    public void deleteArticleDir(Long articleId) {
        Path articleDir = basePath.resolve("articles").resolve(String.valueOf(articleId));
        if (Files.exists(articleDir)) {
            FileUtil.del(articleDir.toFile());
        }
    }

    @Override
    public String toPublicUrl(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return "";
        }
        String prefix = storageProperties.getPublicPrefix();
        if (!prefix.startsWith("/")) {
            prefix = "/" + prefix;
        }
        if (!prefix.endsWith("/")) {
            prefix = prefix + "/";
        }
        String normalizedPath = relativePath.replace("\\", "/");
        while (normalizedPath.startsWith("/")) {
            normalizedPath = normalizedPath.substring(1);
        }
        String ctx = StringUtils.hasText(contextPath) ? contextPath : "";
        return ctx + prefix + normalizedPath;
    }

    public Path getBasePath() {
        return basePath;
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        if (file.getSize() > FileConstants.MAX_FILE_SIZE) {
            throw new BusinessException("图片大小不能超过20MB");
        }
        String extension = FileUtil.extName(file.getOriginalFilename());
        if (extension == null || !FileConstants.ALLOWED_IMAGE_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BusinessException("不支持的图片格式");
        }
    }

    private void writeWebp(MultipartFile file, Path targetPath) {
        FileUtil.mkdir(targetPath.getParent().toFile());
        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage source = ImageIO.read(inputStream);
            if (source == null) {
                throw new BusinessException("无法读取图片内容，请更换图片后重试");
            }
            BufferedImage resized = Thumbnails.of(source)
                    .width(storageProperties.getMaxWidth())
                    .keepAspectRatio(true)
                    .asBufferedImage();
            writeWebpImage(resized, targetPath);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("图片保存失败: {}", targetPath, e);
            throw new BusinessException("图片保存失败，请检查图片格式后重试");
        }
    }

    private void writeWebpImage(BufferedImage image, Path targetPath) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType("image/webp");
        if (!writers.hasNext()) {
            throw new BusinessException("WebP 编码器不可用");
        }
        ImageWriter writer = writers.next();
        try (ImageOutputStream outputStream = ImageIO.createImageOutputStream(Files.newOutputStream(targetPath))) {
            writer.setOutput(outputStream);
            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            if (writeParam.canWriteCompressed()) {
                writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                String[] compressionTypes = writeParam.getCompressionTypes();
                if (compressionTypes != null && compressionTypes.length > 0) {
                    writeParam.setCompressionType(compressionTypes[0]);
                }
                writeParam.setCompressionQuality(storageProperties.getWebpQuality());
            }
            writer.write(null, new IIOImage(image, null, null), writeParam);
        } finally {
            writer.dispose();
        }
    }

    private Path resolveSafePath(String relativePath) {
        String normalized = relativePath.replace("\\", "/");
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        Path resolved = basePath.resolve(normalized).normalize();
        if (!resolved.startsWith(basePath)) {
            throw new BusinessException("非法文件路径");
        }
        return resolved;
    }
}
