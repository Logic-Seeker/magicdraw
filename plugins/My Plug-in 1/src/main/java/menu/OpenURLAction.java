package main.java.menu;//package ui.menu;
//
//import com.nomagic.magicdraw.actions.MDAction;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.awt.event.ActionEvent;
//
//public class OpenURLAction extends MDAction {
//    private static final Logger logger = LoggerFactory.getLogger(OpenURLAction.class);
//
//    private String urlType;
//
//    public OpenURLAction(String urlType, String id, String name) {
//        super(id, name, null, null);
//        this.urlType = urlType;
//    }
//
//    @Override
//    public void actionPerformed(ActionEvent e) {
//        String url = "";
//        String displayName = "Digital Thread";
//        /*if ("attach".equals(urlType)) {
//            url = Identifier.buildChannelManagementURL();
//            displayName = "Create Digital Thread Channel";
//            logger.info("Opening SBE URI: {}", url);
//        } else if ("subscribe".equals(urlType)) {
//            url = Identifier.buildSubscribeURL(Environment.entitySet, Environment.partition);
//            displayName = "Create Digital Thread Subscription";
//            logger.info("Opening SBE URI: {}", url);
//        }
//
//        SbeUI sbeUI = new SbeUI();
//        sbeUI.run(url, displayName);*/
//    }
//}
