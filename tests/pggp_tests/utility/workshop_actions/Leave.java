package cp2022.tests.pggp_tests.utility.workshop_actions;

import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;

public class Leave implements Action{
    @Override
    public void doWork(SimulationWithBugCheck workshop, Worker worker, int verbose) {
        if(verbose == 1) System.out.println(
                "Worker " + worker.id.id + " invokes leave().");
        if(verbose == 2) System.out.println(
                "Worker " + worker.id.id + " leaves the workshop");
        workshop.leave();
        if(verbose == 1) System.out.println(
                "Worker " + worker.id.id + " finished leave().");
    }

}
