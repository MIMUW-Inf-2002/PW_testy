package cp2022.tests.pggp_tests.tests;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.workshop_actions.Action;

public class Test9SimpleQueueInsideAndUse extends Test {
    // Dwaj pracownicy używają odpowiednio stanowisk 0 i 2 oraz 1 i 2. Jeden z nich musi więc poczekać na 2.

    public Test9SimpleQueueInsideAndUse() {
        timeOfAuthor = 204L;
    }
    public boolean run(Boolean verbose) {
        Action[] firstWorkerActions = {
                enter(0),
                use(),
                switchTo(2),
                leave()
        };
        Action[] secondWorkerActions = {
                enter(1),
                use(),
                switchTo(2),
                leave()
        };

        Worker[] workers = {
                new Worker(1, firstWorkerActions),
                new Worker(2, secondWorkerActions)
        };

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(3, 200, workers, verbose, false);
        return wrapper.start();
    }
}
