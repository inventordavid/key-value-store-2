package lung.key_value_store;

import lung.key_value_store.api.ErrorListener;
import lung.key_value_store.api.KeysAndValues;
import lung.key_value_store.api.UndoHistory;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Modified in Technical Test Round 2
 *
 * Developed with Java Version: Oracle Java JDK 1.8.0_181
 *
 * @Author WAN, Kwok Lung
 */
public class KeysAndValuesImpl implements KeysAndValues {

    private static final Logger logger = LoggerFactory.getLogger(KeysAndValuesImpl.class);

    static {
        // Initiates Apache log4j
        BasicConfigurator.configure();
    }

    private final ErrorListener errorListener;

    /**
     * The main data store that stores the key-value paris accepted.
     */
    private final Map<String, String> dataStore;

    /**
     * The sorted (unique) key set of the above main data store.
     */
    private final SortedSet<String> dataStoreKeys;

    /**
     * Expected number of unique dataStoreKeys in the inputs
     *
     * This will be used for setting up the initial size of the main data
     * data store HashMap to improve performance by :
     *
     * - lowering the chance that HashMap auto-resizes afterwards
     *
     * - improving the hash function to make keys distribute with
     *   less duplications
     *
     */
    private final int EXPECTED_NUMBER_OF_UNIQUE_KEYS = 1 << 10;

    /**
     * Reusable StringBuilder to be used for building an error message to avoid
     * recreating a temp StringBuilder every time.
     */
    private final StringBuilder errorMessageStringBuilder;

    /**
     * Reusable StringBuilder to be used in the method "display" to avoid
     * recreating a temp StringBuilder every time "display" is called.
     */
    private final StringBuilder displayStringBuilder;

    /**
     * The question does not mention there would be many more atomic keys, so I
     * assume there will be only one set of atomic keys and so, for simplicity,
     * I use three StringBuilder's here to handle the only set of  atomic key.
     * If there would be many more atomic keys, I would use another data structure.
     *
     * Each is used to store the value of any of the key in the atomic key
     * set (441, 442, 500). Initial size is 64-char.
     */
    private final StringBuilder valueForKey441 = new StringBuilder(1 << 6);
    private final StringBuilder valueForKey442 = new StringBuilder(1 << 6);
    private final StringBuilder valueForKey500 = new StringBuilder(1 << 6);

    /**
     * CONSTANTS
     */
    private static final String ATOMIC_KEY_441 = "441";
    private static final String ATOMIC_KEY_442 = "442";
    private static final String ATOMIC_KEY_500 = "500";

    /**
     * Added in Technical Test Round 2
     *
     * A temporary Map used temporarily to save the current value of each
     * modified key. This Map is reused in each accept(). It's cleared after
     * use. It's also used by putIntoDataStore() which is called by accept().
     */
    private final Map<String, String> dataStoreForEachAccept;

    /**
     * Added in Technical Test Round 2
     *
     * Expected size of dataStoreForEachAccept.
     *
     * The reason of using this is the same as that for
     * EXPECTED_NUMBER_OF_UNIQUE_KEYS
     */
    private final int EXPECTED_NUMBER_OF_UNIQUE_KEYS_IN_DATA_STORE_FOR_EACH_ACCEPT = 1 << 6;

    /**
     * Added in Technical Test Round 2
     *
     * The instance of UndoHistory that will do all the snapshot save/load
     * work.
     */
    private final UndoHistory undoHistory;

    /**
     * Added in Technical Test Round 2
     *
     * The size of the max number of times of undoing i.e. the size of the
     * Undo History.
     */
    public static final int UNDO_HISTORY_SIZE = 256;

    /**
     * Constructor
     *
     * As the question mentions "Provide a way of injecting an ErrorListener into your KeysAndValues implementation.
     * Do not create a global instance. Use D.I.", an ErrorListener is accepted
     * as an argument in the Constructor.
     *
     * @param errorListener
     */
    public KeysAndValuesImpl(final ErrorListener errorListener) {
        this.errorListener = errorListener;

        /**
         * To initialize the data store with 25% larger than the expected data
         * size.
         */
        this.dataStore = new HashMap<String, String>((int)(EXPECTED_NUMBER_OF_UNIQUE_KEYS * 1.25));

        /**
         * The stored key set is stored in a TreeSet, in ascending order
         * alphabetically and case-insensitively.
         */
        this.dataStoreKeys = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);

        /**
         * The StringBuilder used in "display" is pre-allocated a length of
         * EXPECTED_NUMBER_OF_UNIQUE_KEYS x 128
         * 128 is the expected max length per line
         */
        this.displayStringBuilder = new StringBuilder(EXPECTED_NUMBER_OF_UNIQUE_KEYS * 128);

        this.errorMessageStringBuilder = new StringBuilder(128);

        /**
         * Added in Technical Test Round 2
         *
         * To initialize the temporary data store for each accept with 25%
         * larger than the expected data size.
         */
        this.dataStoreForEachAccept = new HashMap<String, String>((int)(EXPECTED_NUMBER_OF_UNIQUE_KEYS_IN_DATA_STORE_FOR_EACH_ACCEPT * 1.25));

