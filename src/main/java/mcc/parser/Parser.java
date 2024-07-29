package mcc.parser;

import mcc.compiler.Constants;
import mcc.node.ASTNode;
import mcc.node.java.*;
import mcc.token.Token;
import mcc.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private List<Token> tokens;
    private int current;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.current = 0;
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().getType() == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().getType() == TokenType.END_OF_FILE;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    // Parsing methods
    public ProgramNode parse() {
        List<ASTNode> statements = new ArrayList<>();
        while (!isAtEnd()) {
            ASTNode statement = statement();
            if (statement != null) {
                statements.add(statement);
            }
        }
        return new ProgramNode(statements);
    }

    private ASTNode parseBuiltInFunction(int requiredParamCount) {
        String identifier = previous().getValue();
        FunctionCallNode node = parseFunctionCall(identifier, requiredParamCount, false);
        consume(TokenType.SEMICOLON, "Expect ';' after parameters.");
        return node;
    }

    private ASTNode statement() {
        if (match(TokenType.IF)) return ifStatement();
        if (match(TokenType.LET)) return assignmentStatement();
        if (match(TokenType.FOR)) return forStatement();
        if (match(TokenType.WHILE)) return whileStatement();
        if (match(TokenType.DO)) return doWhileStatement();
        if (match(TokenType.FUNCTION)) return functionDefinition();
        if (match(TokenType.EXIT)) return exitStatement();
        if (match(TokenType.BREAK)) return breakStatement();
        if (match(TokenType.CONTINUE)) return continueStatement();
        if (match(TokenType.RETURN)) return returnStatement();
        if (match(TokenType.SWITCH)) return switchStatement();
        if (match(TokenType.IDENTIFIER)) {
            String identifier = previous().getValue();
            if (check(TokenType.LEFT_PAREN)) {
                    FunctionCallNode node = parseFunctionCall(identifier, -1, false);
                    consume(TokenType.SEMICOLON, "Expect ';' after parameters.");
                    return node;
                }
            else
                return parseOperation(identifier);
        }

        if (match(TokenType.DRAW_PIXEL))
            return parseBuiltInFunction(2);
        if (match(TokenType.CLEAR_PIXEL))
            return parseBuiltInFunction(2);
        if (match(TokenType.GET_PIXEL))
            return parseBuiltInFunction(2);
        if (match(TokenType.PUSH_SCREEN))
            return parseBuiltInFunction(0);
        if (match(TokenType.CLEAR_SCREEN))
            return parseBuiltInFunction(0);
        if (match(TokenType.WRITE_CHAR))
            return parseBuiltInFunction(1);
        if (match(TokenType.WRITE_STRING)) {
            WriteStringNode node = parseWriteString();
            consume(TokenType.SEMICOLON, "Expect ';' after parameters.");
            return node;
        }
        if (match(TokenType.PUSH_CHARS))
            return parseBuiltInFunction(0);
        if (match(TokenType.CLEAR_CHARS))
            return parseBuiltInFunction(0);
        if (match(TokenType.SHOW_NUMBER))
            return parseBuiltInFunction(1);
        if (match(TokenType.CLEAR_NUMBER))
            return parseBuiltInFunction(0);
        if (match(TokenType.RANDOM))
            return parseBuiltInFunction(0);
        if (match(TokenType.GET_BUTTON_START))
            return parseBuiltInFunction(0);
        if (match(TokenType.GET_BUTTON_SELECT))
            return parseBuiltInFunction(0);
        if (match(TokenType.GET_BUTTON_A))
            return parseBuiltInFunction(0);
        if (match(TokenType.GET_BUTTON_B))
            return parseBuiltInFunction(0);
        if (match(TokenType.GET_BUTTON_UP))
            return parseBuiltInFunction(0);
        if (match(TokenType.GET_BUTTON_RIGHT))
            return parseBuiltInFunction(0);
        if (match(TokenType.GET_BUTTON_DOWN))
            return parseBuiltInFunction(0);
        if (match(TokenType.GET_BUTTON_LEFT))
            return parseBuiltInFunction(0);
        if (match(TokenType.SHOW_SIGNED))
            return parseBuiltInFunction(0);
        if (match(TokenType.SHOW_UNSIGNED))
            return parseBuiltInFunction(0);

        advance();
        return null;
    }

    private ExitNode exitStatement() {
        consume(TokenType.SEMICOLON, "Expect ';' after 'exit'.");
        return new ExitNode();
    }
    private BreakNode breakStatement() {
        consume(TokenType.SEMICOLON, "Expect ';' after 'break'.");
        return new BreakNode();
    }
    private ContinueNode continueStatement() {
        consume(TokenType.SEMICOLON, "Expect ';' after 'continue'.");
        return new ContinueNode();
    }

    private ReturnNode returnStatement() {
        ASTNode returnValue = null;

        // Check if there's a return value
        if (!check(TokenType.SEMICOLON)) {
            returnValue = parseExpressionOrFunction();
        }

        consume(TokenType.SEMICOLON, "Expect ';' after return statement.");
        return new ReturnNode(returnValue);
    }

    private ASTNode parseFunctionOrBooleanExpression() {
        if (matchBooleanFunctionCall()) {
            String identifier = previous().getValue();
            if (check(TokenType.LEFT_PAREN)) {
                return parseFunctionCall(identifier, -1, true);
            } else {
                current--; // Backtrack one token
            }
        }
        return parseBooleanExpression();
    }

    private IfNode ifStatement() {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'if'.");
        ASTNode condition = parseFunctionOrBooleanExpression(); //TODO, change that to everywhere
        consume(TokenType.RIGHT_PAREN, "Expect ')' after if condition.");

        ProgramNode thenBranch = block();
        List<ElseIfNode> elseifBranches = new ArrayList<>();
        ProgramNode elseBranch = null;

        // Handle zero or more elseif branches
        while (match(TokenType.ELSEIF)) {
            consume(TokenType.LEFT_PAREN, "Expect '(' after 'elseif'.");
            ASTNode elseifCondition = parseFunctionOrBooleanExpression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after elseif condition.");

            ProgramNode elseifBranch = block();
            elseifBranches.add(new ElseIfNode(elseifCondition, elseifBranch));
        }

        // Handle optional else branch
        if (match(TokenType.ELSE)) {
            elseBranch = block();
        }

        return new IfNode(condition, thenBranch, elseifBranches, elseBranch);
    }

    private WhileNode whileStatement() {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'while'.");
        ASTNode condition = parseFunctionOrBooleanExpression();
        consume(TokenType.RIGHT_PAREN, "Expect ')' after while condition.");

        ProgramNode body = block();
        return new WhileNode(condition, body);
    }

    private DoWhileNode doWhileStatement() {
        ProgramNode body = block();
        consume(TokenType.WHILE, "Expect 'while' after do block.");
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'while'.");
        ASTNode condition = parseFunctionOrBooleanExpression();
        consume(TokenType.RIGHT_PAREN, "Expect ')' after while condition.");
        consume(TokenType.SEMICOLON, "Expect ';' after while condition.");
        return new DoWhileNode(body, condition);
    }

    private ForNode forStatement() {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'for'.");
        consume(TokenType.LET, "Expect assignation in for loop.");
        AssignmentNode initialization = assignmentStatement();
        ASTNode condition = parseFunctionOrBooleanExpression();
        consume(TokenType.SEMICOLON, "Expect ';' after condition.");
        OperationNode increment = parseOperation(null);
        consume(TokenType.RIGHT_PAREN, "Expect ')' after for statement.");
        ProgramNode body = block();
        return new ForNode(initialization, condition, increment, body);
    }

    private OperationNode parseOperation(String identifier) {
        String leftValue = identifier;
        if (leftValue == null)
            leftValue = parseVariable();

        // Parse the operator
        String operator;
        if (match(TokenType.LEFT_SHIFT_ASSIGN)) {
            operator = "<<=";
        } else if (match(TokenType.RIGHT_SHIFT_ASSIGN)) {
            operator = ">>=";
        } else if (match(TokenType.PLUS_ASSIGN)) {
            operator = "+=";
        } else if (match(TokenType.MINUS_ASSIGN)) {
            operator = "-=";
        } else if (match(TokenType.BITWISE_AND_ASSIGN)) {
            operator = "&=";
        } else if (match(TokenType.BITWISE_OR_ASSIGN)) {
            operator = "|=";
        } else if (match(TokenType.BITWISE_XOR_ASSIGN)) {
            operator = "^=";
        } else if (match(TokenType.BITWISE_NOR_ASSIGN)) {
            operator = "!|=";
        } else if (match(TokenType.BITWISE_XNOR_ASSIGN)) {
            operator = "!^=";
        } else if (match(TokenType.BITWISE_NAND_ASSIGN)) {
            operator = "!&=";
        } else if (match(TokenType.MODULO_ASSIGN)) {
            operator = "%=";
        } else if (match(TokenType.INCREMENT)) {
            operator = "++";
            return new OperationNode(leftValue, operator, null);
        } else if (match(TokenType.DECREMENT)) {
            operator = "--";
            return new OperationNode(leftValue, operator, null);
        } else if (match(TokenType.ASSIGN)) {
            operator = "=";
        } else {
            throw error(peek(), "Expected an operation (for example, '++', '+=' or '=').");
        }

        ASTNode rightValue = parseExpressionOrFunction();

        return new OperationNode(leftValue, operator, rightValue);
    }

    private ProgramNode block() {
        List<ASTNode> statements = new ArrayList<>();
        consume(TokenType.LEFT_BRACE, "Expect '{' before block.");
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            ASTNode statement = statement();
            if (statement != null) {
                statements.add(statement);
            }
        }
        consume(TokenType.RIGHT_BRACE, "Expect '}' after block.");
        return new ProgramNode(statements);
    }

    private FunctionNode functionDefinition() {
        String functionName = consume(TokenType.IDENTIFIER, "Expect function name.").getValue();
        consume(TokenType.LEFT_PAREN, "Expect '(' after function name.");

        List<String> parameters = new ArrayList<>();
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                parameters.add(consume(TokenType.IDENTIFIER, "Expect parameter name.").getValue());
            } while (match(TokenType.COMMA));
        }

        consume(TokenType.RIGHT_PAREN, "Expect ')' after parameters.");
        ProgramNode body = block();

        return new FunctionNode(functionName, parameters, body);
    }

    private FunctionCallNode parseFunctionCall(String functionName, int requiredParamCount, boolean invert) {
        consume(TokenType.LEFT_PAREN, "Expect '(' after function name.");

        List<ASTNode> parameters = new ArrayList<>();
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                parameters.add(parseExpressionOrFunction());
            } while (match(TokenType.COMMA));
        }

        consume(TokenType.RIGHT_PAREN, "Expect ')' after parameters.");


        if (requiredParamCount != -1 && parameters.size() != requiredParamCount)
            throw new ParseException("Expected " + requiredParamCount + " parameters, but got " + parameters.size() + ".");

        return new FunctionCallNode(functionName, parameters, false);
    }

    private ASTNode parseBooleanExpression() {


        ASTNode node = parsePrimaryBooleanExpression();

        while (match(TokenType.AND, TokenType.OR, TokenType.XOR, TokenType.NOR, TokenType.NAND, TokenType.XNOR)) {
            String operator = previous().getValue();
            ASTNode right = parsePrimaryBooleanExpression();

            node = new BooleanOperationNode(node, operator, right, false);
        }

        return node;
    }

    private ASTNode parsePrimaryBooleanExpression() {
        if (match(TokenType.NOT)) {
            consume(TokenType.LEFT_PAREN, "Expect '(' after '!'.");
            ASTNode node = parseBooleanExpression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after condition.");

            if (node instanceof ConditionNode)
                ((ConditionNode) node).setInvert(true);
            if (node instanceof BooleanOperationNode)
                ((BooleanOperationNode) node).setInvert(true);

            return node;
        } else if (match(TokenType.LEFT_PAREN)) {
            ASTNode node = parseBooleanExpression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after condition.");
            return node;
        } else {
            ASTNode leftValue = parseExpressionOrFunction();
            String operator = parseOperator();
            ASTNode rightValue = parseExpressionOrFunction();
            return new ConditionNode(leftValue, operator, rightValue, false);
        }
    }

    private ASTNode parseExpressionOrFunction() {
        if (matchFunctionCall()) {
            String identifier = previous().getValue();
            if (check(TokenType.LEFT_PAREN)) {
                return parseFunctionCall(identifier, -1, true);
            } else {
                current--; // Backtrack one token
            }
        }
        return parseExpression();
    }

    private AssignmentNode assignmentStatement() {
        Token nameToken = consume(TokenType.IDENTIFIER, "Expect variable name after 'let'.");
        String variableName = nameToken.getValue();

        // Check if there's an assignment (eg: let a = 1;)
        if (match(TokenType.ASSIGN)) {
            ASTNode value = parseExpressionOrFunction();
            consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
            return new AssignmentNode(variableName, value);
        }

        // No assignment, handle as a variable declaration (eg: let a;)
        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
        return new AssignmentNode(variableName, null);
    }

    private boolean matchFunctionCall() {
        return match(
            TokenType.IDENTIFIER,
            TokenType.GET_PIXEL,
            TokenType.RANDOM,
            TokenType.GET_BUTTON_START,
            TokenType.GET_BUTTON_SELECT,
            TokenType.GET_BUTTON_A,
            TokenType.GET_BUTTON_B,
            TokenType.GET_BUTTON_UP,
            TokenType.GET_BUTTON_RIGHT,
            TokenType.GET_BUTTON_DOWN,
            TokenType.GET_BUTTON_LEFT
        );
    }

    private boolean matchBooleanFunctionCall() {
        return match(
                TokenType.GET_BUTTON_START,
                TokenType.GET_BUTTON_SELECT,
                TokenType.GET_BUTTON_A,
                TokenType.GET_BUTTON_B,
                TokenType.GET_BUTTON_UP,
                TokenType.GET_BUTTON_RIGHT,
                TokenType.GET_BUTTON_DOWN,
                TokenType.GET_BUTTON_LEFT
        );
    }

    private String parseVariable() {
        if (match(TokenType.IDENTIFIER)) {
            return previous().getValue();
        }
        throw error(peek(), "Expected a variable.");
    }

    private String parseOperator() {
        if (match(TokenType.EQUAL, TokenType.NOT_EQUAL, TokenType.LESS_THAN, TokenType.GREATER_THAN,
                TokenType.LESS_EQUAL, TokenType.GREATER_EQUAL)) {
            return previous().getValue();
        }

        throw error(peek(), "Expected an operator.");
    }

    private ASTNode parseExpression() {
        ASTNode node = parsePrimary();

        while (match(TokenType.LEFT_SHIFT, TokenType.RIGHT_SHIFT, TokenType.PLUS, TokenType.MINUS, TokenType.BITWISE_AND, TokenType.BITWISE_OR, TokenType.BITWISE_XOR, TokenType.BITWISE_NAND, TokenType.BITWISE_NOR, TokenType.BITWISE_XNOR, TokenType.MODULO)) {
            String operator = previous().getValue();
            ASTNode right = parsePrimary();
            node = new ExpressionNode(node, operator, right);
        }

        return node;
    }

    private ASTNode parsePrimary() {

        boolean invert = match(TokenType.BITWISE_NOT);

        // Check if we have a number
        if (match(TokenType.NUMBER)) {
            String value = previous().getValue();
            return new ValueNode(value, true, invert);
        }

        // Check if we have a char
        if (match(TokenType.CHAR)) {
            String value = previous().getValue();
            value = value.substring(1, value.length() - 1);

            if (value.length() != 1)
                return new ValueNode("0", true, invert);

            return new ValueNode((int)value.charAt(0) + "", true, invert);
        }

        // Check for function calls and variables
        if (match(TokenType.IDENTIFIER)) {
            String functionName = previous().getValue();

            if (match(TokenType.LEFT_PAREN)) {
                current--;
                ASTNode node = parseFunctionCall(functionName, -1, invert);
                return node;
            } else {
                // If no parentheses, treat as a variable
                return new ValueNode(functionName, false, invert); // Treat as variable
            }
        }

        // Check for nested expressions
        if (match(TokenType.LEFT_PAREN)) {
            ASTNode expression = parseExpression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return expression;
        }

        throw error(peek(), "Expected primary expression.");
    }

    private WriteStringNode parseWriteString() {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'writeString'.");
        List<ASTNode> parameters = new ArrayList<>();

        // Check if the next token is a STRING
        if (match(TokenType.STRING)) {
            String stringValue = previous().getValue();
            if (stringValue.length() - 2 > Constants.CHARACTER_COUNT) {
                throw error(peek(), "Can only write strings that are at most " + Constants.CHARACTER_COUNT + " characters long.");
            }
            parameters.add(new ValueNode(stringValue, false, false)); // Assuming 'false' indicates it's a string
        } else {
            // Parse as a list of chars or expressions
            while (!isAtEnd() && !check(TokenType.RIGHT_PAREN)) {
                parameters.add(parseExpressionOrFunction());
                if (!match(TokenType.COMMA)) break; // Assuming parameters are separated by commas
            }
        }

        consume(TokenType.RIGHT_PAREN, "Expect ')' after parameters.");
        return new WriteStringNode(parameters);
    }

    private SwitchNode switchStatement() {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'switch'.");
        ASTNode switchExpression = parseExpressionOrFunction();
        consume(TokenType.RIGHT_PAREN, "Expect ')' after switch expression.");

        consume(TokenType.LEFT_BRACE, "Expect '{' after switch expression.");
        List<CaseNode> cases = new ArrayList<>();
        ProgramNode defaultCase = null;

        // Handle cases
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            if (match(TokenType.CASE)) {
                ASTNode caseExpression = parseExpressionOrFunction();
                consume(TokenType.COLON, "Expect ':' after case expression.");
                ProgramNode body = block();
                cases.add(new CaseNode(caseExpression, body));
            } else if (match(TokenType.DEFAULT)) {
                consume(TokenType.COLON, "Expect ':' after default.");
                defaultCase = block();
            } else {
                break; // Exit the loop if neither case nor default
            }
        }

        consume(TokenType.RIGHT_BRACE, "Expect '}' after switch statement.");

        return new SwitchNode(switchExpression, cases, defaultCase);
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    private ParseException error(Token token, String message) {
        return new ParseException(message + " (found at line " + token.getLine() + " near '" + token.getValue() + "')");
    }

    static class ParseException extends RuntimeException {
        ParseException(String message) {
            super("Syntax error: " + message);
        }
    }
}