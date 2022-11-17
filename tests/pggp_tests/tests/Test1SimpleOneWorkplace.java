package cp2022.tests.pggp_tests.tests;

import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.workshop_actions.Action;


public class Test1SimpleOneWorkplace extends Test {
    // Prosty test z jednym stanowiskiem i pracownikiem, który wchodzi i wychodzi z niego kilkukrotnie.
    public Test1SimpleOneWorkplace() {
        timeOfAuthor = 19L;
    }
    public boolean run(Boolean verbose) {

        Action[] firstWorkerActions = {enter(0),  leave()};
        firstWorkerActions = repeat(firstWorkerActions, 4);

        Worker[] workers = {new Worker(1, firstWorkerActions)};

        SimulationWithBugCheck wrapper = new SimulationWithBugCheck(1, 100, workers, verbose, false);
        return wrapper.start();
    }
}
