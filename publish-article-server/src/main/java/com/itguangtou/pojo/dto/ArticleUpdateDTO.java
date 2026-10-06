package com.itguangtou.pojo.dto;

import lombok.Data;

import java.util.List;

@Data
public class ArticleUpdateDTO {

    private String title;
    private String content;
    private String coverUrl;
    private String slug;
    private String promotionLink;
    private List<String> imageUrls;
}
