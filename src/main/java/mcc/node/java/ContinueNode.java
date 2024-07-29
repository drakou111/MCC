package mcc.node.java;

import mcc.compiler.AssemblyHelper;
import mcc.compiler.VariableList;
import mcc.node.ASTNode;

public class ContinueNode extends ASTNode {
    @Override
    public String toString() {
        return "{\"type\": \"continue\"}";
    }

    @Override
    public String compile(VariableList variableList, int optionalRegisterOffset, String optionalAddress, String secondOptionalAddress) {
        if (!optionalAddress.isEmpty() && !secondOptionalAddress.isEmpty()) {
            return AssemblyHelper.jump(secondOptionalAddress);
        }
        throw new IllegalArgumentException("Error while compiling. Can only use 'continue' inside a loop.");
    }
}