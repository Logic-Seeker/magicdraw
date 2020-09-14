package main.java.menu;//package ui.menu;
//
//import com.nomagic.actions.AMConfigurator;
//import com.nomagic.actions.ActionsCategory;
//import com.nomagic.actions.ActionsManager;
//import com.nomagic.actions.NMAction;
//import com.nomagic.magicdraw.actions.MDActionsCategory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class SBEMainMenuPick implements AMConfigurator {
//    private static final Logger logger = LoggerFactory.getLogger(SBEMainMenuPick.class);
//
//
//    public int getPriority() {
////        return AMConfigurator.MEDIUM_PRIORITY;
//        return 69;
//    }
//
//
//    public void configure(ActionsManager mngr) {
//        logger.info("Adding Digital Thread main menu picks");
//
//        NMAction category = mngr.getActionFor("SBE_DIGITAL_THREAD");
//        if (category == null) {
//            category = new MDActionsCategory("SBE_DIGITAL_THREAD", "DigitalThread");
//        }
//
//        ((ActionsCategory) category).setNested(true);
//        mngr.addCategory((ActionsCategory) category);
//
//        AttachAction attach = new AttachAction("SBE_ATTACH", "Attach");
//        category.addAction(attach);
//
//        /*PublishAction publish = new PublishAction("SBE_PUBLISH", "Publish...");
//        category.addAction(publish);*/
//
//        ActionsCategory subscriptionCategory = new MDActionsCategory("SBE_SUBSCRIPTION", "Subscription");
//        subscriptionCategory.setNested(true);
//        category.addAction(subscriptionCategory);
//
//        /*OpenURLAction createSubscription = new OpenURLAction("subscribe", "SBE_CREATE_SUBSCRIPTION", "Create...");
//        subscriptionCategory.addAction(createSubscription);*/
//
//        /*SubscribeAction refreshSubscription = new SubscribeAction("SBE_REFRESH_SUBSCRIPTION", "Refresh");
//        subscriptionCategory.addAction(refreshSubscription);*/
//
////        List<String> blankURLs = new ArrayList<>();
////        OpenURLAction navigate = new OpenURLAction(blankURLs, "SBE_NAVIGATE", "Navigate...");
////        category.addAction(navigate);
//    }
//}
