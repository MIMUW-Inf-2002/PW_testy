package cp2022.tests.pggp_tests.tests;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.workshop_actions.Action;

public class Test20StarvationBigStar extends Test {
    // Są 102 stanowiska, na stanowisko 0 swobodnie wchodzą ludzie i wychodzą. Na stanowiska 1-100 weszli ludzie. Chcą zmienić na stanowisko 101 i wyjść.

    public Test20StarvationBigStar() {
        timeOfAuthor = 6680L;
    }
    public boolean run(Boolean verbose) {
        Worker[] workers = new Worker[100];
        Action[] workerActions = {
                enter(0),
                leave()
        };

        for (int i = 0; i < 100; i++) {
            workers[i] = new Worker(i, workerActions);
        }
        for (int i = 100; i < 100; i++) {
            workers[i] = new Worker(i, new Action[]{enter(i - 100 + 1), switchTo(101), leave()});
        }

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(102, 1, workers, verbose, true);
        return wrapper.start();
    }
}
