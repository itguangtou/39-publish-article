package com.itguangtou.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArticleDetailVO {

    private Long id;
    private String title;
    private String slug;
    private String promotionLink;
    private List<String> images;
    private LocalDateTime createdAt;
    private AuthorVO author;
}
