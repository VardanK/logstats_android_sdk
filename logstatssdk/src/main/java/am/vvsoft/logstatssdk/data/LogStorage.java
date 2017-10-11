package am.vvsoft.logstatssdk.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vkurkchiyan on 10/10/17.
 */

public class LogStorage {

    public final int MAX_BUFFER_SIZE = 1000;
    public final int MIN_ELEMENTS_TO_FLUSH = 100;

    private volatile List<LogEntry> pending = null;
    private volatile List<LogEntry> buffer = null;

    private volatile boolean overflow = false;
    private volatile boolean retry = false;
    private volatile boolean disabled = false;

    /**
     * Add log entry into log buffer
     * @param entry
     */
    public void addEntry(LogEntry entry){
        synchronized (this){
            if(disabled){
                return;
            }

            if(buffer == null){
                buffer = new ArrayList<>();
            }

            if(buffer.size() < MAX_BUFFER_SIZE) {
                buffer.add(entry);
            } else {
                overflow = true;
            }
        }
    }

    /**
     * Returns number of elements in active buffer
     * @return
     */
    public int getBufferSize(){
        synchronized (this) {
            return buffer.size();
        }
    }

    public boolean bufferEmpty(){
        synchronized (this) {
            return (buffer == null || buffer.size() == 0);
        }
    }

    public boolean bufferReady(){
        synchronized (this) {
            if(buffer != null && buffer.size() >= MIN_ELEMENTS_TO_FLUSH){
                return true;
            }
        }

        return false;
    }

    /**
     * Indicates that the buffer size is small and it was overflown
     * @return
     */
    public boolean isOverflow(){
        return overflow;
    }

    /**
     * Copy all the records from buffer list into pending list and reset the buffer
     *
     * @return the pending list
     */
    public List<LogEntry> createPendingList(){
        synchronized (this) {
            if(pending == null && buffer != null) {
                pending = buffer;
                buffer = null;
            }
        }

        return pending;
    }

    /**
     * After successfully sending the data to server, reset the pending logs list
     */
    public void resetPending(){
        synchronized (this) {
            pending = null;
        }
    }

    /**
     * Indicates if there are items that are on on the way right now
     *
     * @return true if pending list is not null, false otherwise
     */
    public boolean hasPending(){
        synchronized (this) {
            return pending != null;
        }
    }

    public void retryPending(){
        synchronized (this) {
            if(pending != null && pending.size() < MAX_BUFFER_SIZE && buffer != null){
                int newSize = pending.size() + buffer.size();
                if(newSize > MAX_BUFFER_SIZE){
                    newSize = MAX_BUFFER_SIZE;
                }

                int newBufferSize = newSize - pending.size();

                pending.addAll(buffer.subList(0, newBufferSize));

                if(newBufferSize < buffer.size()) {
                    buffer = buffer.subList(newBufferSize, buffer.size());
                } else {
                    buffer = null;
                }
            }

            retry = true;
        }
    }

    public boolean isPendingRetry(){
        return retry;
    }

    public void resetRetryFlag(){
        synchronized (this) {
            retry = false;
        }
    }

    public void setDisabled(){
        synchronized (this) {
            disabled = true;

            pending = null;
            buffer = null;

            overflow = false;
            retry = false;
        }
    }
}
