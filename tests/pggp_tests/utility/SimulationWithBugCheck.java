package cp2022.tests.pggp_tests.utility;

import cp2022.base.Workplace;
import cp2022.base.WorkplaceId;
import cp2022.base.Workshop;
import cp2022.solution.WorkshopFactory;

import java.util.*;
import java.util.concurrent.*;

/*
    SimulationWithBugCheck:
        - creates threads for workers,
        - wraps Workshop object, such that before and after enter, switch and leave,
          security and liveliness conditions are checked,
        - checks whether every worker invoked use() method of workplace if she wanted to do so.
 */
public class SimulationWithBugCheck implements Workshop {
    private final Semaphore mutex; // Mutex for internal use of SimulationWithBugCheck class.
    private final Workshop wrappedWorkshop;
    public final int verbose;

    public volatile boolean errorBoolean = false;

    // Workplace info.
    private final int numberOfWorkplaces;
    private final  ArrayList<WorkplaceId> workplaceIds;


    // Properties of the workplace.
    public final int timeOfOneWork;

    // Information about the workers in the workshop. Used to check the safety property of the workshop implementation.

    private final Map <Thread, WorkerId> threadToWorkerId;
    private final Map <WorkerId, Thread> workerIdToThread;

    private final Map<WorkerId, WorkplaceId> workerToCurrentWorkplace;
    // If
    private final Map<WorkerId, WorkplaceId> workerToWorkplaceInReleaseDuration; // workplace

    private final Map<WorkplaceId, WorkerId> someoneUsesWorkplace;

    // Used to check liveliness property of the workshop implementation.
    private final Map<WorkerId, Integer> enteredAfterWorkerRequest;
    private final Map<WorkerId, Collection<WorkerId>> enteredAfterWorkerRequestList;
    private final Map<WorkerId, Integer> requestAge;
    private volatile int globalAge = 0;
    private final boolean doCheckLiveliness;
    private final Semaphore orderWait;

    public static int timeOfWaitBetweenActionsWhenOrderMatters = 10;

    // Used to check whether workplace.use() is fact invoke original workplace.use().
    private final Map<WorkplaceId, Integer> usagesOfWorkplace;

    public int getUsages(WorkplaceId id) {
        return this.usagesOfWorkplace.get(id);
    }

    public Integer getWorkplaceIntId(Workplace currentWorkplace) {
        for (int i = 0; i < this.numberOfWorkplaces; i++) {
            if(workplaceIds.get(i).compareTo(currentWorkplace.getId()) == 0) {
                return i;
            }
        }

        throw new RuntimeException("Test error - wrong workplace id.");
    }

    public Semaphore getEnsureOrderSemaphore() {
        return orderWait;
    }


    // Implementation of the workplace, which collaborated with SimulationWithBugCheck to check bugs.
    private class WorkplaceImplementation extends Workplace {
        public final int timeOfWork;
        private final SimulationWithBugCheck workshop;
        private final WorkplaceIdImplementation id;

        protected WorkplaceImplementation(int id, int timeInMilliseconds, SimulationWithBugCheck workshop) {
            super(new WorkplaceIdImplementation(id));
            this.id = (WorkplaceIdImplementation) super.getId();
            this.timeOfWork = timeInMilliseconds;
            this.workshop = workshop;
        }

