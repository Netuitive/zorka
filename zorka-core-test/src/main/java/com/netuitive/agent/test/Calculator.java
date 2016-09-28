package com.netuitive.agent.test;

public class Calculator {

    public Integer calculate(String operator, Integer first, Integer second) {

        if (operator.equals("+")) {
            return add(first, second);
        } else if (operator.equals("-")) {
            return minus(first, second);
        } else if (operator.equals("*")) {
            return multiply(first, second);
        } else if (operator.equals("/")) {
            return divide(first, second);
        } else {
            throw new IllegalArgumentException("'" + operator + "' is not supported, use one of [+|-|*|/] operators");
        }
    }

    private Integer add(Integer first, Integer second) {
        return first + second;
    }

    private Integer minus(Integer first, Integer second) {
        return first - second;
    }

    private Integer multiply(Integer first, Integer second) {
        return first * second;
    }

    private Integer divide(Integer first, Integer second) {
        return first / second;
    }
}
