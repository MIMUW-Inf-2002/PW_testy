package cp2022.tests.pggp_tests.tests.bigrandom;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;

public class TestBigRandomStarvation2 extends Test {

    // Jest 100 pracowników, 3 stanowiska i każdy chce zrobić po 100 losowych akcji.
    // W akcjach są losowe sleepy.

    public TestBigRandomStarvation2() {
        timeOfAuthor = 61320L;
    }
    public boolean run(int verbose) {
        Worker[] workers = new Worker[10];

        for (int i = 0; i < 10; i++) {
            workers[i] = workerRandomActionsAndSleeps(
                    i,
                    100,
                    3,
                    20,
                    50,
                    10
            );
        }

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(3, 10, workers, verbose, true);
        return wrapper.start();
    }
}
