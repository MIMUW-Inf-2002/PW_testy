package cp2022.tests.pggp_tests.tests;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.workshop_actions.Action;

public class Test18StarvationStar extends Test {
    // 3 wierzchołki skaczą pomiędzy stanowiskiem 0 i jednym ze stanowisk spośród 1, 2, 3, innym dla każdego z nich. Poza tym do stanowiska 4 jest długa kolejka.

    public Test18StarvationStar() {
        timeOfAuthor = 11236L;
    }
    public boolean run(Boolean verbose) {
        Worker[] workers = new Worker[103];
        Action[] workerActions = {
                enter(0),
                sleep(10),
                leave()
        };

        for (int i = 0; i < 100; i++) {
            workers[i] = new Worker(i, workerActions);
        }


        workers[100] = new Worker(100, concat(sleep(550), jumpBetween(1, 2, 10)));
        workers[101] = new Worker(101, concat(sleep(550), jumpBetween(1, 3, 10)));
        workers[102] = new Worker(102, concat(sleep(550), jumpBetween(1, 4, 10)));

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(5, 50, workers, verbose, true);
        return wrapper.start();
    }
}
