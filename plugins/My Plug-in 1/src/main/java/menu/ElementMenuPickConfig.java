package main.java.menu;//package ui.menu;
//
//import com.nomagic.actions.ActionsManager;
//import com.nomagic.magicdraw.actions.DiagramContextAMConfigurator;
//import com.nomagic.magicdraw.actions.MDActionsCategory;
//import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
//import com.nomagic.magicdraw.uml.symbols.PresentationElement;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.nomagic.uml2.ext.jmi.helpers.CoreHelper.getName;
//
//public class ElementMenuPickConfig implements DiagramContextAMConfigurator {
//
//
//
//
//    public void configure(ActionsManager mngr,
//                          DiagramPresentationElement diagram,
//                          PresentationElement[] selected,
//                          PresentationElement requestor) {
//
//        // Actions must be added into some category.
//        // So create the new one, or add an action into the existing category.
//
//        // Add a category into the manager.
//        // A category isn't displayed in a shortcut menu.
//
//        if (mngr.getActionFor("SBE_OPEN_DIGITAL_THREAD") == null && selected.length > 0) {
//            MDActionsCategory category = new MDActionsCategory("DIGITAL_THREAD", "Digital Thread");
//            OpenURLAction action = new OpenURLAction("SBE_OPEN_DIGITAL_THREAD", "Link to Digital Thread", "open element");
////            action.setDisplayName(getName(selected[0]));
//            category.addAction(action);
//            mngr.addCategory(category);
//        }
//
//    }
//
//    public int getPriority() {
//        return MEDIUM_PRIORITY;
//    }
//
//    /*private List<String> doGetUrlToLoad(PresentationElement[] elements) {
//        List<String> result = new ArrayList<>();
//        if (elements == null || elements.length <= 0) {
//            return result;
//        }
//        for (PresentationElement element : elements) {
//            String projectName = TeamworkCloud.getCurrentProjectName();
//            String identity = Identifier.identityBuilder(getName(element), element.getID());
//            result.add(Identifier.buildDigitalThreadURL(projectName, identity));
//        }
//        return result;
//    }*/
//}
