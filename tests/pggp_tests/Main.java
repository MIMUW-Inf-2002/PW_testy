package cp2022.tests.pggp_tests;

import cp2022.tests.pggp_tests.tests.bigrandom.*;
import cp2022.tests.pggp_tests.tests.deadlock.*;
import cp2022.tests.pggp_tests.tests.efficiency.TestEfficiencyBigRandom;
import cp2022.tests.pggp_tests.tests.efficiency.TestEfficiencyCycle;
import cp2022.tests.pggp_tests.tests.efficiency.TestEfficiencyOrderErrorCatch;
import cp2022.tests.pggp_tests.tests.efficiency.TestEfficiencyParallel;
import cp2022.tests.pggp_tests.tests.simple.*;
import cp2022.tests.pggp_tests.tests.starvation.*;
import cp2022.tests.pggp_tests.utility.SimulationWithBugCheck;
import cp2022.tests.pggp_tests.utility.Test;

public class Main {
    public static void main(String[] args) {
        // If you want to log information, change to 1 or 2.
        int verbose = 0;

        System.out.println("Parameter verbose = " + verbose + ". It can be changed in the code of the tests to print the logs.");
        System.out.println("Tests add some operations after and before switch, enter and leave. Since we are unable to detect" +
                "when this functions start and finish – all tests should be passed.");

        if(verbose > 0) {
            System.out.println("If the test doesn't check the order of events, the order of logs may not be true.");
        }


        System.out.println("If test has 'starvation' in name it will be slow due to test architecture.");

        System.out.println("Tests add something to the");

        System.out.println("");

        // How much time will elapse between two following actions. Applied only when liveliness is checked.
        SimulationWithBugCheck.timeOfWaitBetweenActionsWhenOrderMatters = 30;

        Test[] tests = {
                new TestSimpleOneWorkplace(),
                new TestSimpleQueue(),
                new TestSimpleOneUse(),
                new TestSimpleOneUseRepeated(),
                new TestSimpleSwitchRepeated(),
                new TestSimpleSwitchAndUse(),
                new TestSimpleQueueInsideAndUse(),
                new TestSimpleOneStaysOneMoves(),
                new TestSimpleOneWorkplaceManyTimes(),
                new TestSimpleTwoQueues(),
                new TestDeadlockPair(),
                new TestDeadlockPairManyTimes(),
                new TestDeadlockTriCycle(),
                new TestDeadlockTriCycleManyTimes(),
                new TestDeadlockOneBigOneSmallCycleWithCommonVertex(),
                new TestStarvationTricycleAndQueue(),
                new TestStarvationOneLongQueue(),
                new TestStarvationStar(),
                new TestStarvationManyQueues(),
                new TestStarvationBigStar(),
                new TestEfficiencyParallel(),
                new TestEfficiencyCycle(),
                new TestBigRandomRotations(),
                new TestBigRandom1(),
                new TestBigRandom2(),
                new TestBigRandom3(),
                new TestBigRandom4(),
                new TestBigRandom5(),
                new TestBigRandom6(),
                new TestBigRandomStarvation(),
                new TestBigRandom7(),
                new TestBigRandomStarvation2(),
                new TestEfficiencyBigRandom(),
                new TestEfficiencyOrderErrorCatch()
        };

        int i = 1;
        for (Test test : tests) {
            System.out.print("Test " + test.getClass().getSimpleName());
            if( test.getTimeOfAuthor() != null) {
                // Czasy tymczasowo nieaktualne, wrócą.
                 System.out.println(" (author's time on students " + test.getTimeOfAuthor() + "ms.)");
            }
            System.out.println();
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
