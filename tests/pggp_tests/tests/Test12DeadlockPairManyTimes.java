package cp2022.tests.pggp_tests.tests;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.workshop_actions.Action;

public class Test12DeadlockPairManyTimes extends Test {
    // Tak jak poprzedni test, przy czym pracownicy zamieniają się wielokrotnie.
    public Test12DeadlockPairManyTimes() {
        timeOfAuthor = 2545L;
    }
    public boolean run(Boolean verbose) {
        Action[] firstWorkerActions = new Action[4002];
        Action[] secondWorkerActions = new Action[4002];

        firstWorkerActions[0] = enter(0);
        secondWorkerActions[0] = enter(1);
        for(int i = 0; i < 1000; i++) {
            firstWorkerActions[4 * i + 1] = switchTo(1);
            firstWorkerActions[4 * i + 2] = use();
            firstWorkerActions[4 * i + 3] = switchTo(0);
            firstWorkerActions[4 * i + 4] = use();

            secondWorkerActions[4 * i + 1] = switchTo(0);
            secondWorkerActions[4 * i + 2] = use();
            secondWorkerActions[4 * i + 3] = switchTo(1);
            secondWorkerActions[4 * i + 4] = use();
        }
        firstWorkerActions[4001] = leave();
        secondWorkerActions[4001] = leave();

        Worker[] workers = {
                new Worker(1, firstWorkerActions),
                new Worker(2, secondWorkerActions),
        };

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(2, 1, workers, verbose, false);
        // It might be too low - try to increase.
        return wrapper.start();
    }
}
