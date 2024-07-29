package mcc.compiler;

import mcc.node.ASTNode;
import mcc.node.java.ValueNode;

public class AssemblyHelper {
    //BUILT-IN
    public static String noOperation() {
        return "NOP\n";
    }
    public static String halt() {
        return "HLT\n";
    }
    public static String add(int left, int right, int output) {
        checkRegisterInBound(left);
        checkRegisterInBound(right);
        checkRegisterInBound(output);
        return "ADD r" + left + " r" + right + " r" + output + "\n";
    }
    public static String subtract(int left, int right, int output) {
        checkRegisterInBound(left);
        checkRegisterInBound(right);
        checkRegisterInBound(output);
        return "SUB r" + left + " r" + right + " r" + output + "\n";
    }
    public static String bitwiseNOR(int left, int right, int output) {
        checkRegisterInBound(left);
        checkRegisterInBound(right);
        checkRegisterInBound(output);
        return "NOR r" + left + " r" + right + " r" + output + "\n";
    }
    public static String bitwiseAND(int left, int right, int output) {
        checkRegisterInBound(left);
        checkRegisterInBound(right);
        checkRegisterInBound(output);
        return "AND r" + left + " r" + right + " r" + output + "\n";
    }
    public static String bitwiseXOR(int left, int right, int output) {
        checkRegisterInBound(left);
        checkRegisterInBound(right);
        checkRegisterInBound(output);
        return "XOR r" + left + " r" + right + " r" + output + "\n";
    }
    public static String bitwiseNOT(int input, int output) {
        checkRegisterInBound(input);
        checkRegisterInBound(output);
        return "NOT r" + input + " r" + output + "\n";
    }

    public static String singleRightShift(int input, int output) {
        checkRegisterInBound(input);
        checkRegisterInBound(output);
        return "RSH r" + input + " r" + output + "\n";
    }
    public static String singleLeftShift(int input, int output) {
        checkRegisterInBound(input);
        checkRegisterInBound(output);
        return "LSH r" + input + " r" + output + "\n";
    }

    public static String loadImmediate(int register, int immediate) {
        checkRegisterInBound(register);
        return "LDI r" + register + " " + immediate + "\n";
    }
    public static String addImmediate(int register, int immediate) {
        checkRegisterInBound(register);
        return "ADI r" + register + " " + immediate + "\n";
    }
    public static String jump(String address) {
        return "JMP " + address;
    }
    public static String branch(String comparison, String address) {
        return "BRH " + comparison + " " + address;
    }
    public static String call(String address) {
        return "CAL " + address + "\n";
    }
    public static String _return() {
        return "RET\n";
    }
    public static String loadMemory(int registerMemoryFrom, int registerTo, int memoryOffset) {
        checkRegisterInBound(registerMemoryFrom);
        checkRegisterInBound(registerTo);
        return "LOD r" + registerMemoryFrom + " r" + registerTo + " " + memoryOffset + "\n";
    }
    public static String saveMemory(int registerMemoryTo, int registerFrom, int memoryOffset) {
        checkRegisterInBound(registerMemoryTo);
        checkRegisterInBound(registerFrom);
        return "STR r" + registerMemoryTo + " r" + registerFrom + " " + memoryOffset + "\n";
    }
    public static String compare(int left, int right) {
        checkRegisterInBound(left);
        checkRegisterInBound(right);
        return "CMP r" + left + " r" + right + "\n";
    }
    public static String copy(int from, int to) {
        checkRegisterInBound(from);
        checkRegisterInBound(to);
        return "MOV r" + from + " r" + to + "\n";
    }
    public static String increment(int register) {
        checkRegisterInBound(register);
        return "INC r" + register + "\n";
    }
    public static String decrement(int register) {
        checkRegisterInBound(register);
        return "DEC r" + register + "\n";
    }

    //MODIFIED

    // Extra gates
    public static String bitwiseXNOR(int left, int right, int output) {
        return bitwiseXOR(left, right, output) +
               bitwiseNOT(output, output);
    }
    public static String bitwiseOR(int left, int right, int output) {
        return bitwiseNOR(left, right, output) +
               bitwiseNOT(output, output);
    }
    public static String bitwiseNAND(int left, int right, int output) {
        return bitwiseAND(left, right, output) +
               bitwiseNOT(output, output);
    }

    // Comparisons
    public static String isEqualTo(int left, int right, int booleanOutput) {
        String address = Address.getValidAddress("is_equal_to");
        String addressEnd = Address.getValidAddress("is_equal_to_end");

        return  compare(left, right) +
                branch("NE", address) +
                loadImmediate(booleanOutput, Constants.REGISTER_MAX_VALUE) +
                jump(addressEnd) +
                address +
                loadImmediate(booleanOutput, 0) +
                addressEnd;

    }
    public static String isNotEqualTo(int left, int right, int booleanOutput) {
        String address = Address.getValidAddress("is_not_equal_to");
        String addressEnd = Address.getValidAddress("is_not_equal_to_end");

        return  compare(left, right) +
                branch("EQ", address) +
                loadImmediate(booleanOutput, Constants.REGISTER_MAX_VALUE) +
                jump(addressEnd) +
                address +
                loadImmediate(booleanOutput, 0) +
                addressEnd;
    }
    public static String isGreaterThan(int left, int right, int booleanOutput) {
        return isLessThan(right, left, booleanOutput);
    }
    public static String isLessThan(int left, int right, int booleanOutput) {
        String address = Address.getValidAddress("is_less_than");
        String addressEnd = Address.getValidAddress("is_less_than_end");

        return  compare(left, right) +
                branch("GE", address) +
                loadImmediate(booleanOutput, Constants.REGISTER_MAX_VALUE) +
                jump(addressEnd) +
                address +
                loadImmediate(booleanOutput, 0) +
                addressEnd;
    }
    public static String isGreaterThanOrEqualTo(int left, int right, int booleanOutput) {
        String address = Address.getValidAddress("is_greater_than_or_equal_to");
        String addressEnd = Address.getValidAddress("is_greater_than_or_equal_to_end");

        return  compare(left, right) +
                branch("LT", address) +
                loadImmediate(booleanOutput, Constants.REGISTER_MAX_VALUE) +
                jump(addressEnd) +
                address +
                loadImmediate(booleanOutput, 0) +
                addressEnd;
    }
    public static String isLessThanOrEqualTo(int left, int right, int booleanOutput) {
        return isGreaterThanOrEqualTo(right, left, booleanOutput);
    }

