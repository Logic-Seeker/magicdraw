/**
 * ***************************************************************************** Copyright (c)
 * <2016>, California Institute of Technology ("Caltech"). U.S. Government sponsorship acknowledged.
 *
 * <p>All rights reserved.
 *
 * <p>Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * <p>- Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. - Redistributions in binary form must reproduce the
 * above copyright notice, this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution. - Neither the name of Caltech nor its
 * operating division, the Jet Propulsion Laboratory, nor the names of its contributors may be used
 * to endorse or promote products derived from this software without specific prior written
 * permission.
 *
 * <p>THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ****************************************************************************
 */
package main.java.com.sbevision.nomagic.plugin;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.CallBehaviorAction;
import com.nomagic.uml2.ext.magicdraw.activities.mdbasicactivities.ControlFlow;
import com.nomagic.uml2.ext.magicdraw.activities.mdfundamentalactivities.Activity;
import com.nomagic.uml2.ext.magicdraw.classes.mddependencies.Abstraction;
import com.nomagic.uml2.ext.magicdraw.classes.mddependencies.Dependency;
import com.nomagic.uml2.ext.magicdraw.classes.mddependencies.Realization;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.components.mdbasiccomponents.Component;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.ext.magicdraw.mdusecases.Actor;
import com.nomagic.uml2.ext.magicdraw.mdusecases.UseCase;
import com.nomagic.uml2.impl.ElementsFactory;
import com.sbevision.common.grpc.UnaryValueProto;
import com.sbevision.common.grpc.ValueProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import static com.nomagic.uml2.ext.jmi.helpers.CoreHelper.setClientElement;
import static com.nomagic.uml2.ext.jmi.helpers.CoreHelper.setSupplierElement;

public class MagicdrawHelper {
  private static final Logger logger = LoggerFactory.getLogger(MagicdrawHelper.class);
  public static final String COULD_NOT_ADD_STEREOTYPE_TO_ELEMENT =
      "Could not add stereotype '{}' to element '{}'";

  private static MagicdrawHelper magicdrawHelper;
  private Project project;
  private Profile sysmlProfile;
  private ElementsFactory elementsFactory;

  private MagicdrawHelper() {
    project = Application.getInstance().getProject();
  }

  public Project getProject() {
    return project;
  }

  public void setProject() {
    project = Application.getInstance().getProject();
    project.getRepository().setCanSetID(true);
    if (project != null) {
      elementsFactory = project.getElementsFactory();
      sysmlProfile = StereotypesHelper.getProfile(project, "SysML");
    }
  }

  private void createPackageIfAbsent(String digital_thread, Package rootPackage) {
    for (Package subPackage : rootPackage.getNestedPackage()) {
      if (subPackage.getName().equals(digital_thread)) return;
      createPackage(digital_thread, rootPackage);
    }
  }

  public static MagicdrawHelper getInstance() {
    if (magicdrawHelper == null) {
      magicdrawHelper = new MagicdrawHelper();
    }
    return magicdrawHelper;
  }

  /**
   * Create a class with a stereotype of "Block" from sysml
   *
   * @param name - Name of new Block element
   * @param owner -
   * @return - returns created class
   */
  public Class createBlock(String name, Element owner) {
    Class newBlock = createClass(name, owner);
    Stereotype block = StereotypesHelper.getStereotype(project, "Block", sysmlProfile);

    StereotypesHelper.addStereotype(newBlock, block);
    return newBlock;
  }

  public CallBehaviorAction createCallBehaviorAction(String name, Element owner) {
    CallBehaviorAction callBehaviorAction = elementsFactory.createCallBehaviorActionInstance();
    callBehaviorAction.setName(name);
    callBehaviorAction.setOwner(owner);
    return callBehaviorAction;
  }

  public Class createClass(String name, Element owner) {
    Class newClass = elementsFactory.createClassInstance();
    finishElement(newClass, name, owner);
    return newClass;
  }

  public Class createRole(String name, Element owner) {

    Class newBlock = createClass(name, owner);
    Profile profile = StereotypesHelper.getProfile(project, "Ford MBSE Profile");
    Stereotype role = StereotypesHelper.getStereotype(project, "Role", profile);

    StereotypesHelper.addStereotype(newBlock, role);
    return newBlock;
  }

