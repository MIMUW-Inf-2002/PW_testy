package cp2022.tests.pggp_tests.tests.deadlock;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;

public class TestDeadlockOneBigOneSmallCycleWithCommonVertex extends Test {
    // Jeden cykl 3-elementowy, jeden 2-elementowy z jednym wspólnym elementem.
    public TestDeadlockOneBigOneSmallCycleWithCommonVertex() {
        timeOfAuthor = 134L;
    }

    public boolean run(int verbose) {

        Worker[] workers = {
                new Worker(1, rotateCycle(0, 0, 2, 100)),
                new Worker(2, rotateCycle(1, 0, 2, 100)),
                new Worker(3, rotateCycle(2, 0, 2, 100)),
                new Worker(4, rotateCycle(2, 2, 3, 100)),
                new Worker(5, rotateCycle(3, 2, 3, 100))
        };

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(4, 1, workers, verbose, false);
        return wrapper.start();
    }
}
