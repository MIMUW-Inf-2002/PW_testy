package cp2022.tests.pggp_tests.utility.workshop_actions;

import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;


public class Enter implements Action{
    int workplaceId;
    public Enter(int wid) {
        workplaceId = wid;
    }

    @Override
    public void doWork(SimulationWithBugCheck workshop, Worker worker, int verbose) {
        if(verbose == 1) System.out.println(
                "Worker " + worker.id.id + " invokes enter(wokrplace " + workplaceId + ").");
        if(verbose == 2) System.out.println(
                "Worker " + worker.id.id + " tries to enter the workshop and occupy workplace " + workplaceId);
        worker.setCurrentWorkplace(workshop.enter(workshop.getWorkplaceId(workplaceId)));
        if(verbose == 1) System.out.println(
                "Worker " + worker.id.id + " finished enter(workplace " + workplaceId + ").");
        if(verbose == 2) System.out.println(
                "Worker " + worker.id.id + " now occupies workplace " + workplaceId);
    }

}
