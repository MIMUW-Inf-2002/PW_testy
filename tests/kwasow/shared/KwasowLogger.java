package cp2022.tests.kwasow.shared;

public class KwasowLogger {

  public static void log(String message, boolean verbose) {
    if (verbose) {
      System.out.println(message);
    }
  }

}
