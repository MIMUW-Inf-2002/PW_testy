package cp2022.tests.pggp_tests.tests.starvation;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;

public class TestStarvationTricycleAndQueue extends Test {
    // Jeden 3-cykl oraz bardzo długa kolejka do 4 stanowiska. UWAGA! Test zakłada, że jeśli jeden użytkownik wejdzie do warsztatu wielokrotnie to liczy się jako inny.

    public TestStarvationTricycleAndQueue() {
        timeOfAuthor = 34101L;
    }
    public boolean run(int verbose) {
        Worker[] workers = {
                new Worker(1, rotateCycle(0, 0, 2, 10)),
                new Worker(2, rotateCycle(1, 0, 2, 10)),
                new Worker(3, rotateCycle(2, 0, 2, 10)),
                new Worker(4, inUseOut(3, 300))
        };

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(4, 20, workers, verbose, true);
        return wrapper.start();
    }
}
