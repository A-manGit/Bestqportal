package com.epam.ta.reportportal.dao;

import com.epam.ta.reportportal.entity.taskq.TaskQ;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskQRepository extends JpaRepository<TaskQ, Integer> {

  Page<TaskQ> findByProjectId(Long projectId, Pageable pageable);

  Page<TaskQ> findByProjectIdAndTitleContainingIgnoreCase(
      Long projectId,
      String title,
      Pageable pageable);

}
