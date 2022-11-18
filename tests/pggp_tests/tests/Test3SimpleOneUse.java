package cp2022.tests.pggp_tests.tests;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.workshop_actions.Action;

public class Test3SimpleOneUse extends Test {
    // Jeden pracownik wchodzi, używa stanowiska i wychodzi.

    public Test3SimpleOneUse() {
        timeOfAuthor = 103L;
    }
    public boolean run(Boolean verbose) {
        Action[] firstWorkerActions = {enter(0), use(),  leave()};

        Worker[] workers = {new Worker(1, firstWorkerActions)};

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(1, 100, workers, verbose, false);
        return wrapper.start();
    }
}
