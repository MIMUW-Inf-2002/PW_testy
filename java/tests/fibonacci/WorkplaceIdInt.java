package cp2022.tests.fibonacci;

import cp2022.base.WorkplaceId;

public class WorkplaceIdInt extends WorkplaceId {

    @Override
    public String toString() {
        return String.valueOf(v);
    }

    private final int v;

    public WorkplaceIdInt(int v) {
        this.v = v;
    }

    @Override
    public int compareTo(WorkplaceId o) {
        if (o instanceof WorkplaceIdInt) {
            WorkplaceIdInt other = (WorkplaceIdInt) o;
            return this.v - other.v;
        }
        throw new RuntimeException("panic: different WorkplaceId classes");
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof WorkplaceIdInt) {
            WorkplaceIdInt other = (WorkplaceIdInt) o;
            return this.v == other.v;
        }
        return false;
    }

    @Override public int hashCode() {
        return v ^ 2137;
    }
}
