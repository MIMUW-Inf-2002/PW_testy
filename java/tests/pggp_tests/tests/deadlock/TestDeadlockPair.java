package cp2022.tests.pggp_tests.tests.deadlock;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.workshop_actions.Action;

public class TestDeadlockPair extends Test {
    // Dwójka pracowników wchodzi na dwa stanowiska i chcą się zamienić.

    public TestDeadlockPair(){
        timeOfAuthor = 204L;
    }

    public boolean run(int verbose) {
        Action[] firstWorkerActions = {
                enter(0),
                sleep(100),
                use(),
                switchTo(1),
                sleep(30),
                use(),
                leave()
        };
        Action[] secondWorkerActions = {
                enter(1),
                sleep(100),
                use(),
                switchTo(0),
                use(),
                leave()
        };

        Worker[] workers = {
                new Worker(1, firstWorkerActions),
                new Worker(2, secondWorkerActions),
        };

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(2, 50, workers, verbose, false);
        return wrapper.start();
    }
}
