package cp2022.tests.pggp_tests.tests.efficiency;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;

public class TestEfficiencyBigRandom extends Test {
    // Jest 100 pracowników, 50 stanowisk i każdy chce zrobić po 100 losowych akcji.
    // Czas pracy ustawiony na 10.
    public TestEfficiencyBigRandom () {
        // Jeśli wykonujemy sekwencyjnie, to 100 * 100 * 10 = 100 sekund.
        // Jest 50 stanowisk, powinno pójść w około 2 sekundy + 3 sekundy na sleepy.
        // Limit to 10 sekund.
        timeLimit = 10000L;
    }


    public boolean run(int verbose) {
        Worker[] workers = new Worker[100];

        for (int i = 0; i < 100; i++) {
            workers[i] = workerRandomActionsAndSleeps(
                    i,
                    100,
                    30,
                    10,
                    10,
                    30);
        }

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(30, 10, workers, verbose, false);
        return wrapper.start();
    }
}
