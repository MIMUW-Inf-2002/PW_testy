package cp2022.tests.fibonacci;

import cp2022.base.Workplace;
import cp2022.base.WorkplaceId;
import cp2022.base.Workshop;

import java.util.ArrayList;
import java.util.List;

public class Worker implements Runnable {

    protected final int preWorkDelay;

    protected final int afterWorkDelay;

    protected final int afterLeaveDelay;

    protected final List<List<Integer>> actions;

    protected final TestWorkshop workshop;

    protected final int id;

    protected long processIdForDebugging;

    public Worker(int id, TestWorkshop workshop, List<List<Integer>> actions, int preWorkDelay, int afterWorkDelay, int afterLeaveDelay) {
        this.id = id;
        this.workshop = workshop;
        this.actions = new ArrayList<>();
        for (List<Integer> list : actions) {
            this.actions.add(new ArrayList<>(list));
        }
        this.preWorkDelay = preWorkDelay;
        this.afterWorkDelay = afterWorkDelay;
        this.afterLeaveDelay = afterLeaveDelay;
    }

    public Worker(int id, TestWorkshop workshop, List<List<Integer>> actions) {
        this(id, workshop, actions, 0, 0, 0);
    }

    @Override
    public void run() {
        try {
            processIdForDebugging = Thread.currentThread().getId();
            for (List<Integer> list : actions) {
                int n = list.size();
                Workplace wp = workshop.enter(list.get(0), id);
                Thread.sleep(preWorkDelay);
                wp.use();
                for (int i=1; i<n; i++) {
                    Thread.sleep(afterWorkDelay);
                    wp = workshop.switchTo(list.get(i), id);
                    Thread.sleep(preWorkDelay);
                    wp.use();
                }
                Thread.sleep(afterWorkDelay);
                workshop.leave(id);
                Thread.sleep(afterLeaveDelay);
            }
        }
        catch (InterruptedException e) {
            throw new RuntimeException("panic: unexpected thread interruption");
        }
    }
}
