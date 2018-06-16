package org.stepik.is2018.posokhin;

import java.util.HashMap;
import java.util.HashSet;

import static org.stepik.is2018.posokhin.LexemeType.*;


public class Lexer {
    private final LexemeReader reader;
    private final HashSet<LexemeExtractor> extractors;

    Lexer(final String expression) {
        reader = new LexemeReader(expression);

        extractors = new HashSet<>();

        extractors.add(new SimpleSymbolExtractor());
        extractors.add(new PolynomialLexemeExtractor());
        extractors.add(new EndOfStringLexemeExtractor());
    }

    Lexeme getLexeme() {
        Lexeme lexeme = null;
        reader.ignoreWhitespaces();
        for (LexemeExtractor e : extractors) {
            lexeme = e.tryGetLexeme();
            if (lexeme != null) {
                break;
            }
        }

        if (lexeme == null) {
            throw new InvalidSyntaxException("Lexeme unrecognized: " + reader.peekSymbol());
        }

        return lexeme;
    }


    private interface LexemeExtractor {
        Lexeme tryGetLexeme();
    }

    private class SimpleSymbolExtractor implements LexemeExtractor {
        private static final char ADD_SYMBOL = '+';
        private static final char SUBSTRACT_SYMBOL = '-';
        private static final char POWER_SYMBOL = '^';
        private static final char LEFT_BRACKET = '(';
        private static final char RIGHT_BRACKET = ')';

        private final HashMap<Character, LexemeType> symbols;

        {
            symbols = new HashMap<>();

            symbols.put(ADD_SYMBOL, PLUS);
            symbols.put(SUBSTRACT_SYMBOL, MINUS);
            symbols.put(POWER_SYMBOL, DEGREE);
            symbols.put(LEFT_BRACKET, OPENING_BRACKET);
            symbols.put(RIGHT_BRACKET, CLOSING_BRACKET);
        }

        @Override
        public Lexeme tryGetLexeme() {
            if (!reader.hasMore()) {
                return null;
            }

            char symbol = reader.peekSymbol();
            LexemeType type = symbols.get(symbol);

            if (type == null) {
                return null;
            }

            return new Lexeme(type, "" + reader.readSymbol());
        }
    }

    private class PolynomialLexemeExtractor implements  LexemeExtractor {
        @Override
        public Lexeme tryGetLexeme() {
            if (!reader.hasMore() || !isValid(reader.peekSymbol())) {
                return null;
            }

            StringBuilder sb = new StringBuilder();
            while (reader.hasMore() && isValid(reader.peekSymbol())) {
                sb.append(reader.readSymbol());
            }

            return new Lexeme(POLYNOMIAL, sb.toString());
        }

        private boolean isValid(char c) {
            return Character.isLowerCase(c) ||
                   Character.isDigit(c) ||
                   c == '^';
        }
    }

    private class EndOfStringLexemeExtractor implements LexemeExtractor {
        @Override
        public Lexeme tryGetLexeme() {
            if (reader.hasMore()) {
                return null;
            } else {
                return new Lexeme(EOF, null);
            }
        }
    }
}
