package main.java.com.sbevision.nomagic.service;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.CallBehaviorAction;
import com.nomagic.uml2.ext.magicdraw.activities.mdbasicactivities.ActivityParameterNode;
import com.nomagic.uml2.ext.magicdraw.activities.mdfundamentalactivities.Activity;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.ext.magicdraw.mdusecases.UseCase;
import main.java.com.sbevision.nomagic.msg.enums.TopLevelStereotypes;
import main.java.com.sbevision.nomagic.plugin.CameoAPI;
import main.java.com.sbevision.nomagic.plugin.MagicdrawHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static main.java.com.sbevision.nomagic.plugin.CameoAPI.getStereotypeList;

public class PublishingService {

  private static final Logger logger = LoggerFactory.getLogger(PublishingService.class);
  private static PublishingService publishingService;
  private static MagicdrawHelper magicdrawHelper = MagicdrawHelper.getInstance();
  public Project project;

  private PublishingService() {
    // hide public constructor so everyone needs the getInstance() method
  }

  public static PublishingService getInstance() {
    if (publishingService == null) {
      publishingService = new PublishingService();
    }
    return publishingService;
  }

  public void execute() {
    CameoAPI cameoAPI = CameoAPI.getInstance();
    cameoAPI.getProject();

    //    ActivityParameterNode parameterNode = new ActivityParameterNodeImpl();
    //    parameterNode.setName("parameterNode");
    //    parameterNode.setOwner(getElementByQualifiedName("1-System Design::1-Concept
    // Level::3-Behavior::Use Cases::Improve visibility while in off-road
    // environment::Approve/Disapprove behavior"));
    //    parameterNode.setOwner(((Activity)getElementByQualifiedName("1-System Design::1-Concept
    // Level::3-Behavior::Use Cases::Improve visibility while in off-road
    // environment::Approve/Disapprove behavior")));

    LinkedList<String> list = new LinkedList<>();
    Activity activity =
        MagicdrawHelper.getInstance()
            .createActivity("success", getPackageBasedOnQualifiedName("Digital Thread"));
    CallBehaviorAction callBehaviorAction1 =
        MagicdrawHelper.getInstance().createCallBehaviorAction("first", activity);
    CallBehaviorAction callBehaviorAction2 =
        MagicdrawHelper.getInstance().createCallBehaviorAction("second", activity);
    CallBehaviorAction callBehaviorAction3 =
        MagicdrawHelper.getInstance().createCallBehaviorAction("third", activity);

    MagicdrawHelper.getInstance()
        .createControlFlow(callBehaviorAction1, callBehaviorAction2, activity);

    ActivityParameterNode activityParameterNode =
        MagicdrawHelper.getInstance()
            .createActivityParameterNode(
                "parameterNaode", getPackageBasedOnQualifiedName("Digital Thread::shobahb"));

//    Stereotype experience =
//        MagicdrawHelper.getInstance()
//            .createStereoType("Experience", getPackageBasedOnQualifiedName("Digital Thread"));
//
//    Stereotype hmiRequirement =
//        MagicdrawHelper.getInstance()
//            .createStereoType("HMIRequirement", getPackageBasedOnQualifiedName("Digital Thread"));

    Element role =
        MagicdrawHelper.getInstance()
            .createRole("useCAE", getPackageBasedOnQualifiedName("Digital Thread"));

    MagicdrawHelper.getInstance().createOperation("operation", role);
    MagicdrawHelper.getInstance()
        .createClass("Test", getPackageBasedOnQualifiedName("Digital Thread"));

    UseCase useCase =
        MagicdrawHelper.getInstance()
            .createUseCase("Test", getPackageBasedOnQualifiedName("Digital Thread"));

    //    MagicdrawHelper.getInstance().create

    StereotypesHelper.getStereotype(project, "SystemBehavior[Usecase]");
    Package pack =
        getPackageBasedOnQualifiedName("1-System Design::1-Concept Level::3-Behavior::Use Cases");
    List<Element> elements = getAllElementInPackage(pack);
  }

  public Package getPackageBasedOnQualifiedName(String qualifiedName) {
    Project project = CameoAPI.getInstance().getProject();
    Package systemReq = Finder.byQualifiedName().find(project, qualifiedName);
    return systemReq;
  }

  public List<Element> getAllElementInPackage(Package pack) {
    Collection<Element> resultSpecific = new ArrayList<>();
    List<Element> result = new ArrayList<>();
    resultSpecific = pack.getOwnedElement();

    List<Element> originalElements = getElementsList(resultSpecific);

    for (Element element : result) {
      List<String> stereotypes = getStereotypeList((element));
      if (TopLevelStereotypes.contains(stereotypes)
          || TopLevelStereotypes.contains(element.getHumanType())) {
        result.add(element);
      }
    }
    return result;
  }

  private List<Element> getElementsList(Collection<Element> resultSpecific) {
    Iterator<Element> iterator = resultSpecific.iterator();
    List<Element> resultElements = new LinkedList<>();
    while (iterator.hasNext()) {
      resultElements.add(iterator.next());
    }
    return resultElements;
  }

  public Element getElementByQualifiedName(String qualifiedName) {
    Project project = CameoAPI.getInstance().getProject();
    Element element = Finder.byQualifiedName().find(project, qualifiedName);
    return element;
  }
}
