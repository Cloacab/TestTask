package com.tsystems.javaschool.tasks.calculator;

import java.util.ArrayList;
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
    private static final Stack<Double> operandStack = new Stack<>();

    /** This is the operator stack
     *  i.e. (+-/*%^)
     */
    private static final Stack<String> operatorStack = new Stack<>();

    /** These are the possible operators */
    private static final String OPERATORS = "+-/*%^()[]{}";
    private static final String BRACES = "()[]{}";
    private static final String NONBRACES = "+-/*%^";
    //                                       +  -  /  *  %   ^  (   )   [   ]   {   }
    private static final int[] PRECEDENCE = {1, 1, 2, 2, 3, 3, -1, -1, -1, -1, -1, -1};
    /** This is an ArrayList of all the discrete
     things (operators/operands) making up an input.
     This is really just getting rid of the spaces,
     and dividing up the "stuff" into manageable pieces.
     */
    static ArrayList<String> input = new ArrayList<String>();

    /**
     * TODO: write this
     * @param postfix
     * @return
     */
    public static ArrayList inputCleaner(String postfix){
        StringBuilder sb = new StringBuilder();
        String noSpaces = postfix.replace(" ", "");
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
                            throw new InfixEvaluator.SyntaxErrorException("You can't have two decimals in a number.");
                        } else if (j == sb.length() - 1) {
                            sb.append(c);
                            j = (sb.length() + 1);
                        }
                    }
                    if (sb.length() == 0) {
                        sb.append(c);
                    }
                    if (i == noSpaces.length()-1) {
                        throw new InfixEvaluator.SyntaxErrorException("You can't end your equation with a decimal!");
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
                    throw new InfixEvaluator.SyntaxErrorException("Make sure your input only contains numbers, operators, or parantheses/brackets/braces.");
                }
            }

            int numLP = 0;
            int numRP = 0;
            int numLB = 0;
            int numRB = 0;
            int numLBr = 0;
            int numRBr = 0;

            for (int f = 0; f < input.size(); f++) {
                switch (input.get(f)) {
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
                throw new InfixEvaluator.SyntaxErrorException("The number of brackets, braces, or parentheses don't match up!");
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
                    throw new InfixEvaluator.SyntaxErrorException("The order of your parentheses, brackets, or braces is off.\nMake sure you open a set of parenthesis/brackets/braces before you close them.");
                }
            }
            if (NONBRACES.indexOf(input.get(input.size()-1)) != -1) {
                throw new InfixEvaluator.SyntaxErrorException("The input can't end in an operator");
            }
            return input;
        } catch (InfixEvaluator.SyntaxErrorException ex) {
//            System.out.println(ex);
            return input;
        }
    }

    /**Method to process operators
     * @param op The operator
     * @throws InfixEvaluator.SyntaxErrorException
     * @throws
     */
    private static void processOperator(String op) throws InfixEvaluator.SyntaxErrorException {
        if (operatorStack.empty() || op.equals("(") || op.equals("[") || op.equals("{")) {
            operatorStack.push(op);
        } else {
            //peek the operator stack and
            //let topOp be the top operator.
            String topOp = operatorStack.peek();
            if (precedence(op) > precedence(topOp)) {
                topOp = op;
                operatorStack.push(op);
            } else {
//                System.out.println(operatorStack);
//                System.out.println(operandStack);
//                System.out.println("--------------");
                //Pop all stacked operators with equal
                // or higher precedence than op.
                while (operandStack.size() >= 2 && !operatorStack.isEmpty()) {
                    double r = operandStack.pop();
                    double l = operandStack.pop();
                    String work = getNextNonBracerOperator();
//                    System.out.println("L:" + l + " R:" + r + " W:" + work);

                    doOperandWork(work, l, r);

                    if(op.equals("(") || op.equals("[") || op.equals("{")) {
                        //matching '(' popped - exit loop.
                        operandStack.push(l);
                        operandStack.push(r);
                        break;
                    }

                    if (!operatorStack.empty()) {
                        //reset topOp
                        topOp = operatorStack.peek();
                    }
                }

                //assert: Operator stack is empty or
                // current operator precedence > top of stack operator precedence.
                if(!op.equals(")") || !op.equals("}") || !op.equals("}")) {
                    operatorStack.push(op);
                }
            }
        }
    }

    /**
     * TODO: write this
     * @param expressions
     * @return
     * @throws InfixEvaluator.SyntaxErrorException
     */
    public static String infixCalculator(ArrayList<String> expressions) throws InfixEvaluator.SyntaxErrorException {
        for (String expression : expressions) {
            if (OPERATORS.indexOf(expression) == -1) {
                operandStack.push(Double.parseDouble(expression));
            } else {
                processOperator(expression);
            }
        }
        while (operandStack.size() >= 2 && !operatorStack.isEmpty()) {
//            System.out.println("--------------");
//            System.out.println(operandStack);
//            System.out.println(operatorStack);

            double r = operandStack.pop();
            double l = operandStack.pop();
            String work = getNextNonBracerOperator();
//            System.out.println("L:" + l + " R:" + r + " W:" + work);

            doOperandWork(work, l, r);
        }
        if(operandStack.isEmpty())
            return null;
        Double last = operandStack.pop();
        return String.valueOf(last);
    }

    /**
     * goes through the stack and pops off all non operatable operations until it gets to one that is in the NONBRACES String
     * @return The next operatable string
     */
    private static String getNextNonBracerOperator() {
        String work = "\0"; // \0 is null,
        while(!operatorStack.isEmpty() && !NONBRACES.contains(work))
            work = operatorStack.pop();
        return work;
    }

    /**
     *
     * @param work The operator you want to work. This really should be a character but its still a string
     * @param l Left side number
     * @param r Right side number
     * @throws InfixEvaluator.SyntaxErrorException If the operator could not be found
     */
    private static void doOperandWork(String work, double l, double r) throws InfixEvaluator.SyntaxErrorException {
        switch (work) {
            case "+": operandStack.push(l+r);
                break;
            case "-": operandStack.push(l-r);
                break;
            case "*": operandStack.push(l*r);
                break;
            case "/": operandStack.push(l/r);
                break;
            case "%": operandStack.push(l%r);
                break;
            case "^": operandStack.push(Math.pow(l, r));
                break;
            default:
                throw new InfixEvaluator.SyntaxErrorException("Invalid operand " + work);
        }
    }

    /**
     * @param op The operator
     * @return the precedence
     */
    private static int precedence(String op) {
        return PRECEDENCE[OPERATORS.indexOf(op)];
    }

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
        ArrayList test = new ArrayList();
//            Scanner f = new Scanner(System.in);

        //System.out.println("Please insert an argument: ");

        //String g = f.nextLine();
//        String g = "22/4*2.159";
//            String g = "(1+3)*3^2+2*4-1";
        test = inputCleaner(statement);

//            for (int z = 0; z < test.size(); z++) {
//                System.out.println(test.get(z));
//            }
        String res = null;
        try {
            res = infixCalculator(test);
        } catch (Exception e) {
            return null;
        }
//            System.out.println(res);

        test.clear();
        return res;
    }
}