  public Class createRole(String name, Element owner, String externalLifecycleId) {

    Class newClass = createClass(name, owner);
    newClass.setID(externalLifecycleId);
    Profile profile = StereotypesHelper.getProfile(project, "Ford MBSE Profile");
    Stereotype role = StereotypesHelper.getStereotype(project, "Role", profile);

    StereotypesHelper.addStereotype(newClass, role);
    return newClass;
  }

  public Class createClass(String name, Element owner, String externalLifecycleId) {
    Class newClass = elementsFactory.createClassInstance();
    finishElement(newClass, name, externalLifecycleId, owner);
    return newClass;
  }

  public Package getPackageBasedOnQualifiedName(String qualifiedName) {
    getProject();
    Package systemReq = Finder.byQualifiedName().find(project, qualifiedName);
    return systemReq;
  }

  public Component createComponent(String name, Element owner) {
    Component comp = elementsFactory.createComponentInstance();
    finishElement(comp, name, owner);
    return comp;
  }

  public Constraint createConstraint(String name, Element owner, ValueSpecification spec) {
    Constraint newConstraint = elementsFactory.createConstraintInstance();
    finishElement(newConstraint, name, owner);
    if (spec != null) {
      newConstraint.setSpecification(spec);
    }
    return newConstraint;
  }

  public Association createDirectedComposition(Element document, Element view) {
    Association assoc = elementsFactory.createAssociationInstance();
    finishElement(assoc, null, document.getOwner());
    Property source =
        createProperty(
            ((NamedElement) view).getName(), document, null, view, "composite", "1", "1");
    Property target = createProperty("", assoc, null, document, "none", "1", "1");
    assoc.getMemberEnd().clear();
    assoc.getMemberEnd().add(0, source);
    assoc.getMemberEnd().add(target);
    assoc.getOwnedEnd().add(0, target);
    return assoc;
  }

  public Dependency createDependency(String name, Element owner, Element source, Element target) {
    Dependency depd = elementsFactory.createDependencyInstance();
    setRelationshipEnds(depd, source, target);
    finishElement(depd, name, owner);
    return depd;
  }

  public Association createAssociation(Element owner, Element source, Element target) {
    Association newAssoc = elementsFactory.createAssociationInstance();

    finishElement(newAssoc, null, owner);
    Property sourceProp =
        createProperty(((NamedElement) target).getName(), source, null, target, "none", "", "");
    Property targetProp =
        createProperty(((NamedElement) source).getName(), target, null, source, "none", "", "");
    newAssoc.getMemberEnd().clear();
    newAssoc.getMemberEnd().add(0, sourceProp);
    newAssoc.getMemberEnd().add(targetProp);
    return newAssoc;
  }

  public Abstraction createAbstraction(Element owner, Element source, Element target) {
    Abstraction newAbstraction = elementsFactory.createAbstractionInstance();

    finishElement(newAbstraction, null, owner);
    setRelationshipEnds(newAbstraction, source, target);

    return newAbstraction;
  }

  public Generalization createGeneralization(
      String name, Element owner, Element source, Element target) {
    Generalization genr = elementsFactory.createGeneralizationInstance();
    setRelationshipEnds(genr, source, target);
    finishElement(genr, name, owner);
    return genr;
  }

  public Realization createRealization(Element owner, Element source, Element target) {
    Realization real = elementsFactory.createRealizationInstance();
    setRelationshipEnds(real, source, target);
    finishElement(real, null, owner);
    return real;
  }

  public Class createDocument(String name, Element owner) {
    Class newDocument = createClass(name, owner);
    Stereotype sysmlDocument = StereotypesHelper.getStereotype(project, "", sysmlProfile);
    StereotypesHelper.addStereotype(newDocument, sysmlDocument);
    return newDocument;
  }

  public Package createPackage(String name, Element owner) {
    logger.debug("Creating Package '{}' with owner '{}'", name, owner.getHumanName());
    Package newPackage = elementsFactory.createPackageInstance();
    finishElement(newPackage, name, owner);
    return newPackage;
  }

  public Package getSBESubPackage(
      String subscriptionName, String partitionName, String entitySetName) {
    Package rootPackage = (Package) getRootElement();
    Package digitalThread = createOrGetExistingPackage("Digital Thread", rootPackage);
    Package subscription = createOrGetExistingPackage(subscriptionName, digitalThread);

    String packageName = partitionName + ": " + entitySetName;
    return createOrGetExistingPackage(packageName, subscription);
  }

