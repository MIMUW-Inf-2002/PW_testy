package cp2022.tests.pggp_tests.tests.starvation;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.workshop_actions.Action;

public class TestStarvationOneLongQueue extends Test {
    // Jedna duża kolejka przed wejściem do jednego stanowiska.

    public TestStarvationOneLongQueue() {
        timeOfAuthor = 14129L;
    }
    public boolean run(int verbose) {
        Action[] workerActions = {
                enter(0),
                sleep(20),
                use(),
                leave()};

        Worker[] workers = new Worker[100];

        for (int i = 0; i < 100; i++) {
            workers[i] = new Worker(i, workerActions);
        }

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(1, 1, workers, verbose, true);
        return wrapper.start();
    }
}