    // HELPER
    public static void checkRegisterInBound(int register) {
        boolean inBounds = register >= 0 && register < Constants.REGISTER_COUNT;
        if (!inBounds)
            throw new IllegalArgumentException("Error during compiling. Expected register between r0 and r" + (Constants.REGISTER_COUNT - 1) + ", but got r" + register + ".");
    }

    // TODO: implement modulo
    public static String operation(ASTNode node, int leftRegister, int rightRegister, int output, String operation) {
        String s = "";
        switch (operation) {
            case "++" -> {
                return copy(leftRegister, output) +
                    increment(output);
            }
            case "--" -> {
                return copy(leftRegister, output) +
                    decrement(output);
            }
            case "<<", "<<=" -> {
                if (!(node instanceof ValueNode))
                    throw new IllegalArgumentException("Error while compiling. Can only bit-shift a number.");
                if (!((ValueNode) node).isNumber())
                    throw new IllegalArgumentException("Error while compiling. Can only bit-shift a number.");
                int value = Integer.parseInt(((ValueNode) node).getValue());
                if (value <= 0)
                    throw new IllegalArgumentException("Error while compiling. Bit-shift value must be above 0.");
                if (value >= Constants.REGISTER_BIT_COUNT)
                    s += AssemblyHelper.loadImmediate(output, 0);
                else {
                    for (int i = 0; i < value; i++)
                        s += AssemblyHelper.singleLeftShift(leftRegister, output);
                }
            }
            case ">>", ">>=" -> {
                if (!(node instanceof ValueNode))
                    throw new IllegalArgumentException("Error while compiling. Can only bit-shift a number.");
                if (!((ValueNode) node).isNumber())
                    throw new IllegalArgumentException("Error while compiling. Can only bit-shift a number.");
                int value = Integer.parseInt(((ValueNode) node).getValue());
                if (value <= 0)
                    throw new IllegalArgumentException("Error while compiling. Bit-shift value must be above 0.");
                if (value >= Constants.REGISTER_BIT_COUNT)
                    s += AssemblyHelper.loadImmediate(output, 0);
                else {
                    for (int i = 0; i < value; i++)
                        s += AssemblyHelper.singleRightShift(leftRegister, output);
                }
            }
            case "+", "+=" -> s += AssemblyHelper.add(leftRegister, rightRegister, output);
            case "-", "-=" -> s += AssemblyHelper.subtract(leftRegister, rightRegister, output);
            case "&", "&=" -> s += AssemblyHelper.bitwiseAND(leftRegister, rightRegister, output);
            case "|", "|=" -> s += AssemblyHelper.bitwiseOR(leftRegister, rightRegister, output);
            case "^", "^=" -> s += AssemblyHelper.bitwiseXOR(leftRegister, rightRegister, output);
            case "!&", "!&=" -> s += AssemblyHelper.bitwiseNAND(leftRegister, rightRegister, output);
            case "!|", "!|=" -> s += AssemblyHelper.bitwiseNOR(leftRegister, rightRegister, output);
            case "!^", "!^=" -> s += AssemblyHelper.bitwiseXNOR(leftRegister, rightRegister, output);
            case "%", "%=" -> throw new IllegalArgumentException("Modulo (%) operator not programmed yet!");
        }
        return s;
    }

    public static String getButtonPress(String addressEnd, String addressNot, int button, boolean invert) {
        String s = "";
        String notAddress = Address.getValidAddress(addressNot);
        String endAddress = Address.getValidAddress(addressEnd);
        s += AssemblyHelper.loadImmediate(2, Constants.CONTROLLER_INPUT_PORT);
        s += AssemblyHelper.loadMemory(2, 2, 0);
        s += AssemblyHelper.loadImmediate(1, button);
        s += AssemblyHelper.bitwiseAND(1, 2, 0);
        s += AssemblyHelper.branch("EQ", notAddress);
        s += AssemblyHelper.loadImmediate(1, 255);
        s += AssemblyHelper.jump(endAddress);
        s += notAddress;
        s += AssemblyHelper.loadImmediate(1, 0);
        s += endAddress;

        if (invert)
            s += AssemblyHelper.bitwiseNOT(1, 1);
        return s;
    }

    public static String setPixelsAndDoSomething(ASTNode param1, ASTNode param2, int actionPort, VariableList variableList) {
        return param1.compile(variableList, 0, "", "") +
            AssemblyHelper.loadImmediate(2, Constants.PIXEL_X_PORT) +
            AssemblyHelper.saveMemory(2, 1, 0) +
            param2.compile(variableList, 0, "", "") +
            AssemblyHelper.loadImmediate(2, Constants.PIXEL_Y_PORT) +
            AssemblyHelper.saveMemory(2, 1, 0) +
            AssemblyHelper.loadImmediate(1, actionPort) +
            AssemblyHelper.saveMemory(1, 0, 0);
    }
}

