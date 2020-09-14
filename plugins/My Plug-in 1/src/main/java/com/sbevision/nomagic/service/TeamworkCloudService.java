package main.java.com.sbevision.nomagic.service;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import main.java.com.sbevision.nomagic.utils.Environment;
import main.java.com.sbevision.nomagic.utils.TeamworkCloud;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeamworkCloudService {
  private static final Logger logger = LoggerFactory.getLogger(TeamworkCloudService.class);
  private static TeamworkCloudService teamworkCloudService;
  private Project project;

  public static TeamworkCloudService getInstance() {
    if (teamworkCloudService == null) {
      teamworkCloudService = new TeamworkCloudService();
    }
    return teamworkCloudService;
  }

  public boolean openProject(String projectName) {
    Environment.setProject(projectName);
    boolean result = TeamworkCloud.loadProject(projectName);
    if (result) {
      project = Application.getInstance().getProject();
    } else {
      logger.error("Could not open project: {}", projectName);
    }
    return result;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void closeProject() {
    project = null;
    TeamworkCloud.closeProject();
  }

  public void createSession() {
    // access a singleton instance by using getInstance()
    // only one session can be active, so check this.
    if (!SessionManager.getInstance().isSessionCreated(project)) {
      // create a new session.
      SessionManager.getInstance().createSession(project, "SBE Automated Edit");
      logger.debug("New Session 'SBE Automated Edit' created");
    } else {
      logger.debug("Could not create session, a session already exists");
    }
  }

  public void closeSession() {
    SessionManager.getInstance().closeSession(project);
  }
}
