package cp2022.tests.pggp_tests.tests.efficiency;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.workshop_actions.Action;

public class TestEfficiencyParallel extends Test {
    // Jest 5 stanowisk i po 1 osobie, która chce wejść na każde z nich. Praca trwa 500ms. Limit czasu: 1 sekunda.
    public TestEfficiencyParallel(){
        timeLimit = 1000L;
        timeOfAuthor = 501L;
    }
    private Worker worker(int i) {
        return new Worker(
                i,
                new Action[]{
                     enter(i - 1),
                     use(),
                     leave()
                }
        );
    }

    public boolean run(int verbose) {

        Worker[] workers = {
                worker(1),
                worker(2),
                worker(3),
                worker(4),
                worker(5)
        };

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(5, 500, workers, verbose, false);
        return wrapper.start();
    }
}
