package com.netuitive.agent.test;

public class Calculator {

    public Integer calculate(String operator, Integer first, Integer second) {
        switch(operator) {
            case "+" : {
                return add(first, second);
            }
            case "-" : {
                return minus(first, second);
            }
            case "*" : {
                return multiply(first, second);
            }
            case "/" : {
                return divide(first, second);
            }
            default : {
                throw new IllegalArgumentException("'" + operator + "' is not supported, use one of [+|-|*|/] operators");
            }
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
