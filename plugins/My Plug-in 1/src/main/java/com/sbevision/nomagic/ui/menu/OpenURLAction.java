package main.java.com.sbevision.nomagic.ui.menu;

import com.nomagic.magicdraw.actions.MDAction;
import main.java.com.sbevision.nomagic.utils.Environment;
import main.java.com.sbevision.nomagic.utils.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;

public class OpenURLAction extends MDAction {
  private static final Logger logger = LoggerFactory.getLogger(OpenURLAction.class);

  public static enum UrlName {
    attach,
    subscribe,
    diff,
    digitalThread
  }

  private UrlName urlType;

  public OpenURLAction(UrlName urlType, String id, String name) {
    super(id, name, null, null);
    this.urlType = urlType;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String url = "";
    String displayName = "Digital Thread";
    if (UrlName.attach == urlType) {
      url = Identifier.buildChannelManagementURL();
      displayName = "Create Digital Thread Channel";
    } else if (UrlName.subscribe == urlType) {
      url = Environment.createSubscriptionUiLink;
      displayName = "Create Digital Thread Subscription";
    } else if (UrlName.diff == urlType) {
      url = Identifier.buildAuthoritativeDiffURL(Environment.entitySet, Environment.partition);
      displayName = "Create Digital Thread Subscription";
    }

    logger.info("Opening SBE URI: {}", url);
//
//    SbeUI sbeUI = new SbeUI();
//    sbeUI.run(url, displayName);displayName
  }
}
