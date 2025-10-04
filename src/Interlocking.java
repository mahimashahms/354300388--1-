package interlocking;

import java.util.List;
import java.util.Map;

/** Interface defining operations for the Islington interlocking system. */
public interface Interlocking {

  void reset();

  boolean isFree(String section);

  boolean requestRoute(String trainId, List<String> sections);

  boolean addTrain(String section, Train t);

  boolean moveTrain(String from, String to);

  boolean moveTrainWithLocks(String from, String to);

  void exitTrain(String section);

  boolean detectCollision(String to);

  boolean detectDeadlock();

  void resolveDeadlock();

  Map<String, Train> getState();

  boolean isCrossingLocked();

  boolean isSouthJunctionLocked();
}
