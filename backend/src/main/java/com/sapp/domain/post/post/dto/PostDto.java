package com.sapp.domain.post.post.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostDto {
    private long id;
    private String title;
    private String content;
}
