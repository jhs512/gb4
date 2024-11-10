package com.sapp.domain.member.member.controller;

import com.sapp.domain.member.member.dto.MemberDto;
import com.sapp.domain.member.member.service.MemberService;
import com.sapp.global.rq.Rq;
import com.sapp.global.rsData.RsData;
import com.sapp.standard.base.Empty.Empty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApiV1MemberController {
    private final MemberService memberService;
    private final Rq rq;


    // 조회
    @GetMapping("/me")
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
    public RsData<Empty> logout() {
        rq.clearAuthCookies();

        return RsData.of("로그아웃 성공");
    }
}
