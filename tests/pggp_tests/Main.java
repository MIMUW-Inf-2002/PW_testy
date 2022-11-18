package cp2022.tests.pggp_tests;

import cp2022.tests.pggp_tests.tests.*;
import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;

public class Main {
    public static void main(String[] args) {
        // If you want to log information, change to true.
        boolean verbose = false;

        System.out.println("Parameter verbose = " + verbose + ". It can be changed in the code of the tests to print the logs.");

        if(verbose) {
            System.out.println("If the test doesn't check the order of events, the order of logs may not be true.");
        }

        System.out.println("");

        // How much time will elapse between two following actions. Applied only when liveliness is checked.
        SimulationWithBugCheck.timeOfWaitBetweenActionsWhenOrderMatters = 30;

        Test[] tests = {
                new Test1SimpleOneWorkplace(),
                new Test2SimpleQueue(),
                new Test3SimpleOneUse(),
                new Test4SimpleOneUseRepeated(),
                new Test5SimpleSwitch(),
                new Test6SimpleSwitchRepeated(),
                new Test7SimpleSwitchAndUse(),
                new Test8SimpleQueueAndUse(),
                new Test9SimpleQueueInsideAndUse(),
                new Test10SimpleOneStaysOneMoves(),
                new Test11DeadlockPair(),
                new Test12DeadlockPairManyTimes(),
                new Test13DeadlockTriCycle(),
                new Test14DeadlockTriCycleManyTimes(),
                new Test15DeadlockOneBigOneSmallCycleWithCommonVertex(),
                new Test16StarvationTricycleAndQueue(),
                new Test17StarvationOneLongQueue(),
                new Test18StarvationStar(),
                new Test19StarvationManyQueues(),
                new Test20StarvationBigStar(),
                new Test21EfficiencyParallel(),
                new Test22EfficiencyCycle(),
                new Test23BigRandomRotations(),
                new Test24BigRandom1(),
                new Test25BigRandom2(),
                new Test26BigRandomStarvation(),
                new Test27BigRandom3(),
                new Test28BigRandom4(),
                new Test29BigRandom5(),
                new Test30EfficiencyOrderErrorCatch()
        };

        int i = 1;
        for (Test test : tests) {
            System.out.println("Test " + i + " (author's time " + test.getTimeOfAuthor() + "ms)");
            if(test.getTimeLimit() != null) {
                System.out.println("Time limit = " + test.getTimeLimit() + "ms");
            }
            long start = System.currentTimeMillis();

            boolean passed = test.run(verbose); // Run the test.

            long finish = System.currentTimeMillis();
            long timeElapsed = finish - start;
            if(test.getTimeLimit() != null) {
                if(test.getTimeLimit() < timeElapsed) {
                    System.out.println("Test took " + timeElapsed + "ms");
                    System.out.println("Time limit exceeded.");
                    passed = false;
                }
            }

            if(passed) {
                System.out.println("PASSED in " + timeElapsed + "ms");
                System.out.println();
            }
            else {
                System.out.println("Not passed.");
                return;
            }
            i++;
        }
    }
}
