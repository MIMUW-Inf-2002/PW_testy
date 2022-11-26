package cp2022.tests.pggp_tests.tests.simple;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.workshop_actions.Action;

public class TestSimpleQueue extends Test {
    // Kolejka 5 pracowników oczekuje na wejście do jednego stanowiska. Celem sprawdzenia żywotności przychodzą po sobie z opóźnieniem.

    public TestSimpleQueue() {
        timeOfAuthor = 1288L;
    }

    @Override
    public boolean run(int verbose) {
        Action[] workerActions = {
                enter(0),
                sleep(100),
                use(),
                leave()};

        Worker[] workers = {
                new Worker(1, workerActions),
                new Worker(2, workerActions),
                new Worker(3, workerActions),
                new Worker(4, workerActions),
                new Worker(5, workerActions)
        };

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(
                        1, 30,  workers, verbose, true);
        return wrapper.start();
    }
}
