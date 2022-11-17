package cp2022.tests.pggp_tests.tests;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;

public class Test24BigRandom1 extends Test {
    // Jest 100 pracowników, 3 stanowiska i każdy chce zrobić po 100 losowych akcji.
    public Test24BigRandom1() {
        timeOfAuthor = 1067L;
    }

    public boolean run(Boolean verbose) {
        Worker[] workers = new Worker[100];

        for (int i = 0; i < 100; i++) {
            workers[i] = workerRandomActions(i, 100, 3, 20);
        }

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(3, 10, workers, verbose, false);
        return wrapper.start();
    }
}
