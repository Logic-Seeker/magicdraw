package main.java.com.sbevision.nomagic.ui;

import com.sbevision.interchange.grpc.PullType;
import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.engine.PasswordStore;
import com.teamdev.jxbrowser.engine.RenderingMode;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import main.java.com.sbevision.nomagic.service.SubscribeService;
import main.java.com.sbevision.nomagic.utils.Environment;
import main.java.com.sbevision.nomagic.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SbeUI {
  private static final Logger logger = LoggerFactory.getLogger(SbeUI.class);

  private JFrame frame;
  private Engine engine;
  private Browser browser;
  private BrowserView browserView;
  private SubscribeService subscribeService = new SubscribeService();

  /** Loads initial browser engine and frame */
  public SbeUI() {
    // Trail license
    EngineOptions options =
        EngineOptions.newBuilder(RenderingMode.OFF_SCREEN)
            .licenseKey(Environment.JX_BROWSER_LICENSE)
            .passwordStore(PasswordStore.GNOME_KEYRING)
            .userDataDir(Utils.getBrowserDataPath())
            .build();
    engine = Engine.newInstance(options);
    browser = engine.newBrowser();
    browserView = BrowserView.newInstance(browser);
    frame = new JFrame();
  }

  /**
   * Creates UI for webview
   *
   * @param url item sbe url to load in iframe
   * @param label item name
   */
  private void createContent(String url, String label, Boolean sinkInButton) {
    frame.addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent e) {
            close();
          }
        });
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    //
    frame.setTitle("Digital Thread");
    frame.setSize(1000, 800);

    if (sinkInButton) {
      JButton okButton = new JButton();
      okButton.setSize(100, 100);
      okButton.setText("Sink-In");
      okButton.setForeground(Color.BLACK);
      okButton.setBackground(new Color(66, 135, 245));
      okButton.addActionListener(e -> okButtonClick());
      JPanel buttonPanel = new JPanel();
      buttonPanel.add(okButton);
      frame.getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
    }

    JLabel itemLabel = new JLabel(label);

    browser.navigation().loadUrl(url);

    frame.getContentPane().add(itemLabel, BorderLayout.PAGE_START);
    frame.getContentPane().add(browserView, BorderLayout.CENTER);

    frame.setVisible(true);
  }

  private void okButtonClick() {
    subscribeService.execute(PullType.SUBSCRIPTION);
  }

  /**
   * staring point of webview
   *
   * @param url item sbe url to load in webview
   * @param label item name
   */
  public void run(String url, String label, Boolean sinkInButton) {
    createContent(url, label, sinkInButton);
  }

  /** Closing frames and browser */
  public void close() {
    frame.dispose();
    browser.close();
    engine.close();
  }
}
