package cp2022.tests.fibonacci.tests;

import cp2022.tests.fibonacci.TestWorkplace;
import cp2022.tests.fibonacci.TestWorkshop;
import cp2022.tests.fibonacci.Utility;
import cp2022.tests.fibonacci.Worker;

import java.util.ArrayList;
import java.util.List;

public class AntiKrzysiekTest extends TestWorkshop {
    @Override
    protected List<TestWorkplace> workplaces() {
        return Utility.simpleWorkshop(this,20,500);
    }

    @Override
    protected List<Worker> workers() {
        List<Worker> res = new ArrayList<>();
        for (int i=0; i<20; i++) {
            res.add(new Worker(i,this, List.of(List.of(i)), 500, 500, 0));
        }
        return res;
    }

    @Override
    public long time() {
        return 1512;
    }
}
