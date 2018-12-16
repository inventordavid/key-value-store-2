package lung.key_value_store;

import lung.key_value_store.api.UndoHistory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

/**
 * Added in Technical Test Round 2
 *
 * Developed with Java Version: Oracle Java JDK 1.8.0_181
 *
 * @Author WAN, Kwok Lung
 */
public class UndoHistoryImpl implements UndoHistory {

    /**
     * Added in Technical Test Round 2
     *
     * The Ring Buffer storing the snapshot of change of the data store after
     * each accept(). It is used for implementing undo().
     */
    private final Deque<String[]> snapshots;

    /**
     * Added in Technical Test Round 2
     *
     * The size of the max number of times of undoing i.e. the size of the
     * history.
     */
    private final int UNDO_HISTORY_SIZE;

    /**
     * Added in Technical Test Round 2
     *
     * Constructor
     */
    public UndoHistoryImpl(int undoHistorySize) {
        /**
         * Added in Technical Test Round 2
         *
         * Set the max Undo History size.
         */
        this.UNDO_HISTORY_SIZE = undoHistorySize;

        /**
         * Added in Technical Test Round 2
         *
         * Use an ArrayDeque to store the snapshot after each accept() into a
         * history Ring Buffer. The Ring Buffer is made by an ArrayDeque. It
         * is used for implementing undo().
         *
         * UNDO_HISTORY_SIZE is the size of the buffer. Array size is checked
         * before each adding. The first element will be removed if the max size
         * is reached.
         */
        this.snapshots = new ArrayDeque<>(this.UNDO_HISTORY_SIZE);
    }

    /**
     * Added in Technical Test Round 2
     *
     * The snapshot is first built with a temporary and reusable HashMap,
     * but it will be converted to a String[] before being stored in the
     * Undo History.
     *
     * The snapshot can actually be saved as a HashMap for simplicity in the
     * Undo History but it's too bulky in memory. It can also be a LinkedList of
     * tuples (pairs) but it's still bulky in memory as well. I have chosen to
     * use String[] instead. It's a 1D array. The data is stored like:
     *
     * [key][value][key][value]...[key][value] in a 1D String[] array.
     * So, "Three=null,one=1" will be stored as ["Three"][null]["one"]["1"].
     *
     * @param dataStoreForEachAccept the temporary Map that stores the snapshot
     */
    @Override
    public void saveSnapshot(Map<String, String> dataStoreForEachAccept) {
        final String[] snapshot = new String[dataStoreForEachAccept.size() * 2];
        int i = 0;
        for (final Map.Entry<String, String> entry : dataStoreForEachAccept.entrySet()) {
            snapshot[i] = entry.getKey();
            snapshot[i + 1] = entry.getValue();
            i += 2;
        }

        /**
         * Added in Technical Test Round 2
         *
         * To make the Deque work as an Ring Buffer
         */
        if (snapshots.size() >= UNDO_HISTORY_SIZE) {
            snapshots.removeFirst();
        }

        /**
         * Added in Technical Test Round 2
         *
         * Append new snapshot to the end of the history
         */
        snapshots.addLast(snapshot);
    }

    /**
     * Added in Technical Test Round 2
     *
     * Get and remove the latest snapshot from the Ring Buffer, and then
     * return it.
     *
     * If there is no snapshot available, it will return null.
     *
     * @return String[] of the snapshot
     */
    @Override
    public String[] loadSnapshot() {
        if (hasSnapshot()) {
            return snapshots.removeLast();
        } else {
            return null;
        }
    }

    /**
     * Added in Technical Test Round 2
     *
     * Check if there is any snapshot in the history.
     *
     * @return true/false
     */
    @Override
    public boolean hasSnapshot() {
        return snapshots.size() > 0;
    }
}
