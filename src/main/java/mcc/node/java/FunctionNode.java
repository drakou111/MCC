package mcc.node.java;

import mcc.compiler.Address;
import mcc.compiler.AssemblyHelper;
import mcc.compiler.Constants;
import mcc.compiler.VariableList;
import mcc.node.ASTNode;

import java.util.List;

public class FunctionNode extends ASTNode {
    private final String functionName;
    private final List<String> parameters;
    private final ProgramNode body;
    //TODO: Add 'ref' attribute

    public FunctionNode(String functionName, List<String> parameters, ProgramNode body) {
        this.functionName = functionName;
        this.parameters = parameters;
        this.body = body;
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public ProgramNode getBody() {
        return body;
    }

    @Override
    public String toString() {
        String s = "{\"type\": \"function\", \"functionName\": \"" + functionName + "\", \"parameters\": [";

        for (int i = 0; i < parameters.size(); i++) {
            s += "\"" + parameters.get(i) + "\"";
            if (i < parameters.size() - 1) {
                s += ", ";
            }
        }
        s += "], \"body\": " + body.toString() + "}";
        return s;
    }

    @Override
    public String compile(VariableList variableList, int optionalRegisterOffset, String optionalAddress, String secondOptionalAddress) {
        String addressStart = Address.tryAddUniqueAddress(functionName + "_start");
        String addressEnd = Address.tryAddUniqueAddress(functionName + "_end");

        Address.tryAddUniqueAddress(addressEnd);

        String s = AssemblyHelper.jump(addressEnd) + // Avoid entering the function without CALL
            addressStart; // Used for the CALL assembly

        variableList.appendVariableSubList();

        // Assumes that the values for the params can be found at r1, r2, r3, ...
        for (int i = 0; i < parameters.size(); i++) {
            variableList.tryAddVariable(parameters.get(i));
            int memoryValue = variableList.getValueOf(parameters.get(i));
            s += AssemblyHelper.loadImmediate(Constants.REGISTER_COUNT - 1, memoryValue) +
                AssemblyHelper.saveMemory(Constants.REGISTER_COUNT - 1, i + 1, 0);
        }

        s += body.compile(variableList, 0, "", "");

        // Return in case the programmer didn't return anything.
        s += AssemblyHelper.loadImmediate(1, 0) +
            AssemblyHelper._return();

        s += addressEnd; // End address
        variableList.removeLastVariableSubList();

        return s;
    }
}