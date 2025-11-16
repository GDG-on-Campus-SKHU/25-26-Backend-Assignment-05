package org.example.oauth.dto.comment.response;

import lombok.Builder;
import lombok.Getter;
import org.example.oauth.domain.comment.Comment;

@Getter
@Builder
public class CommentResponse {
    private Long commentId;
    private String postTitle;
    private Long authorId;
    private String authorName;
    private String content;

    public static CommentResponse commentInfo(Comment comment) {
        return CommentResponse.builder()
                .commentId(comment.getId())
                .postTitle(comment.getPost().getTitle())
                .authorId(comment.getAuthor().getId())
                .authorName(comment.getAuthor().getName())
                .content(comment.getContent())
                .build();
    }
}
