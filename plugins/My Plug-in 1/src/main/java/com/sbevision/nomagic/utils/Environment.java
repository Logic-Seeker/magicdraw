package main.java.com.sbevision.nomagic.utils;

public class Environment {

  private Environment() {
    // just hiding the public constructor
  }

  public static void setAttachUiLink(String attachUiLink) {
    Environment.attachUiLink = attachUiLink;
  }

  public static void setCreateSubscriptionUiLink(String createSubscriptionUiLink) {
    Environment.createSubscriptionUiLink = createSubscriptionUiLink;
  }

  public static void setSubscriptionMergeUiLink(String subscriptionMergeUiLink) {
    Environment.subscriptionMergeUiLink = subscriptionMergeUiLink;
  }

  public static void setAuthoritativeMergeUiLink(String authoritativeMergeUiLink) {
    Environment.authoritativeMergeUiLink = authoritativeMergeUiLink;
  }

  public static String attachUiLink = null;
  public static String createSubscriptionUiLink = null;
  public static String subscriptionMergeUiLink = null;
  public static String authoritativeMergeUiLink = null;

  // SBE connection details
  public static final String SBE_CONNECTOR_HOST =
      envSetDefault("SBE_CONNECTOR_SERVICE_HOST", "grpc-shobhab-develop.sbe-devops.com");
  public static final int SBE_CONNECTOR_PORT =
      Integer.parseInt(envSetDefault("SBE_CONNECTOR_SERVICE_PORT", "443"));
  public static final String SBE_GRPC_CERT = envSetDefault("SBE_CONNECTOR_CERT_PATH", null);
  public static final String SBE_FULLY_QUALIFIED_DOMAIN_NAME =
      envSetDefault("SBE_FULLY_QUALIFIED_DOMAIN_NAME", "shobhab-develop.sbe-devops.com");
  public static final int SBE_SUBSCRIPTION_TIMEOUT_SECONDS =
      Integer.parseInt(envSetDefault("SBE_SUBSCRIPTION_TIMEOUT_SECONDS", "240"));

  // SBE internally used variables
  public static final String SOURCE_TYPE = envSetDefault("SBE_CAMEO_SOURCE_TYPE", "CAMEO");
  public static final String SBE_PROJECT_IDENTIFIER =
      envSetDefault("SBE_PROJECT_IDENTIFIER", "project");

  // Constant
  public static final String DIGITAL_THREAD = "Digital Thread";
  public static final String SUBSCRIPTION_FQN = "subscription_fqn";
  public static final String DIGITAL_THREAD_PROFILE = "Profile";
  public static final String DIGITAL_THREAD_STEREOTYPE = "DigitalThread";
  public static final String EXTERNAL_ID = "external_id";

  // Browser license
  public static final String JX_BROWSER_LICENSE =
      "1BNDIEOFAYVILBB8QPV5IQTER6RYT1NXJ6INA9Q0JMLQSS0QZV2TCEWFGTG1ULRFQLAOF8";

  // Authorization constants
  public static final String AUTHORIZATION = "Authorization";
  public static final String BEARER = "Bearer ";
  public static final String TOKEN =
      BEARER
          + envSetDefault(
              "JWT_BEARER_TOKEN",
              "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7InVzZXJJZCI6MCwidXNlcm5hbWUiOiJhZG1pbiIsInBhc3N3b3JkIjoiYWRtaW5fcGFzc3dvcmQyMDE3Iiwicm9sZSI6ImFkbWluIiwidmVyaWZpZWQiOiJ0cnVlIn0sImp0aSI6IjQ2OTIzODAxZDBiNmVmODVmM2ZmMjlkMjU5NGExOWQxZDExZmMwZWU2OWVkZjQxM2Q5M2MyZDBlMTA1MDcxZGI3NTdhZGQzN2EwN2I0ZjU3MTg0MjI3ZGI3YmJiNzZhMjJlYTM0MTQ3YWY2MmE5NmYxMmM2MWFjNGQ3MWFkMWQ1IiwiaWF0IjoxNTk0NDAxNjI3LCJleHAiOjE2MTAyOTkyMjd9.yemvES7X37spcl9u6wy32mMkv4X1vAFE4jIbeITjUJE");

  // Attachment variables
  public static String source = null;
  public static String channel = null;
  public static String project = null;
  public static String partition = null;
  public static String entitySet = null;
  public static boolean attached = false;

  public static String getSource() {
    return source;
  }

  public static void setSource(String source) {
    Environment.source = source;
  }

  public static void setChannel(String channel) {
    Environment.channel = channel;
  }

  public static void setProject(String project) {
    Environment.project = project;
  }

  public static void setPartition(String partition) {
    Environment.partition = partition;
  }

  public static void setEntitySet(String entitySet) {
    Environment.entitySet = entitySet;
  }

  public static void setAttached(boolean attached) {
    Environment.attached = attached;
  }

  private static String envSetDefault(String name, String defaultValue) {
    return System.getenv(name) == null ? defaultValue : System.getenv(name);
  }
}
