package cp2022.tests.pggp_tests.tests.simple;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.workshop_actions.Action;

public class TestSimpleOneStaysOneMoves extends Test {
   // Jeden wchodzi i pracuje bardzo długo na jednym stanowisku. Drugi wchodzi, pracuje na drugim stanowisku i wychodzi, po czym powtarza to kilkukrotnie.
    public TestSimpleOneStaysOneMoves(){
        timeOfAuthor = 352L;
    }

    public boolean run(int verbose) {
        Action[] firstWorkerActions = {
                enter(0),
                use(),
                sleep(100),
                sleep(100),
                leave()
        };
        Action[] secondWorkerActions = {
                enter(1),
                use(),
                leave(),
                enter(1),
                use(),
                leave(),
                enter(1),
                use(),
                leave(),
                enter(1),
                use(),
                leave(),
                enter(1),
                use(),
                leave()
        };

        Worker[] workers = {
                new Worker(1, firstWorkerActions),
                new Worker(2, secondWorkerActions),
        };

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(
                        2, 50, workers, verbose, false);
        return wrapper.start();
    }
}