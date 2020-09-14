package main.java.com.sbevision.nomagic.ui.menu;

import com.nomagic.magicdraw.actions.MDAction;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import main.java.com.sbevision.nomagic.service.AttachService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class AttachAction extends MDAction {
  private static final Logger logger = LoggerFactory.getLogger(AttachAction.class);
  private AttachService attachService;
  private Project project;

  public AttachAction(String id, String name) {
    super(id, name, null, null);
    this.attachService = AttachService.getInstance();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    project = Application.getInstance().getProject();
        if (project == null) {
          String message = "Cannot load to 'Digital Thread' no project is open";
          logger.error(message);
          JOptionPane.showMessageDialog(null, message);
          return;
        }

        attachService.execute(true);

    //        Package rootPackage = (Package) project.getPrimaryModel();
    //        Package digitalThread = createPackageIfAbsent(Environment.DIGITAL_THREAD,
    // rootPackage);
    //        Profile profile = StereotypesHelper.getProfile(project,
    //     Environment.DIGITAL_THREAD_PROFILE);
    //        if (profile == null) {
    //          profile = project.getElementsFactory().createProfileInstance();
    //          profile.setName(Environment.DIGITAL_THREAD_PROFILE);
    //          profile.setOwner(digitalThread);
    //
    //          // get a metaclass "Class"
    //          com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class metaClass =
    //     StereotypesHelper.getMetaClassByName(project, "Element");
    //          // create a stereotype, stereotypes will be applicable to classes
    //          Stereotype stereotype = StereotypesHelper.createStereotype(profile,
    //     Environment.DIGITAL_THREAD_STEREOTYPE, Arrays.asList(metaClass));
    //          // create a tag definition
    //          Property property = project.getElementsFactory().createPropertyInstance();
    //          // a tag name
    //          property.setName(Environment.SUBSCRIPTION_FQN);
    //          stereotype.getAttribute().add(property);
    //        }
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
