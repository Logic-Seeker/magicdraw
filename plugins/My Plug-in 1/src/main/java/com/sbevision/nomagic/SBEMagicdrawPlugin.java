package main.java.com.sbevision.nomagic;

import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.uml.DiagramTypeConstants;
import main.java.com.sbevision.nomagic.ui.menu.ElementMenuPickConfig;
import main.java.com.sbevision.nomagic.ui.menu.SBEMainMenuPick;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Main method run by MagicDraw to initialize plugin */
public class SBEMagicdrawPlugin extends Plugin {

  private static final Logger logger = LoggerFactory.getLogger(SBEMagicdrawPlugin.class);

  protected boolean initialized = false;

  private Project project;

  @Override
  public void init() {
    logger.info("Starting SBE Digital Thread plugin");
    /* UI Setup */
    ActionsConfiguratorsManager acm = ActionsConfiguratorsManager.getInstance();

    // Add Digital thread to model right-click menu(for classes only)
    ElementMenuPickConfig elementMenuPickConfig = new ElementMenuPickConfig();
    acm.addBaseDiagramContextConfigurator(
        DiagramTypeConstants.UML_ANY_DIAGRAM, elementMenuPickConfig);

    // Add SBE to main menu(top bar menu)
    SBEMainMenuPick sbeMainMenuPick = new SBEMainMenuPick();
    acm.addMainMenuConfigurator(sbeMainMenuPick);
    /* End UI Setup */

    initialized = true;
  }

  @Override
  public boolean close() {
    // no teardown needed
    return true;
  }

  @Override
  public boolean isSupported() {
    // plugin can check here for specific conditions
    // if false is returned plugin is not loaded.
    return true;
  }
}
