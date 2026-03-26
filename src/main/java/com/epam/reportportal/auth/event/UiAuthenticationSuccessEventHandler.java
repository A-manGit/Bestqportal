/*
 * Copyright 2019 EPAM Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.reportportal.auth.event;

import com.epam.reportportal.auth.commons.ReportPortalUser;
import com.epam.reportportal.auth.dao.ProjectRepository;
import com.epam.reportportal.auth.dao.UserRepository;
import com.epam.reportportal.auth.entity.project.Project;
import com.epam.reportportal.auth.entity.project.ProjectRole;
import com.epam.reportportal.auth.entity.user.ProjectUser;
import com.epam.reportportal.auth.entity.user.User;
import com.epam.reportportal.auth.event.activity.ProjectCreatedEvent;
import com.epam.reportportal.auth.integration.github.RPOAuth2User;
import com.epam.reportportal.auth.rules.exception.ErrorType;
import com.epam.reportportal.auth.rules.exception.ReportPortalException;
import com.epam.reportportal.auth.util.PersonalProjectService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Updates Last Login field in database User entity.
 *
 * @author Andrei Varabyeu
 */
@Component
@RequiredArgsConstructor
public class UiAuthenticationSuccessEventHandler {

  private static final String DEFAULT_PROJECT_NAME = "default";

  private final UserRepository userRepository;
  private final ProjectRepository projectRepository;
  private final PersonalProjectService personalProjectService;
  private final ApplicationEventPublisher eventPublisher;

  /**
   * Handles the UI user signed in event. Updates the last login date for the user and generates a
   * personal project if the user has no projects. Also, if the user is inactive, it will be
   * activated for SAML authentication.
   *
   * @param event the UI user signed in event
   */
  @EventListener
  @Transactional
  public void onApplicationEvent(UiUserSignedInEvent event) {
    String username = event.getAuthentication().getName();

    userRepository.updateLastLoginDate(username);

    ReportPortalUser signedInUser = acquireUser(event.getAuthentication());
    if (MapUtils.isEmpty(signedInUser.getProjectDetails())) {
      User user = userRepository.findByLogin(username)
          .orElseThrow(() -> new ReportPortalException(ErrorType.USER_NOT_FOUND, username));
      if (user.getUserType() == com.epam.reportportal.auth.entity.user.UserType.GITHUB) {
        addUserToDefaultProject(user);
      } else {
        Project project = personalProjectService.generatePersonalProject(user);
        user.getProjects().addAll(project.getUsers());
        eventPublisher.publishEvent(new ProjectCreatedEvent(project.getId(), project.getName()));
      }
    }
  }

  private void addUserToDefaultProject(User user) {
    Project defaultProject = projectRepository.findByName(DEFAULT_PROJECT_NAME)
        .orElseThrow(() -> new ReportPortalException(ErrorType.BAD_REQUEST_ERROR,
            "Default project '" + DEFAULT_PROJECT_NAME + "' was not found"));

    boolean hasMembership = user.getProjects().stream()
        .anyMatch(projectUser -> projectUser.getProject() != null
            && DEFAULT_PROJECT_NAME.equals(projectUser.getProject().getName()));

    if (!hasMembership) {
      ProjectUser membership = new ProjectUser()
          .withUser(user)
          .withProject(defaultProject)
          .withProjectRole(ProjectRole.MEMBER);
      user.getProjects().add(membership);
      defaultProject.getUsers().add(membership);
    }
  }

  private ReportPortalUser acquireUser(Authentication authentication) {
    if (authentication instanceof Saml2Authentication rpAuth) {
      userRepository.findByLogin(rpAuth.getName())
          .filter(user -> !user.getActive())
          .ifPresent(user -> {
            user.setActive(true);
            userRepository.save(user);
          });
      return userRepository.findByLogin(rpAuth.getName())
          .map(user -> ReportPortalUser.userBuilder().fromUser(user))
          .orElseThrow(() -> new ReportPortalException(
              ErrorType.USER_NOT_FOUND, rpAuth.getPrincipal()
          ));
    } else if (authentication.getPrincipal() instanceof RPOAuth2User ghUser) {
      if (!(ghUser.getReportPortalUser()).isEnabled()) {
        SecurityContextHolder.clearContext();
        throw new LockedException("User account is locked");
      }
      return ghUser.getReportPortalUser();
    } else {
      if (!((ReportPortalUser) authentication.getPrincipal()).isEnabled()) {
        SecurityContextHolder.clearContext();
        throw new LockedException("User account is locked");
      }
      return (ReportPortalUser) authentication.getPrincipal();
    }
  }
}