  private Package createOrGetExistingPackage(String name, Package owner) {
    Package result = null;
    for (Package subPackage : owner.getNestedPackage()) {
      if (subPackage.getName().equals(name)) {
        result = subPackage;
      }
    }
    if (result == null) {
      result = createPackage(name, owner);
    }

    return result;
  }

  public Element getRootElement() {
    if (project != null) {
      return project.getPrimaryModel();
    }
    return null;
  }

  public Property createPartProperty(String name, Element owner) {
    logger.debug("Creating Part Property '{}' with owner '{}'", name, owner.getHumanName());
    Property newProp = createProperty(name, owner, null, null, null, null, null);
    addStereotype(newProp, "PartProperty");
    return newProp;
  }

  public Operation createOperation(String name, Element owner) {
    logger.debug("Creating Operation '{}' with owner '{}'", name, owner.getHumanName());
    Operation newOp = elementsFactory.createOperationInstance();
    finishElement(newOp, name, owner);
    return newOp;
  }

  public UseCase createUseCase(String name, Element owner) {
    setProject();
    UseCase useCase = elementsFactory.createUseCaseInstance();
    finishElement(useCase, name, owner);
    return useCase;
  }

  public UseCase createUseCase(String name, Element owner, String externalLifecycleId) {
    setProject();
    UseCase useCase = elementsFactory.createUseCaseInstance();
    finishElement(useCase, name, externalLifecycleId, owner);
    return useCase;
  }

  public Port createPort(String name, Element owner) {
    logger.debug("Creating Port '{}' with owner '{}'", name, owner.getHumanName());
    Port newPort = elementsFactory.createPortInstance();
    finishElement(newPort, name, owner);
    return newPort;
  }

  public Property createProperty(
      String name,
      Element owner,
      ValueSpecification defaultValue,
      Element typeElement,
      String aggregation,
      String multMin,
      String multMax) {
    logger.debug("Creating Property '{}' with owner '{}'", name, owner.getHumanName());
    Property prop = elementsFactory.createPropertyInstance();
    finishElement(prop, name, owner);
    prop.setVisibility(VisibilityKindEnum.PUBLIC);

    if (defaultValue != null) {
      prop.setDefaultValue(defaultValue);
    }

    if (typeElement != null) {
      prop.setType((Type) typeElement);
    }

    if (aggregation != null) {
      prop.setAggregation(AggregationKindEnum.getByName(aggregation));
    }

    if (multMin != null) {
      setMultiplicity(multMin, prop, prop.getLowerValue());
    }

    if (multMax != null) {
      setMultiplicity(multMax, prop, prop.getUpperValue());
    }

    return prop;
  }

  private void setMultiplicity(String multMin, Property prop, ValueSpecification value) {
    try {
      Long spmin = new Long(multMin);
      if (value == null) {
        value = elementsFactory.createLiteralIntegerInstance();
      } else if (value instanceof LiteralInteger) {
        ((LiteralInteger) value).setValue(spmin.intValue());
      } else if (value instanceof LiteralUnlimitedNatural) {
        ((LiteralUnlimitedNatural) value).setValue(spmin.intValue());
      }
      prop.setLowerValue(value);
    } catch (NumberFormatException e) {
      logger.error("", e);
    }
  }

