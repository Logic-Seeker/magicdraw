package main.java.com.sbevision.nomagic.service;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.sbevision.interchange.grpc.AttachProto;
import com.sbevision.interchange.grpc.AttachRequestProto;
import main.java.com.sbevision.nomagic.repository.ConsumerRepository;
import main.java.com.sbevision.nomagic.ui.SbeUI;
import main.java.com.sbevision.nomagic.utils.Environment;
import main.java.com.sbevision.nomagic.utils.TeamworkCloud;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class AttachService {

  private static AttachService attachService;
  private static final Logger logger = LoggerFactory.getLogger(AttachService.class);
  private ConsumerRepository consumerRepository = ConsumerRepository.getInstance();
  private Project project;

  private AttachService() {
    // hide public constructor so everyone needs the getInstance() method
  }

  public static AttachService getInstance() {
    if (attachService == null) {
      attachService = new AttachService();
    }
    return attachService;
  }

  public void execute(boolean openUI) {
    project = Application.getInstance().getProject();
    if (project == null) {
      String message = "Cannot load to 'Digital Thread' no project is open";
      logger.error(message);
      JOptionPane.showMessageDialog(null, message);
      return;
    }

    Package rootPackage = (Package) project.getPrimaryModel();
    Package digitalThread = createPackageIfAbsent(Environment.DIGITAL_THREAD, rootPackage);

    String projectName = TeamworkCloud.getCurrentProjectName();
    AttachRequestProto attachRequestProto = createAttachRequest(projectName);

    try {
      AttachProto attachProto = consumerRepository.attach(attachRequestProto);
      if (!attachProto.getDataSourceId().isEmpty()) {
        Environment.setProject(projectName);
        Environment.setSource(attachProto.getDataSourceId());
        Environment.setChannel(attachProto.getChannel());
        Environment.setAttachUiLink(attachProto.getAttachUiLink());
        Environment.setCreateSubscriptionUiLink(attachProto.getCreateSubscriptionUiLink());
        Environment.setAuthoritativeMergeUiLink(attachProto.getAuthoritativeMergeUiLink());
        Environment.setSubscriptionMergeUiLink(attachProto.getSubscriptionMergeUiLink());
        if (openUI) {
          JOptionPane.showMessageDialog(null, "Attached to digital thread\n" + attachProto);
        }
      } else {
        String url = attachProto.getAttachUiLink();
        String displayName = "Create Digital Thread Channel";
        logger.info("Opening SBE URI: {}", url);
        SbeUI sbeUI = new SbeUI();
        sbeUI.run(url, displayName, false);
      }
    } catch (Exception e) {
      logger.error("The connection to the digital thread was interrupted, could not attach", e);
    }
  }

  public Boolean checkIfAttachedToCorrectProject() {
    if (Environment.project == null) {
      return false;
    }
    String currentProject = TeamworkCloud.getCurrentProjectName();
    return currentProject.equals(Environment.project);
  }

  private AttachRequestProto createAttachRequest(String projectName) {
    return AttachRequestProto.newBuilder()
        .setDataSourceType(Environment.SOURCE_TYPE)
        .putChannelProperties(Environment.SBE_PROJECT_IDENTIFIER, projectName)
        .build();
  }

  private Package createPackageIfAbsent(String digital_thread, Package rootPackage) {
    for (Package subPackage : rootPackage.getNestedPackage()) {
      if (subPackage.getName().equals(digital_thread)) {
        return subPackage;
      }
    }
    Package aPackage = createPackage(digital_thread, rootPackage);
    return aPackage;
  }

  public Package createPackage(String name, Element owner) {
    logger.debug("Creating Package '{}' with owner '{}'", name, owner.getHumanName());
    Package newPackage = project.getElementsFactory().createPackageInstance();
    finishElement(newPackage, name, owner);
    return newPackage;
  }

  private Element finishElement(Element newElement, String name, Element owner) {
    if (newElement instanceof NamedElement && !(name == null || name.isEmpty())) {
      ((NamedElement) newElement).setName(name);
    }
    newElement.setOwner(owner);
    return newElement;
  }
}
