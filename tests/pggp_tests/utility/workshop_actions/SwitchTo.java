package cp2022.tests.pggp_tests.utility.workshop_actions;

import cp2022.tests.pggp_tests.utility.Worker;
import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;

public class SwitchTo implements Action{
    private final int wid;

    public SwitchTo(int wid) {
        this.wid = wid;
    }

    @Override
    public void doWork(SimulationWithBugCheck workshop, Worker worker, boolean verbose) {
        if(verbose) System.out.println(
                "Worker " + worker.id.id + " invokes switchTo(" + wid + ").");
        worker.setCurrentWorkplace(workshop.switchTo(workshop.getWorkplaceId(wid)));
        if(verbose) System.out.println(
                "Worker " + worker.id.id + " finished switchTo(" + wid + ").");
    }
}
