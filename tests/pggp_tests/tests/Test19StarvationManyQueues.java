package cp2022.tests.pggp_tests.tests;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.workshop_actions.Action;

public class Test19StarvationManyQueues extends Test {
    // Jest kilka długich kolejek.
    public Test19StarvationManyQueues() {
        timeOfAuthor = 9991L;
    }


    private Worker worker(int nr, int workplace) {
        Action[] workerActions = {
                enter(workplace),
                sleep(15),
                leave()
        };
        return new Worker(nr, workerActions);
    }

    public boolean run(Boolean verbose) {
        Worker[] workers = new Worker[99];

        for (int i = 0; i < 33; i++) {
            workers[i] = worker(i, 0);
            workers[i + 33] = worker(i + 33, 1);
            workers[i + 66] = worker(i + 66, 2);
        }


        SimulationWithBugCheck wrapper = new SimulationWithBugCheck(5, 1, workers, verbose, true);
        return wrapper.start(); // Maybe increase it?
    }
}
