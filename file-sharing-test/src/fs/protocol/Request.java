package fs.protocol;

import java.io.Serializable;
import java.util.IllegalFormatException;

/**
 * Abstract type for all client requests
 */
public abstract class Request implements Serializable {

    public static class IllegalRequestException extends Exception {
        public IllegalRequestException(String errorMessage) {
            super(errorMessage);
        }
    }

    /**
     * Validate format and content of request and throw {@link IllegalRequestException} if not correct.
     * @throws IllegalRequestException
     */
    public abstract void validate() throws IllegalRequestException;

}
