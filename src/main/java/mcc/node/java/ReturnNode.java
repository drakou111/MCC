package mcc.node.java;

import mcc.compiler.AssemblyHelper;
import mcc.compiler.VariableList;
import mcc.node.ASTNode;

public class ReturnNode extends ASTNode {
    private final ASTNode returnValue;

    public ReturnNode(ASTNode returnValue) {
        this.returnValue = returnValue;
    }

    public ASTNode getReturnValue() {
        return returnValue;
    }

    @Override
    public String toString() {
        return "{\"type\": \"return\", \"returnValue\": " + (returnValue != null ? returnValue : "null") + "}";
    }

    @Override
    public String compile(VariableList variableList, int optionalRegisterOffset, String optionalAddress, String secondOptionalAddress) {
        //Saves to r1
        return returnValue.compile(variableList, optionalRegisterOffset, "", "") +
            AssemblyHelper._return();
    }
}
