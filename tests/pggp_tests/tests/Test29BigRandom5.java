package cp2022.tests.pggp_tests.tests;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;

public class Test29BigRandom5 extends Test {
    // Jest 100 pracowników, 1000 stanowisk i każdy chce zrobić po 10000 losowych akcji. Czas pracy ustawiony na 0.
    public Test29BigRandom5() {
        timeOfAuthor = 176283L;
    }

    public boolean run(Boolean verbose) {
        Worker[] workers = new Worker[100];

        for (int i = 0; i < 100; i++) {
            workers[i] = workerRandomActions(i, 10000, 1000, 30);
        }

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(1000, 0, workers, verbose, false);
        return wrapper.start();
    }
}