  public ValueProto getValue(ValueSpecification valueSpecification) {
    Object attribute = ModelHelper.getValueSpecificationValue(valueSpecification);
    Object result;
    ValueProto.Builder valueProto = ValueProto.newBuilder();

    if (attribute instanceof LiteralString) {
      result = ((LiteralString) valueSpecification).getValue();
      UnaryValueProto unaryValueProto =
          UnaryValueProto.newBuilder().setStringValue(result.toString()).build();
      valueProto.setUnaryValue(unaryValueProto).build();
    } else if (attribute instanceof LiteralBoolean) {
      result = ((LiteralBoolean) valueSpecification).isValue();
      UnaryValueProto unaryValueProto =
          UnaryValueProto.newBuilder().setBoolValue((boolean) result).build();
      valueProto.setUnaryValue(unaryValueProto).build();
    } else if (attribute instanceof LiteralInteger) {
      result = ((LiteralInteger) valueSpecification).getValue();
      UnaryValueProto unaryValueProto =
          UnaryValueProto.newBuilder().setIntegerValue((int) result).build();
      valueProto.setUnaryValue(unaryValueProto).build();
    } else if (attribute instanceof LiteralReal) {
      result = ((LiteralReal) valueSpecification).getValue();
      UnaryValueProto unaryValueProto =
          UnaryValueProto.newBuilder().setStringValue(result.toString()).build();
      valueProto.setUnaryValue(unaryValueProto).build();
    } else if (attribute instanceof LiteralUnlimitedNatural) {
      result = ((LiteralUnlimitedNatural) valueSpecification).getValue();
      UnaryValueProto unaryValueProto =
          UnaryValueProto.newBuilder().setStringValue(result.toString()).build();
      valueProto.setUnaryValue(unaryValueProto).build();
    } else if (attribute instanceof EnumerationLiteral) {
      EnumerationLiteral literal = (EnumerationLiteral) attribute;
      result = literal.getName();
      UnaryValueProto unaryValueProto =
          UnaryValueProto.newBuilder().setStringValue(result.toString()).build();
      valueProto.setUnaryValue(unaryValueProto).build();
    } else if (attribute instanceof Class) {
      Class literal = (Class) attribute;
      result = literal.getHumanName();
    } else if (attribute instanceof LiteralNull) {
      // nothing to do here, result is already null
    } else if (attribute instanceof Boolean) {
      result = attribute.toString();
      UnaryValueProto unaryValueProto =
          UnaryValueProto.newBuilder().setBoolValue(Boolean.valueOf((String) result)).build();
      valueProto.setUnaryValue(unaryValueProto).build();
    } else {
      result = attribute.toString();
      UnaryValueProto unaryValueProto =
          UnaryValueProto.newBuilder().setStringValue(result.toString()).build();
      valueProto.setUnaryValue(unaryValueProto).build();
    }

    return valueProto.build();
  }

  public void setValue(ValueSpecification attribute, Object value) {

    if (attribute instanceof LiteralString) {
      ((LiteralString) attribute).setValue((String) value);
    } else if (attribute instanceof LiteralBoolean) {
      ((LiteralBoolean) attribute).setValue((boolean) value);
    } else if (attribute instanceof LiteralInteger) {
      ((LiteralInteger) attribute).setValue((int) value);
    } else if (attribute instanceof LiteralReal) {
      ((LiteralReal) attribute).setValue((double) value);
    } else if (attribute instanceof LiteralUnlimitedNatural) {
      ((LiteralUnlimitedNatural) attribute).setValue((int) value);
    } else if (attribute instanceof LiteralNull) {
      // nothing to do here
    }
  }

  /**
   * The stereotype is searched for in all profiles. If this stereotype does not exist, the method
   * will do nothing and return false
   *
   * @param element - object that needs to be stereotyped
   * @param newStereotype - stereotype to apply
   * @return - true if stereotype was applied, false if the operation failed and a stereotype was
   *     not applied
   */
  public boolean addStereotype(Element element, String newStereotype) {
    // assign stereotype
    if (element == null) {
      logger.error("could not add stereotype '{}' to null element", newStereotype);
      return false;
    } else if (newStereotype == null) {
      logger.error(
          "could not stereotype element '{}' with a null stereotype", element.getHumanName());
      return false;
    }
    logger.debug("Adding Stereotype '{}' to element '{}'", newStereotype, element.getHumanName());

    StereotypesHelper.addStereotypeByString(element, newStereotype);
    if (StereotypesHelper.hasStereotype(element, newStereotype)) {
      return true;
    }

    logger.error(COULD_NOT_ADD_STEREOTYPE_TO_ELEMENT, newStereotype, element.getHumanName());
    return false;
  }

