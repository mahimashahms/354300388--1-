
//interlockingimpl
import java.util.*;

/**
 * Implementation of the Interlocking system for the Islington corridor.
 * Handles safe train movements, locks for shared sections, and queued transitions.
 */
public class InterlockingImpl implements Interlocking {

    private final Map<String, Train> trains = new HashMap<>();
    private final Map<String, String> sectionOccupancy = new HashMap<>();
    private final Map<String, Queue<Train>> waitingQueue = new HashMap<>();
    private final Set<String> lockedSections = new HashSet<>();

    // Define shared/critical sections
    private static final Set<String> SHARED_SECTIONS = new HashSet<>(Arrays.asList("S5", "S6", "S10"));

    public InterlockingImpl() {
        reset();
    }

    @Override
    public void reset() {
        trains.clear();
        sectionOccupancy.clear();
        waitingQueue.clear();
        lockedSections.clear();
    }

    @Override
    public boolean addTrain(String name, int from, int to) {
        String start = "S" + from;
        String end = "S" + to;
        if (sectionOccupancy.containsKey(start)) {
            System.out.println("❌ Section " + start + " already occupied, cannot add " + name);
            return false;
        }

        Train t = new Train(name, from, to);
        trains.put(name, t);
        sectionOccupancy.put(start, name);

        System.out.println("✅ Added Train " + name + " at " + start + " going to " + end);
        return true;
    }

    @Override
    public boolean moveTrain(String from, String to) {
        String trainName = sectionOccupancy.get(from);
        if (trainName == null) {
            System.out.println("⚠️ No train found at " + from);
            return false;
        }

        Train train = trains.get(trainName);
        if (train == null) return false;

        // If destination free → move immediately
        if (isSectionFree(to)) {
            performMove(train, from, to);
            return true;
        }

        // Otherwise, queue it
        queueTrainForSection(train, to);
        System.out.println("🚉 Train " + trainName + " waiting for " + to + " (occupied)");
        return true;
    }

    @Override
    public boolean moveTrainWithLocks(String from, String to) {
        String trainName = sectionOccupancy.get(from);
        if (trainName == null) return false;
        Train train = trains.get(trainName);
        if (train == null) return false;

        // Acquire lock if shared section
        if (SHARED_SECTIONS.contains(to)) {
            if (lockedSections.contains(to)) {
                queueTrainForSection(train, to);
                System.out.println("🔒 Train " + trainName + " waiting for lock on " + to);
                return true;
            }
            lockedSections.add(to);
            System.out.println("🔐 Lock acquired for section " + to + " by " + trainName);
        }

        return moveTrain(from, to);
    }

    private void performMove(Train train, String from, String to) {
        sectionOccupancy.remove(from);
        sectionOccupancy.put(to, train.getName());
        train.setCurrentSection(Integer.parseInt(to.replace("S", "")));

        // If this was a shared section → release lock when leaving
        if (SHARED_SECTIONS.contains(from)) {
            lockedSections.remove(from);
            System.out.println("🔓 Lock released from " + from);
        }

        System.out.println("➡️ Train " + train.getName() + " moved " + from + " → " + to);

        // After moving, check if any waiting trains can now move into freed section
        processWaitingTrains(from);
    }

    private void processWaitingTrains(String freedSection) {
        Queue<Train> queue = waitingQueue.get(freedSection);
        if (queue == null || queue.isEmpty()) return;

        Train next = queue.poll();
        if (next != null) {
            System.out.println("🟢 Waking train " + next.getName() + " waiting for " + freedSection);
            String current = "S" + next.getCurrentSection();
            performMove(next, current, freedSection);
        }
    }

    private void queueTrainForSection(Train t, String section) {
        waitingQueue.computeIfAbsent(section, k -> new LinkedList<>()).add(t);
    }

    private boolean isSectionFree(String section) {
        return !sectionOccupancy.containsKey(section);
    }

    // Debug helper
    public void printOccupancy() {
        System.out.println("\n=== TRACK OCCUPANCY ===");
        for (Map.Entry<String, String> e : sectionOccupancy.entrySet()) {
            System.out.println(e.getKey() + " → " + e.getValue());
        }
    }
}
