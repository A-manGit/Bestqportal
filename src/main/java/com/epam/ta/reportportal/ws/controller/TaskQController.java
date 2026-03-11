package com.epam.ta.reportportal.ws.controller;

import com.epam.ta.reportportal.commons.ReportPortalUser;
import com.epam.ta.reportportal.core.taskq.GetTaskQHandler;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.epam.ta.reportportal.auth.permissions.Permissions.ASSIGNED_TO_PROJECT;
import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
@RestController
@PreAuthorize(ASSIGNED_TO_PROJECT)
@RequestMapping("/v1/{projectName}/tasks")
public class TaskQController {

    private final GetTaskQHandler getTaskQHandler;

    @Transactional(readOnly = true)
    @GetMapping
    @ResponseStatus(OK)
    @Operation(summary = "Get all taskq entries for the project")
    public Page<TaskQResource> getTasks(@PathVariable String projectName,
            Pageable pageable,
            @AuthenticationPrincipal ReportPortalUser user) {

        // Currently TaskQ doesn't support dynamic Filter-based query builder in commons
        // QueryTarget.
        // We fetch taskq entries by project using pageable and ignore request filters
        // for now.
        return getTaskQHandler.getTaskQs(projectName, pageable, user);
    }

}
