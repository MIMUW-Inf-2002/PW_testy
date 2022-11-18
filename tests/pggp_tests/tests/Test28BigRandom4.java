package cp2022.tests.pggp_tests.tests;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;

public class Test28BigRandom4 extends Test {
    // Jest 100 pracowników, 50 stanowisk i każdy chce zrobić po 10 losowych akcji.
    public Test28BigRandom4() {
        timeOfAuthor = 1675L;
    }

    public boolean run(Boolean verbose) {
        Worker[] workers = new Worker[100];

        for (int i = 0; i < 100; i++) {
            workers[i] = workerRandomActions(i, 50, 50, 30);
        }

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(50, 3, workers, verbose, false);
        return wrapper.start();
    }
}
