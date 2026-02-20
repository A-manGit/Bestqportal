package com.epam.ta.reportportal.ws.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.epam.ta.reportportal.commons.ReportPortalUser;
import com.epam.ta.reportportal.commons.querygen.Filter;
import com.epam.ta.reportportal.entity.dashboard.Dashboard;
import com.epam.ta.reportportal.model.Page;
import com.epam.ta.reportportal.model.dashboard.DashboardResource;
import com.epam.ta.reportportal.ws.resolver.FilterFor;
import com.epam.ta.reportportal.ws.resolver.SortFor;

import io.swagger.v3.oas.annotations.Operation;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static com.epam.ta.reportportal.auth.permissions.Permissions.ASSIGNED_TO_PROJECT;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@PreAuthorize(ASSIGNED_TO_PROJECT)
@RequestMapping("/v1/{projectName}/tasks")
public class TaskQController {
    @Transactional(readOnly = true)
    @GetMapping
    @ResponseStatus(OK)
    @Operation(summary = "Get all takskQ")
    public String getTests(@PathVariable String projectName,
            @SortFor(Dashboard.class) Pageable pageable,
            @AuthenticationPrincipal ReportPortalUser user) {

        return "This is a success message" ;
    }

}
