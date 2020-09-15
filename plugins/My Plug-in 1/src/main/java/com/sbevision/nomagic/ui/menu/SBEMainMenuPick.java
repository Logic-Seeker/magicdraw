package main.java.com.sbevision.nomagic.ui.menu;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.sbevision.interchange.grpc.PullType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SBEMainMenuPick implements AMConfigurator {
  private static final Logger logger = LoggerFactory.getLogger(SBEMainMenuPick.class);

  @Override
  public int getPriority() {
    //        return AMConfigurator.MEDIUM_PRIORITY;
    return 69;
  }

  @Override
  public void configure(ActionsManager mngr) {
    logger.info("Adding Digital Thread main menu picks");

    NMAction category = mngr.getActionFor("SBE_DIGITAL_THREAD");
    if (category == null) {
      category = new MDActionsCategory("SBE_DIGITAL_THREAD", "DigitalThread");
    }

    ((ActionsCategory) category).setNested(true);
    mngr.addCategory((ActionsCategory) category);

    AttachAction attach = new AttachAction("SBE_ATTACH", "Attach");
    category.addAction(attach);

    PublishAction publish = new PublishAction("SBE_PUBLISH", "Publish");
    category.addAction(publish);

    ActionsCategory authoritativeCategory =
        new MDActionsCategory("SBE_AUTHORITATIVE", "Authoritative");
    authoritativeCategory.setNested(true);
    category.addAction(authoritativeCategory);

    RefreshAction sync =
        new RefreshAction(
            "SBE_AUTHORITATIVE_REFRESH", "Authoritative Refresh", PullType.AUTHORITATIVE);
    authoritativeCategory.addAction(sync);

    UIAction diff =
        new UIAction(
            UIAction.UrlName.authoritatitve_diff,
            "SBE_AUTHORITATIVE_DIFF",
            "Authoritative Diff");
    authoritativeCategory.addAction(diff);

    ActionsCategory subscriptionCategory = new MDActionsCategory("SBE_SUBSCRIPTION", "Subscribed");
    subscriptionCategory.setNested(true);
    category.addAction(subscriptionCategory);

    UIAction createSubscription =
        new UIAction(
            UIAction.UrlName.subscribe,
            "SBE_CREATE_SUBSCRIPTION",
            "Subscription Create");
    subscriptionCategory.addAction(createSubscription);

    RefreshAction refreshSubscription =
        new RefreshAction(
            "SBE_REFRESH_SUBSCRIPTION", "Subscription Refresh", PullType.SUBSCRIPTION);
    subscriptionCategory.addAction(refreshSubscription);

    UIAction subscriptionDiff =
        new UIAction(
            UIAction.UrlName.subscription_diff,
            "SBE_SUBSCRIPTION_DIFF",
            "Subscription Diff");
    subscriptionCategory.addAction(subscriptionCategory);

    //        List<String> blankURLs = new ArrayList<>();
    //        OpenURLAction navigate = new OpenURLAction(blankURLs, "SBE_NAVIGATE", "Navigate...");
    //        category.addAction(navigate);
  }
}
