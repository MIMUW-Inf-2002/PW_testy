package cp2022.tests.pggp_tests.tests.simple;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.workshop_actions.Action;

public class TestSimpleSwitchAndUse extends Test {
    // Jeden pracownik zmienia miejsca pracy i używa ich.
    public TestSimpleSwitchAndUse() {
        timeOfAuthor = 610L;
    }
    public boolean run(int verbose) {
        Action[] firstWorkerActions = {
                enter(0),
                use(),
                switchTo(1),
                use(),
                switchTo(2),
                use(),
                switchTo(0),
                use(),
                switchTo(0),
                use(),
                switchTo(0),
                use(),
                switchTo(1),
                use(),
                switchTo(2),
                use(),
                leave()
        };

        Worker[] workers = {new Worker(1, firstWorkerActions)};

        SimulationWithBugCheck wrapper = new SimulationWithBugCheck(
                3, 100,  workers, verbose, false);
        return wrapper.start();
    }
}