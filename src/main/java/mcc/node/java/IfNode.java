package mcc.node.java;

import mcc.compiler.Address;
import mcc.compiler.AssemblyHelper;
import mcc.compiler.VariableList;
import mcc.node.ASTNode;

import java.util.List;

public class IfNode extends ASTNode {
    ASTNode condition;
    ProgramNode thenBranch;
    List<ElseIfNode> elseIfNodes;
    ProgramNode elseBranch;

    public IfNode(ASTNode condition, ProgramNode thenBranch, List<ElseIfNode> elseIfNodes, ProgramNode elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseIfNodes = elseIfNodes;
        this.elseBranch = elseBranch;
    }

    @Override
    public String toString() {
        String s = String.format(
            "{\"type\": \"if\", \"condition\": %s, \"thenBranch\": %s, \"elseIfs\": [",
            condition != null ? condition.toString() : "null", thenBranch != null ? thenBranch.toString() : "null"
        );

        for (int i = 0; i < elseIfNodes.size(); i++) {
            s += elseIfNodes.get(i).toString();
            if (i < elseIfNodes.size() - 1) {
                s += ", ";
            }
        }

        s += String.format(
            "], \"elseBranch\": %s}",
            elseBranch != null ? elseBranch.toString() : "null"
        );

        return s;
    }

    @Override
    public String compile(VariableList variableList, int optionalRegisterOffset, String optionalAddress, String secondOptionalAddress) {
        String s = "";

        String ifAddress = Address.getValidAddress("skip_if");
        String endAddress = Address.getValidAddress("if_end");

        // Condition
        s += condition.compile(variableList, 0, "", "");
        s += AssemblyHelper.compare(1, 0);
        s += AssemblyHelper.branch("EQ", ifAddress);

        //If code
        if (thenBranch != null) {
            variableList.appendVariableSubList();
            s += thenBranch.compile(variableList, 0, "", "");
            variableList.removeLastVariableSubList();
        }

        s += AssemblyHelper.jump(endAddress);
        s += ifAddress;

        // Elseif code
        if (elseIfNodes != null && !elseIfNodes.isEmpty())
            for (ElseIfNode elseIfNode : elseIfNodes)
                s += elseIfNode.compile(variableList, 0, endAddress, "");

        //Else code
        if (elseBranch != null) {
            variableList.appendVariableSubList();
            s += elseBranch.compile(variableList, 0, "", "");
            variableList.removeLastVariableSubList();
        }
        s += endAddress;

        return s;
    }
}
