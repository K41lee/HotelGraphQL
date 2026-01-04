package Impl;

public class ReservationResult {
    private final boolean success;
    private final String message;
    private final String reference;

    private ReservationResult(boolean success, String message, String reference) {
        this.success = success;
        this.message = message;
        this.reference = reference;
    }

    public static ReservationResult success(String reference) {
        return new ReservationResult(true, "Réservation confirmée", reference);
    }

    public static ReservationResult failure(String message) {
        return new ReservationResult(false, message, null);
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getReference() { return reference; }
}
