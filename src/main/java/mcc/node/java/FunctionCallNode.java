package mcc.node.java;

import mcc.compiler.Address;
import mcc.compiler.AssemblyHelper;
import mcc.compiler.Constants;
import mcc.compiler.VariableList;
import mcc.node.ASTNode;

import java.util.List;

public class FunctionCallNode extends ASTNode {
    private final String functionName;
    private final List<ASTNode> parameters;
    private boolean invert;

    public FunctionCallNode(String functionName, List<ASTNode> parameters, boolean invert) {
        this.functionName = functionName;
        this.parameters = parameters;
        this.invert = invert;
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<ASTNode> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        String s = "{\"type\": \"functionCall\", \"functionName\": \"" + functionName + "\", \"invert\": "+ invert +", \"parameters\": [";
        for (int i = 0; i < parameters.size(); i++) {
            s += parameters.get(i);
            if (i < parameters.size() - 1) {
                s += ", ";
            }
        }
        s += "]}";
        return s;
    }

    //TODO: add built-in functions
    @Override
    public String compile(VariableList variableList, int optionalRegisterOffset, String optionalAddress, String secondOptionalAddress) {
        String s = "";

        switch (functionName) {
            case "getButtonSelect" -> { return AssemblyHelper.getButtonPress("end_select", "not_select", Constants.SELECT, invert); }
            case "getButtonStart" -> { return AssemblyHelper.getButtonPress("end_start", "not_start", Constants.START, invert); }
            case "getButtonRight" -> { return AssemblyHelper.getButtonPress("end_right", "not_right", Constants.RIGHT, invert); }
            case "getButtonDown" -> { return AssemblyHelper.getButtonPress("end_down", "not_down", Constants.DOWN, invert); }
            case "getButtonLeft" -> { return AssemblyHelper.getButtonPress("end_left", "not_left", Constants.LEFT, invert); }
            case "getButtonUp" -> { return AssemblyHelper.getButtonPress("end_up", "not_up", Constants.UP, invert); }
            case "getButtonA" -> { return AssemblyHelper.getButtonPress("end_a", "not_a", Constants.A, invert); }
            case "getButtonB" -> { return AssemblyHelper.getButtonPress("end_b", "not_b", Constants.B, invert); }
            case "clearScreen" -> { return AssemblyHelper.loadImmediate(1, Constants.CLEAR_SCREEN_BUFFER_PORT) + AssemblyHelper.saveMemory(1, 0, 0); }
            case "clearNumber" -> { return AssemblyHelper.loadImmediate(1, Constants.CLEAR_NUMBER_PORT) + AssemblyHelper.saveMemory(1, 0, 0); }
            case "clearPixel" -> { return AssemblyHelper.setPixelsAndDoSomething(parameters.get(0), parameters.get(1), Constants.CLEAR_PIXEL_PORT, variableList); }
            case "pushScreen" -> { return AssemblyHelper.loadImmediate(1, Constants.BUFFER_SCREEN_PORT) + AssemblyHelper.saveMemory(1, 0, 0); }
            case "clearChars" -> { return AssemblyHelper.loadImmediate(1, Constants.CLEAR_CHARS_BUFFER_PORT) + AssemblyHelper.saveMemory(1, 0, 0); }
            case "showNumber" -> { return parameters.get(0).compile(variableList, 0, "", "") + AssemblyHelper.loadImmediate(2, Constants.SNOW_NUMBER_PORT) + AssemblyHelper.saveMemory(2, 1, 0); }
            case "drawPixel" -> { return AssemblyHelper.setPixelsAndDoSomething(parameters.get(0), parameters.get(1), Constants.DRAW_PIXEL_PORT, variableList); }
            case "writeChar" -> { return parameters.get(0).compile(variableList, 0, "", "") + AssemblyHelper.loadImmediate(2, Constants.WRITE_CHAR_PORT) + AssemblyHelper.saveMemory(2, 1, 0); }
            case "pushChars" -> { return AssemblyHelper.loadImmediate(1, Constants.BUFFER_CHARS_PORT) + AssemblyHelper.saveMemory(1, 0, 0); }
            case "getPixel" -> { return AssemblyHelper.setPixelsAndDoSomething(parameters.get(0), parameters.get(1), Constants.LOAD_PIXEL_PORT, variableList); }
            case "random" -> { return AssemblyHelper.loadImmediate(1, Constants.RNG_PORT) + AssemblyHelper.loadMemory(1, 1, 0); }
            case "showSigned" -> { return AssemblyHelper.loadImmediate(1, Constants.SIGNED_MODE_PORT) + AssemblyHelper.saveMemory(1, 0, 0); }
            case "showUnsigned" -> { return AssemblyHelper.loadImmediate(1, Constants.UNSIGNED_MODE_PORT) + AssemblyHelper.saveMemory(1, 0, 0); }

            default -> {
                for (int i = 0; i < parameters.size(); i++) {
                    s += parameters.get(i).compile(variableList, i, "", "");
                }
                if (!Address.uniqueAlreadyExists(functionName + "_start"))
                    throw new IllegalArgumentException("Error during compile. Tried to call a function that wasn't created yet. Try to move your functions at the top of your program.");

                s += AssemblyHelper.call("." + functionName + "_start");

                if (invert)
                    s += AssemblyHelper.bitwiseNOT(1, 1);

                return s;
            }
        }
    }
}