        /*
            Method does two things:
                - checks whether process trying to work in this workplace, possesses this workplace,
                - writes in the history that this workplace was used.
         */
        @Override
        public void use() {
            try {
                mutex.acquire();
                WorkerId currentWorkerId = workshop.getWorkerIdOfCurrentThread();

                WorkplaceIdImplementation printableWid = id;

                if(verbose == 1) System.out.println(
                        "Worker " + currentWorkerId.id + " invokes use() on workplace " +
                                printableWid.id.intValue() + ".");
                if (verbose == 2) {
                    System.out.println("Worker " + currentWorkerId.id + " starts using workplace " + id);
                }

                // We check whether our worker posses workplace we want to work on exclusively.

                for(WorkerId workerId : workerToWorkplaceInReleaseDuration.keySet()){
                    if(workerId != currentWorkerId && workerToWorkplaceInReleaseDuration.get(workerId).compareTo(id) == 0) {
                        // If not, we throw an exception.
                        errorBoolean = true;
                        throw new RuntimeException("[Exception] Worker " + currentWorkerId.id + " tried to work on the workplace "
                                + id.id + ", " + "which is occupied by the worker " + workerId.id);
                    }
                }

                // We increase stored number of usages.
                usagesOfWorkplace.put(this.id, usagesOfWorkplace.get(this.id) + 1);
                someoneUsesWorkplace.put(this.id, currentWorkerId);

                mutex.release();
                if(timeOfWork > 0) Thread.sleep(timeOfWork);

                acquireMutexOrPanic();
                if(verbose == 1) System.out.println(
                        "Worker " + currentWorkerId.id + " finished use() on workplace " +
                                id.id.intValue() + ".");
                if (verbose == 2) {
                    System.out.println("Worker " + currentWorkerId.id + " stops using workplace " + id );
                }
                someoneUsesWorkplace.remove(this.id);
                mutex.release();

            } catch (InterruptedException e) {
                throw new RuntimeException("Test panic - error in the tests. There should not be any interruption.");
            }
        }

        @Override
        public String toString() {
            return "workplace "+ id.id;
        }
    }

    public SimulationWithBugCheck(int numberOfWorkplaces,
                                  int timeOfOneWork,
                                  Worker[] workers,
                                  int verbose,
                                  boolean doCheckLiveliness) {

        this.mutex = new Semaphore(1, true); // Mutex used by the simulation.

        // If verbose is true, simulation will print logs into console.
        this.verbose = verbose;

        // Lot of initialization...

        this.timeOfOneWork = timeOfOneWork;
        this.numberOfWorkplaces = numberOfWorkplaces;

        this.threadToWorkerId = new ConcurrentHashMap<>();
        this.workerIdToThread = new ConcurrentHashMap<>();

        this.workerToCurrentWorkplace = new ConcurrentHashMap<>();
        this.workerToWorkplaceInReleaseDuration = new ConcurrentHashMap<>();
        this.someoneUsesWorkplace = new ConcurrentHashMap<>();

        this.enteredAfterWorkerRequest = new ConcurrentHashMap<>();
        this.enteredAfterWorkerRequestList = new ConcurrentHashMap<>();
        this.requestAge = new ConcurrentHashMap<>();
        this.doCheckLiveliness = doCheckLiveliness;


        ArrayList<Workplace> workplaces = new ArrayList<>();
        this.workplaceIds = new ArrayList<>();
        this.usagesOfWorkplace = new ConcurrentHashMap<>();
        for (int i = 0; i < numberOfWorkplaces; i++) {
            WorkplaceImplementation workplace = new WorkplaceImplementation(i, timeOfOneWork, this);
            workplaces.add(workplace);
            workplaceIds.add(workplace.id);
            usagesOfWorkplace.put(workplace.id, 0);
        }


        this.orderWait = new Semaphore(1, true);

        // Initialization of workers.

        for (Worker worker : workers) {
            Thread worker_thread = worker.createThreadWithDoingWork(this);
            this.threadToWorkerId.put(worker_thread, worker.getId());
            if(workerIdToThread.get(worker.getId()) != null) {
                throw new RuntimeException("Test error - two workers have the same id.");
            }

            this.workerIdToThread.put(worker.getId(), worker_thread);
        }

        wrappedWorkshop = WorkshopFactory.newWorkshop(workplaces);
    }

