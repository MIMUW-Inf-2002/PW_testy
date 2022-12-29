package cp2022.tests.fibonacci;

import cp2022.base.Workplace;
import cp2022.base.WorkplaceId;
import cp2022.base.Workshop;
import cp2022.solution.WorkshopFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class TestWorkshop {

    private final Workshop internal;

    int verbose;

    private final ConcurrentHashMap<Integer, Integer> position;

    private final ConcurrentHashMap<Integer, Integer> usage;

    private List<Worker> workers;

    private final Map<Integer, Map<Long, Integer>> occupation;

    private final Map<Integer, WorkplaceId> workplaceIds;

    public TestWorkshop() {
        position = new ConcurrentHashMap<>();
        usage = new ConcurrentHashMap<>();
        occupation = new HashMap<>();
        workplaceIds = new HashMap<>();
        List<TestWorkplace> wp = workplaces();
        wp.forEach((w) ->
        {
            workplaceIds.put(w.v, w.getId());
        });
        internal = WorkshopFactory.newWorkshop(new ArrayList<>(wp));
    }

    protected abstract List<TestWorkplace> workplaces();

    protected abstract List<Worker> workers();

    public abstract long time();

    public void init() {
        workers = new ArrayList<>(workers());
        Collections.shuffle(workers, new Random(2137));
    }

    public void run(int verbose) {
        this.verbose = verbose;
        List<Thread> threads = new ArrayList<>();
        for (Worker worker : workers) {
            threads.add(new Thread(worker));
        }
        for (Thread th : threads) {
            th.start();
            Thread.yield();
        }
        try {
            for (Thread th : threads) {
                th.join();
            }
        }
        catch (InterruptedException e) {
            throw new RuntimeException("panic: unexpected thread interruption");
        }
    }


    public Workplace enter(int placeId, int workerId) {
        if (verbose > 0) {
            System.out.println("Worker " + workerId + " tries to enter the workshop and occupy workplace " + placeId);
        }
        Workplace res = internal.enter(getWid(placeId));

        if (new WorkplaceIdInt(placeId).compareTo(res.getId()) != 0) {
            throw new RuntimeException("Test failed: worker " + workerId + " received workplace with id " + res.getId() + " but expected workplace with id " + placeId);
        }

        position.put(workerId, placeId);

        synchronized (occupation) {
            if (usage.containsKey(placeId)) {
                throw new RuntimeException("Test failed: worker " + workerId + " has entered the workshop at workplace " + placeId + " when it was being used by worker " + usage.get(placeId));
            }
            if (!occupation.containsKey(placeId)) {
                occupation.put(placeId, new HashMap<>());
            }
            occupation.get(placeId).put(Thread.currentThread().getId(), workerId);
        }

        if (verbose > 0) {
            System.out.println("Worker " + workerId + " now occupies workplace " + placeId);
        }
        return res;
    }

    public Workplace switchTo(int placeId, int workerId) {
        if (verbose > 0) {
            System.out.println("Worker " + workerId + " tries to switch its workplace to workplace " + placeId);
        }
        Workplace res = internal.switchTo(getWid(placeId));

        if (new WorkplaceIdInt(placeId).compareTo(res.getId()) != 0) {
            throw new RuntimeException("Test failed: worker " + workerId + " received workplace with id " + res.getId() + " but expected workplace with id " + placeId);
        }

        int oldPlaceId = position.get(workerId);

        synchronized (occupation) {
            if (usage.containsKey(placeId)) {
                throw new RuntimeException("Test failed: worker " + workerId + " has switched to workplace " + placeId + " when it was being used by worker " + usage.get(placeId));
            }
            occupation.get(oldPlaceId).remove(Thread.currentThread().getId());
            if (!occupation.containsKey(placeId)) {
                occupation.put(placeId, new HashMap<>());
            }
            occupation.get(placeId).put(Thread.currentThread().getId(), workerId);
        }

        position.put(workerId, placeId);

        if (verbose > 0) {
            System.out.println("Worker " + workerId + " now occupies workplace " + placeId);
        }
        return res;
    }

    public void leave(int workerId) {
        int oldPlaceId = position.remove(workerId);
        synchronized (occupation) {
            occupation.get(oldPlaceId).remove(Thread.currentThread().getId());
        }
        if (verbose > 0) {
            System.out.println("Worker " + workerId + " leaves the workshop");
        }
        internal.leave();
    }

    public void use(int placeId) {
        int workerId;
        synchronized (occupation) {
            Map<Long, Integer> map = occupation.get(placeId);
            long threadId = Thread.currentThread().getId();
            workerId = map.get(threadId);
            if (map.size() > 1) {
                throw new RuntimeException("Worker " + workerId + " is using the workplace " + placeId + " while someone else is occupying it");
            }
            usage.put(placeId, workerId);
        }
        if (verbose > 0) {
            System.out.println("Worker " + workerId + " starts using workplace " + placeId);
        }
    }

    public void endUse(int placeId) {
        int workerId;
        synchronized (occupation) {
            Map<Long, Integer> map = occupation.get(placeId);
            long threadId = Thread.currentThread().getId();
            workerId = map.get(threadId);
            usage.remove(placeId);
        }
        if (verbose > 0) {
            System.out.println("Worker " + workerId + " stops using workplace " + placeId);
        }
    }

    WorkplaceId getWid(int id) {
        return workplaceIds.get(id);
    }
}
