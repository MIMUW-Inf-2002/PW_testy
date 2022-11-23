package cp2022.tests.pggp_tests.tests.bigrandom;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;

public class TestBigRandom6 extends Test {
    // Jest 100 pracowników, 5 stanowisk i każdy chce zrobić po 100 losowych akcji.
    // Czas pracy ustawiony na 10.

    public TestBigRandom6() {
        timeOfAuthor = 16509L;
    }

    public boolean run(int verbose) {
        Worker[] workers = new Worker[100];


        for (int i = 0; i < 100; i++) {
            workers[i] = workerRandomActionsAndSleeps(
                    i,
                    100,
                    5,
                    10,
                    30,
                    30);
        }

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(5, 10, workers, verbose, false);
        return wrapper.start();
    }
}
