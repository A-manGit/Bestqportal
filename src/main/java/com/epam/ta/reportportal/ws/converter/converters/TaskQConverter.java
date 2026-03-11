package com.epam.ta.reportportal.ws.converter.converters;

import com.epam.ta.reportportal.entity.taskq.TaskQ;
import com.epam.ta.reportportal.model.taskq.TaskQResource;
import java.util.function.Function;

public final class TaskQConverter {

  private TaskQConverter() {
    // static only
  }

  public static final Function<TaskQ, TaskQResource> TO_RESOURCE = entity -> {
    TaskQResource r = new TaskQResource();
    r.setId(entity.getId());
    r.setName(entity.getName());
    r.setSummary(entity.getSummary());
    r.setStatus(entity.getStatus());
    r.setCreatedAt(entity.getCreatedAt());
    r.setCreatedBy(entity.getCreatedBy());
    return r;
  };
}
