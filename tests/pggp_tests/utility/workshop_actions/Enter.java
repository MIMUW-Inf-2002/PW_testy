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
        worker.setCurrentWorkplace(workshop.enter(workshop.getWorkplaceId(workplaceId)));
    }

}
