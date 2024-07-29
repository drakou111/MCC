package mcc.node.java;

import mcc.compiler.Address;
import mcc.compiler.AssemblyHelper;
import mcc.compiler.VariableList;
import mcc.node.ASTNode;

import java.util.List;

public class SwitchNode extends ASTNode {
    private final ASTNode switchExpression;
    private final List<CaseNode> cases;
    private final ProgramNode defaultCase;

    public SwitchNode(ASTNode switchExpression, List<CaseNode> cases, ProgramNode defaultCase) {
        this.switchExpression = switchExpression;
        this.cases = cases;
        this.defaultCase = defaultCase;
    }

    public ASTNode getSwitchExpression() {
        return switchExpression;
    }

    public List<CaseNode> getCases() {
        return cases;
    }

    public ProgramNode getDefaultCase() {
        return defaultCase;
    }

    @Override
    public String toString() {
        String s = "{\"type\": \"switch\", \"switchExpression\": " + switchExpression + ", \"cases\":  [";

        for (int i = 0; i < cases.size(); i++) {
            s += cases.get(i);
            if (i < cases.size() - 1) {
                s += ", ";
            }
        }

        s += "], \"defaultCase\": " + defaultCase + "}";
        return s;
    }

    @Override
    public String compile(VariableList variableList, int optionalRegisterOffset, String optionalAddress, String secondOptionalAddress) {
        String s = "";

        String switchEnd = Address.getValidAddress("switch_end");

        s += switchExpression.compile(variableList, 0, "", "");

        variableList.appendVariableSubList();

        String key = variableList.forceAddVariable("!switch");
        Integer memoryIndex = variableList.getValueOf(key);

        s += AssemblyHelper.loadImmediate(2, memoryIndex);
        s += AssemblyHelper.saveMemory(2, 1, 0);

        for (int i = 0; i < cases.size(); i++) {
            variableList.appendVariableSubList();
            s += cases.get(i).compile(variableList, memoryIndex, switchEnd, "");
            variableList.removeLastVariableSubList();
        }

        s += defaultCase.compile(variableList, 0, "", "");

        s += switchEnd;

        variableList.removeLastVariableSubList();

        return s;
    }
}