  /**
   * The stereotype is searched for sysml profile. If this stereotype does not exist, the method
   * will do nothing and return false
   *
   * @param element - object that needs to be stereotyped
   * @param newStereotypeString - stereotype to apply
   * @return - true if stereotype was applied, false if the operation failed and a stereotype was
   *     not applied
   */
  public boolean addSysMLStereotype(Element element, String newStereotypeString) {

    // assign stereotype
    if (element == null) {
      logger.error("could not add stereotype '{}' to null element", newStereotypeString);
      return false;
    } else if (newStereotypeString == null) {
      logger.error(
          "could not stereotype element '{}' with a null stereotype", element.getHumanName());
      return false;
    }
    logger.debug(
        "Adding Stereotype '{}' to element '{}'", newStereotypeString, element.getHumanName());

    Stereotype newStereotype =
        StereotypesHelper.getStereotype(project, newStereotypeString, sysmlProfile);
    if (newStereotype == null) {
      logger.error(
          COULD_NOT_ADD_STEREOTYPE_TO_ELEMENT, newStereotypeString, element.getHumanName());
      return false;
    }
    StereotypesHelper.addStereotype(element, newStereotype);

    if (StereotypesHelper.hasStereotype(element, newStereotypeString)) {
      return true;
    }

    logger.error(COULD_NOT_ADD_STEREOTYPE_TO_ELEMENT, newStereotypeString, element.getHumanName());
    return false;
  }

  /**
   * Simple method to change the owner of an existing element NOTE: This will also change where the
   * element is displayed in the containment tree, so it is good to use if an element is contained
   * by another element
   *
   * @param toChange - the element in need of a new parent
   * @param newOwner - the new partent element
   */
  public void changeOwner(Element newOwner, Element toChange) {

    CameoAPI cameoAPI = CameoAPI.getInstance();
    Collection<Property> properties = cameoAPI.getAllAttributesFromModel(toChange);
    String id = null;
    for (Property property : properties) {
      ValueSpecification vs = property.getDefaultValue();
      if (vs != null && vs.getName().equals("Id")) {
        id = getValue(vs).getUnaryValue().getStringValue();
      }
    }

    toChange.setOwner(newOwner);

    if (id != null) {
      properties = cameoAPI.getAllAttributesFromModel(toChange);
      for (Property property : properties) {
        ValueSpecification vs = property.getDefaultValue();
        if (vs != null && vs.getName().equals("Id")) {
          setValue(vs, id);
        }
      }
    }
  }

  /**
   * ****************************************************************************************************
   *
   * <p>Helper methods for element creation functions
   *
   * <p>****************************************************************************************************
   */

  /**
   * Convenience method to fill element properties name and owner.
   *
   * @param newElement The element to be finished.
   * @param name The name of the NamedElement. This will be applied to a NamedElement unless name is
   *     null. This parameter will be ignored if the element is not a NamedElement.
   * @param owner The owner of the element to be finished.
   * @return The finished newElement.
   */
  private Element finishElement(Element newElement, String name, Element owner) {
    if (newElement instanceof NamedElement && !(name == null || name.isEmpty())) {
      ((NamedElement) newElement).setName(name);
    }
    newElement.setOwner(owner);
    return newElement;
  }

  private Element finishElement(Element newElement, String name, String id, Element owner) {
    if (newElement instanceof NamedElement && !(name == null || name.isEmpty())) {
      ((NamedElement) newElement).setName(name);
    }
    newElement.setOwner(owner);
    newElement.setID(id);
    return newElement;
  }

  /**
   * Convenience method to set or update relationship ends
   *
   * @param dr
   * @param source
   * @param target
   */
  private void setRelationshipEnds(DirectedRelationship dr, Element source, Element target) {
    setClientElement(dr, source);
    setSupplierElement(dr, target);
  }

  public Element createActivity(String displayName, Element owner) {
    Activity activity = elementsFactory.createActivityInstance();
    finishElement(activity, displayName, owner);
    return activity;
  }

  public Element createActivity(String displayName, Element owner, String externalLifecycleId) {
    Activity activity = elementsFactory.createActivityInstance();
    finishElement(activity, displayName, externalLifecycleId, owner);
    return activity;
  }

  public void createControlFlow(
      CallBehaviorAction callBehaviorAction1,
      CallBehaviorAction callBehaviorAction2,
      Element owner) {
    setProject();
    ControlFlow controlFlow = elementsFactory.createControlFlowInstance();
    controlFlow.setSource(callBehaviorAction1);
    controlFlow.setTarget(callBehaviorAction2);
    controlFlow.setOwner(owner);
  }

  public Actor creatActor(String name, Element owner) {
    Actor actor = elementsFactory.createActorInstance();
    actor.setName(name);
    actor.setOwner(owner);
    return actor;
  }
}
