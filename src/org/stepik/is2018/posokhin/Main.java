package org.stepik.is2018.posokhin;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        StringBuilder expression = new StringBuilder();
        while (sc.hasNext()) {
            expression.append(sc.next());
        }

        Lexer lexer = new Lexer(expression.toString());
        Parser parser = new Parser(lexer);

        String result;
        try {
            result = parser.calculate();
        } catch (TooBigDegreeException tbde) {
            System.out.print("ERROR: TOO BIG");
            return;
        } catch (Exception e) {
            System.out.print("ERROR: INVALID");
            return;
        }

        System.out.print(result);
    }
}
