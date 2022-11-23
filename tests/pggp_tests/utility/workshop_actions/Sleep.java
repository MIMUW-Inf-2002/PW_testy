package cp2022.tests.pggp_tests.utility.workshop_actions;

import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;

public class Sleep implements Action{
    private final int milliseconds;

    public Sleep(int milliseconds) {
        this.milliseconds = milliseconds;
    }
    @Override
    public void doWork(SimulationWithBugCheck workshop, Worker worker, int verbose) {
        try {
            if(verbose == 1) System.out.println(
                    "Worker " + worker.id.id + " invokes Thread.sleep(" + milliseconds + "ms).");
            Thread.sleep(milliseconds);
            if(verbose == 1) System.out.println(
                    "Worker " + worker.id.id + " finished Thread.sleep(" + milliseconds + "ms).");
        } catch (InterruptedException e) {
            throw new RuntimeException("Test error - worker was sleeping.");
        }
    }
}
