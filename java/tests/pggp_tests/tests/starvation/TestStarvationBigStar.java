package cp2022.tests.pggp_tests.tests.starvation;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.workshop_actions.Action;

public class TestStarvationBigStar extends Test {
    // Są 102 stanowiska, na stanowisko 0 swobodnie wchodzą ludzie i wychodzą. Na stanowiska 1-100 weszli ludzie. Chcą zmienić na stanowisko 101 i wyjść.

    public TestStarvationBigStar() {
        timeOfAuthor = 9245L;
    }
    public boolean run(int verbose) {
        Worker[] workers = new Worker[100];
        Action[] workerActions = {
                enter(0),
                use(),
                leave()
        };

        for (int i = 0; i < 100; i++) {
            workers[i] = new Worker(i, workerActions);
        }
        for (int i = 100; i < 100; i++) {
            workers[i] = new Worker(i, new Action[]{enter(i - 100 + 1), use(), switchTo(101), use(), leave()});
        }

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(102, 1, workers, verbose, true);
        return wrapper.start();
    }
}
