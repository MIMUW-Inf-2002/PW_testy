package cp2022.tests.fibonacci.tests;

import cp2022.tests.fibonacci.TestWorkplace;
import cp2022.tests.fibonacci.TestWorkshop;
import cp2022.tests.fibonacci.Utility;
import cp2022.tests.fibonacci.Worker;

import java.util.ArrayList;
import java.util.List;

public class CycleManyTimesTest extends TestWorkshop {
    @Override
    protected List<TestWorkplace> workplaces() {
        return Utility.simpleWorkshop(this, 8,6);
    }

    @Override
    protected List<Worker> workers() {
        List<Worker> res = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            res.add(new Worker(i, this, Utility.repeat(10, Utility.cycle(i%8,1,8,32)),1,1,1));
        }
        return res;
    }

    @Override
    public long time() {
        return 5544;
    }
}
