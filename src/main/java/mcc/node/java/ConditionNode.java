package mcc.node.java;

import mcc.compiler.AssemblyHelper;
import mcc.compiler.VariableList;
import mcc.node.ASTNode;

public class ConditionNode extends ASTNode {
    private final ASTNode leftValue;
    private final String operator;
    private final ASTNode rightValue;
    private boolean invert; // New field

    public ConditionNode(ASTNode leftValue, String operator, ASTNode rightValue, boolean invert) {
        this.leftValue = leftValue;
        this.operator = operator;
        this.rightValue = rightValue;
        this.invert = invert;
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

    public boolean isInvert() {
        return invert;
    }

    public void setInvert(boolean invert) {
        this.invert = invert;
    }

    @Override
    public String toString() {
        return String.format(
            "{\"type\": \"condition\", \"leftValue\": %s, \"operator\": \"%s\", \"rightValue\": %s, \"invert\": %b}",
                leftValue, operator, rightValue, invert
        );
    }

    @Override
    public String compile(VariableList variableList, int optionalRegisterOffset, String optionalAddress, String secondOptionalAddress) {
        int leftRegister = optionalRegisterOffset + 1;
        int rightRegister = optionalRegisterOffset + 2;

        String s = "";
        s += leftValue.compile(variableList, optionalRegisterOffset, "", ""); //left in r1
        s += rightValue.compile(variableList, optionalRegisterOffset + 1, "", ""); //right in r2

        switch (operator) {
            case "==" -> s += AssemblyHelper.isEqualTo(leftRegister,  rightRegister, leftRegister);
            case "!=" -> s += AssemblyHelper.isNotEqualTo(leftRegister,  rightRegister, leftRegister);
            case ">" -> s += AssemblyHelper.isGreaterThan(leftRegister,  rightRegister, leftRegister);
            case "<" -> s += AssemblyHelper.isLessThan(leftRegister,  rightRegister, leftRegister);
            case ">=" -> s += AssemblyHelper.isGreaterThanOrEqualTo(leftRegister,  rightRegister, leftRegister);
            case "<=" -> s += AssemblyHelper.isLessThanOrEqualTo(leftRegister,  rightRegister, leftRegister);
        }

        if (invert)
            s += AssemblyHelper.bitwiseNOT(leftRegister, leftRegister);

        return s;
    }
}