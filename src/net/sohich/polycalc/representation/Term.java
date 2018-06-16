package net.sohich.polycalc.representation;

import java.util.Arrays;

public class Term implements Comparable<Term> {
    public static final int VARIABLES_COUNT = 26;
    private static final int MAX_DEGREE = 32;
    private final int ratio;
    private final int[] degrees;

    public Term(int ratio, final int[] degrees) {
        this.ratio = ratio;

        if (degrees.length != VARIABLES_COUNT) {
            throw new IllegalArgumentException("Incorrect variables' degrees count.");
        }

        this.degrees = degrees.clone();

        checkDegree();
    }

    public Term multiply(final Term other) {
        int[] newDegrees = new int[VARIABLES_COUNT];
        for (int i = 0; i < VARIABLES_COUNT; ++i) {
            newDegrees[i] = degrees[i] + other.degrees[i];
        }

        return new Term(ratio * other.ratio, newDegrees);
    }

    @Override
    public int compareTo(final Term other) {
        for (int i = 0; i < VARIABLES_COUNT; ++i) {
            if (degrees[i] != other.degrees[i]) {
                return degrees[i] - other.degrees[i];
            }
        }

        return 0;
    }

    public boolean isSimilar(final Term other) {
        return Arrays.equals(degrees, other.degrees);
    }

    public Term reductSimilar(final Term other) {
        if (!isSimilar(other)) {
            throw new IllegalArgumentException
                    ("Term " + this + " is not similar to " + other + ". Unable to make a reduction");
        }

        return new Term(ratio + other.ratio, degrees);
    }

    public Term applyUnaryMinus() {
        return new Term(-ratio, degrees);
    }

    public int getRatio() {
        return ratio;
    }

    public int[] getDegrees() {
        return degrees;
    }

    public boolean isNumber() {
        for (int degree: degrees) {
            if (degree != 0) {
                return false;
            }
        }

        return true;
    }

    private void checkDegree() {
        int result = 0;
        for (int degree: degrees) {
            result += degree;
        }

        if (result > MAX_DEGREE) {
            throw new TooBigDegreeException("Got too much total degree in term " + this + ".");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (ratio == -1 && !isNumber()) {
            sb.append('-');
        } else if (ratio != 1 || isNumber()) {
            sb.append(ratio);
        }
        for (int i = 0; i < VARIABLES_COUNT; ++i) {
            if (degrees[i] > 0) {
                sb.append((char)('a' + i));

                if (degrees[i] > 1) {
                    sb.append('^');
                    sb.append(degrees[i]);
                }
            }
        }

        return sb.toString();
    }
}
