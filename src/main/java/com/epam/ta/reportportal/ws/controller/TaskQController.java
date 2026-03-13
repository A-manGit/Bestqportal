package com.epam.ta.reportportal.ws.controller;

import com.epam.ta.reportportal.commons.ReportPortalUser;
import com.epam.ta.reportportal.core.taskq.GetTaskQHandler;
import com.epam.ta.reportportal.dao.TaskQRepository;
import com.epam.ta.reportportal.entity.taskq.TaskQ;
import com.epam.ta.reportportal.util.ProjectExtractor;
import com.epam.ta.reportportal.ws.converter.converters.TaskQConverter;
import java.time.LocalDateTime;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import com.epam.reportportal.rules.exception.ReportPortalException;
import com.epam.reportportal.rules.exception.ErrorType;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.epam.ta.reportportal.model.Page;
import com.epam.ta.reportportal.model.taskq.TaskQResource;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.epam.ta.reportportal.auth.permissions.Permissions.ASSIGNED_TO_PROJECT;
import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
@RestController
@PreAuthorize(ASSIGNED_TO_PROJECT)
@RequestMapping("/v1/{projectName}/tasks")
public class TaskQController {

    private final GetTaskQHandler getTaskQHandler;
    private final TaskQRepository taskQRepository;
    private final ProjectExtractor projectExtractor;

    @Transactional(readOnly = true)
    @GetMapping
    @ResponseStatus(OK)
    @Operation(summary = "Get all taskq entries for the project")
    public Page<TaskQResource> getTasks(@PathVariable String projectName,
            Pageable pageable,
            @AuthenticationPrincipal ReportPortalUser user) {
        return getTaskQHandler.getTaskQs(projectName, pageable, user);
    }

    @Transactional
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a task in taskq for the project")
    public TaskQResource createTask(@PathVariable String projectName,
            @RequestBody TaskQResource request,
            @AuthenticationPrincipal ReportPortalUser user) {

        // Resolve project id from project name and user
        final ReportPortalUser.ProjectDetails projectDetails = projectExtractor.extractProjectDetails(user,
                projectName);

        // Build entity from request (ignore incoming projectId to trust path/project)
        TaskQ toSave = TaskQ.builder()
                .projectId(projectDetails.getProjectId())
                .title(request.getTitle())
                .lable(request.getLable())
                .status(request.getStatus())
                .videoUrl(request.getVideoUrl())
                .creator(request.getCreator())
                .sub(request.getSub())
                .automation(request.getAutomation())
                .priority(request.getPriority())
                .summary(request.getSummary())
                .steps(request.getSteps())
                .remarks(request.getRemarks())
                .createdAt(LocalDateTime.now())
                .build();
        TaskQ saved = taskQRepository.save(toSave);
        return TaskQConverter.TO_RESOURCE.apply(saved);
    }

    @Transactional
    @PatchMapping("/{id}")
    @Operation(summary = "Patch (partial update) a task in taskq for the project")
    public TaskQResource patchTask(@PathVariable String projectName,
            @PathVariable Integer id,
            @RequestBody TaskQResource request,
            @AuthenticationPrincipal ReportPortalUser user) {

        final ReportPortalUser.ProjectDetails projectDetails = projectExtractor.extractProjectDetails(user,
                projectName);

        TaskQ existing = taskQRepository.findById(id)
                .orElseThrow(() -> new ReportPortalException(ErrorType.NOT_FOUND, id));

        if (!existing.getProjectId().equals(projectDetails.getProjectId())) {
            throw new ReportPortalException(ErrorType.ACCESS_DENIED, id);
        }

        // Apply partial updates only for non-null fields
        if (request.getTitle() != null) {
            existing.setTitle(request.getTitle());
        }
        if (request.getLable() != null) {
            existing.setLable(request.getLable());
        }
        if (request.getStatus() != null) {
            existing.setStatus(request.getStatus());
        }
        if (request.getVideoUrl() != null) {
            existing.setVideoUrl(request.getVideoUrl());
        }
        if (request.getCreator() != null) {
            existing.setCreator(request.getCreator());
        }
        if (request.getSub() != null) {
            existing.setSub(request.getSub());
        }
        if (request.getAutomation() != null) {
            existing.setAutomation(request.getAutomation());
        }
        if (request.getPriority() != null) {
            existing.setPriority(request.getPriority());
        }
        if (request.getSummary() != null) {
            existing.setSummary(request.getSummary());
        }
        if (request.getSteps() != null) {
            existing.setSteps(request.getSteps());
        }
        if (request.getRemarks() != null) {
            existing.setRemarks(request.getRemarks());
        }

        TaskQ saved = taskQRepository.save(existing);

        return TaskQConverter.TO_RESOURCE.apply(saved);
    }

    @Transactional
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a task from taskq for the project")
    public void deleteTask(@PathVariable String projectName,
            @PathVariable Integer id,
            @AuthenticationPrincipal ReportPortalUser user) {

        final ReportPortalUser.ProjectDetails projectDetails = projectExtractor.extractProjectDetails(user,
                projectName);

        TaskQ existing = taskQRepository.findById(id)
                .orElseThrow(() -> new ReportPortalException(ErrorType.NOT_FOUND, id));

        if (!existing.getProjectId().equals(projectDetails.getProjectId())) {
            throw new ReportPortalException(ErrorType.ACCESS_DENIED, id);
        }

        taskQRepository.deleteById(id);
    }

}
