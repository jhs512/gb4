"use client";

import { useEffect, useState } from "react";
import client from "@/lib/openapi_fetch";
import { components } from "@/lib/backend/apiV1/schema";
import Link from "next/link";

export default function ClientPage() {
  const [posts, setPosts] = useState<components["schemas"]["PostDto"][]>([]);

  useEffect(() => {
    client.GET("/api/v1/posts").then((res) => {
      res.data?.content && setPosts(res.data.content);
    });
  }, []);

  return (
    <ul>
      {posts.map((post) => (
        <li key={post.id}>
          <Link href={`/p/${post.id}`}>{post.title}</Link>
        </li>
      ))}
    </ul>
  );
}
