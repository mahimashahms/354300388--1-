package interlocking;

/** Represents a train entity with ID and type. */
public class Train {

  /** Train category. */
  public enum Type {
    PASSENGER,
    FREIGHT
  }

  private final String id;
  private final Type type;

  public Train(String id, Type type) {
    this.id = id;
    this.type = type;
  }

  public String getId() {
    return id;
  }

  public Type getType() {
    return type;
  }

  @Override
  public String toString() {
    return id + "(" + type + ")";
  }
}
