package com.example.oauthgoogleloginexample.controller;

import com.example.oauthgoogleloginexample.dto.issue.IssueCreateRequest;
import com.example.oauthgoogleloginexample.dto.issue.IssueResponse;
import com.example.oauthgoogleloginexample.dto.issue.IssueUpdateRequest;
import com.example.oauthgoogleloginexample.service.IssueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Issues", description = "이슈 생성/조회/수정/삭제 API")
public class IssueController {

    private final IssueService issueService;

    @PostMapping("/projects/{projectId}/issues")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "이슈 생성",
            description = "특정 프로젝트 하위에 신규 이슈를 생성합니다.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = @ApiResponse(responseCode = "201", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = IssueResponse.class)))
    )
    public ResponseEntity<IssueResponse> createUnderProject(@PathVariable Long projectId,
                                                            @Valid @RequestBody IssueCreateRequest request) {
        IssueResponse response = issueService.createUnderProject(projectId, request);
        return ResponseEntity.created(URI.create("/api/issues/" + response.getId()))
                .body(response);
    }

    @GetMapping("/issues/{id}")
    @Operation(summary = "이슈 단건 조회", description = "식별자를 통해 단일 이슈를 조회합니다.")
    public ResponseEntity<IssueResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(issueService.findById(id));
    }

    @GetMapping("/issues")
    @Operation(summary = "이슈 목록 조회", description = "전체 이슈 리스트를 조회합니다.")
    public ResponseEntity<List<IssueResponse>> list() {
        return ResponseEntity.ok(issueService.findAll());
    }

    @PutMapping("/issues/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "이슈 수정",
            description = "특정 이슈의 내용을 수정합니다.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = IssueResponse.class)))
    )
    public ResponseEntity<IssueResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody IssueUpdateRequest request) {
        return ResponseEntity.ok(issueService.update(id, request));
    }

    @DeleteMapping("/issues/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "이슈 삭제",
            description = "특정 이슈를 삭제합니다.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = @ApiResponse(responseCode = "204", description = "삭제 성공")
    )
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        issueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
