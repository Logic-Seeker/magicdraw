package main.java.com.sbevision.nomagic.ui.menu;

import com.nomagic.magicdraw.actions.MDAction;
import main.java.com.sbevision.nomagic.service.AttachService;
import main.java.com.sbevision.nomagic.ui.SbeUI;
import main.java.com.sbevision.nomagic.utils.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;

public class AuthoritativeDiffAction extends MDAction {
  private static final Logger logger = LoggerFactory.getLogger(UIAction.class);

  public static enum UrlName {
    attach,
    subscribe,
    diff,
    digitalThread
  }

  private UrlName urlType;
  private AttachService attachService;

  public AuthoritativeDiffAction(UrlName urlType, String id, String name) {
    super(id, name, null, null);
    this.urlType = urlType;
    this.attachService = AttachService.getInstance();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (attachService.checkIfAttachedToCorrectProject()) {
      create_subscription_ui();
    } else {
      AttachService.getInstance().execute(false);
      create_subscription_ui();
    }
  }

  private void create_subscription_ui() {
    String url = "";
    String displayName = Environment.DIGITAL_THREAD;
    if (UrlName.subscribe == urlType) {
      url = Environment.authoritativeMergeUiLink;
      displayName = "Digital Thread Diff UI";
    }
    logger.info("Opening SBE URI: {}", url);
    SbeUI sbeUI = new SbeUI();
    sbeUI.run(url, displayName, true);
  }
}
