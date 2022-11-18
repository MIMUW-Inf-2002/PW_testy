package cp2022.tests.pggp_tests.utility.workshop_actions;

import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;

public class Leave implements Action{
    @Override
    public void doWork(SimulationWithBugCheck workshop, Worker worker, boolean verbose) {
        if(verbose) System.out.println(
                "Worker " + worker.id.id + " invokes leave().");
        workshop.leave();
        if(verbose) System.out.println(
                "Worker " + worker.id.id + " finished leave().");
    }

}
