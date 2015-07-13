package me.abje.minicommerce;

public class JSONResponse {
    private boolean success;
    private String message;

    public JSONResponse() {
    }

    public JSONResponse(boolean success, String message) {
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
