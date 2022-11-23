package cp2022.tests.pggp_tests.tests.simple;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.workshop_actions.Action;

public class TestSimpleTwoQueues extends Test {
    /*
        Test ma bardzo prosto sprawdzić, czy nie zachodzi głodzenie.
        Są dwie kolejki, do dwóch stanowisk.
        W pierwszym przychodzą 2 osoby i na jednej się blokuje.
        Druga kolejka, założona z 4 osób przychodzi później i nie ma blokowania.
        Ostatnia osoba musi zostać zablokowana, aby zapobiec głodzeniu.

     */

    @Override
    public boolean run(int verbose) {
        Action[] worker1Actions = {
                enter(0),
                sleep(1000),
                use(),
                leave()
        };
        Action[] worker2Actions = {
                enter(0),
                use(),
                leave()
        };
        Action[] workersSecondQueueActions = {
                sleep(200),
                enter(1),
                use(),
                leave()
        };

        Worker[] workers = {
                new Worker(1, worker1Actions),
                new Worker(2, worker2Actions),
                new Worker(3, workersSecondQueueActions),
                new Worker(4, workersSecondQueueActions),
                new Worker(5, workersSecondQueueActions),
                new Worker(6, workersSecondQueueActions),
        };

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(
                        2, 30,  workers, verbose, true);
        return wrapper.start();
    }
}
