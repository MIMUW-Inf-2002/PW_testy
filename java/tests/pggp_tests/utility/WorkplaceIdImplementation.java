package cp2022.tests.pggp_tests.utility;

import cp2022.base.WorkplaceId;

public class WorkplaceIdImplementation extends WorkplaceId {
    public final Integer id;

    public WorkplaceIdImplementation(int id) {
        this.id = id;
    }

    @Override
    public int compareTo(WorkplaceId other) {
        if (!(other instanceof WorkplaceIdImplementation)) {
            throw new RuntimeException("Incomparable workplace types!");
        }
        return this.id.compareTo(((WorkplaceIdImplementation)other).id);
    }

    @Override
    public String toString() {
        return "wId(" + id + ")";
    }
}
