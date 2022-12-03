package cp2022.tests.fibonacci.tests;

import cp2022.tests.fibonacci.TestWorkplace;
import cp2022.tests.fibonacci.TestWorkshop;
import cp2022.tests.fibonacci.Utility;
import cp2022.tests.fibonacci.Worker;

import java.util.List;

public class TwoWorkersOppositeManyTimesTest extends TestWorkshop {
    @Override
    protected List<TestWorkplace> workplaces() {
        return Utility.simpleWorkshop(this, 5, 1);
    }

    @Override
    protected List<Worker> workers() {
        return List.of(
                new Worker(0, this, Utility.repeat(50,Utility.cycle(0,1,5,20))),
                new Worker(1, this, Utility.repeat(50,Utility.cycle(4,-1,5,20)))
        );
    }

    @Override
    public long time() {
        return 1341;
    }
}
