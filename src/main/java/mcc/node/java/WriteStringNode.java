package mcc.node.java;

import mcc.compiler.AssemblyHelper;
import mcc.compiler.Constants;
import mcc.compiler.VariableList;
import mcc.node.ASTNode;

import java.util.List;

public class WriteStringNode extends ASTNode {
    private final List<ASTNode> parameters;

    public WriteStringNode(List<ASTNode> parameters) {
        this.parameters = parameters;
    }

    public List<ASTNode> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        String s = "{\"type\": \"writeString\", \"parameters\": [";
        for (int i = 0; i < parameters.size(); i++) {
            s += parameters.get(i);
            if (i < parameters.size() - 1) {
                s += ", ";
            }
        }
        s += "]}";
        return s;
    }

    @Override
    public String compile(VariableList variableList, int optionalRegisterOffset, String optionalAddress, String secondOptionalAddress) {
        boolean isString = false;

        if (parameters.size() == 1) {
            if (parameters.get(0) instanceof ValueNode valueNode) {
                String value = valueNode.getValue();
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    isString = true;
                }
            }
        }

        String s = AssemblyHelper.loadImmediate(2, Constants.WRITE_CHAR_PORT);

        if (isString) {
            for (int i = 1; i < ((ValueNode) parameters.get(0)).getValue().length() - 1; i++) {
                int c = ((ValueNode) parameters.get(0)).getValue().charAt(i);
                s += AssemblyHelper.loadImmediate(1, c);
                s += AssemblyHelper.saveMemory(2, 1, 0);
            }
            return s;
        }

        for (int i = 0; i < parameters.size(); i++) {
            s += parameters.get(i).compile(variableList, 0, "", "");
            s += AssemblyHelper.saveMemory(2, 1, 0);
        }
        return s;
    }
}
