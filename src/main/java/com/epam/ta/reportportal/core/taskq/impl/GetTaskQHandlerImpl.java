package com.epam.ta.reportportal.core.taskq.impl;

import com.epam.ta.reportportal.commons.ReportPortalUser;
import com.epam.ta.reportportal.commons.querygen.Filter;
import com.epam.ta.reportportal.core.taskq.GetTaskQHandler;
import com.epam.ta.reportportal.dao.TaskQRepository;
import com.epam.ta.reportportal.util.ProjectExtractor;
import com.epam.ta.reportportal.ws.converter.PagedResourcesAssembler;
import com.epam.ta.reportportal.ws.converter.converters.TaskQConverter;
import com.epam.ta.reportportal.model.taskq.TaskQResource;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetTaskQHandlerImpl implements GetTaskQHandler {

  private final TaskQRepository taskQRepository;
  private final ProjectExtractor projectExtractor;

  @Autowired
  public GetTaskQHandlerImpl(TaskQRepository taskQRepository, ProjectExtractor projectExtractor) {
    this.taskQRepository = taskQRepository;
    this.projectExtractor = projectExtractor;
  }

  @Override
  public com.epam.ta.reportportal.model.Page<TaskQResource> getTaskQs(String projectName,
      Pageable pageable, ReportPortalUser user) {
    // resolve project details (will throw if user has no access)
    ReportPortalUser.ProjectDetails projectDetails = projectExtractor.extractProjectDetails(user, projectName);

    Page<com.epam.ta.reportportal.entity.taskq.TaskQ> page = taskQRepository
        .findByProjectId(projectDetails.getProjectId(), pageable);

    return PagedResourcesAssembler.pageConverter(TaskQConverter.TO_RESOURCE).apply(page);
  }
}
