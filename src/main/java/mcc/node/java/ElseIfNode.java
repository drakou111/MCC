package mcc.node.java;

import mcc.compiler.Address;
import mcc.compiler.AssemblyHelper;
import mcc.compiler.VariableList;
import mcc.node.ASTNode;

public class ElseIfNode extends ASTNode {
    private final ASTNode condition;
    private final ProgramNode thenBranch;

    public ElseIfNode(ASTNode condition, ProgramNode thenBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
    }

    @Override
    public String toString() {
       return String.format(
            "{\"type\": \"elseIf\", \"condition\": %s, \"thenBranch\": %s}",
            condition != null ? condition.toString() : "null", thenBranch != null ? thenBranch.toString() : "null"
       );
    }

    @Override
    public String compile(VariableList variableList, int optionalRegisterOffset, String optionalAddress, String secondOptionalAddress) {
        String s = "";

        String elseIfAddress = Address.getValidAddress("skip_else_if");
        String endAddress = optionalAddress;

        // Condition
        s += condition.compile(variableList, 0, "", "");
        s += AssemblyHelper.compare(1, 0);
        s += AssemblyHelper.branch("EQ", elseIfAddress);

        // Elseif code
        if (thenBranch != null) {
            variableList.appendVariableSubList();
            s += thenBranch.compile(variableList, 0, "", "");
            variableList.removeLastVariableSubList();
        }

        s += AssemblyHelper.jump(endAddress);
        s += elseIfAddress;

        return s;
    }
}
