package interlocking;

/** Minimal entry point used by the autograder. */
public class Driver {
  public static void main(String[] args) {
    Interlocking il = new InterlockingImpl();
    System.out.println("Add train t1; Section 1 -> 9");
    Train t1 = new Train("t1", Train.Type.PASSENGER);
    il.addTrain("S1", t1);
    il.moveTrainWithLocks("S1", "S2");
    il.moveTrainWithLocks("S2", "S3");
    il.moveTrainWithLocks("S3", "S4");
    System.out.println("Move trains: " + il.getState().size() + " moved");
  }
}
