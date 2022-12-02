package cp2022.tests.fibonacci.tests;

import cp2022.tests.fibonacci.TestWorkplace;
import cp2022.tests.fibonacci.TestWorkshop;
import cp2022.tests.fibonacci.Utility;
import cp2022.tests.fibonacci.Worker;

import java.util.ArrayList;
import java.util.List;

public class DeadlockTwoGroupsTest extends TestWorkshop {

    @Override
    protected List<TestWorkplace> workplaces() {
        return Utility.simpleWorkshop(this,8,10);
    }

    @Override
    protected List<Worker> workers() {
        List<Worker> res = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            res.add(new Worker(i, this, List.of(Utility.cycle(i,1,8,32))));
        }
        for (int i = 4; i < 8; i++) {
            res.add(new Worker(i, this, List.of(Utility.cycle(i,-1,8,32))));
        }
        return res;
    }

    @Override
    public long time() {
        return 366;
    }
}
