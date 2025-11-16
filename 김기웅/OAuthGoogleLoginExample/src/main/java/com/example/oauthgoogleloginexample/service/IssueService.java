package com.example.oauthgoogleloginexample.service;

import com.example.oauthgoogleloginexample.common.exception.NotFoundException;
import com.example.oauthgoogleloginexample.domain.Issue;
import com.example.oauthgoogleloginexample.domain.Project;
import com.example.oauthgoogleloginexample.dto.issue.IssueCreateRequest;
import com.example.oauthgoogleloginexample.dto.issue.IssueResponse;
import com.example.oauthgoogleloginexample.dto.issue.IssueUpdateRequest;
import com.example.oauthgoogleloginexample.repository.IssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;
    private final ProjectService projectService;

    @Transactional
    public IssueResponse createUnderProject(Long projectId, IssueCreateRequest request) {
        Project project = projectService.getEntity(projectId);
        Issue issue = Issue.of(
                request.getTitle(),
                request.getDescription(),
                request.getStatus(),
                request.getPriority()
        );
        project.addIssue(issue);
        Issue saved = issueRepository.save(issue);
        return IssueResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public IssueResponse findById(Long id) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("이슈를 찾을 수 없습니다. id=" + id));
        return IssueResponse.from(issue);
    }

    @Transactional(readOnly = true)
    public List<IssueResponse> findAll() {
        return issueRepository.findAllWithProject().stream()
                .map(IssueResponse::from)
                .toList();
    }

    @Transactional
    public IssueResponse update(Long id, IssueUpdateRequest request) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("이슈를 찾을 수 없습니다. id=" + id));
        issue.change(
                request.getTitle(),
                request.getDescription(),
                request.getStatus(),
                request.getPriority()
        );
        return IssueResponse.from(issue);
    }

    @Transactional
    public void delete(Long id) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("삭제하려는 이슈가 존재하지 않습니다. id=" + id));
        issueRepository.delete(issue);
    }
}
