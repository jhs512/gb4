"use client";

import client from "@/lib/openapi_fetch";
import { useLoginMember } from "@/stores/member";
import { useEffect } from "react";

export default function ClientPage() {
  const {
    setLoginMember,
    isLogin,
    loginMember,
    isLoginMemberPending,
    removeLoginMember,
  } = useLoginMember();

  useEffect(() => {
    client.GET("/api/v1/members/me").then(({ data }) => {
      if (data) {
        setLoginMember(data.data);
      }
    });
  }, []);

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    const formData = new FormData(e.target as HTMLFormElement);
    const username = formData.get("username") as string;
    const password = formData.get("password") as string;

    const { data, error } = await client.POST("/api/v1/members/login", {
      body: {
        username,
        password,
      },
    });

    if (error) {
      alert(error.msg);
    } else {
      setLoginMember(data.data.item);
    }
  };

  const logout = () => {
    client.POST("/api/v1/members/logout").then(({ error }) => {
      if (error) {
        alert(error.msg);
      } else {
        removeLoginMember();
      }
    });
  };

  return (
    <>
      {isLoginMemberPending && <div>로그인 정보가 아직 세팅되지 않음</div>}
      {isLogin ? (
        <div>
          <div>로그인됨</div>
          <div>
            <img
              className="w-10 h-10 rounded-full"
              src={loginMember.profileImgUrl}
              alt="프로필 이미지"
            />
          </div>
          <div>{loginMember.name}</div>

          <button onClick={logout}>로그아웃</button>
        </div>
      ) : (
        <form onSubmit={handleSubmit}>
          <div>
            <input name="username" type="text" required placeholder="아이디" />
            <input
              name="password"
              type="password"
              required
              placeholder="비밀번호"
            />
            <button type="submit">로그인</button>
          </div>
        </form>
      )}
    </>
  );
}
