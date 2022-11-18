package cp2022.tests.pggp_tests.tests;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;

public class Test26BigRandomStarvation extends Test {
    // Jest 10 pracowników, 5 stanowisk i każdy chce zrobić po 100 losowych akcji.
    public Test26BigRandomStarvation() {
        timeOfAuthor = 34046L;
    }

    public boolean run(Boolean verbose) {
        Worker[] workers = new Worker[10];

        for (int i = 0; i < 10; i++) {
            workers[i] = workerRandomActions(i, 100, 5, 20);
        }

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(5, 10, workers, verbose, true);
        return wrapper.start();
    }
}
