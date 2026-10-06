package com.itguangtou.config;

import com.itguangtou.interceptor.LogInterceptor;
import com.itguangtou.service.impl.ImageStorageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final LogInterceptor logInterceptor;
    private final StorageProperties storageProperties;
    private final ImageStorageServiceImpl imageStorageService;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String publicPrefix = storageProperties.getPublicPrefix();
        if (!publicPrefix.endsWith("/")) {
            publicPrefix = publicPrefix + "/";
        }
        if (!publicPrefix.startsWith("/")) {
            publicPrefix = "/" + publicPrefix;
        }
        registry.addResourceHandler(publicPrefix + "**")
                .addResourceLocations(imageStorageService.getBasePath().toUri().toString());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor).addPathPatterns("/**");
    }
}
