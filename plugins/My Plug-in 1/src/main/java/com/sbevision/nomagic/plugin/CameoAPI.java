package main.java.com.sbevision.nomagic.plugin;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.utils.Counter;
import main.java.com.sbevision.nomagic.msg.enums.TopLevelStereotypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CameoAPI {

  private final Logger logger = LoggerFactory.getLogger(CameoAPI.class);

  private static CameoAPI cameoAPI;
  private Project project;

  private CameoAPI() {}

  public static CameoAPI getInstance() {
    if (cameoAPI == null) {
      cameoAPI = new CameoAPI();
    }
    return cameoAPI;
  }

  public void setProject() {
    this.project = Application.getInstance().getProject();
    Counter counter = project.getCounter();
    counter.setCanResetIDForObject(true);
  }

  /* find models */
  public BaseElement findByID(String id) {
    setProject();
    logger.debug("Searching for element with ID: '{}'", id);
    if (id == null) {
      logger.error("Could not find element with null id");
      return null;
    }
    return project.getElementByID(id);
  }

  /* get */
  public Collection<BaseElement> getAllModels() {
    setProject();
    Collection<BaseElement> baseElements = project.getAllElements();
    Collection<BaseElement> result = new ArrayList<>();

    for (BaseElement baseElement : baseElements) {
      if (baseElement instanceof Operation) {
        // special case, we need the Operation object to be loaded.
        result.add(baseElement);
      } else if (baseElement instanceof Element) {
        List<String> stereotypes = getStereotypeList((Element) baseElement);
        // TopLevelStereotypes contains list of stereotype. Only those on the list are returned.
        if (TopLevelStereotypes.contains(stereotypes)) {
          result.add(baseElement);
        }
      }
    }

    return result;
  }

  public Collection<DirectedRelationship> getAllOutboundRelationsFromModel(Element model) {
    setProject();
    if (model instanceof NamedElement) {
      return model.get_directedRelationshipOfSource();
    } else {
      return Collections.emptyList();
    }
  }

  public Collection<DirectedRelationship> getAllInboundRelationsFromModel(Element model) {
    setProject();
    if (model instanceof NamedElement) {
      return model.get_directedRelationshipOfTarget();
    } else {
      return Collections.emptyList();
    }
  }

  public Collection<Relationship> getAllNonDirectedRelationsFromModel(Element model) {
    setProject();
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
    setProject();
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
      for (Classifier classifier : instanceSpecification.getClassifier()) {
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

  public Collection<Element> getElementsBasedOnPackageQualifiedName(String packageQualifiedName) {
    Package systemReq = Finder.byQualifiedName().find(project, packageQualifiedName);

    Collection<Element> ownedElements = systemReq.getOwnedElement();
    List<Element> result = new ArrayList<>();

    for (Element element : ownedElements) {
      List<String> stereotypes = getStereotypeList((element));
      if (TopLevelStereotypes.contains(stereotypes)
              || TopLevelStereotypes.contains(element.getHumanType())) {

        result.add(element);
      }
    }
    return result;
  }
}
