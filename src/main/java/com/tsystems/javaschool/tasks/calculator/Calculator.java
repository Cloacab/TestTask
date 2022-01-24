package com.tsystems.javaschool.tasks.calculator;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

public class Calculator {

    public static class SyntaxErrorException extends Exception {
        /** Construct a SyntaxErrorException with the specified message.
         @param message The message
         */
        SyntaxErrorException(String message) {
            super(message);
        }
    }
    /** This is the stack of operands:
     i.e. (doubles/parentheses/brackets/curly braces)
     */
    private static Stack<Double> operandStack = new Stack<Double>();

    /** This is the operator stack
     *  i.e. (+-/*%^)
     */
    private static Stack<String> operatorStack = new Stack<String>();

    /** These are the possible operators */
    private static final String OPERATORS = "+-/*%^()[]{}";
    private static final String BRACES = "()[]{}";
    private static final String NONBRACES = "+-/*%^";
    private static final int[] PRECEDENCE = {1, 1, 2, 2, 2, -1, -1, -1, -1, -1, -1};
    /** This is an ArrayList of all the discrete
     things (operators/operands) making up an input.
     This is really just getting rid of the spaces,
     and dividing up the "stuff" into manageable pieces.
     */
    static ArrayList<String> input = new ArrayList<String>();

    public static ArrayList inputCleaner(String postfix){
        StringBuilder poop = new StringBuilder();
        String doody = postfix.replace(" ", "");
        try {
            for (int i = 0; i < doody.length(); i++) {
                char c = doody.charAt(i);
                boolean isNum = (c >= '0' && c <= '9');

                if (isNum) {
                    poop.append(c);
                    if (i == doody.length()-1) {
                        input.add(poop.toString());
                        poop.delete(0, poop.length());
                    }
                } else if (c == '.') {
                    for (int j = 0; j < poop.length(); j++) {
                        if (poop.charAt(j) == '.') {
                            throw new SyntaxErrorException("You can't have two decimals in a number.");
                        } else if (j == poop.length() - 1) {
                            poop.append(c);
                            j = (poop.length() + 1);
                        }
                    }
                    if (poop.length() == 0) {
                        poop.append(c);
                    }
                    if (i == doody.length()-1) {
                        throw new SyntaxErrorException("You can't end your equation with a decimal!");
                    }
                } else if (OPERATORS.indexOf(c)!= -1) {
                    if (poop.length() != 0) {
                        input.add(poop.toString());
                        poop.delete(0, poop.length());
                    }
                    poop.append(c);
                    input.add(poop.toString());
                    poop.delete(0, poop.length());
                } else {
                    throw new SyntaxErrorException("Make sure your input only contains numbers, operators, or parantheses/brackets/braces.");
                }
            }

            int numLP = 0;
            int numRP = 0;
            int numLB = 0;
            int numRB = 0;
            int numLBr = 0;
            int numRBr = 0;

            for (int f = 0; f < input.size(); f++) {
                String trololol = input.get(f);

                switch (trololol) {
                    case "(": numLP++;
                        break;
                    case "[": numLB++;
                        break;
                    case "{": numLBr++;
                        break;
                    case ")": numRP++;
                        break;
                    case "]": numRB++;
                        break;
                    case "}": numRBr++;
                        break;
                    default: //do nothing
                        break;
                }

            }
            if (numLP != numRP || numLB != numRB || numLBr != numRBr) {
                throw new SyntaxErrorException("The number of brackets, braces, or parentheses don't match up!");
            }

            int doop = 0;
            int scoop = 0;
            int foop = 0;
            for (int f = 0; f < input.size(); f++) {
                String awesome = input.get(f);
                switch (awesome) {
                    case "(": doop++;
                        break;
                    case "[": scoop++;
                        break;
                    case "{": foop++;
                        break;
                    case ")": doop--;
                        break;
                    case "]": scoop--;
                        break;
                    case "}": foop--;
                        break;
                    default: //do nothing
                        break;
                }
                if (doop < 0 || scoop < 0 || foop < 0) {
                    throw new SyntaxErrorException("The order of your parentheses, brackets, or braces is off.\nMake sure you open a set of parenthesis/brackets/braces before you close them.");
                }
            }
            if (NONBRACES.indexOf(input.get(input.size()-1)) != -1) {
                throw new SyntaxErrorException("The input can't end in an operator");
            }
            return input;
        } catch (SyntaxErrorException ex) {
            System.out.println(ex);
            return input;
        }
    }

    /**Method to process operators
     * @param op The operator
     * @throws EmptyStackException
     */

//public class Calculator {

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
//        ArrayList<String> test = new ArrayList<>();
//            Scanner f = new Scanner(System.in);

        //System.out.println("Please insert an argument: ");

        //String g = f.nextLine();
//        String g = "22/4*2.159";
//        String g = "(1+3)*3^2+2*4-1";
//        test = inputCleaner(statement);

//            for (int z = 0; z < test.size(); z++) {
//                System.out.println(test.get(z));
//            }
//        String res = null;
//        try {
//            res = infixCalculator(test);
//        } catch (Exception e) {
//            return null;
//        }
//            System.out.println(res);
//
//        test.clear();

        String res = String.valueOf(calculate(statement));
        System.out.println(res);
        return res;
    }

    private static double calculate(String expresion) {
        double result = 0;
        String operation = "";
        List<Character> openBrackets = new ArrayList<Character>();
        List<Character> closeBrackets = new ArrayList<Character>();
        StringBuilder innerInput = new StringBuilder();

        for (int i = 0; i < expresion.length(); i++) {
            char inputChar = expresion.charAt(i);
            if(openBrackets.isEmpty()){
                if (Character.isDigit(inputChar)) {

                    if (operation == "" && result == 0) {
                        result = Character.digit(inputChar, Character.MAX_RADIX);
                        continue;
                    } else if (operation != "") {
                        result = calculateWithOperation(operation, Character.digit(inputChar, Character.MAX_RADIX), result);
                        continue;
                    }
                }
                // if the input is operation then we must set the operation in order
                // to be taken into consideration again ..
                if (inputChar == '+' || inputChar == '-' || inputChar == '*' || inputChar == '/') {
                    operation = Character.toString(inputChar);
                    continue;
                }
            }
            if (inputChar == '(') {
                // set operation to be empty in order to calculate the
                // operations inside the brackets ..
                openBrackets.add(inputChar);
                continue;
            }
            if(inputChar ==')'){
                closeBrackets.add(inputChar);
                if(openBrackets.size() == closeBrackets.size()){
                    openBrackets.remove((Character)'(');
                    closeBrackets.remove((Character)')');
                    double evalResult =  calculate(innerInput.toString());
                    result = calculateWithOperation(operation,evalResult,result);
                    innerInput.setLength(0);
                }
                if(openBrackets.size()> closeBrackets.size()){
                    continue;
                }
                //break;
            }
            else{
                innerInput.append(inputChar);
            }
        }
        return result;
    }

    /**
     * this method to calculate the simple expressions
     * @param operation
     * @param inputChar
     * @param output
     * @return
     */
    private static double calculateWithOperation(String operation, double inputChar, double output) {
        switch (operation) {
            case "+":
                output = output + inputChar;
                break;

            case "-":
                output = output - inputChar;
                break;

            case "*":
                output = output * inputChar;
                break;

            case "/":
                output = output / inputChar;
                break;

            default:
                break;
        }
        return output;
    }
}
