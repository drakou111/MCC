package mcc.node;

import mcc.compiler.VariableList;

public abstract class ASTNode {
    public abstract String compile(VariableList variableList, int optionalRegisterOffset, String optionalAddress, String secondOptionalAddress);
}
