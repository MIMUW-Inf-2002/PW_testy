package cp2022.tests.pggp_tests.tests.bigrandom;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;

public class TestBigRandom5 extends Test {
    // Jest 100 pracowników, 1000 stanowisk i każdy chce zrobić po 1000 losowych akcji. Czas pracy ustawiony na 0.
    public TestBigRandom5() {
        timeOfAuthor = 12208L;
    }
    public boolean run(int verbose) {
        Worker[] workers = new Worker[100];

        for (int i = 0; i < 100; i++) {
            workers[i] = workerRandomActions(i, 1000, 1000, 30);
        }

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(1000, 0, workers, verbose, false);
        return wrapper.start();
    }
}
