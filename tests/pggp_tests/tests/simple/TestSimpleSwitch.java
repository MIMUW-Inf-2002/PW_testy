package cp2022.tests.pggp_tests.tests.simple;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.workshop_actions.Action;

public class TestSimpleSwitch extends Test {
    //Jeden pracownik wchodzi i zmienia stanowiska za pomocą switchTo().

    public TestSimpleSwitch() {
        timeOfAuthor = 4L;
    }
    public boolean run(int verbose) {
        Action[] firstWorkerActions = {
                enter(0),
                switchTo(1),
                switchTo(2),
                switchTo(0),
                switchTo(1),
                switchTo(2),
                leave()
        };

        Worker[] workers = {new Worker(1, firstWorkerActions)};

        SimulationWithBugCheck wrapper = new SimulationWithBugCheck(
                3, 100,  workers, verbose, false);
        return wrapper.start();
    }
}
