package com.sapp.domain.post.post.controller;

import com.sapp.domain.post.post.dto.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApiV1PostController {
    @GetMapping
    public List<PostDto> getItems() {
        return new ArrayList<>() {
            {
                add(PostDto.builder().id(1L).title("title1").content("content1").build());
                add(PostDto.builder().id(2L).title("title2").content("content2").build());
            }
        };
    }
}