    public boolean start() {

        // Start all workers.
        for(Map.Entry<WorkerId, Thread> p : workerIdToThread.entrySet()) {
            p.getValue().setName("Worker " + p.getKey().id + " thread.");
            p.getValue().start();
        }

        for (Thread thread : workerIdToThread.values()) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException("Test error. Should not happen.");
            }
        }

        return !this.errorBoolean;
    }

    public WorkerId getWorkerIdOfCurrentThread() {
        return threadToWorkerId.get(Thread.currentThread());
    }
    @Override
    public Workplace enter(WorkplaceId wid) {
        acquireMutexOrPanic();


        WorkplaceIdImplementation widToPrint = (WorkplaceIdImplementation) wid;
        if(verbose == 1) System.out.println(
                "Worker " + getWorkerIdOfCurrentThread().id + " invokes enter(wokrplace " + widToPrint.id.intValue() + ").");
        if(verbose == 2) System.out.println(
                "Worker " + getWorkerIdOfCurrentThread().id + " tries to enter the workshop and occupy workplace " + widToPrint);


        // We put the request to enter into proper Map.
        putRequest(getWorkerIdOfCurrentThread());
        mutex.release();

        Workplace workplace = wrappedWorkshop.enter(wid);

        acquireMutexOrPanic();

        // Since one worker entered the workshop,
        // every value of the enteredAfterWorkerRequest must be increased by 1.
        int finished_request_time = requestAge.get(getWorkerIdOfCurrentThread());
        removeRequest(getWorkerIdOfCurrentThread());
        increaseAgeByOneForYoungerThan(finished_request_time);

        checkWhetherLivelinessIsAbused();

        // Update maps.
        workerToCurrentWorkplace.put(getWorkerIdOfCurrentThread(), wid);


        if(someoneUsesWorkplace.containsKey(wid)) {
            throw new RuntimeException("Worker " + getWorkerIdOfCurrentThread().id + " tried to enter the workplace "
                    + getWorkplaceIntId(workplace) + " which is used by the Worker" + someoneUsesWorkplace.get(wid).id);
        }


        if(verbose == 1) System.out.println(
                "Worker " + getWorkerIdOfCurrentThread().id + " finished enter(workplace " + widToPrint.id.intValue() + ").");
        if(verbose == 2) System.out.println(
                "Worker " + getWorkerIdOfCurrentThread().id + " now occupies workplace " + widToPrint);

        mutex.release();

        return workplace;
    }

    private void increaseAgeByOneForYoungerThan(int time) {
        for(Map.Entry<WorkerId, Integer> pair : this.requestAge.entrySet()) {
            if(pair.getValue() < time) {
                enteredAfterWorkerRequest.put(pair.getKey(), enteredAfterWorkerRequest.get(pair.getKey()) + 1);
                if(enteredAfterWorkerRequestList.get(pair.getKey()) == null) {
                    enteredAfterWorkerRequestList.put(pair.getKey(), new ConcurrentLinkedQueue<>());
                }
                enteredAfterWorkerRequestList.get(pair.getKey()).add(getWorkerIdOfCurrentThread());
            }
        }
    }

    @Override
    public Workplace switchTo(WorkplaceId wid) {
        // Insert request in the map.
        acquireMutexOrPanic();

        WorkplaceIdImplementation widToPrint = (WorkplaceIdImplementation) wid;


        if(verbose == 1) System.out.println(
                "Worker " + getWorkerIdOfCurrentThread().id + " invokes switchTo(" + widToPrint.id + ").");
        if(verbose == 2) System.out.println(
                "Worker " +  getWorkerIdOfCurrentThread().id + " tries to switch its workplace to workplace " + widToPrint);
        putRequest(getWorkerIdOfCurrentThread());
        WorkplaceId oldWorkplace = workerToCurrentWorkplace.get(getWorkerIdOfCurrentThread());

        workerToWorkplaceInReleaseDuration.put(getWorkerIdOfCurrentThread(), oldWorkplace);
        mutex.release();
        /*
            Kilka słów wyjaśnienia, co tu się dzieje.
            Zasadniczo nie wiadomo kiedy nastąpi koniec funkcji switchTo() – bo pomiędzy ostatnim
            zwolnieniem elementu synchronizacji a zwróceniem argumentu można zostać odsuniętym od procesora.

            Stąd ,,doklejam'' do switchTo() kilka innych wywołań. Nie jest to w 100% zgodne z opisem zadania,
            ale jest mu równoważne. To znaczy, jeśli w programie testującym może zajść błąd, to program nie jest poprawny.
            Chyba że ktoś używa jakichś monitorów i uważa, że lock na obiekcie zwalnia się po wywołaniu metody.
            No to chyba nie jest prawda, ale jak jest to sorry, wówczas testy mogą nie być odpowiednie. Ale chyba jest ok.
         */

        Workplace workplace = wrappedWorkshop.switchTo(wid);

        // Remove request in the map.
        acquireMutexOrPanic();

        if(verbose == 1) System.out.println(
                "Worker " + getWorkerIdOfCurrentThread().id + " finished switchTo(" + widToPrint.id + ").");
        if(verbose == 2) System.out.println(
                "Worker " + getWorkerIdOfCurrentThread().id + " now occupies workplace " + widToPrint);

        removeRequest(getWorkerIdOfCurrentThread());

        if(someoneUsesWorkplace.containsKey(wid)) {
            throw new RuntimeException("Worker " + getWorkerIdOfCurrentThread().id + " tried to enter the workplace "
                    + getWorkplaceIntId(workplace) + " which is used by the Worker " + someoneUsesWorkplace.get(wid).id);
        }

        // Update info about workshop ownership.
        workerToCurrentWorkplace.put(getWorkerIdOfCurrentThread(), wid);
        workerToWorkplaceInReleaseDuration.remove(getWorkerIdOfCurrentThread());

        mutex.release();

        return workplace;
    }

    private void putRequest(WorkerId workerId) {
        enteredAfterWorkerRequest.put(workerId, 0);
        requestAge.put(workerId, this.globalAge);
        globalAge++;
    }

    private void removeRequest(WorkerId workerId) {
        enteredAfterWorkerRequest.remove(workerId);
        enteredAfterWorkerRequestList.remove(workerId);
        requestAge.remove(workerId);
    }

    @Override
    public void leave() {
        // Remove request in the map.
        acquireMutexOrPanic();

        if(verbose == 1) System.out.println(
                "Worker " + getWorkerIdOfCurrentThread().id + " invokes leave().");
        if(verbose == 2) System.out.println(
                "Worker " + getWorkerIdOfCurrentThread().id + " leaves the workshop");
        removeRequest(getWorkerIdOfCurrentThread());

        // Remove info about workshop ownership.
        WorkplaceId workplace = workerToCurrentWorkplace.get(getWorkerIdOfCurrentThread());
        workerToCurrentWorkplace.remove(getWorkerIdOfCurrentThread());
        workerToWorkplaceInReleaseDuration.remove(getWorkerIdOfCurrentThread());

        if(verbose == 1) System.out.println(
                "Worker " + getWorkerIdOfCurrentThread().id + " finished leave().");

        mutex.release();
        wrappedWorkshop.leave();
    }

    public WorkplaceId getWorkplaceId(int id) {
        if(id < 0 && id > numberOfWorkplaces) {
            throw new RuntimeException("Test error - wrong workplace id.");
        }
        return this.workplaceIds.get(id);
    }

    private void acquireMutexOrPanic() {
        try {
            this.mutex.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException("Panic: test was interrupted.");
        }
    }

    private void checkWhetherLivelinessIsAbused() {
        if(!doCheckLiveliness) {
            return;
        }
        for(WorkerId id : enteredAfterWorkerRequest.keySet()) {
            if(enteredAfterWorkerRequest.get(id) >= 2 * numberOfWorkplaces) {
                errorBoolean = true;
                System.out.println("Worker " + getWorkerIdOfCurrentThread().id + " unsuccessfully tried finish enter().");
                System.out.println("------------- STARVATION_ERROR --------------");
                for (WorkerId wid : enteredAfterWorkerRequestList.get(id)) {
                    if(verbose > 0) {
                        System.out.println("Worker " + wid.id + " entered before worker " +
                                id.id + " fulfilled its request.");
                    }
                }
                throw new RuntimeException("Error - 2*N workers entered workshop before worker " +
                        id.id + " make a change.");
            }
        }
    }

    public boolean getDoCheckLiveliness() {
        return doCheckLiveliness;
    }
}
