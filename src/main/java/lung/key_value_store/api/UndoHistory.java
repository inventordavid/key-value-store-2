package lung.key_value_store.api;

import java.util.Map;

/**
 * Added in Technical Test Round 2
 *
 * Developed with Java Version: Oracle Java JDK 1.8.0_181
 *
 * @Author WAN, Kwok Lung
 */
public interface UndoHistory {

    void saveSnapshot(Map<String, String> dataStoreForEachAccept);

    String[] loadSnapshot();

    boolean hasSnapshot();

}