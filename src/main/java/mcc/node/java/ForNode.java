package mcc.node.java;

import mcc.compiler.Address;
import mcc.compiler.AssemblyHelper;
import mcc.compiler.VariableList;
import mcc.node.ASTNode;

public class ForNode extends ASTNode {
    private final AssignmentNode initialization;
    private final ASTNode condition;
    private final OperationNode update;
    private final ProgramNode body;

    public ForNode(AssignmentNode initialization, ASTNode condition, OperationNode update, ProgramNode body) {
        this.initialization = initialization;
        this.condition = condition;
        this.update = update;
        this.body = body;
    }

    public AssignmentNode getInitialization() {
        return initialization;
    }

    public ASTNode getCondition() {
        return condition;
    }

    public OperationNode getUpdate() {
        return update;
    }

    public ProgramNode getBody() {
        return body;
    }

    @Override
    public String toString() {
        return String.format(
            "{\"type\": \"for\", \"initialization\": %s, \"condition\": %s, \"update\": %s, \"body\": %s}",
            initialization, condition, update, body
        );
    }

    @Override
    public String compile(VariableList variableList, int optionalRegisterOffset, String optionalAddress, String secondOptionalAddress) {

        if (body == null)
            return "";

        String s = "";
        String forStartAddress = Address.getValidAddress("for_start");
        String forMiddleAddress = Address.getValidAddress("for_middle");
        String forEndAddress = Address.getValidAddress("for_end");

        variableList.appendVariableSubList();

        //int i = 0;
        s += initialization.compile(variableList, 0, "", "");

        /// Start
        s += forStartAddress;

        //i < 10;
        s += condition.compile(variableList, 0, "", "");
        s += AssemblyHelper.compare(1, 0);
        s += AssemblyHelper.branch("EQ", forEndAddress);

        // body
        s += body.compile(variableList, 0, forEndAddress, forMiddleAddress);

        // i++; and loop back
        s += forMiddleAddress;
        s += update.compile(variableList, 0, "", "");
        s += AssemblyHelper.jump(forStartAddress);

        // End
        s += forEndAddress;

        variableList.removeLastVariableSubList();

        return s;
    }
}