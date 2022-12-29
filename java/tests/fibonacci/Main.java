package cp2022.tests.fibonacci;

import cp2022.tests.fibonacci.tests.*;

import java.util.List;

public class Main{
    public static void main(String[] args) {
        int verbose = 0;

        List<TestWorkshop> list = List.of(
            new OneWorkerCorrectSwitchingTest(),
            new ManyWorkersOneGapTest(),
            new ConcurrencyTest(),
            new TwoWorkersOppositeTest(),
            new TwoWorkersOppositeManyTimesTest(),
            new CycleTest(),
            new CycleManyTimesTest(),
            new WorkerStayingTest(),
            new DeadlockTwoGroupsTest(),
            new DeadlockTwoGroupsManyTimesTest(),
            new DeadlockTreeTest(),
            new ManyWorkersChaosTest(),
            new RandomTest(),
            new AllPermutationsTest()
        );
        System.out.println("\nATTENTION:\nThese tests don't check for starvation! \n");
        for (TestWorkshop test : list) {
            System.out.println("Running fibonacci test: " + test.getClass().getSimpleName() + " (" + test.time() + "ms on students):\n");
            test.init();
            long start = System.currentTimeMillis();
            test.run(verbose);
            long end = System.currentTimeMillis();
            System.out.println("Test completed in " + (end-start) + "ms\n");
        }
    }
}
