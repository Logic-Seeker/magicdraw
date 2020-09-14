package main.java.com.sbevision.nomagic.plugin;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import main.java.com.sbevision.nomagic.msg.enums.TopLevelStereotypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CameoAPI {

  private static Logger logger = LoggerFactory.getLogger(CameoAPI.class);

  private static CameoAPI cameoAPI;
  private Project project;

  private CameoAPI() {
  }

  public static CameoAPI getInstance() {
    if (cameoAPI == null) {
      cameoAPI = new CameoAPI();
    }
    return cameoAPI;
  }

  public Project getProject() {
    this.project = Application.getInstance().getProject();
    return this.project;
  }

  /* find models */
  public BaseElement findByID(String id) {
    getProject();
    logger.debug("Searching for element with ID: '{}'", id);
    if (id == null) {
      logger.error("Could not find element with null id");
      return null;
    }
    return project.getElementByID(id);
  }

  /* get */
  public Collection<Element> getAllModels() {

    getProject();

//    Package systemReq =
//        Finder.byQualifiedName()
//            .find(project, "1-System Design::1-Concept Level::3-Behavior::Use Cases");

    Collection<Element> result = new ArrayList<>();
    Collection<Element> resultSpecific = new ArrayList<>();
//    result = systemReq.getOwnedElement();

    logger.debug("element count :{}", result.size());
    for (Element element : result) {
      List<String> stereotypes = getStereotypeList((element));
//      if (TopLevelStereotypes.contains(stereotypes) || TopLevelStereotypes.contains(element.getHumanType())) {
//
//        resultSpecific.add(element);
//      }
    }

    //    for (BaseElement baseElement : baseElements) {
    //      logger.debug("parent name:{}", ((Element) baseElement).getOwner().getHumanName());
    //      if (((Element) baseElement).getOwner().getHumanName().equals("System Requirements")) {
    //
    //        if (baseElement instanceof Operation) {
    //          // special case, we need the Operation object to be loaded
    //          result.add(baseElement);
    //        } else if (baseElement instanceof Element) {
    //          // for elements, we check if it is a stereotype that we care about before adding it
    // to out
    //          // models
    //
    //          List<String> stereotypes = getStereotypeList((Element) baseElement);
    //
    //          if (TopLevelStereotypes.contains(stereotypes)
    //              || TopLevelStereotypes.contains(baseElement.getHumanType())) {
    //            if (logger.isDebugEnabled()) {
    //              logger.debug(
    //                  "Stereotypes for {}: {} : {}",
    //                  baseElement.getHumanName(),
    //                  baseElement.getHumanType(),
    //                  stereotypes);
    //            }
    //            result.add(baseElement);
    //          }
    //        }
    //      }
    //    }

    return resultSpecific;
  }

  public Collection<DirectedRelationship> getAllOutboundRelationsFromModel(Element model) {
    getProject();
    if (model instanceof NamedElement) {
      return model.get_directedRelationshipOfSource();
    } else {
      return Collections.emptyList();
    }
  }

  public Collection<DirectedRelationship> getAllInboundRelationsFromModel(Element model) {
    getProject();
    if (model instanceof NamedElement) {
      return model.get_directedRelationshipOfTarget();
    } else {
      return Collections.emptyList();
    }
  }

  public Collection<Relationship> getAllNonDirectedRelationsFromModel(Element model) {
    getProject();
    Collection<Relationship> result = new ArrayList<>();
    if (model instanceof NamedElement) {
      Collection<Relationship> relationships = model.get_relationshipOfRelatedElement();
      for (Relationship relationship : relationships) {
        if (!(relationship instanceof DirectedRelationship)) {
          result.add(relationship);
        }
      }
      return result;
    } else {
      return Collections.emptyList();
    }
  }

  public Collection<Property> getAllAttributesFromModel(BaseElement model) {
    getProject();
    Class modelWithAttributes;
    if (model instanceof Class) {
      modelWithAttributes = (Class) model;
    } else {
      return Collections.emptyList();
    }

    modelWithAttributes.getAttribute();
    return modelWithAttributes.getOwnedAttribute();
  }

  public static List<String> getStereotypeList(Element mdElement) {
    if (mdElement == null) {
      return Collections.emptyList();
    }

    List<String> stereotypes = new ArrayList<>();

    InstanceSpecification instanceSpecification = mdElement.getAppliedStereotypeInstance();
    if (instanceSpecification != null) {
      logger.debug("{} has instanceSpecification", mdElement.getHumanName());
      for (Classifier classifier : instanceSpecification.getClassifier()) {
        logger.debug(" have classifier name:{}", classifier.getName());
        stereotypes.add(classifier.getName());
      }
    } else {
      stereotypes.add(mdElement.getHumanType());
    }

    return stereotypes;
  }

  public static String getStereotype(Element mdElement) {
    List<String> stereotypes = getStereotypeList(mdElement);

    if (mdElement instanceof Operation) {
      return "Operation";
    }
    if (stereotypes.isEmpty()) {
      return "";
    }

    Boolean isBlock = false;
    for (String stereotype : stereotypes) {
      if (stereotype.equals("Block")) {
        isBlock = true;
      }
      if (TopLevelStereotypes.contains(stereotype) && !stereotype.equals("Block")) {
        return stereotype;
      }
    }
    if (isBlock) {
      return "Block";
    } else {
      return "";
    }
  }

  public static String getName(Element mdElement) {
    try {
      String name;
      if (mdElement instanceof NamedElement) {
        name = ((NamedElement) mdElement).getName();
      } else {
        name = mdElement.getHumanName();
      }
      return name;
    } catch (Exception e) {
      return "";
    }
  }
}
