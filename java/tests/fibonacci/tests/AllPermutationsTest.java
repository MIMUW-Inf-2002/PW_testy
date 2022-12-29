package cp2022.tests.fibonacci.tests;

import cp2022.tests.fibonacci.TestWorkplace;
import cp2022.tests.fibonacci.TestWorkshop;
import cp2022.tests.fibonacci.Utility;
import cp2022.tests.fibonacci.Worker;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AllPermutationsTest extends TestWorkshop {
    @Override
    protected List<TestWorkplace> workplaces() {
        return Utility.simpleWorkshop(this, 5, 6);
    }

    @Override
    protected List<Worker> workers() {
        List<Worker> res = new ArrayList<>();
        List<List<Integer>> permutations = permute(0,1,2,3,4);
        int i = 0;
        for (List<Integer> permutation : permutations) {
            List<Integer> list = new ArrayList<>(permutation);
            list.addAll(permutation);
            res.add(new Worker(i, this, Utility.repeat(2,list), 2, 2, 2));
            i++;
        }
        return res;
    }

    @Override
    public long time() {
        return 9276;
    }

    private List<List<Integer>> permute(int... nums) {
        if (nums.length == 0) {
            return List.of(new ArrayList<>());
        }
        else if (nums.length == 1) {
            return List.of(List.of(nums[0]));
        }
        else {
            List<List<Integer>> res = new ArrayList<>();
            for (int i=0; i<nums.length; i++) {
                int[] arr = new int[nums.length-1];
                for (int j=0; j<nums.length; j++) {
                    if (j<i) {
                        arr[j] = nums[j];
                    }
                    else if (j>i) {
                        arr[j-1] = nums[j];
                    }
                }
                final int x = nums[i];
                List<List<Integer>> previous = permute(arr);
                previous.forEach((l) ->{
                    LinkedList<Integer> temp = new LinkedList<>(l);
                    temp.add(0, x);
                    res.add(temp);
                });
            }
            return res;
        }
    }
}
