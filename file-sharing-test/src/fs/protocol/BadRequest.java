package fs.protocol;

public class BadRequest extends Response {

    private String errorMessage;

    public BadRequest(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Get errorMessage
     *
     * @return value of errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }
}
