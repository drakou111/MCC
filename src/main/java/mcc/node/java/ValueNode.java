package mcc.node.java;

import mcc.compiler.AssemblyHelper;
import mcc.compiler.VariableList;
import mcc.node.ASTNode;

public class ValueNode extends ASTNode {
    private final String value;
    private final boolean isNumber;
    private boolean inverted;

    public ValueNode(String value, boolean isNumber, boolean inverted) {
        this.value = value;
        this.isNumber = isNumber;
        this.inverted = inverted;
    }

    public String getValue() {
        return value;
    }

    public boolean isNumber() {
        return isNumber;
    }

    @Override
    public String toString() {
        return "{\"type\": \"value\", \"value\": \"" + value + "\", \"isNumber\": " + isNumber + ", \"inverted\": " + inverted + "}";
    }

    @Override
    public String compile(VariableList variableList, int optionalRegisterOffset, String optionalAddress, String secondOptionalAddress) {
        String s = "";
        int register = optionalRegisterOffset + 1;
        if (isNumber) {
            s += AssemblyHelper.loadImmediate(register, Integer.parseInt(value));
            if (inverted)
                s += AssemblyHelper.bitwiseNOT(register, register);
            return s;
        } else {
            Integer memoryIndex = variableList.getValueOf(value);
            if (memoryIndex != null) {
                s = AssemblyHelper.loadImmediate(register, memoryIndex);
                s += AssemblyHelper.loadMemory(register, register, 0);
                if (inverted)
                    s += AssemblyHelper.bitwiseNOT(register, register);
                return s;
            } else {
                throw new IllegalArgumentException("Error while compiling. Tried to use an unassigned variable.");
            }
        }
    }
}
