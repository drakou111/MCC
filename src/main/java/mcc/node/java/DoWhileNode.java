package mcc.node.java;

import mcc.compiler.Address;
import mcc.compiler.AssemblyHelper;
import mcc.compiler.VariableList;
import mcc.node.ASTNode;

public class DoWhileNode extends ASTNode {
    private final ProgramNode body;
    private final ASTNode condition;

    public DoWhileNode(ProgramNode body, ASTNode condition) {
        this.body = body;
        this.condition = condition;
    }

    public ProgramNode getBody() {
        return body;
    }

    public ASTNode getCondition() {
        return condition;
    }

    @Override
    public String toString() {
        return String.format(
            "{\"type\": \"doWhile\", \"body\": %s, \"condition\": %s}",
            body, condition
        );
    }

    @Override
    public String compile(VariableList variableList, int optionalRegisterOffset, String optionalAddress, String secondOptionalAddress) {
        if (body == null)
            return "";

        String s = "";
        String doWhileStartAddress = Address.getValidAddress("do_while_start");
        String doWhileMiddleAddress = Address.getValidAddress("do_while_middle");
        String doWhileEndAddress = Address.getValidAddress("do_while_end");

        variableList.appendVariableSubList();

        /// Start
        s += doWhileStartAddress;

        // body
        s += body.compile(variableList, 0, doWhileEndAddress, doWhileMiddleAddress);

        s += doWhileMiddleAddress;

        //i < 10;
        s += condition.compile(variableList, 0, "", "");
        s += AssemblyHelper.compare(1, 0);
        s += AssemblyHelper.branch("EQ", doWhileEndAddress);

        // loop back
        s += AssemblyHelper.jump(doWhileStartAddress);

        // End
        s += doWhileEndAddress;

        variableList.removeLastVariableSubList();

        return s;
    }
}
