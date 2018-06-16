package net.sohich.polycalc;

public class InvalidSyntaxException extends RuntimeException {
    public InvalidSyntaxException() {
    }

    public InvalidSyntaxException(String message) {
        super(message);
    }
}
