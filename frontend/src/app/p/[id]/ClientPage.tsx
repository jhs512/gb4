"use client";

import { useEffect, useState } from "react";
import client from "@/lib/openapi_fetch";
import { components } from "@/lib/backend/apiV1/schema";

export default function ClientPage({ id }: { id: number }) {
  const [post, setPost] = useState<
    components["schemas"]["PostDto"] | undefined
  >(undefined);

  useEffect(() => {
    client
      .GET("/api/v1/posts/{id}", {
        params: {
          path: {
            id,
          },
        },
      })
      .then((res) => {
        res.data && setPost(res.data);
      });
  }, []);

  return (
    <div>
      <h1>{post?.title}</h1>
      <p>
        {post?.id} : {post?.body}
      </p>
    </div>
  );
}
