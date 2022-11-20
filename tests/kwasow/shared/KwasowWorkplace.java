package cp2022.tests.kwasow.shared;

import cp2022.base.Workplace;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static cp2022.tests.kwasow.shared.KwasowLogger.log;
import static java.lang.Thread.sleep;

public class KwasowWorkplace extends Workplace {
  private final String name;
  private static final Random random = new Random();
  private final boolean verbose;
  private final AtomicInteger numberOfUsers = new AtomicInteger(0);

  public KwasowWorkplace(KwasowWorkplaceId id, String name, boolean verbose) {
    super(id);

    this.name = name;
    this.verbose = verbose;
  }

  @Override
  public void use() {
    String dyiManiac = Thread.currentThread().getName();
    log(dyiManiac + " starts using " + name, verbose);

    if (!numberOfUsers.compareAndSet(0, 1)) {
      throw new IllegalStateException(
          "Two users using " + name + " at the same time! " + dyiManiac + " entered second");
    }

    try {
      sleep(random.nextInt(1000) + 2000);
    } catch (InterruptedException e) {
      throw new RuntimeException("panic: unexpected thread interruption");
    } finally {
      if (!numberOfUsers.compareAndSet(1, 0)) {
        throw new IllegalStateException("Two users using " + name + " at the same time!");
      }

      log(dyiManiac + " stops using " + name, verbose);
    }
  }

  public String getName() {
    return name;
  }

}
