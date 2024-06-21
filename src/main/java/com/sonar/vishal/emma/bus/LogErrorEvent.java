package com.sonar.vishal.emma.bus;

public class LogErrorEvent {

    private String message;

    private Object exception;

    public String getMessage() {
        return message;
    }

    public LogErrorEvent setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getException() {
        return exception;
    }

    public LogErrorEvent setException(Object exception) {
        this.exception = exception;
        return this;
    }
}
