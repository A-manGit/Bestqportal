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
    r.setProjectId(entity.getProjectId());
    r.setTitle(entity.getTitle());
    r.setLable(entity.getLable());
    r.setStatus(entity.getStatus());
    r.setVideoUrl(entity.getVideoUrl());
    r.setCreator(entity.getCreator());
    r.setSub(entity.getSub());
    r.setAutomation(entity.getAutomation());
    r.setPriority(entity.getPriority());
    r.setSummary(entity.getSummary());
    r.setSteps(entity.getSteps());
    r.setRemarks(entity.getRemarks());
    r.setCreatedAt(entity.getCreatedAt());
    return r;
  };
}
