package mcc.node.java;

import mcc.compiler.AssemblyHelper;
import mcc.compiler.VariableList;
import mcc.node.ASTNode;

public class BooleanOperationNode extends ASTNode {
    private final ASTNode left;
    private final String operator;
    private final ASTNode right;
    private boolean invert;

    public BooleanOperationNode(ASTNode left, String operator, ASTNode right, boolean invert) {
        this.left = left;
        this.operator = operator;
        this.right = right;
        this.invert = invert;
    }

    public ASTNode getLeft() {
        return left;
    }

    public String getOperator() {
        return operator;
    }

    public ASTNode getRight() {
        return right;
    }

    public void setInvert(boolean invert) {
        this.invert = invert;
    }

    @Override
    public String toString() {
        return String.format(
            "{\"type\": \"booleanOperation\", \"left\": %s, \"operator\": \"%s\", \"right\": %s, \"invert\": %b}",
            left, operator, right, invert
        );
    }

    @Override
    public String compile(VariableList variableList, int optionalRegisterOffset, String optionalAddress, String secondOptionalAddress) {
        int leftRegister = optionalRegisterOffset + 1;
        int rightRegister = optionalRegisterOffset + 2;

        String s = "";

        s += left.compile(variableList, optionalRegisterOffset, "", ""); //bool in r1
        s += right.compile(variableList, optionalRegisterOffset + 1, "", ""); //bool in r2

        switch (operator) {
            case "||" -> s += AssemblyHelper.bitwiseOR(leftRegister, rightRegister, leftRegister);
            case "&&" -> s += AssemblyHelper.bitwiseAND(leftRegister, rightRegister, leftRegister);
            case "^^" -> s += AssemblyHelper.bitwiseXOR(leftRegister, rightRegister, leftRegister);
            case "!||" -> s += AssemblyHelper.bitwiseNOR(leftRegister, rightRegister, leftRegister);
            case "!&&" -> s += AssemblyHelper.bitwiseNAND(leftRegister, rightRegister, leftRegister);
            case "!^^" -> s += AssemblyHelper.bitwiseXNOR(leftRegister, rightRegister, leftRegister);
        }

        if (invert)
            s += AssemblyHelper.bitwiseNOT(leftRegister, leftRegister);

        return s;
    }
}
