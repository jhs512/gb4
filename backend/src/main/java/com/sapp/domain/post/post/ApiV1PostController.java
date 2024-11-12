package com.sapp.domain.post.post;

import com.sapp.domain.member.member.entity.Member;
import com.sapp.domain.post.post.dto.PostDto;
import com.sapp.domain.post.post.entity.Post;
import com.sapp.domain.post.post.service.PostService;
import com.sapp.global.app.AppConfig;
import com.sapp.global.rq.Rq;
import com.sapp.global.rsData.RsData;
import com.sapp.standard.base.Empty.Empty;
import com.sapp.standard.base.KwTypeV1;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "ApiV1PostController", description = "POST API 컨트롤러")
public class ApiV1PostController {
    private final PostService postService;
    private final Rq rq;


    // 조회
    private PostDto toPostDto(Member actor, Post post) {
        PostDto postDto = new PostDto(post);

        postDto.setActorCanRead(postService.canRead(actor, post));
        postDto.setActorCanModify(postService.canModify(actor, post));
        postDto.setActorCanDelete(postService.canDelete(actor, post));

        return postDto;
    }

    @GetMapping
    @Operation(summary = "다건 조회")
    public Page<PostDto> getItems(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "") String kw,
            @RequestParam(defaultValue = "ALL") KwTypeV1 kwType
    ) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page - 1, AppConfig.getBasePageSize(), Sort.by(sorts));
        Page<Post> itemPage = postService.findByKw(kwType, kw, null, true, true, pageable);

        Member actor = rq.getMember();

        Page<PostDto> postDtos = itemPage.map(post -> toPostDto(actor, post));

        return postDtos;
    }

    @GetMapping("/{id}")
    @Operation(summary = "단건 조회")
    public PostDto getItem(
            @PathVariable long id
    ) {
        Member actor = rq.getMember();

        Post post = postService.findById(id).get();

        postService.checkCanRead(actor, post);

        PostDto postDto = toPostDto(actor, post);

        return postDto;
    }

    // 처리
    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "삭제")
    public RsData<Empty> deleteItem(
            @PathVariable long id
    ) {
        Member actor = rq.getMember();

        Post post = postService.findById(id).get();

        postService.checkCanDelete(actor, post);

        postService.delete(post);

        return RsData.of("삭제 성공");
    }


    public record PostModifyItemReqBody(
            @NotBlank String title,
            @NotBlank String body,
            @NotNull boolean published,
            @NotNull boolean listed
    ) {
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "수정")
    public PostDto modifyItem(
            @PathVariable long id,
            @Valid @RequestBody PostModifyItemReqBody reqBody
    ) {
        Member actor = rq.getMember();

        Post post = postService.findById(id).get();

        postService.checkCanModify(actor, post);

        postService.modify(post, reqBody.title, reqBody.body, reqBody.published, reqBody.listed);

        return toPostDto(actor, post);
    }


    public record PostWriteItemReqBody(
            @NotBlank String title,
            @NotBlank String body,
            @NotNull boolean published,
            @NotNull boolean listed
    ) {
    }

    @PostMapping
    @Transactional
    @Operation(summary = "작성")
    public Post writeItem(
            @Valid @RequestBody PostWriteItemReqBody reqBody
    ) {
        Member author = rq.getMember();

        return postService.write(author, reqBody.title, reqBody.body, reqBody.published, reqBody.listed);
    }
}
