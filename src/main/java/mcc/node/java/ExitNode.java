package mcc.node.java;

import mcc.compiler.AssemblyHelper;
import mcc.compiler.VariableList;
import mcc.node.ASTNode;

public class ExitNode extends ASTNode {
    @Override
    public String toString() {
        return "{\"type\": \"exit\"}";
    }

    @Override //Done
    public String compile(VariableList variableList, int optionalRegisterOffset, String optionalAddress, String secondOptionalAddress) {
        return AssemblyHelper.halt();
    }
}