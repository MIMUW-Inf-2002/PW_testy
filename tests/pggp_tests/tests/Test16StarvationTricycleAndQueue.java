package cp2022.tests.pggp_tests.tests;

import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;
import cp2022.tests.pggp_tests.utility.Worker;

public class Test16StarvationTricycleAndQueue extends Test {
    // Jeden 3-cykl oraz bardzo długa kolejka do 4 stanowiska. UWAGA! Test zakłada, że jeśli jeden użytkownik wejdzie do warsztatu wielokrotnie to liczy się jako inny.

    public Test16StarvationTricycleAndQueue() {
        timeOfAuthor = 19101L;
    }
    public boolean run(Boolean verbose) {
        Worker[] workers = {
                new Worker(1, rotateCycle(0, 0, 2, 10)),
                new Worker(2, rotateCycle(1, 0, 2, 10)),
                new Worker(3, rotateCycle(2, 0, 2, 10)),
                new Worker(4, inOut(3, 200))
        };

        SimulationWithBugCheck wrapper =
                new SimulationWithBugCheck(4, 800, workers, verbose, true);
        return wrapper.start();
    }
}
