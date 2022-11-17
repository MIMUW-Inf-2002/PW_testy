package cp2022.tests.pggp_tests.tests;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;

public class Test22EfficiencyCycle extends Test {
    // Dużo rzeczy, które chcą chodzić po tym samym 5-cyklu. Z limitem czasu.
    public Test22EfficiencyCycle() {
        timeLimit = 7000L;
        timeOfAuthor = 4232L;
    }

    public boolean run(Boolean verbose) {

        Worker[] workers = new Worker[20];

        for (int i = 0; i < 20; i++) {
            workers[i] = new Worker(
                    i,
                    rotateCycle(0, 0, 5, 10)
            );
        }

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(6, 100, workers, verbose, false);
        return wrapper.start();
    }
}
