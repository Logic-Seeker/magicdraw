package main.java.com.sbevision.nomagic.ui.menu;

import com.nomagic.magicdraw.actions.MDAction;
import main.java.com.sbevision.nomagic.service.AttachService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;

public class AttachAction extends MDAction {
  private static final Logger logger = LoggerFactory.getLogger(AttachAction.class);
  private AttachService attachService;

  public AttachAction(String id, String name) {
    super(id, name, null, null);
    this.attachService = AttachService.getInstance();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    attachService.execute(true);
  }
}
