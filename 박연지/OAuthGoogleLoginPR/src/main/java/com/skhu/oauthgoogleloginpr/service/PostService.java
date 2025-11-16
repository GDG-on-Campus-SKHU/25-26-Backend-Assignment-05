package com.skhu.oauthgoogleloginpr.service;

import com.skhu.oauthgoogleloginpr.domain.Post;
import com.skhu.oauthgoogleloginpr.domain.Role;
import com.skhu.oauthgoogleloginpr.domain.User;
import com.skhu.oauthgoogleloginpr.dto.post.PostInfoResponseDto;
import com.skhu.oauthgoogleloginpr.dto.post.PostSaveRequestDto;
import com.skhu.oauthgoogleloginpr.global.code.ErrorStatus;
import com.skhu.oauthgoogleloginpr.global.exception.GeneralException;
import com.skhu.oauthgoogleloginpr.repository.PostRepository;
import com.skhu.oauthgoogleloginpr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostInfoResponseDto createPost(PostSaveRequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Post post = Post.builder()
                .title(requestDto.title())
                .content(requestDto.content())
                .user(user)
                .build();

        postRepository.save(post);
        return PostInfoResponseDto.from(post);
    }

    @Transactional(readOnly = true)
    public List<PostInfoResponseDto> findAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(PostInfoResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PostInfoResponseDto findPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
        return PostInfoResponseDto.from(post);
    }

    @Transactional
    public PostInfoResponseDto updatePost(Long postId, PostSaveRequestDto requestDto, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        if (!post.getUser().getId().equals(userId) && user.getRole() != Role.ADMIN) {
            throw new GeneralException(ErrorStatus.POST_ACCESS_DENIED);
        }

        post.update(requestDto.title(), requestDto.content());
        return PostInfoResponseDto.from(post);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        if (!post.getUser().getId().equals(userId) && user.getRole() != Role.ADMIN) {
            throw new GeneralException(ErrorStatus.POST_ACCESS_DENIED);
        }

        postRepository.delete(post);
    }
}
