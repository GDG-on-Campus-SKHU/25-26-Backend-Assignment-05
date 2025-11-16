package com.skhu.oauthgoogleloginpr.controller;

import com.skhu.oauthgoogleloginpr.dto.post.PostInfoResponseDto;
import com.skhu.oauthgoogleloginpr.dto.post.PostSaveRequestDto;
import com.skhu.oauthgoogleloginpr.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostInfoResponseDto> createPost(
            @RequestBody PostSaveRequestDto requestDto,
            Principal principal
    ) {
        Long userId = Long.parseLong(principal.getName());
        return ResponseEntity.ok(postService.createPost(requestDto, userId));
    }

    @GetMapping
    public ResponseEntity<List<PostInfoResponseDto>> getAllPosts() {
        return ResponseEntity.ok(postService.findAllPosts());
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostInfoResponseDto> getPostById(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.findPostById(postId));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<PostInfoResponseDto> updatePost(
            @PathVariable Long postId,
            @RequestBody PostSaveRequestDto requestDto,
            Principal principal
    ) {
        Long userId = Long.parseLong(principal.getName());
        return ResponseEntity.ok(postService.updatePost(postId, requestDto, userId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            Principal principal
    ) {
        Long userId = Long.parseLong(principal.getName());
        postService.deletePost(postId, userId);
        return ResponseEntity.noContent().build();
    }
}

