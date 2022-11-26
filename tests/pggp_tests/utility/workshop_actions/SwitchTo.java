package cp2022.tests.pggp_tests.utility.workshop_actions;

import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;

public class SwitchTo implements Action{
    private final int wid;

    public SwitchTo(int wid) {
        this.wid = wid;
    }

    @Override
    public void doWork(SimulationWithBugCheck workshop, Worker worker, int verbose) {
        if(verbose == 1) System.out.println(
                "Worker " + worker.id.id + " invokes switchTo(" + wid + ").");
        if(verbose == 2) System.out.println(
                "Worker " + worker.id.id + " tries to switch its workplace to workplace " + wid);
        worker.setCurrentWorkplace(workshop.switchTo(workshop.getWorkplaceId(wid)));
        if(verbose == 1) System.out.println(
                "Worker " + worker.id.id + " finished switchTo(" + wid + ").");
        if(verbose == 2) System.out.println(
                "Worker " + worker.id.id + " now occupies workplace " + wid);
    }
}
