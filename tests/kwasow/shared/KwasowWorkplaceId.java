package cp2022.tests.kwasow.shared;

import cp2022.base.WorkplaceId;

public class KwasowWorkplaceId extends WorkplaceId {
  private final int id;

  public KwasowWorkplaceId(int id) {
    this.id = id;
  }

  @Override
  public int compareTo(WorkplaceId other) {
    if (!(other instanceof KwasowWorkplaceId)) {
      throw new RuntimeException("Incomparable workplace types!");
    }
    return Integer.compare(id, ((KwasowWorkplaceId) other).getId());
  }

  public int getId() {
    return this.id;
  }
}
