package main.java.com.sbevision.nomagic.msg.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TopLevelStereotypes {

  // Operation is a special case that also must be loaded view
  // cameoService.getAllModels() and elementConverter.setStereotype()
  private static List<String> topLevelStereotypesList =
      Arrays.asList(
          "Deployment Item",
          "Logical Item",
          "Requirement",
          "interfaceRequirement",
          "functionalRequirement",
          "performanceRequirement",
          "designConstraint",
          "Environment",
          "external",
          "Enterprise",
          "Organization",
          "Person",
          "System context",
          "Activity",
          "Block",
          "UseCase",
          "System Function",
          "Role",
          "SystemBehavior",
          "Use Case Specification",
          "MalfunctioningBehavior");

  // , "ProxyPort", "moe"
  private TopLevelStereotypes() {
    // hide public constructor
  }

  /**
   * @param comparisonList - List of stereotypes for comparison against enum
   * @return - Returns true if any item in comparisonList is contained within the enum
   */
  public static boolean contains(List<String> comparisonList) {
    if (comparisonList == null || comparisonList.isEmpty()) {
      return false;
    }

    return !Collections.disjoint(topLevelStereotypesList, comparisonList);
  }

  public static boolean contains(String listItem) {
    if (listItem == null || listItem.isEmpty()) {
      return false;
    }

    return topLevelStereotypesList.contains(listItem);
  }
}
