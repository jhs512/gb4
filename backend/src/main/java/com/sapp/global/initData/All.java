package com.sapp.global.initData;

import com.sapp.domain.member.member.entity.Member;
import com.sapp.domain.member.member.service.MemberService;
import com.sapp.domain.post.post.service.PostService;
import com.sapp.global.app.AppConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class All {
    @Autowired
    @Lazy
    private All self;

    private final PostService postService;
    private final MemberService memberService;

    @Bean
    ApplicationRunner initDataAll() {
        return (args) -> {
            self.work1();
        };
    }

    @Transactional
    public void work1() {
        if (memberService.count() > 0) return;

        Member memberSystem = memberService.join("system", "1234", "시스템");
        if (AppConfig.isNotProd()) memberSystem.setRefreshToken("system");

        Member memberAdmin = memberService.join("admin", "1234", "관리자");
        if (AppConfig.isNotProd()) memberAdmin.setRefreshToken("admin");

        Member memberUser1 = memberService.join("user1", "1234", "유저1");
        if (AppConfig.isNotProd()) memberUser1.setRefreshToken("user1");

        Member memberUser2 = memberService.join("user2", "1234", "유저2");
        if (AppConfig.isNotProd()) memberUser2.setRefreshToken("user2");

        Member memberUser3 = memberService.join("user3", "1234", "유저3");
        if (AppConfig.isNotProd()) memberUser3.setRefreshToken("user3");

        if (postService.count() > 0) return;

        postService.write(memberUser1, "제목 1", "내용 1", true, true);
        postService.write(memberUser1, "제목 2", "내용 2", true, true);
        postService.write(memberUser2, "제목 3", "내용 3", true, false);
        postService.write(memberUser3, "제목 4", "내용 4", false, false);

        for (int i = 5; i <= 1000; i++) {
            postService.write(memberUser3, "제목 " + i, "내용 " + i, true, true);
        }
    }
}

