package com.epam.ta.reportportal.model.taskq;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TaskQResource {
  private Integer id;
  private Long projectId;
  private String title;
  private String lable;
  private String status;
  private String videoUrl;
  private String creator;
  private String sub;
  private Boolean automation;
  private String priority;
  private String summary;
  private String steps;
  private String remarks;
  private LocalDateTime createdAt;
}
