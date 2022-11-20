package cp2022.tests.pggp_tests.utility;

import cp2022.tests.pggp_tests.utility.workshop_actions.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public abstract class Test {
    protected Action sleep(int milliseconds)
    {
        return new Sleep(milliseconds);
    }

    protected Action sleepRandomBetween(int min, int max) {
        Random r = new Random();
        return sleep(min + r.nextInt(max - min));
    }

    protected Action enter(int workplaceId) {
        return new Enter(workplaceId);
    }

    protected Action leave() {
        return new Leave();
    }

    protected Action switchTo(int workplaceId) {
        return new SwitchTo(workplaceId);
    }

    protected Action use() {
        return new Use();
    }

    protected Long timeLimit = null;
    protected Long timeOfAuthor = null;

    protected Action[] repeat(Action[] actions, int n) {
        Action[] output = new Action[actions.length * n];
        for (int i = 0; i < actions.length * n; i++) {
            output[i] = actions[i % actions.length];
        }
        return output;
    }

    // Worker enters workplace 'begin' and then start rotating
    // 'begin', 'begin + 1', ..., 'last', 'first', 'first + 1', ..., 'last', ...
    public Action[] rotateCycle(int begin, int first, int last, int len) {
        Action[] actions = new Action[len];
        actions[0] = enter(begin);
        for (int i = 1; i < len - 2; i += 2) {
            actions[i] = use();
            actions[i + 1] = switchTo(first + (begin + i / 2) % (last - first + 1));
        }
        actions[len - 1] = use();
        actions[len - 1] = leave();
        return actions;
    }

    protected Action[] inUseOut(int workplaceId, int times) {
        Action[] actions = new Action[3 * times];
        for (int i = 0; i < 3 * times; i += 3) {
            actions[i] = enter(workplaceId);
            actions[i + 1] = use();
            actions[i + 2] = leave();
        }
        return actions;
    }


    // Worker jumps between workplaces wid1 and wid2.
    protected Action[] jumpBetween(int wid1, int wid2, int times) {
        Action[] actions = new Action[times + 1];
        actions[0] = enter(wid1);
        for (int i = 1; i < times; i++) {
            if(i % 2 == 1) {
                actions[i] = switchTo(wid2);
            }
            else {
                actions[i] = switchTo(wid1);
            }
        }
        actions[times] = leave();
        return actions;
    }

    // Worker enters the workshop and does switch() randomly.
    protected Worker workerRandomSwitches(int id, int numberOfActions, int numberOfWorkplaces) {
        Random rand = new Random();
        Action[] actions = new Action[numberOfActions + 2];
        actions[0] = enter(rand.nextInt(numberOfWorkplaces));
        actions[numberOfActions + 1] = leave();
        int previous = -1;
        int current = -1;
        for (int j = 1; j < numberOfActions + 1; j++) {
            while (current == previous) {
                current = rand.nextInt(numberOfWorkplaces);
            }
            actions[j] = switchTo(current);
            previous = current;
        }
        return new Worker(id, actions);
    }

    protected Worker workerRandomActions(int id, int numberOfActions, int numberOfWorkplaces, int percentChanceLeave) {
        boolean inside = true;
        Random rand = new Random();
        Action[] actions = new Action[numberOfActions + 2];
        actions[0] = enter(rand.nextInt(numberOfWorkplaces));
        actions[numberOfActions + 1] = leave();

        int previous = -1;
        int current = -1;
        for (int j = 1; j < numberOfActions + 1; j++) {
            if(inside){

                while (current == previous) {
                    current = rand.nextInt(numberOfWorkplaces);
                }
                previous = current;
                if(rand.nextInt() % 100 < percentChanceLeave){
                    actions[j] = leave();
                    inside = false;
                }
                else{
                    actions[j] = switchTo(current);
                }
            }
            else{
                actions[j] = enter(rand.nextInt(numberOfWorkplaces));
                inside = true;
            }
        }

        if(actions[numberOfActions] instanceof Leave) {
            actions[numberOfActions] = switchTo((previous + 1) % numberOfWorkplaces);
        }
        return new Worker(id, actions);
    }

    // Concat two arrays of actions.
    public Action[] concat(Action[] array1, Action[] array2) {
        List<Action> resultList = new ArrayList<>(array1.length + array2.length);
        Collections.addAll(resultList, array1);
        Collections.addAll(resultList, array2);

        Action[] resultArray = (Action[]) Array.newInstance(array1.getClass().getComponentType(), 0);
        return resultList.toArray(resultArray);
    }
    public Action[] concat(Action action, Action[] array2) {
        return concat(new Action[]{action}, array2);
    }

    public abstract boolean run(Boolean verbose);

    public Long getTimeOfAuthor() {
        return timeOfAuthor;
    }

    public Long getTimeLimit() {
        return timeLimit;
    }
}
