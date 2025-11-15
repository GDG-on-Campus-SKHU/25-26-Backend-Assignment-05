package org.example.oauth.controller.comment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.oauth.dto.comment.request.CommentCreateRequest;
import org.example.oauth.dto.comment.request.CommentUpdateRequest;
import org.example.oauth.dto.comment.response.CommentResponse;
import org.example.oauth.service.comment.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Void> createComment(@PathVariable Long postId,
                                              @Valid @RequestBody CommentCreateRequest commentCreateRequest) {
        Long id = commentService.createComment(postId, commentCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/posts/" + postId + "#comment-" + id)
                .build();
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getComments(postId));
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<Void> updateComment(@PathVariable Long commentId,
                                              @Valid @RequestBody CommentUpdateRequest commentUpdateRequest) {
        commentService.updateComment(commentId, commentUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
