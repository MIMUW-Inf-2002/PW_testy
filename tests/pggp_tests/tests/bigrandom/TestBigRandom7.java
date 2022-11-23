package cp2022.tests.pggp_tests.tests.bigrandom;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;

public class TestBigRandom7 extends Test {
    // Jest 100 pracowników, 30 stanowisk i każdy chce zrobić po 100 losowych akcji.
    // Czas pracy ustawiony na 10.

    public TestBigRandom7(){
        timeOfAuthor = 4598L;
    }

    public boolean run(int verbose) {
        Worker[] workers = new Worker[100];


        for (int i = 0; i < 100; i++) {
            workers[i] = workerRandomActionsAndSleeps(
                    i,
                    100,
                    30,
                    10,
                    30,
                    30);
        }

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(30, 10, workers, verbose, false);
        return wrapper.start();
    }
}
