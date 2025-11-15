package org.example.oauth.dto.post.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostUpdateRequest {

    @NotBlank(message = "글 제목은 비어있을 수 없습니다.")
    private String title;

    @NotBlank(message = "글 내용은 비어있을 수 없습니다.")
    private String content;
}

