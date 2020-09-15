package myplugin1;

import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.uml.DiagramTypeConstants;
import main.java.com.sbevision.nomagic.ui.menu.ElementMenuPickConfig;
import main.java.com.sbevision.nomagic.ui.menu.SBEMainMenuPick;

public class MyPlugin1 extends Plugin
{
	public static boolean initialized;
	
	@Override
	public void init()
	{
		ActionsConfiguratorsManager acm = ActionsConfiguratorsManager.getInstance();

		// Add Digital thread to model right-click menu(for classes only)
		ElementMenuPickConfig elementMenuPickConfig = new ElementMenuPickConfig();
		acm.addBaseDiagramContextConfigurator(
						DiagramTypeConstants.UML_ANY_DIAGRAM, elementMenuPickConfig);

		SBEMainMenuPick sbeMainMenuPick = new SBEMainMenuPick();
		acm.addMainMenuConfigurator(sbeMainMenuPick);
		initialized = true;
	}

	@Override
	public boolean close()
	{
		return true;
	}

	@Override
	public boolean isSupported()
	{
		return true;
	}
}
