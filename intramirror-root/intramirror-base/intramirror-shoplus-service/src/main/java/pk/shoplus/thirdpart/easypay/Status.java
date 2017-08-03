package pk.shoplus.thirdpart.easypay;

public enum Status {

    /**
     * Success
     */
    SUCCESS(0000, "Success"),

    /**
     * Failure
     */
    Failure(0001, "Failure");

    private int statusCode;

    private String description;

    Status(int statusCode, String description) {
        this.statusCode = statusCode;
        this.description = description;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getDescription() {
        return this.description;
    }
}
