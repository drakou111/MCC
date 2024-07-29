package mcc.node.java;

import mcc.compiler.AssemblyHelper;
import mcc.compiler.VariableList;
import mcc.node.ASTNode;

public class AssignmentNode extends ASTNode {
    private final String variableName;
    private final ASTNode value;

    public AssignmentNode(String variableName, ASTNode value) {
        this.variableName = variableName;
        this.value = value;
    }

    public String getVariableName() {
        return variableName;
    }

    public ASTNode getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format(
            "{\"type\": \"assignment\", \"variableName\": \"%s\", \"value\": %s}",
            variableName, value != null ? value.toString() : "null"
        );
    }

    @Override
    public String compile(VariableList variableList, int optionalRegisterOffset, String optionalAddress, String secondOptionalAddress) {
        Integer memoryIndex = variableList.getValueOf(variableName);
        if (memoryIndex != null)
            throw new IllegalArgumentException("Error while compiling. Tried to create a variable already created.");
        variableList.tryAddVariable(variableName);

        String s;
        if (value != null)
            s = value.compile(variableList,0, "", "");
        else
            return "";

        memoryIndex = variableList.getValueOf(variableName);
        s += AssemblyHelper.loadImmediate(2, memoryIndex);
        s += AssemblyHelper.saveMemory(2, 1, 0);
        return s;
    }
}