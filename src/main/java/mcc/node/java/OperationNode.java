package mcc.node.java;

import mcc.compiler.AssemblyHelper;
import mcc.compiler.VariableList;
import mcc.node.ASTNode;

public class OperationNode extends ASTNode {
    private final String variable;
    private final String operator;
    private final ASTNode value;

    public OperationNode(String variable, String operator, ASTNode value) {
        this.variable = variable;
        this.operator = operator;
        this.value = value;
    }

    public String getVariable() {
        return variable;
    }

    public String getOperator() {
        return operator;
    }

    public ASTNode getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format(
            "{\"type\": \"operation\", \"variable\": \"%s\", \"operator\": \"%s\", \"value\": %s}",
            variable, operator, value
        );
    }

    @Override
    public String compile(VariableList variableList, int optionalRegisterOffset, String optionalAddress, String secondOptionalAddress) {
        String s = "";
        if (value != null)
            s += value.compile(variableList, 0, "", "");
        Integer memoryIndex = variableList.getValueOf(variable);
        if (memoryIndex != null) {
            s += AssemblyHelper.loadImmediate(3, memoryIndex);
            s += AssemblyHelper.loadMemory(3, 2, 0);

            s += AssemblyHelper.operation(value, 2, 1, 1, operator);

            s += AssemblyHelper.saveMemory(3, 1, 0);
        } else {
            throw new IllegalArgumentException("Error while compiling. Tried to use an unassigned variable in an operation.");
        }
        return s;
    }
}