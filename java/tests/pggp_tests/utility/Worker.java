package cp2022.tests.pggp_tests.utility;

import cp2022.base.Workplace;
import cp2022.tests.pggp_tests.utility.workshop_actions.*;

public class Worker {
    public final WorkerId id;
    private Workplace currentWorkplace;
    private final Action[] work;

    public Worker(int id, Action[] work) {
        this.work = work;
        this.id = new WorkerId(id);
    }

    public WorkerId getId() {
        return id;
    }

    class DoWork implements Runnable {
        private final SimulationWithBugCheck workshop;
        private final Worker worker;

        public DoWork(SimulationWithBugCheck workshop, Worker worker) {
            this.workshop = workshop;
            this.worker = worker;
        }

        @Override
        public void run() {

            int number_of_work = 0;
            int needToUse = 0;
            int prev = 0;
            // Doing all actions in the loop.
            for(Action action : this.worker.work) {
                // We wait some time in the critical section to ensure proper order of the workers.
                // This function is invoked only if order is checked.

                ensureOrderWait(workshop);
                action.doWork(workshop, this.worker, workshop.verbose);
                number_of_work += 1;
            }
        }
    }


    public Thread createThreadWithDoingWork(SimulationWithBugCheck workshop) {
        DoWork workRunnable = new DoWork(workshop, this);
        return new Thread(workRunnable);
    }

    public void setCurrentWorkplace(Workplace currentWorkplace) {
        this.currentWorkplace = currentWorkplace;
    }

    public Workplace getCurrentWorkplace() {
        return this.currentWorkplace;
    }

    private void ensureOrderWait(SimulationWithBugCheck workshop) {
        if(workshop.getDoCheckLiveliness()) {
            try {
                workshop.getEnsureOrderSemaphore().acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            try {
                Thread.sleep(SimulationWithBugCheck.timeOfWaitBetweenActionsWhenOrderMatters);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            workshop.getEnsureOrderSemaphore().release();
        }
    }
}
