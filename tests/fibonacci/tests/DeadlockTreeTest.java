package cp2022.tests.fibonacci.tests;

import cp2022.tests.fibonacci.TestWorkplace;
import cp2022.tests.fibonacci.TestWorkshop;
import cp2022.tests.fibonacci.Utility;
import cp2022.tests.fibonacci.Worker;

import java.util.ArrayList;
import java.util.List;

public class DeadlockTreeTest extends TestWorkshop {
    @Override
    protected List<TestWorkplace> workplaces() {
        return Utility.simpleWorkshop(this, 128, 10);
    }

    @Override
    protected List<Worker> workers() {
        List<Worker> res = new ArrayList<>();
        for(int i=0; i<64; i++) {
            List<Integer> steps = new ArrayList<>();
            int step = 64+i;
            for (int j=0; j<5; j++) {
                while(step > 0) {
                    steps.add(step);
                    step/=2;
                }
                steps.add(step);
                step = 64+i;
            }
            res.add(new Worker(2*i, this, List.of(steps), 1,1,1));
            res.add(new Worker(2*i+1, this, List.of(steps), 1,1,1));
        }
        return res;
    }

    @Override
    public long time() {
        return 8382;
    }
}
