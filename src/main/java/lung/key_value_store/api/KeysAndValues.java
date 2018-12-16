package lung.key_value_store.api;

/**
 * Modified in Technical Test Round 2
 *
 * Developed with Java Version: Oracle Java JDK 1.8.0_181
 *
 * @Author WAN, Kwok Lung
 */
public interface KeysAndValues {

    void accept(String kvPairs);

    String display();

    /**
     * Added in Technical Test Round 2
     */
    void undo();

}
