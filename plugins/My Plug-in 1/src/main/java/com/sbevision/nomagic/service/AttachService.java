package main.java.com.sbevision.nomagic.service;

//import main.java.com.sbevision.nomagic.ui.SbeUI;
import main.java.com.sbevision.nomagic.ui.SbeUI;
import main.java.com.sbevision.nomagic.utils.TeamworkCloud;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttachService {

  private static final Logger logger = LoggerFactory.getLogger(AttachService.class);
  private static AttachService attachService;

  private AttachService() {
    // hide public constructor so everyone needs the getInstance() method
  }

  public static AttachService getInstance() {
    if (attachService == null) {
      attachService = new AttachService();
    }
    return attachService;
  }

  public void execute(boolean openUI) {
    String project = TeamworkCloud.getCurrentProjectName();

    try {

      if (openUI) {
        String url = "www.google.com";
        String displayName = "Create Digital Thread Channel";
        logger.info("Opening SBE URI: {}", url);
        SbeUI sbeUI = new SbeUI();
        sbeUI.run(url, displayName);
        execute(false);
      }
    } catch (Exception e) {
      logger.error("The connection to the digital thread was interrupted, could not attach", e);
    }
  }

  //  private AttachRequestProto createAttachRequest(String projectName) {
  //    return AttachRequestProto.newBuilder()
  //        .setDataSourceType(Environment.SOURCE_TYPE)
  //        .putChannelProperties(Environment.SBE_PROJECT_IDENTIFIER, projectName)
  //        .build();
  //  }
}
