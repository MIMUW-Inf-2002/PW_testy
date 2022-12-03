package cp2022.tests.fibonacci.tests;

import cp2022.tests.fibonacci.TestWorkplace;
import cp2022.tests.fibonacci.TestWorkshop;
import cp2022.tests.fibonacci.Utility;
import cp2022.tests.fibonacci.Worker;

import java.util.List;

public class ManyWorkersOneGapTest extends TestWorkshop {
    @Override
    protected List<TestWorkplace> workplaces() {
        return Utility.simpleWorkshop(this, 5,10);
    }

    @Override
    protected List<Worker> workers() {
        return List.of(
                new Worker(0, this, List.of(Utility.cycle(0,1,5,20)), 10, 10, 10),
                new Worker(1, this, List.of(Utility.cycle(1,1,5,20)), 10, 10, 10),
                new Worker(2, this, List.of(Utility.cycle(2,1,5,20)), 10, 10, 10),
                new Worker(3, this, List.of(Utility.cycle(3,1,5,20)), 10, 10, 10)
        );
    }

    @Override
    public long time() {
        return 626;
    }
}
