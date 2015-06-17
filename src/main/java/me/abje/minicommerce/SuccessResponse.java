package me.abje.minicommerce;

public class SuccessResponse {
    private boolean success;
    private String message;

    public SuccessResponse() {
    }

    public SuccessResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
