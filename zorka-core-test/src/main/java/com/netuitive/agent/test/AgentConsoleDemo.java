package com.netuitive.agent.test;

import java.io.Console;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AgentConsoleDemo {

    private static Pattern EXPRESSION_PATTERN = Pattern.compile("(\\d+)\\s*([+|\\-|*|/])\\s*(\\d+)");

    public static void main(String... args) {


        Console console = System.console();
        String help = "please enter 'int [+|-|*|/] int' to calculate or 'q' to exit\n";
        console.printf(help);
        Calculator calculator = new Calculator();
        while (true) {
            String expression = console.readLine("?: ");
            if ("q".equalsIgnoreCase(expression)) {
                break;
            }
            Matcher m = EXPRESSION_PATTERN.matcher(expression);
            if (!m.matches()) {
                console.printf(help);
            } else {

                String operator = m.group(2);
                try {
                    Integer first = Integer.valueOf(m.group(1));
                    Integer second = Integer.valueOf(m.group(3));

                    Integer result = calculator.calculate(operator, first, second);

                    console.printf("%d\n", result);
                } catch (NumberFormatException nfe) {
                    console.printf(help);
                } catch (IllegalArgumentException iae) {
                    console.printf("ERROR: " + iae.getMessage() + "\n");
                    console.printf(help);
                } catch (Exception e) {
                    console.printf("ERROR: " + e.getMessage() + "\n");
                }
            }
        }
    }
}
