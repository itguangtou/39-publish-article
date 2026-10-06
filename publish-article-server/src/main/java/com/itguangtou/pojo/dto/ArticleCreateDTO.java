package com.itguangtou.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class ArticleCreateDTO {

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "正文不能为空")
    private String content;

    private String coverUrl;
    private String slug;
    private String promotionLink;
    private List<String> imageUrls;
}
