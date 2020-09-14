package main.java.com.sbevision.nomagic.utils;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectDescriptor;
import com.nomagic.magicdraw.core.project.ProjectsManager;
import com.nomagic.magicdraw.esi.EsiUtils;
import com.nomagic.magicdraw.teamwork2.ServerLoginInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


/**
 * view documentation here {@link https://docs.com.sbevision.nomagic.com/display/MD190/Project+loading+and+saving}
 */
public class TeamworkCloud {

    private static final Logger logger = LoggerFactory.getLogger(TeamworkCloud.class);

    private TeamworkCloud() {
        // hide public constructor for static class
    }

    /**
     * Login to server running in Wakefield with user Administrator.
     */
    private static void login() {
        String server = "wakefield-twcloud.sbe-devops.com";
        logger.debug("Logging into twcloud server: {}", server);
        EsiUtils.getTeamworkService().login(new ServerLoginInfo(server, "Administrator", "sbeTWC2019", false), true);
    }

    public static boolean loadProject(String projectName) {
        TeamworkCloud.login();
        logger.debug("loading project: {}", projectName);
        List<ProjectDescriptor> projects = TeamworkCloud.getProjects();
        if (projects != null) {
            Optional<ProjectDescriptor> projectDescriptorOptional = projects.stream().filter(projectDescriptor -> projectDescriptor.getRepresentationString().equals(projectName)).findFirst();
            ProjectDescriptor project;
            if (projectDescriptorOptional.isPresent()) {
                project = projectDescriptorOptional.get();

            } else {
                logger.error("Could not find project to open, are you logged into teamwork cloud?");
                return false;
            }
            TeamworkCloud.loadProject(project);
            logger.info("Opening project: {}", projects.get(0).getRepresentationString());
            logger.info("with uri: {}", projects.get(0).getURI());
        } else {
            logger.error("Could not find project to open, are you logged into teamwork cloud?");
            return false;
        }

        return true;
    }

    public static String getCurrentProjectName() {
        Application application = Application.getInstance();
        Project project;

        if (application != null) {
            project = application.getProject();
            if (project != null) {
                return project.getName();
            }
        }

        return null;
    }

    private static void loadProject(ProjectDescriptor projectDescriptor) {
        ProjectsManager projectsManager = Application.getInstance().getProjectsManager();
        if (projectDescriptor != null) {
            projectsManager.loadProject(projectDescriptor, true);
        }
    }

    public static void closeProject() {
        logger.debug("Closing project");
        ProjectsManager projectsManager = Application.getInstance().getProjectsManager();
        projectsManager.closeProject();
    }

    /**
     * Prints all projects from server
     */
    private static List<ProjectDescriptor> getProjects() {
        try {
            return EsiUtils.getRemoteProjectDescriptors();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
