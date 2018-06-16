package net.sohich.polycalc.representation;

public class TooBigDegreeException extends RuntimeException {
    public TooBigDegreeException() {
    }

    public TooBigDegreeException(String message) {
        super(message);
    }
}
