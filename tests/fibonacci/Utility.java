package cp2022.tests.fibonacci;

import cp2022.base.Workplace;

import java.util.ArrayList;
import java.util.List;

public class Utility {
    public static List<Integer> cycle(int begin, int gap, int n, int times) {
        List<Integer> res = new ArrayList<>();
        int pos = begin;
        for (int i = 0; i < times; i++) {
            res.add(pos);
            pos = ((pos+gap) % n + n) % n;
        }
        return res;
    }

    @SafeVarargs
    public static List<List<Integer>> repeat(int times, List<Integer>... paths) {
        List<List<Integer>> res = new ArrayList<>();
        for (int i = 0; i < times; i++) {
            for (List<Integer> path : paths) {
                res.add(new ArrayList<>(path));
            }
        }
        return res;
    }

    public static List<TestWorkplace> simpleWorkshop(TestWorkshop workshop, int n, int usageTime) {
        List<TestWorkplace> res = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            res.add(new TestWorkplace(i, workshop, usageTime));
        }
        return res;
    }
}
