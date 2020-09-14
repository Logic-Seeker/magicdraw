package main.java.com.sbevision.nomagic.ui.menu;//package main.java.com.sbevision.nomagic.ui.menu;
//
//import com.nomagic.magicdraw.actions.MDAction;
//import com.sbevision.interchange.grpc.PullType;
//import com.sbevision.nomagic.service.AttachService;
//import com.sbevision.nomagic.service.SubscribeService;
//import com.sbevision.nomagic.utils.Environment;
//import com.sbevision.nomagic.utils.TeamworkCloud;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.util.StringUtils;
//
//import javax.swing.*;
//import java.awt.event.ActionEvent;
//
//public class SubscribeAction extends MDAction {
//  private static final Logger logger = LoggerFactory.getLogger(SubscribeAction.class);
//  private SubscribeService subscribeService;
//  private AttachService attachService;
//  private PullType pullType;
//
//  public SubscribeAction(String id, String name, PullType pullType) {
//    super(id, name, null, null);
//    this.subscribeService = SubscribeService.getInstance();
//    this.attachService = AttachService.getInstance();
//    this.pullType = pullType;
//  }
//
//  @Override
//  public void actionPerformed(ActionEvent e) {
//
//    // todo refactor duplicated code for preaction checks
//
//    if (StringUtils.isEmpty(Environment.project)) {
//      String message = "Cannot load to 'Digital Thread' no project is open";
//      logger.error(message);
//      JOptionPane.showMessageDialog(null, message);
//      return;
//    }
//
//    // attach before trying to publish
//    attachService.execute(false);
//
//    if (!Environment.project.equals(TeamworkCloud.getCurrentProjectName())) {
//      String message =
//          "Cannot load to 'Digital Thread' project currently open is not the attached project";
//      logger.error(message);
//      JOptionPane.showMessageDialog(null, message);
//      return;
//    }
//
//    if (!Environment.attached) {
//      String message = "Cannot load to 'Digital Thread' project is not yet attached";
//      logger.error(message);
//      JOptionPane.showMessageDialog(null, message);
//      return;
//    }
//
//    if (StringUtils.isEmpty(Environment.channel) || StringUtils.isEmpty(Environment.source)) {
//      String message = "Cannot load to 'Digital Thread' required information is missing";
//      logger.error(message);
//      JOptionPane.showMessageDialog(null, message);
//      return;
//    }
//
//    subscribeService.execute(pullType);
//  }
//}
