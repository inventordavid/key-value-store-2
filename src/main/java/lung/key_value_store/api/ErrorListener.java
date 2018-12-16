package lung.key_value_store.api;

/**
 * Developed with Java Version: Oracle Java JDK 1.8.0_181
 *
 * @Author WAN, Kwok Lung
 */
public interface ErrorListener {

    void onError(String msg);

    void onError(String msg, Exception e);

}
