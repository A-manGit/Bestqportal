package com.epam.ta.reportportal.entity.taskq;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a task in the taskq table.
 */
@Entity
@Table(name = "taskq")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskQ {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "project_id", nullable = false)
  private Long projectId;

  @Column(nullable = false)
  private String title;

  private String lable;

  private String status;

  @Column(name = "video_url")
  private String videoUrl;

  private String creator;

  private String sub;

  private Boolean automation;

  private String priority;

  @Column(columnDefinition = "TEXT")
  private String summary;

  @Column(columnDefinition = "TEXT")
  private String steps;

  @Column(columnDefinition = "TEXT")
  private String remarks;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

}
