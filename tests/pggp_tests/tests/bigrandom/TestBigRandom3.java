package cp2022.tests.pggp_tests.tests.bigrandom;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;

public class TestBigRandom3 extends Test {
    // Jest 100 pracowników, 10 stanowisk i każdy chce zrobić po 100 losowych akcji.
    public TestBigRandom3() {
        timeOfAuthor = 1957L;
    }

    public boolean run(int verbose) {
        Worker[] workers = new Worker[100];

        for (int i = 0; i < 100; i++) {
            workers[i] = workerRandomActions(i, 100, 10, 30);
        }

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(10, 1, workers, verbose, false);
        return wrapper.start();
    }
}
