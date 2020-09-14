package main.java.com.sbevision.nomagic.msg.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


// todo: use Class RequirementsConstants from magicdraw
public class Requirements {

    // Operation is a special case that also must be loaded view
    // cameoService.getAllModels() and elementConverter.setStereotype()
    private static List<String> reqs =
            Arrays.asList("Requirement", "interfaceRequirement", "functionalRequirement",
                    "performanceRequirement", "designConstraint");

    private Requirements() {
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

        return !Collections.disjoint(reqs, comparisonList);
    }

    public static boolean contains(String listItem) {
        if (listItem == null || listItem.isEmpty()) {
            return false;
        }

        return reqs.contains(listItem);
    }

}
