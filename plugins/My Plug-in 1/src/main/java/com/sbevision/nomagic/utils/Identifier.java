package main.java.com.sbevision.nomagic.utils;

public class Identifier {

    private Identifier() {
        // hide public constructor for static only class
    }

    /**
     * @param name item name
     * @param guid item GUID
     * @return unique identity as same format as name for embedded (combines name guid and source)
     */
    public static String identityBuilder(String name, String guid) {
        name = getSbeValidName(name);
        return name + "_" + guid.replaceAll("\\s+", "") + "_" + Environment.source;
    }

    /**
     * @param projectName
     * @param identity
     * @return sbe url for selected item with identity
     */
    public static String buildDigitalThreadURL(String projectName, String identity) {
        return String.format("https://%s/embedded/#/editor/sbe/%s/%s/%s",
                Environment.SBE_FULLY_QUALIFIED_DOMAIN_NAME, Environment.entitySet, projectName, identity);
    }

    public static String buildChannelManagementURL() {
        return String.format("https://%s/embedded/#/channel-manager", Environment.SBE_FULLY_QUALIFIED_DOMAIN_NAME);
    }

    public static String buildSubscribeURL(String entitySet, String partition) {
        return String.format("https://%s/embedded/#/subscribe/%s/%s",
                Environment.SBE_FULLY_QUALIFIED_DOMAIN_NAME, entitySet, partition);
    }

    public static String buildAuthoritativeDiffURL(String entitySet, String partition) {
        return String.format("https://%s/embedded/#/diff/partition/%s/entityset/%s",
                Environment.SBE_FULLY_QUALIFIED_DOMAIN_NAME, partition, entitySet);
    }

    private static String getSbeValidName(String name) {
        return name.replaceAll("[^-_a-zA-Z0-9]*", "");
    }
}

