package org.example.oauth.service.comment;

import lombok.RequiredArgsConstructor;
import org.example.oauth.domain.comment.Comment;
import org.example.oauth.domain.post.Post;
import org.example.oauth.domain.user.User;
import org.example.oauth.dto.comment.request.CommentCreateRequest;
import org.example.oauth.dto.comment.request.CommentUpdateRequest;
import org.example.oauth.dto.comment.response.CommentResponse;
import org.example.oauth.exception.ErrorMessage;
import org.example.oauth.exception.NotFoundException;
import org.example.oauth.repository.CommentRepository;
import org.example.oauth.repository.PostRepository;
import org.example.oauth.repository.UserRepository;
import org.example.oauth.util.UserValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createComment(Long postId, CommentCreateRequest commentCreateRequest) {
        Long userId = UserValidator.requireLogin();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_POST));

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_USER));

        Comment comment = Comment.builder()
                .post(post)
                .author(author)
                .content(commentCreateRequest.getContent())
                .build();

        return commentRepository.save(comment).getId();
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long postId) {
        return commentRepository.findAll().stream()
                .filter(comment -> comment.getPost().getId().equals(postId))
                .map(CommentResponse::commentInfo)
                .toList();
    }

    @Transactional
    public void updateComment(Long commentId, CommentUpdateRequest commentUpdateRequest) {
        Comment comment = validateUser(commentId);
        comment.updateComment(commentUpdateRequest.getContent());
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = validateUser(commentId);
        commentRepository.delete(comment);
    }

    private Comment validateUser(Long commendId) {
        Long userId = UserValidator.requireLogin();
        Comment comment = commentRepository.findById(commendId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_COMMENT));
        boolean allowed = UserValidator.isAdmin() || comment.getAuthor().getId().equals(userId);

        if (!allowed) {
            throw new NotFoundException(ErrorMessage.NO_PERMISSION);
        }

        return comment;
    }
}
