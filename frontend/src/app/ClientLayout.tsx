"use client";

import Link from "next/link";

export default function ClientLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <>
      <header className="flex gap-2">
        <Link href="/">홈</Link>
        <Link href="/p/list">글 목록</Link>
        <Link href="/member/login">로그인</Link>
      </header>
      <main className="flex-1">{children}</main>
      <footer>- GB4 -</footer>
    </>
  );
}
