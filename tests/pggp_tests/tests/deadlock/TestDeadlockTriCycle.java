package cp2022.tests.pggp_tests.tests.deadlock;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.workshop_actions.Action;

public class TestDeadlockTriCycle extends Test  {
    // Cykl złożony z 3 pracowników.

    public TestDeadlockTriCycle() {
        timeOfAuthor = 206L;
    }
    public boolean run(int verbose) {
        Action[] firstWorkerActions = {
                enter(0),
                sleep(100),
                use(),
                switchTo(1),
                use(),
                leave()
        };
        Action[] secondWorkerActions = {
                enter(1),
                sleep(100),
                use(),
                switchTo(2),
                use(),
                leave()
        };
        Action[] thirdWorkerActions = {
                enter(2),
                sleep(100),
                use(),
                switchTo(0),
                use(),
                leave()
        };

        Worker[] workers = {
                new Worker(1, firstWorkerActions),
                new Worker(2, secondWorkerActions),
                new Worker(3, thirdWorkerActions)
        };

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(3, 50, workers, verbose, false);
        return wrapper.start();
    }
}
