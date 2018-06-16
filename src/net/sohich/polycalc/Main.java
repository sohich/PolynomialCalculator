package net.sohich.polycalc;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter expression:");
        String expression = sc.next();

        Lexer lexer = new Lexer(expression);
        Parser parser = new Parser(lexer);

        String result;
        try {
            result = parser.calculate();
        } catch (Exception e) {
            System.err.print(e.getMessage() + " Finishing.");
            return;
        }

        System.out.print(result);
    }
}
