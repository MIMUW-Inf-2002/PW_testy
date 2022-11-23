package cp2022.tests.pggp_tests.tests.simple;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.workshop_actions.Action;

public class TestSimpleOneUse extends Test {
    // Jeden pracownik wchodzi, używa stanowiska i wychodzi.

    public TestSimpleOneUse() {
        timeOfAuthor = 102L;
    }
    public boolean run(int verbose) {
        Action[] firstWorkerActions = {enter(0), use(),  leave()};

        Worker[] workers = {new Worker(1, firstWorkerActions)};

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(
                        1, 100,  workers, verbose, false);
        return wrapper.start();
    }
}
