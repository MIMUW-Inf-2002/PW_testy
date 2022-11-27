package cp2022.tests.pggp_tests.tests.simple;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.workshop_actions.Action;

public class TestSimpleOneWorkplaceManyTimes extends Test {
    public boolean run(int verbose) {
        Action[] firstWorkerActions = concat(
                enter(0),
                repeat(new Action[]{use(), switchTo(0)}, 100));
        firstWorkerActions = concat(firstWorkerActions, use());
        firstWorkerActions = concat(firstWorkerActions, leave());

        Worker[] workers = {new Worker(1, firstWorkerActions)};

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(
                        1, 10,  workers, verbose, false);
        return wrapper.start();
    }
}
