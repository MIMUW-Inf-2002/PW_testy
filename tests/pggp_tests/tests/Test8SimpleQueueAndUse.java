package cp2022.tests.pggp_tests.tests;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.workshop_actions.Action;

public class Test8SimpleQueueAndUse extends Test {
    // Pracownicy po kolei używają jednego ze stanowisk i wychodzą.
    public Test8SimpleQueueAndUse() {
        timeOfAuthor = 389L;
    }
    @Override
    public boolean run(Boolean verbose) {
        Action[] workerActions = {
                sleep(30),
                enter(0),
                sleep(10),
                use(),
                sleep(10),
                leave()};

        Worker[] workers = {
                new Worker(1, workerActions),
                new Worker(2, workerActions),
                new Worker(3, workerActions),
                new Worker(4, workerActions),
                new Worker(5, workerActions)
        };

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(1, 50, workers, verbose, false);
        return wrapper.start();
    }
}