        /**
         * Added in Technical Test Round 2
         */
        this.undoHistory = new UndoHistoryImpl(UNDO_HISTORY_SIZE);
    }

    /**
     * Modified in Technical Test Round 2
     *
     * The "accept" method implemented as described in the doc.
     *
     * @param kvPairs
     */
    public final void accept(String kvPairs) {
        // Input "numbers" cannot be null.
        Objects.requireNonNull(kvPairs, "Input cannot be null.");

        /**
         * Make a shortcut to avoid frequent address redirection.
         */
        final StringBuilder valueForKey441 = this.valueForKey441;
        final StringBuilder valueForKey442 = this.valueForKey442;
        final StringBuilder valueForKey500 = this.valueForKey500;

        /**
         * Reset the temp values which store the values of the keys in the
         * atomic key set (441,442,500).
         */
        valueForKey441.setLength(0);
        valueForKey442.setLength(0);
        valueForKey500.setLength(0);

        /**
         * Use Java 8 Stream to split the input String with the delimiter ","
         */
        Pattern.compile(",")

                .splitAsStream(kvPairs)

                .forEach(line -> {
                    /**
                     * For each comma separated part, split with "=".
                     */
                    final String[] parts = line.split("=");

                    if (parts.length == 2) {
                        final String inputKey = parts[0].trim();
                        final String inputValue = parts[1].trim();

                        /**
                         * If any key of the atomic key set (441,442,500) found
                         */
                        if (inputKey.equals(ATOMIC_KEY_441) || inputKey.equals(ATOMIC_KEY_442) || inputKey.equals(ATOMIC_KEY_500)) {
                            if (inputKey.equals(ATOMIC_KEY_441)) {
                                /**
                                 * Previously matched 441 already, so now overlapping.
                                 */
                                if (valueForKey441.length() > 0) {
                                    throwError("Key 441 is overlapping in the atomic group (441, 442, 500).");
                                    return;
                                } else {
                                    valueForKey441.append(inputValue);
                                }

                            } else if (inputKey.equals(ATOMIC_KEY_442)) {
                                /**
                                 * Previously matched 442 already, so now overlapping.
                                 */
                                if (valueForKey442.length() > 0) {
                                    throwError("Key 442 is overlapping in the atomic group (441, 442, 500).");
                                    return;
                                } else {
                                    valueForKey442.append(inputValue);
                                }

                            } else if (inputKey.equals(ATOMIC_KEY_500)) {
                                /**
                                 * Previously matched 500 already, so now overlapping.
                                 */
                                if (valueForKey500.length() > 0) {
                                    throwError("Key 500 is overlapping in the atomic group (441, 442, 500).");
                                    return;
                                } else {
                                    valueForKey500.append(inputValue);
                                }
                            }

                            /**
                             * If the whole set of the atomic key set (441,442,500)
                             * is found, save all of them to the data store and
                             * then reset the temp values.
                             */
                            if (valueForKey441.length() > 0 && valueForKey442.length() > 0 && valueForKey500.length() > 0) {
                                putIntoDataStore(ATOMIC_KEY_441, valueForKey441.toString());
                                putIntoDataStore(ATOMIC_KEY_442, valueForKey442.toString());
                                putIntoDataStore(ATOMIC_KEY_500, valueForKey500.toString());
                                valueForKey441.setLength(0);
                                valueForKey442.setLength(0);
                                valueForKey500.setLength(0);
                            }

                        } else {
                            /**
                             * Not a key in the atomic key set
                             */
                            putIntoDataStore(inputKey, inputValue);
                        }

                    } else {
                        /**
                         * Invalid format found.
                         */
                        throwError("Invalid format.");
                        return;
                    }
                });

        /**
         * If any one key of the atomic key set was found, it's in an
         * incomplete state.
         */
        if (valueForKey441.length() > 0 || valueForKey442.length() > 0 || valueForKey500.length() > 0) {
            errorMessageStringBuilder.setLength(0);
            errorMessageStringBuilder.append("atomic group(441,442,500) missing ");

            if (valueForKey441.length() == 0) errorMessageStringBuilder.append(ATOMIC_KEY_441).append(",");
            if (valueForKey442.length() == 0) errorMessageStringBuilder.append(ATOMIC_KEY_442).append(",");
            if (valueForKey500.length() == 0) errorMessageStringBuilder.append(ATOMIC_KEY_500).append(",");

            /**
             * Remove the last ","
             */
            errorMessageStringBuilder.setLength(errorMessageStringBuilder.length() - 1);

            throwError(errorMessageStringBuilder.toString());
        }

        /**
         * Added in Technical Test Round 2
         *
         * The snapshot is first built with a temporary and reusable HashMap
         * and then the Map will be used as an input argument for UndoHistory
         * to save the snapshot.
         */
        undoHistory.saveSnapshot(dataStoreForEachAccept);

        /**
         * Added in Technical Test Round 2
         *
         * To clear the temp data-store after use in each accept().
         */
        dataStoreForEachAccept.clear();
    }

    /**
     * Added in Technical Test Round 2
     *
     * According to the question readme:
     * reverts the current set of kvPairs to what it was before the last accept().
     * can be called multiple times.
     */
    public final void undo() {
        /**
         * If the undo history contains any snapshot
         */
        if (undoHistory.hasSnapshot()) {
            /**
             * Make a shortcut to avoid frequent address redirection.
             */
            final Map<String,String> dataStore = this.dataStore;

            /**
             * Get the latest snapshot from the undo history
             */
            final String[] snapshot = undoHistory.loadSnapshot();

            /**
             * Recover the snapshot
             */
            final int len = snapshot.length;
            for (int i=0; i<len; i+=2) {
                final String key = snapshot[i];
                final String value = snapshot[i + 1];

                if (value == null) {
                    /**
                     * "null" value in the snapshot means the key did not exist
                     * in the data store, so the key should be removed for
                     * undoing.
                     */
                    dataStore.remove(key);
                    dataStoreKeys.remove(key);

                } else {
                    /**
                     * Recover the old value
                     */
                    dataStore.put(key, value);
                }
            }
        }
    }

    /**
     * Modified in Technical Test Round 2
     *
     * Input a pair of key-value in the data store.
     * @param inputKey key
     * @param inputValue value
     */
    private final void putIntoDataStore(final String inputKey, final String inputValue) {
        /**
         * Added in Technical Test Round 2
         *
         * In each accept(), before the FIRST modification
         * inside the accept() for each key, save the value
         * of that key into a temp data store, so this temp
         * data-store saves the current snapshot of all
         * keys that are modified in this accept().
         *
         * If there is no existing value of the key, the temp
         * data-store (snapshot) will store "null" as the value.
         *
         * The temporary data-store is reused for each accept() and must be
         * cleared after use in each accept(). It's also used by
         * putIntoDataStore() which is called by accept().
         */
        if (!dataStoreForEachAccept.containsKey(inputKey)) {
            dataStoreForEachAccept.put(inputKey, dataStore.get(inputKey));
        }

        /**
         * Make a shortcut to avoid frequent address redirection.
         */
        final Map<String,String> dataStore = this.dataStore;

        if (dataStore.containsKey(inputKey)) {
            final String value = dataStore.get(inputKey);

            if (isInteger(inputValue)) {
                /**
                 * If the existing value of the key is also an integer
                 */
                if (isInteger(value)) {

                    try {
                        /**
                         * Accumulate
                         */
                        final int sum = Integer.parseInt(value) + Integer.parseInt(inputValue);
                        dataStore.put(inputKey, String.valueOf(sum));

                    } catch (NumberFormatException e) {
                        /**
                         * Integer value > Integer.MAX_VALUE so Integer.parseInt
                         * throws an exception.
                         */
                        throwError("The integer value overflows.", e);
                    }

                } else {
                    /**
                     * Overwrite
                     */
                    dataStore.put(inputKey, inputValue);
                }
            } else {
                /**
                 * Overwrite
                 */
                dataStore.put(inputKey, inputValue);
            }

        } else {
            dataStore.put(inputKey, inputValue);
            dataStoreKeys.add(inputKey);
        }
    }

    /**
     * The "display" method implemented as described in the doc.
     *
     */
    public final String display() {
        return displayWithStringBuilder().toString();
    }

    public final StringBuilder displayWithStringBuilder() {
        /**
         * Make a shortcut to avoid frequent address redirection.
         */
        final StringBuilder displayStringBuilder = this.displayStringBuilder;

        /**
         * Reuse the same StringBuilder to avoid creating garbage.
         */
        displayStringBuilder.setLength(0);

        for (final String key : dataStoreKeys) {
            displayStringBuilder.append(key)
                    .append("=")
                    .append(dataStore.get(key))
                    .append("\n");
        }

        // To remove to last char "\n"
        if (displayStringBuilder.length() > 0) {
            displayStringBuilder.setLength(displayStringBuilder.length() - 1);
        }

        return displayStringBuilder;
    }

    /**
     * A wrapper method to call ErrorListener.onError(String)
     * @param msg message String to input to onError
     */
    public void throwError(final String msg) {
        if (errorListener != null) {
            errorListener.onError(msg);
        }
    }

    /**
     * A wrapper method to call ErrorListener.onError(String,Exception)
     * @param msg message String to input to onError
     * @param e Exception info to input to onError
     */
    public void throwError(final String msg, final Exception e) {
        if (errorListener != null) {
            errorListener.onError(msg, e);
        }
    }

    /**
     * For better performance than Integer.parseInt for checking if an input
     * String represents an integer or not.
     *
     * @param input
     * @return true/false of whether the input is an integer
     */
    private static final boolean isInteger(final String input) {
        if (input == null) {
            return false;
        }

        if (input.isEmpty()) {
            return false;
        }

        int i = 0;
        final int len = input.length();

        if (input.charAt(0) == '-') {
            if (len == 1) {
                return false;
            }
            i = 1;
        }

        for (; i < len; ++i) {
            char c = input.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }

        return true;
    }

}
