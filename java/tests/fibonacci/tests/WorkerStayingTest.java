package cp2022.tests.fibonacci.tests;

import cp2022.tests.fibonacci.TestWorkplace;
import cp2022.tests.fibonacci.TestWorkshop;
import cp2022.tests.fibonacci.Utility;
import cp2022.tests.fibonacci.Worker;

import java.util.List;

public class WorkerStayingTest extends TestWorkshop {
    @Override
    protected List<TestWorkplace> workplaces() {
        return Utility.simpleWorkshop(this, 3,10);
    }

    @Override
    protected List<Worker> workers() {
        return List.of(
                new Worker(0,this,Utility.repeat(5, Utility.cycle(0,0,3,10))),
                new Worker(1,this,List.of(Utility.cycle(1,1,3,40)))
        );
    }

    @Override
    public long time() {
        return 818;
    }
}
