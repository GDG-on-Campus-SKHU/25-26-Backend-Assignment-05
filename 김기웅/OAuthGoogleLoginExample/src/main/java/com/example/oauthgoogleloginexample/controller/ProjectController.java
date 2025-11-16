package com.example.oauthgoogleloginexample.controller;

import com.example.oauthgoogleloginexample.dto.project.ProjectCreateRequest;
import com.example.oauthgoogleloginexample.dto.project.ProjectResponse;
import com.example.oauthgoogleloginexample.service.ProjectService;
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
@RequestMapping("/api/projects")
@Tag(name = "Projects", description = "프로젝트 생성/조회/삭제 API")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "프로젝트 생성",
            description = "인증된 사용자가 신규 프로젝트를 생성합니다.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "생성 성공",
                            content = @Content(schema = @Schema(implementation = ProjectResponse.class)))
            }
    )
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectCreateRequest request) {
        ProjectResponse response = projectService.create(request);
        return ResponseEntity.created(URI.create("/api/projects/" + response.getId()))
                .body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "프로젝트 단건 조회", description = "식별자로 프로젝트 상세 정보를 조회합니다.")
    public ResponseEntity<ProjectResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.findById(id));
    }

    @GetMapping
    @Operation(summary = "프로젝트 목록 조회", description = "등록된 모든 프로젝트를 조회합니다.")
    public ResponseEntity<List<ProjectResponse>> list() {
        return ResponseEntity.ok(projectService.findAll());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "프로젝트 삭제",
            description = "인증된 사용자가 프로젝트를 삭제합니다.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = @ApiResponse(responseCode = "204", description = "삭제 완료")
    )
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
