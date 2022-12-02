package cp2022.tests.fibonacci.tests;

import cp2022.tests.fibonacci.TestWorkplace;
import cp2022.tests.fibonacci.TestWorkshop;
import cp2022.tests.fibonacci.Utility;
import cp2022.tests.fibonacci.Worker;

import java.util.List;

public class OneWorkerCorrectSwitchingTest extends TestWorkshop {

    @Override
    protected List<TestWorkplace> workplaces() {
        return Utility.simpleWorkshop(this, 5, 10);
    }

    @Override
    protected List<Worker> workers() {
        return List.of(
                new Worker(0, this, List.of(List.of(0,1,2,3,4), List.of(3,1,2,4,0)), 10, 10, 10)
        );
    }

    @Override
    public long time() {
        return 329;
    }
}
