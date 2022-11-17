package cp2022.tests.pggp_tests.tests;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.workshop_actions.Action;

public class Test4SimpleOneUseRepeated extends Test {
    // Jak powyżej, tylko że pracownik wchodzi i wychodzi wielokrotnie.

    public Test4SimpleOneUseRepeated() {
        timeOfAuthor = 505L;
    }


    public boolean run(Boolean verbose) {
        Action[] firstWorkerActions = {enter(0), use(),  leave()};
        firstWorkerActions = repeat(firstWorkerActions, 5);

        Worker[] workers = {new Worker(1, firstWorkerActions)};

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(1, 100, workers, verbose, false);
        return wrapper.start();
    }
}
