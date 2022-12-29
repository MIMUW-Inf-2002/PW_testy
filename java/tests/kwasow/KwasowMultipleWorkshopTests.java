package cp2022.tests.kwasow;

import java.util.ArrayList;

public class KwasowMultipleWorkshopTests {

  public static void run() {
    run(false);
  }

  public static void run(boolean verbose) {
    System.out.println("\nRunning test: " + KwasowMultipleWorkshopTests.class.getSimpleName());

    Runnable workshop = () -> {
        KwasowBigCycleTest.test(verbose);
    };

    ArrayList<Thread> threads = new ArrayList<>(6);

    for (int i = 0; i < 6; i++) {
      Thread t = new Thread(workshop, "Workshop " + i);
      threads.add(t);
      t.start();
    }

    try {
      for (Thread t : threads) {
        t.join();
      }
    } catch (InterruptedException e) {
      System.out.println("The " + KwasowMultipleWorkshopTests.class.getSimpleName() + " test was interrupted");
    }
  }

}
