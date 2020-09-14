package main.java.com.sbevision.nomagic.ui.menu;

import com.nomagic.magicdraw.actions.MDAction;
import main.java.com.sbevision.nomagic.service.PublishingService;

import java.awt.event.ActionEvent;

// import com.sbevision.nomagic.service.AttachService;
// import com.sbevision.nomagic.service.PublishingService;
// import com.sbevision.nomagic.utils.Environment;
// import com.sbevision.nomagic.utils.TeamworkCloud;

public class PublishAction extends MDAction {
  public PublishingService publishingService = PublishingService.getInstance();

  public PublishAction(String id, String name) {
    super(id, name, null, null);
  }

  @Override
  public void actionPerformed(ActionEvent e) {

    publishingService.execute();
  }
}
