package main.java.com.sbevision.nomagic.plugin;//package main.java.com.sbevision.nomagic.plugin;
//
//import com.nomagic.uml2.ext.jmi.helpers.CoreHelper;
//import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
//import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
//import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
//import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
//import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
////import com.sbevision.common.grpc.UnaryValueProto;
////import com.sbevision.common.grpc.ValueProto;
////import com.sbevision.interchange.grpc.*;
////import com.sbevision.nomagic.msg.enums.TopLevelStereotypes;
////import com.sbevision.nomagic.utils.Environment;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.*;
//
//import static com.nomagic.magicdraw.sysml.util.SysMLProfile.isRequirement;
//
//public class ExternalItemConvertor {
//  public static final String OUTBOUND = "OUTBOUND";
//  public static final String INBOUND = "INBOUND";
//  private static final Logger logger = LoggerFactory.getLogger(ExternalItemConvertor.class);
//  private static CameoAPI cameoAPI = CameoAPI.getInstance();
//  private static MagicdrawHelper mdHelper = MagicdrawHelper.getInstance();
//
//  private ExternalItemConvertor() {
//    // hide public constructor for static only class
//  }
//
//  public static ExternalItemProto toExternalItem(Element mdElement) {
//    mdHelper.setProject();
//    if (mdElement == null) {
//      return null;
//    }
//    ExternalItemProto.Builder externalItem = createExternalItemBuilder();
//    addShapes(externalItem, mdElement);
//    addAssociationRelation(externalItem, mdElement);
//    addCompositionAggregationAndAggregateAssociationFromOwnedElement(externalItem, mdElement);
//    externalItem.setCorrelation(getCorrelation(mdElement));
//
//    logger.debug("External Item name: {}", externalItem.getCorrelation().getExternalName());
//
//    return externalItem.build();
//  }
//
//  private static void addCompositionAggregationAndAggregateAssociationFromOwnedElement(ExternalItemProto.Builder externalItem, Element mdElement) {
//    logger.debug("Main Object : {}", mdElement.getHumanName());
//
//    for (Element element : mdElement.getOwnedElement()) {
//      if (element.getHumanType().equals("Part Property")) {
//        addAggregationRelation(externalItem, element);
//      } else if (element.getHumanType().equals("Port")) {
//        addCompositionRelation(externalItem, element);
//      } else if (element.getHumanType().equals("Connector")) {
//        logger.debug("Connector type");
//        addAggregateAssociationRelation(externalItem, element);
//      } else if (element.getHumanType().equals("Action")) {
//        logger.debug("type of object:{}", element.getHumanType());
//        logger.debug("name of object:{}", element.getHumanName());
//      }
//    }
//    logger.debug("No of aggregateAssociation : {}", externalItem.getAggregateAssociationsCount());
//  }
//
//  private static void addAggregateAssociationRelation(ExternalItemProto.Builder externalItem, Element element) {
//
//    AggregateAssociationProto.Builder aggregateAssociationProto = AggregateAssociationProto.newBuilder();
//
//    Connector connector = (Connector) element;
//    ConnectorEnd origin = connector.getEnd().get(0);
//    ConnectorEnd destination = connector.getEnd().get(1);
//
//    String originType = origin.getRole().getHumanType();
//    String destinationType = destination.getRole().getHumanType();
//
//    aggregateAssociationProto.setLinkName("Connector");
//
//    if (originType.equals("Port")) {
//      try {
//        aggregateAssociationProto.setSourceItem(getCorrelation(origin.getPartWithPort()));
//        aggregateAssociationProto.setOwningSourceItem(getCorrelation(origin.getPartWithPort().getOwner()));
//        aggregateAssociationProto.setSourcePath(origin.getRole().getName());
//        if (destinationType.equals("Port")) {
//          aggregateAssociationProto.setDestinationItem(getCorrelation(destination.getPartWithPort()));
//          aggregateAssociationProto.setDestinationPath(destination.getRole().getName());
//          aggregateAssociationProto.setOwningDestinationItem(getCorrelation(destination.getPartWithPort().getOwner()));
//        } else if (destinationType.equals("Part Property")) {
//          aggregateAssociationProto.setDestinationItem(getCorrelation(destination.getRole().getType()));
//          aggregateAssociationProto.setOwningDestinationItem(getCorrelation(destination.getOwner()));
//        } else {
//          return;
//        }
//      } catch (Exception e) {
//        return;
//      }
//    } else if (originType.equals("Part Property")) {
//      try {
//        aggregateAssociationProto.setSourceItem(getCorrelation(origin.getRole()));
//        aggregateAssociationProto.setOwningSourceItem(getCorrelation(origin.getRole().getOwner()));
//        if (destinationType.equals("Port")) {
//          aggregateAssociationProto.setDestinationItem(getCorrelation(destination.getPartWithPort()));
//          aggregateAssociationProto.setDestinationPath(destination.getRole().getName());
//          aggregateAssociationProto.setOwningDestinationItem(getCorrelation(destination.getPartWithPort().getOwner()));
//        } else if (destinationType.equals("Part Property")) {
//          aggregateAssociationProto.setDestinationItem(getCorrelation(destination.getRole()));
//          aggregateAssociationProto.setOwningDestinationItem(getCorrelation(destination.getRole().getOwner()));
//        } else {
//          return;
//        }
//      } catch (Exception e) {
//        logger.debug("Port is not part of aggregate object");
//        return;
//      }
//    } else {
//      return;
//    }
//    externalItem.addAggregateAssociations(aggregateAssociationProto.build());
//  }
//
//  private static void addAggregationRelation(ExternalItemProto.Builder externalItem, Element element) {
//    AggregationProto.Builder aggregation = AggregationProto.newBuilder();
//    aggregation.setExternalName(((NamedElement) element).getName());
//    aggregation.putExternalLocator(Environment.EXTERNAL_ID, element.getID());
//    aggregation.setOccurrenceType("Part Property");
//    if (((Property) element).getType() != null) {
//      aggregation.setTarget(getCorrelation((Element) (((Property) element).getType())));
//    }
//    aggregation.putAllAttributes(getAttributes(element));
//    externalItem.addAggregations(aggregation.build());
//  }
//
//  private static void addCompositionRelation(ExternalItemProto.Builder externalItem, Element element) {
//
//    CompositionProto.Builder composition = CompositionProto.newBuilder();
//    composition.setExternalName(((NamedElement) element).getName());
//    composition.putExternalLocator(Environment.EXTERNAL_ID, element.getID());
//    composition.addAllShapes(getAllShape(element));
//    composition.addAllAssociations(getAllAssociations(element));
//    externalItem.addCompositions(composition.build());
//  }
//
//  private static Iterable<? extends AssociationProto> getAllAssociations(Element element) {
//    List<AssociationProto> associationProtoList = new LinkedList<>();
//    String id = element.getID();
//    if (((Port) element).hasEnd()) {
//      Collection<ConnectorEnd> connector = ((Port) element).getEnd();
//      Iterator connectorEndIterator = connector.iterator();
//      while (connectorEndIterator.hasNext()) {
//        ConnectorEnd connectorEnd = (ConnectorEnd) connectorEndIterator.next();
//        if (connectorEnd.get_connectorOfEnd().getEnd().size() == 2) {
//          ConnectorEnd originElement = (ConnectorEnd) connectorEnd.get_connectorOfEnd().getEnd().get(0);
//          ConnectorEnd destinationElement = (ConnectorEnd) connectorEnd.get_connectorOfEnd().getEnd().get(1);
//
//          if (id.equals(originElement.getRole().getID())) {
//            AssociationProto.Builder associationProto = AssociationProto.newBuilder();
//            associationProto.setPath(destinationElement.getRole().getName());
//            associationProto.setLinkName("Port");
//            associationProto.setDirection(AssociationDirectionProto.OUTBOUND);
//            try {
//              associationProto.setItem(getCorrelation(destinationElement.getPartWithPort()));
//              logger.debug("Destination : {}", destinationElement.getPartWithPort().getName());
//              associationProto.setOwningItem(getCorrelation(destinationElement.getPartWithPort().getOwner()));
//              logger.debug("Destination owner : {}", destinationElement.getPartWithPort().getOwner());
//
//            } catch (Exception e) {
//              associationProto.setItem(getCorrelation(destinationElement.getRole()));
//            }
//            associationProtoList.add(associationProto.build());
//          }
//
//          //          logger.debug(
//          //              "element name: {},element id: {},owner name: {},,owner id: {}",
//          //              originElement.getRole().getName(),
//          //              originElement.getRole().getID(),
//          //              originElement.getRole().getOwner().getHumanName(),
//          //              originElement.getRole().getOwner().getID());
//          //
//          //          if (destinationElement.getRole().getEnd().iterator().hasNext()) {
//          //
//          //            try {
//          //
//          //              logger.debug(
//          //                  "element name: {},element id: {},owner name: {},,owner id: {}",
//          //                  destinationElement.getRole().getName(),
//          //                  destinationElement.getRole().getID(),
//          //                  destinationElement
//          //                      .getRole()
//          //                      .getEnd()
//          //                      .iterator()
//          //                      .next()
//          //                      .getPartWithPort()
//          //                      .getHumanName(),
//          //                  destinationElement
//          //                      .getRole()
//          //                      .getEnd()
//          //                      .iterator()
//          //                      .next()
//          //                      .getPartWithPort()
//          //                      .getID());
//          //            } catch (Exception e) {
//          //              logger.debug("no part with port");
//          //            }
//          //          }
//        }
//      }
//    }
//
//    return associationProtoList;
//  }
//
//  private static Iterable<? extends ShapeProto> getAllShape(Element element) {
//    List<ShapeProto> shapeProtos = new ArrayList<>();
//    InstanceSpecification instanceSpecification = ((Element) element).getAppliedStereotypeInstance();
//    if (instanceSpecification != null) {
//      for (Classifier classifier : instanceSpecification.getClassifier()) {
//        ShapeProto.Builder shapeProto = ShapeProto.newBuilder();
//        shapeProto.setShapeId(classifier.getName());
//        shapeProto.setCategory(ShapeCategoryProto.TYPE);
//        shapeProto.putAllAttributes(getValueProtoAttributes(element));
//        shapeProtos.add(shapeProto.build());
//      }
//    } else {
//      if (element.getHumanType().equals("Port")) {
//        ShapeProto.Builder shapeProto = ShapeProto.newBuilder();
//        shapeProto.setShapeId(element.getHumanType());
//        shapeProto.setCategory(ShapeCategoryProto.TYPE);
//        shapeProto.putAllAttributes(getValueProtoAttributes(element));
//        shapeProtos.add(shapeProto.build());
//      }
//    }
//    return shapeProtos;
//  }
//
//  private static ExternalItemProto.Builder createExternalItemBuilder() {
//    ExternalItemProto.Builder externalItem = ExternalItemProto.newBuilder();
//    return externalItem;
//  }
//
//  public static CorrelationProto.Builder getCorrelation(Element mdElement) {
//    CorrelationProto.Builder correlation = CorrelationProto.newBuilder();
//    correlation.setDataSourceId(Environment.getSource());
//    correlation.setExternalName(CameoAPI.getName(mdElement));
//
//    correlation.putExternalLocator(Environment.EXTERNAL_ID, mdElement.getID());
//    logger.debug("external item:{}, id:{}", mdElement.getHumanName(), mdElement.getID());
//    correlation.setChannelName(Environment.channel);
//    return correlation;
//  }
//
//  /**
//   * There is only one shape for now {@link ShapeCategoryProto#TYPE}
//   *
//   * @param externalItem
//   * @param mdElement
//   */
//  private static void addShapes(ExternalItemProto.Builder externalItem, Element mdElement) {
//
//    externalItem.addShapes(getShape(mdElement, ShapeCategoryProto.TYPE));
//  }
//
//  private static ShapeProto getShape(Element mdElement, ShapeCategoryProto shapeCategory) {
//    ShapeProto.Builder shape = ShapeProto.newBuilder();
//    shape.setShapeId(CameoAPI.getStereotype(mdElement));
//    shape.setCategory(shapeCategory);
//    getAttributes(shape, mdElement);
//    return shape.build();
//  }
//
//  private static void addAssociationRelation(ExternalItemProto.Builder externalItem, Element mdElement) {
//
//    // todo: simplify links/make resuable
//    for (DirectedRelationship dependency : cameoAPI.getAllOutboundRelationsFromModel(mdElement)) {
//      for (Element dependencySource : dependency.getTarget()) {
//        AssociationProto.Builder association = createObjectProperty(dependencySource, OUTBOUND);
//        addLinkName(association, dependency);
//        externalItem.addAssociations(association);
//      }
//    }
//    for (DirectedRelationship dependency : cameoAPI.getAllInboundRelationsFromModel(mdElement)) {
//      for (Element dependencySource : dependency.getSource()) {
//        AssociationProto.Builder association = createObjectProperty(dependencySource, INBOUND);
//        addLinkName(association, dependency);
//        externalItem.addAssociations(association);
//      }
//    }
//
//    String elementHumanName = mdElement.getHumanName();
//    //    logger.debug("Non directed Relationships for {}", elementHumanName);
//    //    for (Relationship dependency : cameoAPI.getAllNonDirectedRelationsFromModel(mdElement)) {
//    //      for (Element related : dependency.getRelatedElement()) {
//    //        // Note: there should only be on owned element for these relationships
//    //        Collection<Element> ownedElements = dependency.getOwnedElement();
//    //        AssociationProto.Builder association = null;
//    //
//    //        // These are the Directed Association relationships, which do have a direction...
//    //        if (ownedElements != null && ownedElements.size() == 1) {
//    //          Element ownedElement = ownedElements.iterator().next();
//    //          String ownedElementHumanName = null;
//    //          if (ownedElement instanceof TypedElement) {
//    //            ownedElementHumanName = ((TypedElement) ownedElement).getType().getHumanName();
//    //          }
//    //
//    //          logger.debug("OwnedElement HumanName: {}", ownedElementHumanName);
//    //          logger.debug("Related HumanName: {}", related.getHumanName());
//    //          logger.debug("MDElement HumanName: {}", mdElement.getHumanName());
//    //
//    //          if (elementHumanName.equals(related.getHumanName()) || ownedElementHumanName ==
//    // null) {
//    //            // do nothing here, an element cannot be related to itself, this is just an
//    // unfortunate
//    //            // sideeffect of cameos, non-directed, directed linking...
//    //            logger.debug("Not Mapped");
//    //          } else if (elementHumanName.equals(ownedElementHumanName)) {
//    //            // This condition being met means, in Magicdraw, that the relationship was formed
//    // from
//    //            // this element,
//    //            // this makes it an "OUTBOUND" relationship in SBE
//    //            logger.debug("{} has a INBOUND to {}", elementHumanName, related.getHumanName());
//    //            logger.debug("{} has a OUTBOUND to {}", related.getHumanName(), elementHumanName);
//    //            logger.debug("Mapped as INBOUND");
//    //            association = createObjectProperty(related, INBOUND);
//    //          } else if (related.getHumanName().equals(ownedElementHumanName)) {
//    //            logger.debug("{} has a INBOUND to {}", related.getHumanName(), elementHumanName);
//    //            logger.debug("{} has a OUTBOUND to {}", elementHumanName, related.getHumanName());
//    //            logger.debug("Mapped as OUTBOUND");
//    //            association = createObjectProperty(related, OUTBOUND);
//    //          }
//    //        } else {
//    //          logger.error(
//    //              "Could not map non-directional relation to direction with elements: {} <--->
//    // {}",
//    //              elementHumanName,
//    //              related.getHumanName());
//    //        }
//    //        if (association != null) {
//    //          addLinkName(association, dependency);
//    //          externalItem.addAssociations(association);
//    //        }
//    //      }
//    //    }
//
//    // todo: can this not be a special case?
//    // get part properties on enterprise classes that are measures of effectiveness (moe's) and
//    // create relation
//    if (CameoAPI.getStereotypeList(mdElement).contains("Enterprise")) {
//      logger.debug("Found an enterprise class");
//      Class enterprise = (Class) mdElement;
//      List<Property> properties = enterprise.getAttribute();
//      for (Property property : properties) {
//        logger.debug("found attribute: {}", property.getHumanName());
//        if (TopLevelStereotypes.contains(CameoAPI.getStereotypeList(property))) {
//          logger.debug("And we care now :)");
//          AssociationProto.Builder association = createObjectProperty(property, INBOUND);
//          association.setLinkType("IsMeasuredBy");
//          externalItem.addAssociations(association);
//        }
//      }
//    }
//
//    // map inbound and outbound Containment relations for relationships
//    // todo: may need to still use my own isRequirement method
//    if (isRequirement(mdElement)) {
//      Class asClass = (Class) mdElement;
//      for (NamedElement subRequirment : asClass.getOwnedMember()) {
//        AssociationProto.Builder association = createObjectProperty(subRequirment, INBOUND);
//        if (association != null) {
//          association.setLinkType("Containment");
//          externalItem.addAssociations(association);
//        }
//      }
//      Element owner = asClass.getOwner();
//      if (isRequirement(owner)) {
//        AssociationProto.Builder association = createObjectProperty(owner, OUTBOUND);
//        if (association != null) {
//          association.setLinkType("Containment");
//          externalItem.addAssociations(association);
//        }
//      }
//    }
//
//    // map inbound operation relations
//    if (mdElement instanceof Class) {
//      logger.debug("Checking for operations");
//      Class classElement = (Class) mdElement;
//      for (Operation operation : classElement.getOwnedOperation()) {
//        logger.debug("Adding operation {} to relations", operation.getName());
//        AssociationProto.Builder association = createObjectProperty(operation, INBOUND);
//        association.setLinkType("Operation");
//        externalItem.addAssociations(association);
//      }
//    }
//
//    // todo: map outbound operation relations
//  }
//
//  private static void addLinkName(AssociationProto.Builder dependencyDTO, Relationship dependency) {
//    List<String> dependencyStereotypes = CameoAPI.getStereotypeList(dependency);
//    StringBuilder linkName = new StringBuilder();
//    if (dependencyStereotypes != null) {
//      for (String stereotype : dependencyStereotypes) {
//        linkName.append(stereotype);
//      }
//    }
//    if (linkName.length() == 0) {
//      linkName.append(dependency.getHumanName());
//    }
//    logger.debug("Name: {} || Linktype: {}", dependency.getHumanName(), linkName);
//    dependencyDTO.setLinkName(linkName.toString());
//  }
//
//  private static AssociationProto.Builder createObjectProperty(Element mdElement, String linkDirection) {
//    if (mdElement == null) {
//      return null;
//    }
//
//    AssociationProto.Builder association = AssociationProto.newBuilder();
//    association.setDirection(AssociationDirectionProto.valueOf(linkDirection));
//    association.setItem(getCorrelation(mdElement));
//
//    return association;
//  }
//
//  private static void getAttributes(ShapeProto.Builder shape, Element mdElement) {
//    // Get tagged attributes(from stereotypes)
//    logger.debug("Properties for {}", mdElement.getHumanName());
//    InstanceSpecification instanceSpecification = mdElement.getAppliedStereotypeInstance();
//
//    if (instanceSpecification != null) {
//      logger.debug("Adding attributes from stereotype instance {}", instanceSpecification.getName());
//      for (Slot slot : instanceSpecification.getSlot()) {
//        logger.debug("Adding slot attribute {}", slot.getHumanName());
//        addAttribute(shape, slot);
//      }
//    }
//
//    // get attributes directly on object
//    for (Property property : cameoAPI.getAllAttributesFromModel(mdElement)) {
//      logger.debug("property title: {}", property.getName());
//      logger.debug("property sterotype: {}", property.getHumanType());
//      if (!property.getHumanType().equals("Part Property") || !property.getHumanType().equals("Port")) {
//        addAttribute(shape, property);
//      }
//    }
//
//    // Add all stereotypes
//    // todo: test if this works
//    UnaryValueProto unaryValueProto = UnaryValueProto.newBuilder().setStringValue(CameoAPI.getStereotypeList(mdElement).toString()).build();
//    shape.putAttributes("Stereotypes", ValueProto.newBuilder().setUnaryValue(unaryValueProto).build());
//
//    // Add display name
//    unaryValueProto = UnaryValueProto.newBuilder().setStringValue(CoreHelper.getName(mdElement)).build();
//    shape.putAttributes("Display Name", ValueProto.newBuilder().setUnaryValue(unaryValueProto).build());
//  }
//
//  private static Map<String, ValueProto> getAttributes(Element mdElement) {
//    Map<String, ValueProto> attributes = new HashMap<>();
//    InstanceSpecification instanceSpecification = mdElement.getAppliedStereotypeInstance();
//
//    if (instanceSpecification != null) {
//      for (Slot slot : instanceSpecification.getSlot()) {
//        addAttribute(attributes, slot);
//      }
//    }
//
//    // get attributes directly on object
//    for (Property property : cameoAPI.getAllAttributesFromModel(mdElement)) {
//      if (property.getHumanType().equals("Part Property")) {
//        addAttribute(attributes, property);
//      } else if (property.getHumanType().equals("Port")) {
//        addAttribute(attributes, property);
//      } else {
//        addAttribute(attributes, property);
//      }
//    }
//
//    // Add all stereotypes
//    // todo: test if this works
//    UnaryValueProto unaryValueProto = UnaryValueProto.newBuilder().setStringValue(CameoAPI.getStereotypeList(mdElement).toString()).build();
//    attributes.put("Stereotypes", ValueProto.newBuilder().setUnaryValue(unaryValueProto).build());
//
//    // Add display name
//    unaryValueProto = UnaryValueProto.newBuilder().setStringValue(CoreHelper.getName(mdElement)).build();
//    attributes.put("Display Name", ValueProto.newBuilder().setUnaryValue(unaryValueProto).build());
//
//    return attributes;
//  }
//
//  private static Map<String, ValueProto> getValueProtoAttributes(Element mdElement) {
//    Map<String, ValueProto> attributes = new HashMap<>();
//    InstanceSpecification instanceSpecification = mdElement.getAppliedStereotypeInstance();
//
//    if (instanceSpecification != null) {
//      for (Slot slot : instanceSpecification.getSlot()) {
//        addValueProtoAttribute(attributes, slot);
//      }
//    }
//
//    // get attributes directly on object
//    for (Property property : cameoAPI.getAllAttributesFromModel(mdElement)) {
//      if (property.getHumanType().equals("Part Property")) {
//        addValueProtoAttribute(attributes, property);
//      } else if (property.getHumanType().equals("Port")) {
//        addValueProtoAttribute(attributes, property);
//      } else {
//        addValueProtoAttribute(attributes, property);
//      }
//    }
//
//    // Add all stereotypes
//    // todo: test if this works
//    UnaryValueProto unaryValueProto = UnaryValueProto.newBuilder().setStringValue(CameoAPI.getStereotypeList(mdElement).toString()).build();
//    attributes.put("Stereotypes", ValueProto.newBuilder().setUnaryValue(unaryValueProto).build());
//
//    // Add display name
//    unaryValueProto = UnaryValueProto.newBuilder().setStringValue(CoreHelper.getName(mdElement)).build();
//    attributes.put("Display Name", ValueProto.newBuilder().setUnaryValue(unaryValueProto).build());
//
//    return attributes;
//  }
//
//  private static void addAttribute(ShapeProto.Builder shape, Property property) {
//    if (property != null && property.getDefaultValue() != null) {
//      ValueSpecification attribute = property.getDefaultValue();
//      ValueProto value = mdHelper.getValue(attribute);
//
//      shape.putAttributes(property.getName(), value);
//    }
//  }
//
//  private static void addAttribute(Map<String, ValueProto> attributes, Property property) {
//    if (property != null && property.getDefaultValue() != null) {
//      ValueSpecification attribute = property.getDefaultValue();
//      attributes.put(property.getName(), getValue(attribute));
//    }
//  }
//
//  private static void addValueProtoAttribute(Map<String, ValueProto> attributes, Property property) {
//    if (property != null && property.getDefaultValue() != null) {
//      ValueSpecification attribute = property.getDefaultValue();
//      Object value = mdHelper.getValue(attribute);
//      UnaryValueProto unaryValueProto = UnaryValueProto.newBuilder().setStringValue(value.toString()).build();
//      attributes.put(property.getName(), ValueProto.newBuilder().setUnaryValue(unaryValueProto).build());
//    }
//  }
//
//  // todo: refactor out to get/set slot value
//  private static void addAttribute(ShapeProto.Builder shape, Slot slot) {
//    if (slot.getDefiningFeature() instanceof Property) {
//
//      Property property = (Property) slot.getDefiningFeature();
//      if (property != null) {
//        for (ValueSpecification attribute : slot.getValue()) {
//          ValueProto value = mdHelper.getValue(attribute);
//          shape.putAttributes(property.getName(), value);
//        }
//      }
//    }
//  }
//
//  private static void addAttribute(Map<String, ValueProto> attributes, Slot slot) {
//    if (slot.getDefiningFeature() instanceof Property) {
//      Property property = (Property) slot.getDefiningFeature();
//      if (property != null) {
//        for (ValueSpecification attribute : slot.getValue()) {
//          attributes.put(property.getName(), getValue(attribute));
//        }
//      }
//    }
//  }
//
//  private static void addValueProtoAttribute(Map<String, ValueProto> attributes, Slot slot) {
//    if (slot.getDefiningFeature() instanceof Property) {
//      Property property = (Property) slot.getDefiningFeature();
//      if (property != null) {
//        for (ValueSpecification attribute : slot.getValue()) {
//          ValueProto value = mdHelper.getValue(attribute);
//          attributes.put(property.getName(), value);
//        }
//      }
//    }
//  }
//
//  private static ValueProto getValue(ValueSpecification attribute) {
//    ValueProto value = mdHelper.getValue(attribute);
//    if (value == null) {
//      return ValueProto.newBuilder().build();
//    }
//    return value;
//  }
//}
