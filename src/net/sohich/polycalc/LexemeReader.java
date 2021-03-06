package net.sohich.polycalc;

import java.util.NoSuchElementException;

class LexemeReader {
    private final String expression;
    private int currentPosition = 0;

    LexemeReader(final String pExpression) {
        expression = pExpression;
    }

    char readSymbol() {
        char symbol = peekSymbol();
        ++currentPosition;
        return symbol;
    }

    char peekSymbol() {
        if (!hasMore()) {
            throw new NoSuchElementException("Expression string has no more symbols.");
        }

        return  expression.charAt(currentPosition);
    }

    boolean hasMore() {
        return currentPosition < expression.length();
    }

    void ignoreWhitespaces() {
        while (hasMore() && Character.isWhitespace(peekSymbol())) {
            ++currentPosition;
        }
    }
}