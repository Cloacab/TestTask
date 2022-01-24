package com.tsystems.javaschool.tasks.calculator;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

public class Calculator2 {

    private static final String OPERATORS = "+-/*()";
    private static final String NONBRACES = "+-/*";

    private static Stack<Double> valueStack = new Stack<>();
    private static Stack<Character> operatorStack = new Stack<>();
    private static boolean error = false;

    /**
     * Evaluate statement represented as string.
     *
     * @param statement mathematical statement containing digits, '.' (dot) as decimal mark,
     *                  parentheses, operations signs '+', '-', '*', '/'<br>
     *                  Example: <code>(1 + 38) * 4.5 - 1 / 2.</code>
     * @return string value containing result of evaluation or null if statement is invalid
     */
    public String evaluate(String statement) {
        // TODO: Implement the logic here
        LinkedList<String> symbols = inputCleaner(statement);
        return processInput(symbols);
    }

    private static LinkedList<String> inputCleaner(String statement){
        if (statement == null || statement.isEmpty()) return new LinkedList<>();
        LinkedList<String> input = new LinkedList<>();
        StringBuilder sb = new StringBuilder();
        String noSpaces = statement.replace(" ", "");
        try {
            for (int i = 0; i < noSpaces.length(); i++) {
                char c = noSpaces.charAt(i);
                boolean isNum = (c >= '0' && c <= '9');

                if (isNum) {
                    sb.append(c);
                    if (i == noSpaces.length()-1) {
                        input.add(sb.toString());
                        sb.delete(0, sb.length());
                    }
                } else if (c == '.') {
                    for (int j = 0; j < sb.length(); j++) {
                        if (sb.charAt(j) == '.') {
                            throw new Calculator.SyntaxErrorException("You can't have two decimals in a number.");
                        } else if (j == sb.length() - 1) {
                            sb.append(c);
                            j = (sb.length() + 1);
                        }
                    }
                    if (sb.length() == 0) {
                        sb.append(c);
                    }
                    if (i == noSpaces.length()-1) {
                        throw new Calculator.SyntaxErrorException("You can't end your equation with a decimal!");
                    }
                } else if (OPERATORS.indexOf(c)!= -1) {
                    if (sb.length() != 0) {
                        input.add(sb.toString());
                        sb.delete(0, sb.length());
                    }
                    sb.append(c);
                    input.add(sb.toString());
                    sb.delete(0, sb.length());
                } else {
                    throw new Calculator.SyntaxErrorException("Make sure your input only contains numbers, operators, or parantheses/brackets/braces.");
                }
            }

            int numLP = 0;
            int numRP = 0;

            for (String s : input) {
                switch (s) {
                    case "(":
                        numLP++;
                        break;
                    case ")":
                        numRP++;
                        break;
                    default: //do nothing
                        break;
                }

            }
            if (numLP != numRP) {
                throw new Calculator.SyntaxErrorException("The number of brackets, braces, or parentheses don't match up!");
            }

            int doop = 0;
            for (String awesome : input) {
                switch (awesome) {
                    case "(":
                        doop++;
                        break;
                    case ")":
                        doop--;
                        break;
                    default: //do nothing
                        break;
                }
                if (doop < 0) {
                    throw new Calculator.SyntaxErrorException("The order of your parentheses, brackets, or braces is off.\nMake sure you open a set of parenthesis/brackets/braces before you close them.");
                }
            }
            if (NONBRACES.contains(input.get(input.size() - 1))) {
                throw new Calculator.SyntaxErrorException("The input can't end in an operator");
            }
            return input;
        } catch (Calculator.SyntaxErrorException ex) {
//            System.out.println(ex);
            return input;
        }
    }

    public String processInput(LinkedList<String> input) {
        if (input.isEmpty()) return null;
        // The tokens that make up the input
//        String[] tokens = input.toArray(new String[0]);

        // Main loop - process all input tokens
        for (String nextToken : input) {
            char ch = nextToken.charAt(0);
            if (ch >= '0' && ch <= '9') {
                double value = Double.parseDouble(nextToken);
                valueStack.push(value);
            } else if (isOperator(ch)) {
                if (operatorStack.empty() || getPrecedence(ch) > getPrecedence(operatorStack.peek())) {
                    operatorStack.push(ch);
                } else {
                    while (!operatorStack.empty() && getPrecedence(ch) <= getPrecedence(operatorStack.peek())) {
                        char toProcess = operatorStack.peek();
                        operatorStack.pop();
                        processOperator(toProcess);
                    }
                    operatorStack.push(ch);
                }
            } else if (ch == '(') {
                operatorStack.push(ch);
            } else if (ch == ')') {
                while (!operatorStack.empty() && isOperator(operatorStack.peek())) {
                    char toProcess = operatorStack.peek();
                    operatorStack.pop();
                    processOperator(toProcess);
                }
                if (!operatorStack.empty() && operatorStack.peek() == '(') {
                    operatorStack.pop();
                } else {
                    error = true;
                }
            }

        }
        // Empty out the operator stack at the end of the input
        while (!operatorStack.empty() && isOperator(operatorStack.peek())) {
            char toProcess = operatorStack.peek();
            operatorStack.pop();
            processOperator(toProcess);
        }
        // Print the result if no error has been seen.
        if (!error) {
            double result = valueStack.peek();

            String res = processResult(result);

            valueStack.pop();
            if (!operatorStack.empty() || !valueStack.empty()) {
                return null;
            } else {
                return res;
            }
        }
        return null;
    }

    private String processResult(double result) {
        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.CEILING);
        Long lastInt = null;
        if (result == Math.floor(result)) {
            lastInt = Math.round(result);
        }
        String res;
        if (lastInt == null) {
            res = df.format(result);
        } else {
            res = String.valueOf(lastInt);
        }
        return res;
    }

    private boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/';
    }

    private int getPrecedence(char ch) {
        if (ch == '+' || ch == '-') {
            return 1;
        }
        if (ch == '*' || ch == '/') {
            return 2;
        }
        return 0;
    }

    private void processOperator(char t) {
        double a, b;
        if (valueStack.empty()) {
//            System.out.println("Expression error.");
            error = true;
            return;
        } else {
            b = valueStack.peek();
            valueStack.pop();
        }
        if (valueStack.empty()) {
//            System.out.println("Expression error.");
            error = true;
            return;
        } else {
            a = valueStack.peek();
            valueStack.pop();
        }
        double r = 0;
        if (t == '+') {
            r = a + b;
        } else if (t == '-') {
            r = a - b;
        } else if (t == '*') {
            r = a * b;
        } else if(t == '/') {
            r = a / b;
        } else {
//            System.out.println("Operator error.");
            error = true;
        }
        valueStack.push(r);
    }

}
