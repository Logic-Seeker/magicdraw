package main.java.com.sbevision.nomagic.utils;//package main.java.com.sbevision.nomagic.utils;

import com.sbevision.interchange.grpc.PullRequestOrOperationResultProto;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This holds the request from subscription(for now) */
public class ObserverMap {
  private static final Logger logger = LoggerFactory.getLogger(ObserverMap.class);

  private StreamObserver<PullRequestOrOperationResultProto> observer;

  public void addObserver(StreamObserver<PullRequestOrOperationResultProto> observer) {
    logger.debug("adding observer to map");
    this.observer = observer;
  }

  public void sendItem(PullRequestOrOperationResultProto item) {
    logger.debug("sending item with observer: {}", item);
    if (observer == null) {
      logger.error("could not send item, observer is null");
    } else {
      observer.onNext(item);
    }
  }

  public void complete() {
    logger.debug("operation completed with observer");
    observer.onCompleted();
  }

  public void remove() {
    observer = null;
  }
}
