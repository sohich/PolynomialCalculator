package org.stepik.is2018.posokhin;

public class InvalidSyntaxException extends RuntimeException {
    public InvalidSyntaxException() {
    }

    public InvalidSyntaxException(String message) {
        super(message);
    }
}
