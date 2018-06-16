package org.stepik.is2018.posokhin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;

public class Polynomial {
    private final ArrayList<Term> terms;

    public Polynomial(final ArrayList<Term> inputTerms) {
        terms = toCanonicForm(inputTerms);
    }

    private static ArrayList<Term> toCanonicForm(final ArrayList<Term> inputTerms) {
        ArrayList<Term> result = new ArrayList<>();

        while (!inputTerms.isEmpty()) {
            Term term = inputTerms.get(0);
            inputTerms.remove(0);
            if (term.getRatio() == 0) {
                continue;
            }

            ListIterator<Term> it = inputTerms.listIterator();
            while (it.hasNext()) {
                Term t = it.next();

                if (term.isSimilar(t)) {
                    term = term.reductSimilar(t);
                    it.remove();
                }
            }

            result.add(term);
        }

        Collections.sort(result);
        Collections.reverse(result);
        return result;
    }

    public Polynomial multiply(final Polynomial other) {
        ArrayList<Term> result = new ArrayList<>();

        for (Term a: terms) {
            for (Term b: other.terms) {
                result.add(a.multiply(b));
            }
        }

        return new Polynomial(toCanonicForm(result));
    }

    public Polynomial sum(final Polynomial other) {
        ArrayList<Term> result = new ArrayList<>();

        result.addAll(terms);
        result.addAll(other.terms);

        return new Polynomial(toCanonicForm(result));
    }

    public Polynomial applyUnaryMinus() {
        ArrayList<Term> result = new ArrayList<>();

        for (Term t: terms) {
            result.add(t.applyUnaryMinus());
        }

        return new Polynomial(result);
    }

    public boolean isNumber() {
        return  terms.isEmpty() ||
                terms.size() == 1 && terms.get(0).isNumber();
    }

    public int getNumber() {
        if (!isNumber()) {
            throw new InvalidSyntaxException("Term" + this + "is not a number.");
        } else if (terms.isEmpty()) {
            return 0;
        } else {
            return terms.get(0).getRatio();
        }
    }

    @Override
    public String toString() {
        if (terms.isEmpty()) {
            return "0";
        }

        StringBuilder sb = new StringBuilder();
        Term term = terms.get(0);
        sb.append(term);
        terms.remove(0);

        for (Term t: terms) {
            if (t.getRatio() > 0) {
                sb.append('+');
            }

            sb.append(t);
        }

        return sb.toString();
    }

}
