package cp2022.tests.kwasow;

import cp2022.base.Workplace;
import cp2022.base.Workshop;
import cp2022.solution.WorkshopFactory;
import cp2022.tests.kwasow.shared.KwasowWorkplace;
import cp2022.tests.kwasow.shared.KwasowWorkplaceId;

import java.util.ArrayList;

import static cp2022.tests.kwasow.shared.KwasowLogger.log;

public class KwasowPairCycleTest {

  public static void run() {
    run(false);
  }

  public static void run(boolean verbose) {
    System.out.println("\nRunning test: " + KwasowPairCycleTest.class.getSimpleName());

    KwasowWorkplace saw = new KwasowWorkplace(new KwasowWorkplaceId(0), "the saw", verbose);
    KwasowWorkplace hammer = new KwasowWorkplace(new KwasowWorkplaceId(1), "the hammer", verbose);

    ArrayList<Workplace> workplaces = new ArrayList<>(2);
    workplaces.add(saw);
    workplaces.add(hammer);

    Workshop workshop = WorkshopFactory.newWorkshop(workplaces);
    ArrayList<Thread> threads = new ArrayList<>(2);

    Runnable worker = () -> {
      try {
        Workplace workplace;
        String myName = Thread.currentThread().getName();

        log(myName + " tries to enter the workshop and occupy " + saw.getName(), verbose);
        workplace = workshop.enter(saw.getId());
        log(myName + " now occupies " + saw.getName(), verbose);
        workplace.use();

        for (int i = 0; i < 2; i++) {
          log(myName + " tries to switch its workplace to " + hammer.getName(), verbose);
          workplace = workshop.switchTo(hammer.getId());
          log(myName + " now occupies " + hammer.getName(), verbose);
          workplace.use();

          log(myName + " tries to switch its workplace to " + saw.getName(), verbose);
          workplace = workshop.switchTo(saw.getId());
          log(myName + " now occupies " + saw.getName(), verbose);
          workplace.use();
        }

        workshop.leave();
        log(myName + " leaves the workshop", verbose);
      } catch (IllegalStateException e) {
        for (Thread t : threads) {
          t.interrupt();
        }

        e.printStackTrace();
      }
    };

    threads.add(new Thread(worker, "Brajan"));
    threads.add(new Thread(worker, "Dżesika"));

    for (Thread t : threads) {
      t.start();
    }

    try {
      for (Thread t : threads) {
        t.join();
      }
    } catch (InterruptedException e) {
      System.out.println("The " + KwasowPairCycleTest.class.getSimpleName() + " test was interrupted");
    }
  }

}
