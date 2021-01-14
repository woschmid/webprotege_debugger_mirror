package edu.stanford.bmir.protege.web.server.debugger;

public class RepairChangeResult {

    private Exception exception;

    public RepairChangeResult(Exception exception) {
        this.exception = exception;
    }

    public Boolean isValid() {
        return exception == null;
    }

    public RepairException getException() {
        return new RepairException(exception);
    }

    public String getMessage() {
        return exception.getMessage();
    }

}
