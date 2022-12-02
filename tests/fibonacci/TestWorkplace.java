package cp2022.tests.fibonacci;

import cp2022.base.Workplace;
import cp2022.base.WorkplaceId;

public class TestWorkplace extends Workplace {

    protected final TestWorkshop workshop;

    protected final int v;

    protected final int usageTime;

    public TestWorkplace(int v, TestWorkshop workshop, int usageTime) {
        super(new WorkplaceIdInt(v));
        this.workshop = workshop;
        this.usageTime = usageTime;
        this.v = v;
    }

    public TestWorkplace(int v, TestWorkshop workshop) {
        this(v, workshop, 0);
    }

    @Override
    public void use() {
        workshop.use(v);
        try {
            Thread.sleep(usageTime);
        }
        catch (InterruptedException e) {
            throw new RuntimeException("panic: unexpected thread interruption");
        }
        workshop.endUse(v);
    }
}
