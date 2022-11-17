package cp2022.tests.pggp_tests.tests;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.workshop_actions.Action;

public class Test6SimpleSwitchRepeated extends Test {
    // Tak jak powyżej, tylko pracownik wchodzi i wychodzi z warsztatu kilkukrotnie.

    public Test6SimpleSwitchRepeated() {
        timeOfAuthor = 3L;
    }
    public boolean run(Boolean verbose) {
        Action[] firstWorkerActions = {
                enter(0),
                switchTo(1),
                switchTo(2),
                switchTo(0),
                switchTo(1),
                switchTo(2),
                leave()
        };
        firstWorkerActions = repeat(firstWorkerActions, 5);

        Worker[] workers = {new Worker(1, firstWorkerActions)};

        SimulationWithBugCheck wrapper = new SimulationWithBugCheck(3, 100, workers, verbose, false);
        return wrapper.start();
    }
}
