package cp2022.tests.fibonacci.tests;

import cp2022.base.Workplace;
import cp2022.tests.fibonacci.TestWorkplace;
import cp2022.tests.fibonacci.TestWorkshop;
import cp2022.tests.fibonacci.Utility;
import cp2022.tests.fibonacci.Worker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomTest extends TestWorkshop {
    @Override
    protected List<TestWorkplace> workplaces() {
        List<TestWorkplace> res = new ArrayList<>();
        for(int i=0; i<50; i++) {
            res.add(new TestWorkplace(i,this,1 + ThreadLocalRandom.current().nextInt(10)));
        }
        return res;
    }

    @Override
    protected List<Worker> workers() {
        List<Worker> res = new ArrayList<>();
        List<Integer> list = Utility.cycle(0,1,50,100);
        for(int i=0; i<100; i++) {
            Collections.shuffle(list);
            res.add(new Worker(i,this,Utility.repeat(2,list), 1, 1, 10));
        }
        return res;
    }

    @Override
    public long time() {
        return 23700;
    }
}
