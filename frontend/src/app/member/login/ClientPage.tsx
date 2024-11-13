"use client";

import client from "@/lib/openapi_fetch";

export default function ClientPage() {
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

    console.log(data, error);
  };

  return (
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
  );
}
