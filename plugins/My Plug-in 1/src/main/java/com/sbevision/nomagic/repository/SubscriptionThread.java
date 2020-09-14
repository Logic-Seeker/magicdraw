package main.java.com.sbevision.nomagic.repository;//package main.java.com.sbevision.nomagic.repository;
//
//import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
//import com.sbevision.interchange.grpc.*;
//import com.sbevision.nomagic.plugin.MagicdrawHelper;
//import com.sbevision.nomagic.service.SubscribeService;
//import com.sbevision.nomagic.utils.Environment;
//import com.sbevision.nomagic.utils.ObserverMap;
//import io.grpc.stub.StreamObserver;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//
///** This is for pulling subscribed items from the consumer in a new thread */
//public class SubscriptionThread implements Runnable {
//  private static final Logger logger = LoggerFactory.getLogger(SubscriptionThread.class);
//
//  private PullRequestProto pullRequestProto;
//  private ConsumerServiceGrpc.ConsumerServiceStub asyncStub;
//  private MagicdrawHelper magicdrawHelper;
//
//  public SubscriptionThread(
//          PullRequestProto pullRequestProto, ConsumerServiceGrpc.ConsumerServiceStub asyncStub) {
//    this.pullRequestProto = pullRequestProto;
//    this.asyncStub = asyncStub;
//    this.magicdrawHelper = MagicdrawHelper.getInstance();
//  }
//
//  /**
//   * The actual thread that runs The {@link CountDownLatch} allow us to put a timeout on the thread
//   * without closing the connection early The {@link ObserverMap} allows us to store the request and
//   * access it's methods outside of normal operation for the request, this is needed to send back
//   * the operation results as the external item has been processed
//   */
//  public void run() {
//    CountDownLatch latch = new CountDownLatch(1);
//    ObserverMap observerMap = new ObserverMap();
//
//    final StreamObserver<PullRequestOrOperationResultProto> requestObserver =
//        asyncStub.pull(
//            new StreamObserver<ExternalItemProto>() {
//              @Override
//              public void onNext(ExternalItemProto value) {
//                try {
//                  // do a thing here
//                  logger.debug("Recieved external item");
//
//                  String subscriptionName = value.getSubscriptionName();
//                  String partitonName = value.getAuthoritativePartitionName();
//                  String entitySetName = value.getAuthoritativeEntitySetName();
//
//                  Package sbePackage =
//                      magicdrawHelper.getSBESubPackage(
//                          subscriptionName, partitonName, entitySetName);
//
//                  logger.debug("Created/retrieved package: {}", sbePackage.getHumanName());
//
//                  OperationResultProto result =
//                      SubscribeService.getInstance().handleExternalItem(value, sbePackage);
//
//                  PullRequestOrOperationResultProto pullRequestOrOperationResultProto =
//                      PullRequestOrOperationResultProto.newBuilder()
//                          .setOperationResult(result)
//                          .build();
//
//                  observerMap.sendItem(pullRequestOrOperationResultProto);
//                } catch (Exception e) {
//                  logger.error("", e);
//                }
//              }
//
//              @Override
//              public void onError(Throwable t) {
//                logger.debug("Recieved pull error");
//                latch.countDown();
//              }
//
//              @Override
//              public void onCompleted() {
//                logger.debug("Recieved pull complete");
//                latch.countDown();
//                observerMap.complete();
//              }
//            });
//
//    observerMap.addObserver(requestObserver);
//    observerMap.sendItem(
//        PullRequestOrOperationResultProto.newBuilder().setPullRequest(pullRequestProto).build());
//    logger.debug("Sent pull request and pull request complete");
//
//    boolean success = false;
//    try {
//      success = latch.await(Environment.SBE_SUBSCRIPTION_TIMEOUT_SECONDS, TimeUnit.MINUTES);
//      if (logger.isDebugEnabled() && !success) {
//        logger.debug("Subscription operation timeout");
//      }
//    } catch (InterruptedException e) {
//      logger.error("subscription update interrupted");
//      Thread.currentThread().interrupt();
//    }
//  }
//}
