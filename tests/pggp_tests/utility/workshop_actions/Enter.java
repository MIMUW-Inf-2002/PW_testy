package cp2022.tests.pggp_tests.utility.workshop_actions;

import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;


public class Enter implements Action{
    int workplaceId;
    public Enter(int wid) {
        workplaceId = wid;
    }

    @Override
    public void doWork(SimulationWithBugCheck workshop, Worker worker, boolean verbose) {
        if(verbose) System.out.println(
                "Worker " + worker.id.id + " invokes enter(wokrplace " + workplaceId + ").");
        worker.setCurrentWorkplace(workshop.enter(workshop.getWorkplaceId(workplaceId)));
        if(verbose) System.out.println(
                "Worker " + worker.id.id + " finished enter(workplace " + workplaceId + ")."
        );
    }

}
