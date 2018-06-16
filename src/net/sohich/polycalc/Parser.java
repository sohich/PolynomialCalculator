package net.sohich.polycalc;

import net.sohich.polycalc.representation.Polynomial;
import net.sohich.polycalc.representation.Term;

import java.util.ArrayList;

import static net.sohich.polycalc.LexemeType.*;

class Parser {
    private final Lexer lexer;
    private Lexeme lastLexeme;
    private Lexeme nextLexeme;
    private Lexeme postponedLexeme;
    private static final Lexeme MULTIPLY_OPERATOR = new Lexeme(MULTIPLY, "*");

    Parser(final Lexer pLexer) {
        lexer = pLexer;
    }


    String calculate() {
        fetchLexeme();
        Polynomial result = parseExpression();
        if (nextLexeme.getType() != LexemeType.EOF) {
            throw new InvalidSyntaxException("Unexpected expression containing");
        }
        return result.toString();
    }

    // here goes ability to get multiply operator implicitly
    private void fetchLexeme() {
        lastLexeme = nextLexeme;

        if (lastLexeme == null) {
            nextLexeme = lexer.getLexeme();
            return;
        }

        if (postponedLexeme != null) {
            nextLexeme = postponedLexeme;
            postponedLexeme = null;
            return;
        }

        postponedLexeme = lexer.getLexeme();

        if (isMultiplyOperatorNeeded(lastLexeme, postponedLexeme)) {
            nextLexeme = MULTIPLY_OPERATOR;
        } else {
            nextLexeme = postponedLexeme;
            postponedLexeme = null;
        }
    }

    private boolean isMultiplyOperatorNeeded(Lexeme left, Lexeme right) {
        LexemeType leftType = left.getType();
        LexemeType rightType = right.getType();

        return (leftType == POLYNOMIAL && rightType == POLYNOMIAL ||
            leftType == POLYNOMIAL && rightType == OPENING_BRACKET ||
            leftType == CLOSING_BRACKET && rightType == POLYNOMIAL ||
            leftType == CLOSING_BRACKET && rightType == OPENING_BRACKET);
    }

    private Polynomial parseExpression() {
        Polynomial result = parseTerm();
        while (nextLexeme.getType() == PLUS
                || nextLexeme.getType() == MINUS) {
            fetchLexeme();
            Polynomial rightPart = parseTerm();
            if (nextLexeme.getType() == MINUS) {
                rightPart = rightPart.applyUnaryMinus();
            }

            result = result.sum(rightPart);
        }
        return result;
    }

    private Polynomial parseTerm() {
        Polynomial result = parseFactor();
        while (nextLexeme.getType() == MULTIPLY) {
            fetchLexeme();
            result = result.multiply(parseFactor());
        }
        return result;
    }

    private Polynomial parseFactor() {
        Polynomial result = parsePower();
        if (nextLexeme.getType() == DEGREE) {
            fetchLexeme();
            Polynomial degree = parseFactor();
            if (!degree.isNumber()) {
                throw new InvalidSyntaxException("" + degree + " cannot be a power!");
            }

            result = calculatePower(result, degree.getNumber());
        }
        return result;
    }

    private Polynomial parsePower() {
        Polynomial result;
        if (nextLexeme.getType() == MINUS) {
            if (lastLexeme != null && lastLexeme.getType() != OPENING_BRACKET) {
                throw new InvalidSyntaxException("Sequence of operators.");
            }
            fetchLexeme();
            result = parsePower().applyUnaryMinus();
        }  else {
            result = parseAtom();
        }

        return result;
    }

    private Polynomial parseAtom() {
        Polynomial result;

        switch (nextLexeme.getType()) {
            case OPENING_BRACKET:
                fetchLexeme();
                result = parseExpression();

                if (nextLexeme.getType() != CLOSING_BRACKET) {
                    throw new InvalidSyntaxException("Closing bracket expected");
                }
                break;
            case POLYNOMIAL:
                result = parsePolynomialTerm(nextLexeme.getValue());
                break;
            default:
                throw new InvalidSyntaxException("Unexpected atom");
        }

        fetchLexeme();
        return result;
    }

    private Polynomial parsePolynomialTerm(String polynomial) {
        int[] degrees = new int[Term.VARIABLES_COUNT];
        int ratio = 1;

        LexemeReader reader = new LexemeReader(polynomial);

        while (reader.hasMore()) {
            char c = reader.peekSymbol();

            if (Character.isDigit(c)) {
                StringBuilder sb = new StringBuilder();
                while (reader.hasMore() && Character.isDigit(reader.peekSymbol())) {
                    sb.append(reader.readSymbol());
                }
                ratio *= Integer.parseInt(sb.toString());
            } else if (Character.isLowerCase(c)) {
                int position = reader.readSymbol() - 'a';

                int count;
                if (reader.hasMore() && reader.peekSymbol() == '^') {
                    reader.readSymbol();
                    StringBuilder sb = new StringBuilder();
                    while (reader.hasMore() && Character.isDigit(reader.peekSymbol())) {
                        sb.append(reader.readSymbol());
                    }
                    if (sb.length() == 0) {
                        throw new InvalidSyntaxException("Unexpected polynomial containing: " + polynomial);
                    }
                    count = Integer.parseInt(sb.toString());
                } else {
                    count = 1;
                }
                degrees[position] += count;
            } else {
                throw new InvalidSyntaxException("Unexpected polynomial containing: " + polynomial);
            }
        }

        ArrayList<Term> result = new ArrayList<>();
        result.add(new Term(ratio, degrees));
        return new Polynomial(result);
    }

    private Polynomial calculatePower(Polynomial polynomial, int power) {
        if (power < 0) {
            throw new InvalidSyntaxException("Power cannot be negative.");
        } else if (power == 0) {
            int[] degrees = new int[Term.VARIABLES_COUNT];
            ArrayList<Term> terms = new ArrayList<>();
            terms.add(new Term(1, degrees));
            return new Polynomial(terms);
        } else {
            return polynomial.multiply(calculatePower(polynomial, --power));
        }
    }

//    public static void main(String[] args) {
//        Parser p = new Parser (new Lexer(""));
//        Polynomial pol = p.parsePolynomialTerm("a");
//        Polynomial polB = p.parsePolynomialTerm("b");
//        Polynomial sum=  pol.sum(polB);
//        Polynomial result=  p.calculatePower(sum, 2);
//        System.out.println(result);
//    }
}