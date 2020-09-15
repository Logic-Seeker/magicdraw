package main.java.com.sbevision.nomagic.repository; // package
// main.java.com.sbevision.nomagic.repository;

import com.google.protobuf.Empty;
import com.sbevision.interchange.grpc.*;
import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.MetadataUtils;
import io.grpc.stub.StreamObserver;
import io.netty.handler.ssl.SslContext;
import main.java.com.sbevision.nomagic.utils.Environment;
import main.java.com.sbevision.nomagic.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ConsumerRepository {
  private static final Logger logger = LoggerFactory.getLogger(ConsumerRepository.class);
  private static ConsumerRepository consumerRepository;

  private ConsumerServiceGrpc.ConsumerServiceBlockingStub blockingStub;
  private ConsumerServiceGrpc.ConsumerServiceStub asyncStub;
  private static ManagedChannel channel;

  public static ConsumerRepository getInstance() {
    if (consumerRepository == null) {
      consumerRepository = new ConsumerRepository();
    }
    return consumerRepository;
  }

  /** Hide public constructor for singleton */
  private ConsumerRepository() {}

  private Metadata getHeader() {
    Metadata header = new Metadata();
    Metadata.Key<String> key =
        Metadata.Key.of(Environment.AUTHORIZATION, Metadata.ASCII_STRING_MARSHALLER);
    header.put(key, Environment.TOKEN);
    return header;
  }

  private ManagedChannel createChannel() {
    ManagedChannel originChannel = null;
    try {
      if (Environment.SBE_CONNECTOR_PORT == 443) {
        SslContext sslcontext = null;
        InputStream ca;
        if (Utils.valueExists(Environment.SBE_GRPC_CERT)) {
          ca = new FileInputStream(Environment.SBE_GRPC_CERT);
        } else {
          ca = ConsumerRepository.class.getClassLoader().getResourceAsStream("sbe_cert.pem");
        }
        sslcontext = GrpcSslContexts.forClient().trustManager(ca).build();

        originChannel =
            NettyChannelBuilder.forAddress(
                    Environment.SBE_CONNECTOR_HOST, Environment.SBE_CONNECTOR_PORT)
                .sslContext(sslcontext)
                .intercept(MetadataUtils.newAttachHeadersInterceptor(getHeader()))
                .build();
      } else {
        originChannel =
            NettyChannelBuilder.forAddress(
                    Environment.SBE_CONNECTOR_HOST, Environment.SBE_CONNECTOR_PORT)
                .intercept(MetadataUtils.newAttachHeadersInterceptor(getHeader()))
                .usePlaintext()
                .build();
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, "Cant connect to Server " + e.getMessage());
    }

    return originChannel;
  }

  private ConsumerServiceGrpc.ConsumerServiceBlockingStub getBlockingStub() {
    if (channel == null
        || ConnectivityState.READY != channel.getState(true)
        || blockingStub == null) {
      channel = createChannel();
      blockingStub = ConsumerServiceGrpc.newBlockingStub(channel);
    }
    return blockingStub;
  }

  private ConsumerServiceGrpc.ConsumerServiceStub getAsyncStub() {
    if (channel == null || ConnectivityState.READY != channel.getState(true) || asyncStub == null) {
      channel = createChannel();
      asyncStub = ConsumerServiceGrpc.newStub(channel);
    }
    return asyncStub;
  }

  public void save(List<ExternalItemProto> externalItems) throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1);
    StreamObserver<Empty> responseObserver =
        new StreamObserver<Empty>() {
          @Override
          public void onNext(Empty summary) {
            // nothing to do with this response, we are just sending data
          }

          @Override
          public void onError(Throwable t) {
            logger.error("{}", t);
            latch.countDown();
          }

          @Override
          public void onCompleted() {
            latch.countDown();
          }
        };

    StreamObserver<ExternalItemProto> requestObserver = getAsyncStub().save(responseObserver);
    try {
      for (ExternalItemProto externalItem : externalItems) {
        requestObserver.onNext(externalItem);
        if (latch.getCount() == 0) {
          // RPC completed or errored before we finished sending.
          // Sending further requests won't error, but they will just be thrown away.
          return;
        }
      }
    } catch (RuntimeException e) {
      // Cancel RPC
      logger.error("", e);
      requestObserver.onError(e);
    }
    // Mark the end of requests
    requestObserver.onCompleted();

    // Receiving happens asynchronously
    latch.await();
  }

  public void pull(PullRequestProto pullRequestProto) {

    SubscriptionThread subscriptionThread =
        new SubscriptionThread(pullRequestProto, getAsyncStub());
    Thread t = new Thread(subscriptionThread);
    t.start();
  }

  public AttachProto attach(AttachRequestProto attachRequestProto) {
    try {
      return getBlockingStub().attach(attachRequestProto);
    } catch (RuntimeException e) {
      logger.error("", e);
    }
    return null;
  }
}
