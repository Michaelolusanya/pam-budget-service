package ikea.imc.pam.budget.service.controller.dto;

public class ErrorDTO {

    private String message;
    private String pointer;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPointer() {
        return pointer;
    }

    public void setPointer(String pointer) {
        this.pointer = pointer;
    }
}
