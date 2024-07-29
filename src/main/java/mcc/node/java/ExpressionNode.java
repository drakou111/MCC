package mcc.node.java;

import mcc.compiler.AssemblyHelper;
import mcc.compiler.VariableList;
import mcc.node.ASTNode;

public class ExpressionNode extends ASTNode {
    private final ASTNode leftValue;
    private final String operator;
    private final ASTNode rightValue;

    public ExpressionNode(ASTNode leftValue, String operator, ASTNode rightValue) {
        this.leftValue = leftValue;
        this.operator = operator;
        this.rightValue = rightValue;
    }

    public ASTNode getLeftValue() {
        return leftValue;
    }

    public String getOperator() {
        return operator;
    }

    public ASTNode getRightValue() {
        return rightValue;
    }

    @Override
    public String toString() {
        return String.format("{\"type\": \"expression\", \"leftValue\": %s, \"operator\": \"%s\", \"rightValue\": %s}",
                leftValue, operator, rightValue);
    }

    @Override
    public String compile(VariableList variableList, int optionalRegisterOffset, String optionalAddress, String secondOptionalAddress) {

        String s = "";

        int leftRegister = optionalRegisterOffset + 1;
        int rightRegister = optionalRegisterOffset + 2;

        s += getExpressionAsNumberAssembly(variableList, leftValue, leftRegister);
        s += getExpressionAsNumberAssembly(variableList, rightValue, rightRegister);

        s += AssemblyHelper.operation(rightValue, leftRegister, rightRegister, leftRegister, operator);

        return s;
    }

    private String getExpressionAsNumberAssembly(VariableList variableList, ASTNode node, int register) {
        if (node instanceof ValueNode valueNode)
            return getValueAsNumberAssembly(variableList, valueNode, register);
        else if (node instanceof ExpressionNode expressionNode)
            return expressionNode.compile(variableList, register - 1, "", "");
        else if (node instanceof FunctionCallNode functionCallNode)
            return functionCallNode.compile(variableList, register - 1, "", "");
        throw new IllegalArgumentException("Error while compiling. An expression was formatted wrongly.");
    }

    private String getValueAsNumberAssembly(VariableList variableList, ValueNode node, int register) {
        if (node.isNumber()) {
            return AssemblyHelper.loadImmediate(register, Integer.parseInt(node.getValue()));
        } else {
            Integer memoryIndex = variableList.getValueOf(node.getValue());
            if (memoryIndex != null) {
                String s = AssemblyHelper.loadImmediate(register, memoryIndex);
                s += AssemblyHelper.loadMemory(register, register, 0);
                return s;
            } else {
                throw new IllegalArgumentException("Error while compiling. Tried to use an unassigned variable in an expression.");
            }
        }
    }
}
