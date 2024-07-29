package mcc.node.java;

import mcc.compiler.Address;
import mcc.compiler.AssemblyHelper;
import mcc.compiler.VariableList;
import mcc.node.ASTNode;

public class WhileNode extends ASTNode {
    private final ASTNode condition;
    private final ProgramNode body;

    public WhileNode(ASTNode condition, ProgramNode body) {
        this.condition = condition;
        this.body = body;
    }

    public ASTNode getCondition() {
        return condition;
    }

    public ProgramNode getBody() {
        return body;
    }

    @Override
    public String toString() {
        return String.format(
            "{\"type\": \"while\", \"condition\": %s, \"body\": %s}",
            condition, body
        );
    }

    @Override
    public String compile(VariableList variableList, int optionalRegisterOffset, String optionalAddress, String secondOptionalAddress) {
        if (body == null)
            return "";

        String s = "";
        String whileStartAddress = Address.getValidAddress("while_start");
        String whileMiddleAddress = Address.getValidAddress("while_middle");
        String whileEndAddress = Address.getValidAddress("while_end");

        variableList.appendVariableSubList();

        /// Start
        s += whileStartAddress;

        //i < 10;
        s += condition.compile(variableList, 0, "", "");
        s += AssemblyHelper.compare(1, 0);
        s += AssemblyHelper.branch("EQ", whileEndAddress);

        // body
        s += body.compile(variableList, 0, whileEndAddress, whileMiddleAddress);

        s += whileMiddleAddress;

        // loop back
        s += AssemblyHelper.jump(whileStartAddress);

        // End
        s += whileEndAddress;

        variableList.removeLastVariableSubList();

        return s;
    }
}
