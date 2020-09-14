package main.java.com.sbevision.nomagic.utils;


import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {
  public static boolean valueExists(String value) {
    return value != null && !value.trim().isEmpty() && !value.equals("null");
  }


  public static Path getUserDataPath() {
    String rootPath = System.getProperty("user.home");
    String SBEFolder = "SBE";
    Path sbePath = Paths.get(rootPath, SBEFolder);
    sbePath.toFile().mkdirs();
    return sbePath;
  }

  public static Path getBrowserDataPath() {
    String browserFolder = ".ui";
    Path browserPath = Paths.get(getUserDataPath().toString(), browserFolder);
    browserPath.toFile().mkdirs();
    return browserPath;
  }

}
