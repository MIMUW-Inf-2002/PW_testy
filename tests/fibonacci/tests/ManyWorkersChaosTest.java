package cp2022.tests.fibonacci.tests;

import cp2022.tests.fibonacci.TestWorkplace;
import cp2022.tests.fibonacci.TestWorkshop;
import cp2022.tests.fibonacci.Utility;
import cp2022.tests.fibonacci.Worker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ManyWorkersChaosTest extends TestWorkshop {
    @Override
    protected List<TestWorkplace> workplaces() {
        return Utility.simpleWorkshop(this,20,8);
    }

    @Override
    protected List<Worker> workers() {
        List<Worker> res = new ArrayList<>();
        res.add(new Worker(0, this, Utility.repeat(5,List.of(2,1,3,7),List.of(4,2), List.of(7,3)), 1,2,10));
        for (int i=0; i<20; i++) {
            res.add(new Worker(3*i+1, this, Utility.repeat(2, Utility.cycle(i,1,20,30)),3,2,1));
            res.add(new Worker(3*i+2, this, Utility.repeat(2, Utility.cycle(i,5,20,30)),3,2,1));
            res.add(new Worker(3*i+3, this, Utility.repeat(2, Utility.cycle(i,-2,20,30)),3,2,1));
        }
        List<Integer> list = Utility.cycle(0,1,20,40);
        Collections.shuffle(list, new Random(73));
        List<List<Integer>> wrapped = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            wrapped.add(List.of(list.get(2*i), list.get(2*i+1)));
        }
        res.add(new Worker(61, this, wrapped, 1, 2, 10));
        Collections.shuffle(list, new Random(42));
        wrapped = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            wrapped.add(List.of(list.get(2*i), list.get(2*i+1)));
        }
        res.add(new Worker(62, this, wrapped, 2, 1, 10));
        Collections.shuffle(list, new Random(12345));
        wrapped = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            wrapped.add(List.of(list.get(2*i), list.get(2*i+1)));
        }
        res.add(new Worker(63, this, wrapped, 2, 1, 10));
        return res;
    }

    @Override
    public long time() {
        return 8855;
    }
}
