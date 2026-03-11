package com.epam.ta.reportportal.core.taskq;

import com.epam.ta.reportportal.commons.ReportPortalUser;
import com.epam.ta.reportportal.commons.querygen.Filter;
import com.epam.ta.reportportal.model.Page;
import com.epam.ta.reportportal.model.taskq.TaskQResource;
import org.springframework.data.domain.Pageable;

public interface GetTaskQHandler {

  Page<TaskQResource> getTaskQs(String projectName, Pageable pageable,
      ReportPortalUser user);

}
