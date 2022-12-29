package cp2022.tests.pggp_tests.tests.deadlock;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.workshop_actions.Action;

public class TestDeadlockPairManyTimes extends Test {
    // Tak jak poprzedni test, przy czym pracownicy zamieniają się wielokrotnie.
    public TestDeadlockPairManyTimes() {
        timeOfAuthor = 8992L;
    }
    public boolean run(int verbose) {
        Action[] firstWorkerActions = new Action[400003];
        Action[] secondWorkerActions = new Action[400003];

        firstWorkerActions[0] = enter(0);
        secondWorkerActions[0] = enter(1);
        for(int i = 0; i < 100000; i++) {
            firstWorkerActions[4 * i + 1] = use();
            firstWorkerActions[4 * i + 2] = switchTo(1);
            firstWorkerActions[4 * i + 3] = use();
            firstWorkerActions[4 * i + 4] = switchTo(0);

            secondWorkerActions[4 * i + 1] = use();
            secondWorkerActions[4 * i + 2] = switchTo(0);
            secondWorkerActions[4 * i + 3] = use();
            secondWorkerActions[4 * i + 4] = switchTo(1);
        }
        firstWorkerActions[400001] = use();
        secondWorkerActions[400001] = use();
        firstWorkerActions[400002] = leave();
        secondWorkerActions[400002] = leave();

        Worker[] workers = {
                new Worker(1, firstWorkerActions),
                new Worker(2, secondWorkerActions),
        };

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(2, 0, workers, verbose, false);
        // It might be too low - try to increase.
        return wrapper.start();
    }
}
