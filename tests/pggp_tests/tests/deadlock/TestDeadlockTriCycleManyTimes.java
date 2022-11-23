package cp2022.tests.pggp_tests.tests.deadlock;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.workshop_actions.Action;

public class TestDeadlockTriCycleManyTimes extends Test {
    // Analogicznie jak w poprzednim teście, tylko kręcenie w cyklu trwa dłużej.

    public TestDeadlockTriCycleManyTimes() {
        timeOfAuthor = 10L;
    }
    public boolean run(int verbose) {
        Action[] firstWorkerActions = rotateCycle(0, 0, 2, 10);
        Action[] secondWorkerActions = rotateCycle(1, 0, 2, 10);
        Action[] thirdWorkerActions = rotateCycle(2, 0, 2, 10);

        Worker[] workers = {
                new Worker(1, firstWorkerActions),
                new Worker(2, secondWorkerActions),
                new Worker(3, thirdWorkerActions)
        };

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(3, 1, workers, verbose, false);
        return wrapper.start();
    }
}
