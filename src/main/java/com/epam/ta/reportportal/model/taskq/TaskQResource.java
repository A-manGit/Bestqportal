package com.epam.ta.reportportal.model.taskq;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TaskQResource {
  private Integer id;
  private String name;
  private String summary;
  private String status;
  private LocalDateTime createdAt;
  private Long createdBy;
}
