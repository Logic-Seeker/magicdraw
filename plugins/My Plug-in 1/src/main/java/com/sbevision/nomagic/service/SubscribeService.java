package main.java.com.sbevision.nomagic.service;

import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.CallBehaviorAction;
import com.nomagic.uml2.ext.magicdraw.classes.mddependencies.Abstraction;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.sbevision.common.grpc.ValueProto;
import com.sbevision.common.grpc.operation.OperationProto;
import com.sbevision.common.grpc.operation.OperationStatus;
import com.sbevision.interchange.grpc.*;
import main.java.com.sbevision.nomagic.plugin.CameoAPI;
import main.java.com.sbevision.nomagic.plugin.MagicdrawHelper;
import main.java.com.sbevision.nomagic.repository.ConsumerRepository;
import main.java.com.sbevision.nomagic.utils.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SubscribeService {
  private static final Logger logger = LoggerFactory.getLogger(SubscribeService.class);
  public static final String ELEMENT_UNSUPPORTED =
      "Models with stereotype '{}' must exist on another element, please load that element with a relation to this element";
  private ConsumerRepository consumerRepository = ConsumerRepository.getInstance();
  private static SubscribeService subscribeService;

  private static MagicdrawHelper mdHelper = MagicdrawHelper.getInstance();
  private CameoAPI cameoAPI = CameoAPI.getInstance();

  public static SubscribeService getInstance() {
    if (subscribeService == null) {
      subscribeService = new SubscribeService();
    }
    return subscribeService;
  }

  public void execute(PullType pullType) {
    mdHelper.setProject();

    consumerRepository.pull(
        PullRequestProto.newBuilder()
            .setDataSource(Environment.source)
            .setChannel(Environment.channel)
            .setSource(pullType)
            .build());
  }

  public OperationResultProto handleExternalItem(
      ExternalItemProto externalItemProto, Package sbePackage) {
    Element element = null;

    if (externalItemProto.getOperation().equals(OperationProto.CREATE)) {
      element = findOrCreate(externalItemProto, sbePackage);
    } else if (externalItemProto.getOperation().equals(OperationProto.UPDATE)) {
      element = update(externalItemProto, sbePackage);
    }

    OperationStatus resultStatus;
    CorrelationProto resultCorrelation;
    resultCorrelation = externalItemProto.getCorrelation();
    logger.debug("element id: {}", element.getID());
    Map externalLocator = getExternalLocator(element);
    if (element == null) {
      resultStatus = OperationStatus.FAILURE;

    } else {
      resultStatus = OperationStatus.SUCCESS;
    }
    resultCorrelation =
        resultCorrelation.toBuilder().putAllExternalLocator(externalLocator).build();

    // Create operation sendOperationResult
    return OperationResultProto.newBuilder()
        .setOperationCorrelationId(externalItemProto.getOperationCorrelationId())
        .setOperationStatus(resultStatus)
        .setCorrelation(resultCorrelation)
        .build();
  }

  private Map getExternalLocator(Element element) {
    Map<String, String> externalLocator = new HashMap<>();
    externalLocator.put("external_id", element.getID());
    return externalLocator;
  }

  // update attributes, relations, name
  private Element update(ExternalItemProto externalItemProto, Element owner) {
    String id =
        externalItemProto.getCorrelation().getExternalLocatorMap().get(Environment.EXTERNAL_ID);

    BaseElement baseElement = cameoAPI.findByID(id);
    if (!(baseElement instanceof Element)) {
      return null;
    }
    Element updatedElement = (Element) baseElement;
    if (baseElement instanceof Class) {
      ((Class) baseElement).getAppliedStereotypeInstance();
    }

    // todo: move displayname out to a helper method
    if (baseElement instanceof NamedElement) {
      String displayName;
      try {

        displayName =
            externalItemProto
                .getShapesList()
                .iterator()
                .next()
                .getAttributesMap()
                .get("Display Name")
                .getUnaryValue()
                .getStringValue();
      } catch (Exception e) {
        displayName = externalItemProto.getCorrelation().getExternalName();
      }
      ((NamedElement) baseElement).setName(displayName);
    }

    setRelations(externalItemProto, updatedElement, owner);

    String type = externalItemProto.getCorrelation().getDataSourceId();
    Map<String, ValueProto> attributes =
        externalItemProto.getShapesList().get(0).getAttributesMap();
    setProperties(attributes, updatedElement, type);

    return updatedElement;
  }

  private Element findOrCreate(ExternalItemProto externalItemProto, Element owner) {
    String externalLifecycleId =
        externalItemProto.getCorrelation().getExternalLocatorMap().get(Environment.EXTERNAL_ID);
    if (externalLifecycleId == null || externalLifecycleId.isEmpty()) {
      Element element = create(externalItemProto, owner);
      return element;
    } else {
      return findOrCreate(externalItemProto, owner, externalLifecycleId);
    }
  }

  private Element findOrCreate(
      ExternalItemProto externalItemProto, Element owner, String externalLifecycleId) {
    String id =
        externalItemProto.getCorrelation().getExternalLocatorMap().get(Environment.EXTERNAL_ID);
    BaseElement baseElement = cameoAPI.findByID(id);

    if (baseElement == null) {
      return create(externalItemProto, owner, externalLifecycleId);
    } else {
      if (!(baseElement instanceof Element)) {
        return null;
      }
      Element result = (Element) baseElement;
      return result;
    }
  }

  private Element create(ExternalItemProto externalItemProto, Element owner) {
    logger.debug("Creating Object without existing Id");

    Element newElement;
    // findOrCreate element
    String type = externalItemProto.getShapesList().iterator().next().getShapeId();
    String externalLifecycleName = externalItemProto.getCorrelation().getExternalName();
    String displayName;

    try {
      displayName =
          externalItemProto
              .getShapesList()
              .iterator()
              .next()
              .getAttributesMap()
              .get("Display Name")
              .getUnaryValue()
              .getStringValue();
    } catch (Exception e) {
      displayName = externalLifecycleName;
    }
    logger.debug("Creating new Object '{}' as type '{}'", displayName, type);
    switch (type) {
      case "Requirement":
      case "Constraint":
        newElement = mdHelper.createClass(displayName, owner);
        mdHelper.addStereotype(newElement, type);
        break;
      case "Role":
        newElement = mdHelper.createRole(displayName, owner);
        break;
      case "UseCase":
        newElement = mdHelper.createUseCase(displayName, owner);
        break;
      case "Activity":
        newElement = mdHelper.createActivity(displayName, owner);
        break;
      case "Actor":
        newElement = mdHelper.creatActor(displayName, owner);
        break;
      default:
        newElement = mdHelper.createBlock(displayName, owner);
        logger.debug("Default Stereotype");
        mdHelper.addStereotype(newElement, type);
    }

    setProperties(externalItemProto.getShapesList().get(0).getAttributesMap(), newElement, type);
    return newElement;
  }

  private Element create(
      ExternalItemProto externalItemProto, Element owner, String externalLifecycleId) {
    logger.debug("Creating Object with existing Id: {}", externalLifecycleId);
    Element newElement;
    // findOrCreate element
    String type = externalItemProto.getShapesList().iterator().next().getShapeId();
    String externalLifecycleName =
        externalItemProto.getCorrelation().getExternalLocatorMap().get(Environment.EXTERNAL_ID);
    String displayName;

    try {
      displayName =
          externalItemProto
              .getShapesList()
              .iterator()
              .next()
              .getAttributesMap()
              .get("Display Name")
              .getUnaryValue()
              .getStringValue();
    } catch (Exception e) {
      displayName = externalLifecycleName;
    }
    logger.debug("Creating new Object '{}' as type '{}'", displayName, type);
    switch (type) {
      case "Requirement":
      case "Constraint":
        newElement = mdHelper.createClass(displayName, owner, externalLifecycleId);
        mdHelper.addStereotype(newElement, type);
        break;
      case "Role":
        newElement = mdHelper.createRole(displayName, owner, externalLifecycleId);
        break;
      case "UseCase":
        newElement = mdHelper.createUseCase(displayName, owner, externalLifecycleId);
        break;
      case "Activity":
        newElement = mdHelper.createActivity(displayName, owner, externalLifecycleId);
        break;
      case "Actor":
        newElement = mdHelper.creatActor(displayName, owner);
        break;
      default:
        newElement = mdHelper.createBlock(displayName, owner);
        mdHelper.addStereotype(newElement, type);
    }

    // go through properties
    setProperties(externalItemProto.getShapesList().get(0).getAttributesMap(), newElement, type);

    return newElement;
  }

  private void setSubscriptionFqn(
      String subscription_fqn, Element newElement, String digitalThread) {
    Stereotype stereotype =
        StereotypesHelper.getAppliedStereotypeByString(newElement, digitalThread);
    logger.debug("Found DigitalThread stereotype: {}", stereotype.getName());
    Property id = StereotypesHelper.getPropertyByName(stereotype, Environment.SUBSCRIPTION_FQN);
    if (id == null) {
      logger.debug("Failed to set property, {} not available", Environment.SUBSCRIPTION_FQN);
    } else {
      StereotypesHelper.setStereotypePropertyValue(
          newElement, stereotype, Environment.SUBSCRIPTION_FQN, subscription_fqn);
    }
  }

  private void setProperties(Map<String, ValueProto> props, Element element, String type) {
    logger.debug("Properties");
    for (Map.Entry<String, ValueProto> attribute : props.entrySet()) {
      String attributeName = attribute.getKey();
      String attributeValue = attribute.getValue().getUnaryValue().getStringValue();
      setProperty(element, type, attributeName, attributeValue);
    }
  }

  private void setProperty(Element element, String stereotypeString, String key, String value) {
    logger.debug("Setting key '{}' on '{}' to '{}'", key, element.getHumanName(), value);

    boolean hasStereotypePropertyValues = StereotypesHelper.hasStereotypePropertyValues(element);
    logger.debug("hasStereotypePropertyValues: {}", hasStereotypePropertyValues);

    Stereotype stereotype =
        StereotypesHelper.getAppliedStereotypeByString(element, stereotypeString);
    if (logger.isDebugEnabled() && stereotype != null) {
      logger.debug("Found stereotype: {}", stereotype.getName());
    }

    stereotype = StereotypesHelper.getAppliedStereotypeByString(element, stereotypeString);
    Property id = StereotypesHelper.getPropertyByName(stereotype, key);
    if (id == null) {
      logger.debug("Failed to set property, {} not available", key);
    } else {
      StereotypesHelper.setStereotypePropertyValue(element, stereotype, key, value);
    }
  }

  private void setRelations(ExternalItemProto externalItemProto, Element element, Element owner) {

    // todo: same as update?
    for (AssociationProto association : externalItemProto.getAssociationsList()) {
      String relationName = association.getLinkName();
      OperationProto operation = association.getOperation();
      AssociationDirectionProto relationDirection = association.getDirection();
      logger.debug("Adding association to '{}' ", relationName);
      BaseElement related =
          cameoAPI.findByID(
              association.getItem().getExternalLocatorMap().get(Environment.EXTERNAL_ID));
      if (!(related instanceof Element)) {
        logger.error("Could not add object property to non-element model");
        return;
      }
      Element relatedElement = (Element) related;

      // only set the relation if it is outbound
      if (AssociationDirectionProto.OUTBOUND.equals(relationDirection)) {
        // todo: does these need a stereotype?
        if ("Association".equals(relationName)) {
          logger.debug("Association Relation:{}", element.getHumanName());
          mdHelper.createAssociation(owner, element, relatedElement);
        } else if ("Generalization".equals(relationName)) {
          logger.debug("Generalization Relation:{}", element.getHumanName());
          mdHelper.createGeneralization(relationName, owner, element, relatedElement);
        } else if ("Generalizes".equals(relationName)) {
          logger.debug("Generalizes Relation:{}", element.getHumanName());
          mdHelper.createGeneralization(relationName, owner, element, relatedElement);
        } else if ("Refine".equals(relationName)) {
          if (operation.equals(OperationProto.DELETE)) {
            logger.debug("Deleting Relation:{}", element.getHumanName());
            findAndDeleteRefineRelation(owner, element, relatedElement);
          } else {
            logger.debug("Refine Relation:{}", element.getHumanName());
            findOrCreateRefineRelation(owner, element, relatedElement, "Refine");
          }
        } else if ("Satisfy".equals(relationName)) {
          if (operation.equals(OperationProto.DELETE)) {
            logger.debug("Deleting Relation:{}", element.getHumanName());
            findAndDeleteRefineRelation(owner, element, relatedElement);
          } else {
            logger.debug("Refine Relation:{}", element.getHumanName());
            findOrCreateRefineRelation(owner, element, relatedElement, "Satisfy");
          }
        } else if ("Operation".equals(relationName)) {
          // todo: handle this relation type
        } else if ("Containment".equals(relationName)) {
          logger.debug("Containment Relation:{}", element.getHumanName());
          mdHelper.changeOwner(element, relatedElement);
        } else if ("DeriveReqt".equals(relationName)) {
          if (operation.equals(OperationProto.DELETE)) {
            logger.debug("Deleting Relation:{}", element.getHumanName());
            findAndDeleteRefineRelation(owner, element, relatedElement);
          } else {
            logger.debug("Refine Relation:{}", element.getHumanName());
            findOrCreateRefineRelation(owner, element, relatedElement, "DeriveReqt");
          }
        } else if ("Realization".equals(relationName)) {
          logger.debug("Realization Relation:{}", element.getHumanName());
          mdHelper.createRealization(owner, element, relatedElement);
        }
      }
    }
    LinkedList<CallBehaviorAction> elementList = new LinkedList<>();
    elementList.add(null);
    elementList.add(null);
    elementList.add(null);
    elementList.add(null);

    int size = 0;

    logger.debug("No of composition: {}", externalItemProto.getCompositionsList().size());
    for (CompositionProto compositionProto : externalItemProto.getCompositionsList()) {
      if (compositionProto.getShapes(0).getShapeId().equals("Operation")) {

        BaseElement related =
            cameoAPI.findByID(
                compositionProto.getExternalLocatorMap().get(Environment.EXTERNAL_ID));
        mdHelper.createOperation(compositionProto.getExternalName(), element);
      }
      if (compositionProto.getShapes(0).getShapeId().equals("actionStep")) {
        size = size + 1;
        int order =
            Integer.valueOf(
                compositionProto
                    .getShapes(0)
                    .getAttributesMap()
                    .get("StepNumber")
                    .getUnaryValue()
                    .getStringValue());
        logger.debug("Order no: {}", order);

        String name =
            compositionProto
                .getShapes(0)
                .getAttributesMap()
                .get("Description")
                .getUnaryValue()
                .getStringValue();
        elementList.add(order - 1, mdHelper.createCallBehaviorAction(name, element));
      }
      logger.debug("size {}", size);
    }
    logger.debug("element size:{}", elementList.size());
    for (int i = 0; i < size - 1; i++) {
      logger.debug(
          "creating control flow {} {}",
          elementList.get(i).getName(),
          elementList.get(i + 1).getName());
      mdHelper.createControlFlow(elementList.get(i), elementList.get(i + 1), element);
    }
    size = 0;
  }

  private void findOrCreateRefineRelation(
      Element owner, Element element, Element relatedElement, String newStereotypeString) {
    Collection<DirectedRelationship> directedRelationshipList =
        cameoAPI.getAllOutboundRelationsFromModel(element);

    Iterator iterator = directedRelationshipList.iterator();

    while (iterator.hasNext()) {
      Relationship directedRelationship = (DirectedRelationship) iterator.next();
      if (((DirectedRelationship) directedRelationship).getTarget().contains(relatedElement)) {
        return;
      }
    }
    Abstraction abstraction = mdHelper.createAbstraction(owner, element, relatedElement);
    mdHelper.addSysMLStereotype(abstraction, newStereotypeString);
  }

  private void findAndDeleteRefineRelation(Element owner, Element element, Element relatedElement) {
    Collection<DirectedRelationship> directedRelationshipList =
        cameoAPI.getAllOutboundRelationsFromModel(element);

    Iterator iterator = directedRelationshipList.iterator();

    while (iterator.hasNext()) {
      Relationship directedRelationship = (DirectedRelationship) iterator.next();
      if (((DirectedRelationship) directedRelationship).getTarget().contains(relatedElement)) {
        ((DirectedRelationship) directedRelationship).dispose();
      }
    }
  }
}
