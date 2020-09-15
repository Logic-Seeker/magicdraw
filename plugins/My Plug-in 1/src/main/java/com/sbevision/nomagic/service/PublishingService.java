package main.java.com.sbevision.nomagic.service;

import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.sbevision.interchange.grpc.ExternalItemProto;
import main.java.com.sbevision.nomagic.plugin.CameoAPI;
import main.java.com.sbevision.nomagic.plugin.ExternalItemConvertor;
import main.java.com.sbevision.nomagic.repository.ConsumerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PublishingService {

  private static final Logger logger = LoggerFactory.getLogger(PublishingService.class);
  private static PublishingService publishingService;
  private ConsumerRepository consumerRepository = ConsumerRepository.getInstance();

  private PublishingService() {
    // hide public constructor so everyone needs the getInstance() method
  }

  public static PublishingService getInstance() {
    if (publishingService == null) {
      publishingService = new PublishingService();
    }
    return publishingService;
  }

  public void execute() {
    CameoAPI cameoAPI = CameoAPI.getInstance();
    cameoAPI.setProject();

    Collection<BaseElement> collection = cameoAPI.getAllModels();
    //    Collection<Element> collection =
    //        cameoAPI.getElementsBasedOnPackageQualifiedName("1-System Design::1-Concept
    // Level::3-Behavior::Use Cases");

    List<ExternalItemProto> externalItems = new ArrayList<>();
    for (BaseElement element : collection) {
      if (element instanceof Element) {
        logger.debug("no. of elements: {}", collection.size());
        ExternalItemProto externalItem = ExternalItemConvertor.toExternalItem((Element) element);
        externalItems.add(externalItem);
      }
    }

    try {
      consumerRepository.save(externalItems);
    } catch (InterruptedException e) {
      logger.error("The connection to the digital thread was interrupted", e);
      Thread.currentThread().interrupt();
    }
  }
}
