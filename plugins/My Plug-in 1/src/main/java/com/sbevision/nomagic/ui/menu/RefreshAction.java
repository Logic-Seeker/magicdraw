package main.java.com.sbevision.nomagic.ui.menu;

import com.nomagic.magicdraw.actions.MDAction;
import com.sbevision.interchange.grpc.PullType;
import main.java.com.sbevision.nomagic.service.AttachService;
import main.java.com.sbevision.nomagic.service.SubscribeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;

public class RefreshAction extends MDAction {
  private static final Logger logger = LoggerFactory.getLogger(RefreshAction.class);
  private SubscribeService subscribeService;
  private AttachService attachService;
  // Determines whether the refresh is for Authoritative or Subscribed Objects
  private PullType pullType;

  public RefreshAction(String id, String name, PullType pullType) {
    super(id, name, null, null);
    this.subscribeService = SubscribeService.getInstance();
    this.attachService = AttachService.getInstance();
    this.pullType = pullType;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (attachService.checkIfAttachedToCorrectProject()) {
      subscribeService.execute(pullType);
    } else {
      attachService.execute(false);
      subscribeService.execute(pullType);
    }
  }
}
