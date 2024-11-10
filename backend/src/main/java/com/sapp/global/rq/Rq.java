package com.sapp.global.rq;

import com.sapp.domain.member.member.entity.Member;
import com.sapp.global.app.AppConfig;
import com.sapp.global.security.SecurityUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Optional;

@Component
@RequestScope
@RequiredArgsConstructor
public class Rq {
    private final HttpServletRequest req;
    private final HttpServletResponse resp;

    @PersistenceContext
    private EntityManager entityManager;

    private SecurityUser user;
    private Member member;
    private Boolean isLogin;
    private Boolean isAdmin;

    // Header Methods
    public void setHeader(String name, String value) {
        resp.setHeader(name, value);
    }

    public String getHeader(String name, String defaultValue) {
        String value = req.getHeader(name);
        return value != null ? value : defaultValue;
    }


    // Status Methods
    public void setStatusCode(int statusCode) {
        resp.setStatus(statusCode);
    }


    // Request Methods
    public String getCurrentUrlPath() {
        return req.getRequestURI();
    }

    public boolean isApi() {
        String xRequestedWith = req.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(xRequestedWith);
    }


    // Cookie Methods
    private String getSiteCookieDomain() {
        String cookieDomain = AppConfig.getSiteCookieDomain();

        if (!cookieDomain.equals("localhost")) {
            return "." + cookieDomain;
        }

        return null;
    }

    public void setCrossDomainCookie(String name, String value) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/")
                .domain(getSiteCookieDomain())
                .sameSite("Strict")
                .secure(true)
                .httpOnly(true)
                .build();

        resp.addHeader("Set-Cookie", cookie.toString());
    }

    public void removeCrossDomainCookie(String name) {
        ResponseCookie cookie = ResponseCookie.from(name, null)
                .path("/")
                .maxAge(0)
                .domain(getSiteCookieDomain())
                .secure(true)
                .httpOnly(true)
                .build();

        resp.addHeader("Set-Cookie", cookie.toString());
    }

    public Cookie getCookie(String name) {
        Cookie[] cookies = req.getCookies();

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie;
            }
        }

        return null;
    }

    public String getCookieValue(String name, String defaultValue) {
        Cookie cookie = getCookie(name);

        if (cookie == null) {
            return defaultValue;
        }

        return cookie.getValue();
    }

    private long getCookieAsLong(String name, int defaultValue) {
        String value = getCookieValue(name, null);

        if (value == null) {
            return defaultValue;
        }

        return Long.parseLong(value);
    }

    public void removeCookie(String name) {
        Cookie cookie = getCookie(name);

        if (cookie == null) {
            return;
        }

        cookie.setPath("/");
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
    }


    // Authentication/User Methods
    public boolean isLogin() {
        if (isLogin == null) getUser();
        return isLogin;
    }

    public boolean isLogout() {
        return !isLogin();
    }

    private SecurityUser getUser() {
        if (isLogin == null) {
            user = Optional.ofNullable(SecurityContextHolder.getContext())
                    .map(context -> context.getAuthentication())
                    .filter(authentication -> authentication.getPrincipal() instanceof SecurityUser)
                    .map(authentication -> (SecurityUser) authentication.getPrincipal())
                    .orElse(null);

            isLogin = user != null;
        }
        return user;
    }

    public void setSecurityUser(SecurityUser securityUser) {
        SecurityContextHolder.getContext().setAuthentication(securityUser.genAuthentication());
    }

    public Member getMember() {
        if (isLogout()) return null;

        if (member == null) {
            member = entityManager.getReference(Member.class, getUser().getId());
            member.setAdmin(isAdmin());
        }

        return member;
    }

    public boolean isAdmin() {
        if (isLogout()) return false;

        if (isAdmin == null) {
            isAdmin = getUser()
                    .getAuthorities()
                    .stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        }

        return isAdmin;
    }

    public void clearAuthCookies() {
        removeCrossDomainCookie("accessToken");
        removeCrossDomainCookie("refreshToken");
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    public void makeAuthCookies(String accessToken, String refreshToken) {
        setCrossDomainCookie("accessToken", accessToken);
        setCrossDomainCookie("refreshToken", refreshToken);
    }
}