package main.java.com.sbevision.nomagic.ui.menu;

import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.DiagramContextAMConfigurator;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;

public class ElementMenuPickConfig implements DiagramContextAMConfigurator {

  @Override
  public void configure(
      ActionsManager mngr,
      DiagramPresentationElement diagram,
      PresentationElement[] selected,
      PresentationElement requestor) {

    // Actions must be added into some category.
    // So create the new one, or add an action into the existing category.

    // Add a category into the manager.
    // A category isn't displayed in a shortcut menu.

    if (mngr.getActionFor("SBE_OPEN_DIGITAL_THREAD") == null && selected.length > 0) {
      MDActionsCategory category = new MDActionsCategory("DIGITAL_THREAD", "Digital Thread");
      UIAction action =
          new UIAction(
              UIAction.UrlName.digitalThread,
              "Link to Digital Thread",
              "open element");
      //            action.setDisplayName(getName(selected[0]));
      category.addAction(action);
      mngr.addCategory(category);
    }
  }

  public int getPriority() {
    return MEDIUM_PRIORITY;
  }
}
