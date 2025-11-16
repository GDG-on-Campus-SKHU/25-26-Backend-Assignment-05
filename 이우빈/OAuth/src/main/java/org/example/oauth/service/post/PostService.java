package org.example.oauth.service.post;

import lombok.RequiredArgsConstructor;
import org.example.oauth.domain.post.Post;
import org.example.oauth.domain.user.User;
import org.example.oauth.dto.post.request.PostCreateRequest;
import org.example.oauth.dto.post.request.PostUpdateRequest;
import org.example.oauth.dto.post.response.PostResponse;
import org.example.oauth.exception.ErrorMessage;
import org.example.oauth.exception.ForbiddenException;
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
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public Long createPost(PostCreateRequest postCreateRequest) {
        Long userId = UserValidator.requireLogin();
        User author = userRepository.getReferenceById(userId);

        Post post = Post.builder()
                .author(author)
                .title(postCreateRequest.getTitle())
                .content(postCreateRequest.getContent())
                .build();

        return postRepository.save(post).getId();
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_POST));

        return PostResponse.postInfo(post, List.of());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> listPosts() {
        return postRepository.findAll().stream()
                .map(post -> PostResponse.postInfo(post, List.of()))
                .toList();
    }

    @Transactional
    public void updatePost(Long postId, PostUpdateRequest postUpdateRequest) {
        Post post = validateUser(postId);
        post.updatePost(postUpdateRequest.getTitle(), postUpdateRequest.getContent());
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = validateUser(postId);

        commentRepository.findAll()
                .stream()
                .filter(comment -> comment.getPost().getId().equals(postId))
                .forEach(commentRepository::delete);

        postRepository.delete(post);
    }

    private Post validateUser(Long postId) {
        Long userId = UserValidator.requireLogin();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_POST));
        boolean allowed = UserValidator.isAdmin() || post.getAuthor().getId().equals(userId);

        if (!allowed) {
            throw new ForbiddenException(ErrorMessage.NO_PERMISSION);
        }

        return post;
    }
}
