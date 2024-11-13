package com.sapp.domain.member.member.controller;

import com.sapp.domain.member.member.dto.MemberDto;
import com.sapp.domain.member.member.service.MemberService;
import com.sapp.global.rq.Rq;
import com.sapp.global.rsData.RsData;
import com.sapp.standard.base.Empty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Transactional(readOnly = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "ApiV1MemberController", description = "MEMBER API 컨트롤러")
public class ApiV1MemberController {
    private final MemberService memberService;
    private final Rq rq;


    // 조회
    @GetMapping("/me")
    @Operation(summary = "내 정보")
    public RsData<MemberDto> getMe() {
        return RsData.of(
                new MemberDto(rq.getMember())
        );
    }


    // 처리
    public record MemberLoginReqBody(@NotBlank String username, @NotBlank String password) {
    }

    public record MemberLoginResBody(@NonNull MemberDto item) {
    }

    @PostMapping("/login")
    @Operation(summary = "로그인")
    public RsData<MemberLoginResBody> login(@Valid @RequestBody MemberLoginReqBody reqBody) {
        RsData<MemberService.MemberAuthAndMakeTokensResBody> authAndMakeTokensRs = memberService.authAndMakeTokens(
                reqBody.username,
                reqBody.password
        );

        String accessToken = authAndMakeTokensRs.getData().accessToken();
        String refreshToken = authAndMakeTokensRs.getData().refreshToken();

        rq.makeAuthCookies(accessToken, refreshToken);

        return authAndMakeTokensRs.newDataOf(
                new MemberLoginResBody(
                        new MemberDto(authAndMakeTokensRs.getData().member())
                )
        );
    }


    @PostMapping("/logout")
    @Transactional(propagation = NOT_SUPPORTED)
    @Operation(summary = "로그아웃")
    public RsData<Empty> logout() {
        rq.clearAuthCookies();

        return RsData.of("로그아웃 성공");
    }
}
