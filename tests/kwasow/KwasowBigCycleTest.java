package cp2022.tests.kwasow;

import cp2022.base.Workplace;
import cp2022.base.Workshop;
import cp2022.solution.WorkshopFactory;
import cp2022.tests.kwasow.shared.KwasowWorkplace;
import cp2022.tests.kwasow.shared.KwasowWorkplaceId;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import static cp2022.tests.kwasow.shared.KwasowLogger.log;

public class KwasowBigCycleTest {

  public static void run() {
    run(false);
  }

  public static void run(boolean verbose) {
    System.out.println("\nRunning test: " + KwasowBigCycleTest.class.getSimpleName());

    KwasowWorkplace saw = new KwasowWorkplace(new KwasowWorkplaceId(0), "the saw", verbose);
    KwasowWorkplace hammer = new KwasowWorkplace(new KwasowWorkplaceId(1), "the hammer", verbose);
    KwasowWorkplace sink = new KwasowWorkplace(new KwasowWorkplaceId(2), "the sink", verbose);

    ArrayList<Workplace> workplaces = new ArrayList<>(3);
    workplaces.add(saw);
    workplaces.add(hammer);
    workplaces.add(sink);

    Workshop workshop = WorkshopFactory.newWorkshop(workplaces);
    ArrayList<Thread> threads = new ArrayList<>(4);

    CountDownLatch syncLatch = new CountDownLatch(3);

    Runnable worker = () -> {
      try {
        Workplace workplace;
        String myName = Thread.currentThread().getName();

        log(myName + " tries to enter the workshop and occupy " + saw.getName(), verbose);
        workplace = workshop.enter(saw.getId());
        log(myName + " now occupies " + saw.getName(), verbose);
        workplace.use();

        for (int i = 0; i < 3; i++) {
          log(myName + " tries to switch its workplace to " + hammer.getName(), verbose);
          workplace = workshop.switchTo(hammer.getId());
          log(myName + " now occupies " + hammer.getName(), verbose);
          workplace.use();

          log(myName + " tries to switch its workplace to " + sink.getName(), verbose);
          workplace = workshop.switchTo(sink.getId());
          log(myName + " now occupies " + sink.getName(), verbose);
          workplace.use();

          log(myName + " tries to switch its workplace to " + saw.getName(), verbose);
          workplace = workshop.switchTo(saw.getId());
          log(myName + " now occupies " + saw.getName(), verbose);
          workplace.use();

          syncLatch.countDown();
        }

        log(myName + " leaves the workshop", verbose);
        workshop.leave();
      } catch (IllegalStateException e) {
        for (Thread t : threads) {
          t.interrupt();
        }

        e.printStackTrace();
      }
    };

    Runnable seba = () -> {
      try {
        // Seba czeka, aż wszystkie miejsca będą zajęte i wszystko się zacykli
        syncLatch.await();
        String myName = Thread.currentThread().getName();

        log(myName + " tries to enter the workshop and occupy " + saw.getName(), verbose);
        Workplace workplace = workshop.enter(saw.getId());
        log(myName + " now occupies " + saw.getName(), verbose);
        workplace.use();

        log(myName + " leaves the workshop", verbose);
        workshop.leave();
      } catch (InterruptedException e) {
        System.out.println("The " + KwasowBigCycleTest.class.getSimpleName() + " test was interrupted");
      }
    };

    threads.add(new Thread(worker, "Brajan"));
    threads.add(new Thread(worker, "Dżesika"));
    threads.add(new Thread(worker, "Janusz"));
    threads.add(new Thread(seba, "Seba"));

    for (Thread t : threads) {
      t.start();
    }

    try {
      for (Thread t : threads) {
        t.join();
      }
    } catch (InterruptedException e) {
      System.out.println("The " + KwasowBigCycleTest.class.getSimpleName() + " test was interrupted");
    }
  }

}
