package cp2022.tests.pggp_tests.tests.efficiency;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.workshop_actions.Action;

public class TestEfficiencyOrderErrorCatch extends Test {

    // Test sprawdza, czy nie zaimplementowano
    // "którzy zaczęli chcieć wejść po tym, gdy on zaczął chcieć, odpowiednio, wejść lub zmienić stanowisko".
    // Najpierw wchodzi osoba A na stanowisko 1 i czeka tam 10 sekundę.
    // Następnie przychodzi osoba B, która chce wejść na stanowisko 1 i poczekać 1 sekundę.
    // Potem 1000 osób ustawia się w kolejce do stanowiska 0.
    // Po sekundzie osoba B powinna od razu wejść na stanowisko A,
    // a nie czekać na kolejkę. Stąd test powinien zająć trochę więcej niż 11 sekund-
    // ustawiono limit 15s.
    public TestEfficiencyOrderErrorCatch() {
        timeLimit = 15000L;
        timeOfAuthor = 10462L;
    }

    public boolean run(int verbose) {
        Action[] queueActions = {
                sleep(100),
                enter(0),
                use(),
                sleep(100),
                leave()
        };

        Worker[] workers = new Worker[102];

        for (int i = 0; i < 100; i++) {
            workers[i] = new Worker(i, queueActions);
        }

        Action[] workerA = {
                enter(1),
                use(),
                sleep(10000),
                leave()
        };
        Action[] workerB = {
                sleep(1000),
                enter(1),
                use(),
                sleep(1000),
                leave()
        };
        workers[100] = new Worker(100, workerA);
        workers[101] = new Worker(101, workerB);

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(3, 10, workers, verbose, false);
        return wrapper.start();
    }
}
