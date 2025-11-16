package org.example.oauth.dto.post.response;

import lombok.Builder;
import lombok.Getter;
import org.example.oauth.domain.comment.Comment;
import org.example.oauth.domain.post.Post;

import java.util.List;

@Getter
@Builder
public class PostResponse {
    private Long postId;
    private Long authorId;
    private String authorName;
    private String title;
    private String content;

    public static PostResponse postInfo(Post post, List<Comment> comments) {
        return PostResponse.builder()
                .postId(post.getId())
                .authorId(post.getAuthor().getId())
                .authorName(post.getAuthor().getName())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }
}
