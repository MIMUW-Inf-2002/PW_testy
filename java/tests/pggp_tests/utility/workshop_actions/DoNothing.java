package cp2022.tests.pggp_tests.utility.workshop_actions;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Worker;

public class DoNothing implements Action {
    @Override
    public void doWork(SimulationWithBugCheck workshop, Worker worker, int verbose) {
        // does nothing
    }
}
