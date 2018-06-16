package net.sohich.polycalc;

class Lexeme {
    private final LexemeType type;
    private final String value;

    // non-null values expected
    Lexeme(final LexemeType pType, final String pValue) {
        this.type = pType;
        this.value = pValue;
    }

    public LexemeType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Lexeme lexeme = (Lexeme) o;

        if (type != lexeme.type) {
            return false;
        }
        return value.equals(lexeme.value);

    }

    @Override
    public int hashCode() {
        final int ratio = 31;
        int result = type.hashCode();
        result = ratio * result + value.hashCode();
        return result;
    }
}