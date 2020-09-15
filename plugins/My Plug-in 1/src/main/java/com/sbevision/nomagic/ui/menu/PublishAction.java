package main.java.com.sbevision.nomagic.ui.menu;

import com.nomagic.magicdraw.actions.MDAction;
import main.java.com.sbevision.nomagic.service.AttachService;
import main.java.com.sbevision.nomagic.service.PublishingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;

public class PublishAction extends MDAction {
  private static final Logger logger = LoggerFactory.getLogger(PublishAction.class);
  private PublishingService publishingService;
  private AttachService attachService;

  public PublishAction(String id, String name) {
    super(id, name, null, null);
    this.publishingService = PublishingService.getInstance();
    this.attachService = AttachService.getInstance();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (attachService.checkIfAttachedToCorrectProject()) {
      publishingService.execute();
    } else {
      attachService.execute(false);
      publishingService.execute();
    }
  }
}
