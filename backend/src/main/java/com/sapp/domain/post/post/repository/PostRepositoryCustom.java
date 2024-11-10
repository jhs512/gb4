package com.sapp.domain.post.post.repository;

import com.sapp.domain.member.member.entity.Member;
import com.sapp.domain.post.post.entity.Post;
import com.sapp.standard.base.KwTypeV1;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
    Page<Post> findByKw(KwTypeV1 kwType, String kw, Member author, Boolean published, Boolean listed, Pageable pageable);
}