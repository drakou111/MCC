package mcc.node.java;

import mcc.compiler.Address;
import mcc.compiler.AssemblyHelper;
import mcc.compiler.VariableList;
import mcc.node.ASTNode;

public class CaseNode extends ASTNode {
    private final ASTNode caseExpression;
    private final ProgramNode body;

    public CaseNode(ASTNode caseExpression, ProgramNode body) {
        this.caseExpression = caseExpression;
        this.body = body;
    }

    public ASTNode getCaseExpression() {
        return caseExpression;
    }

    public ProgramNode getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "{\"type\": \"case\", \"caseExpression\": " + caseExpression + ", \"body\": " + body + "}";
    }

    @Override
    public String compile(VariableList variableList, int optionalRegisterOffset, String optionalAddress, String secondOptionalAddress) {
        int memoryIndex = optionalRegisterOffset;
        String switchEnd = optionalAddress;
        String caseEndAddress = Address.getValidAddress("case_end");

        String s = caseExpression.compile(variableList, 0, "", "");

        s += AssemblyHelper.loadImmediate(2, memoryIndex);
        s += AssemblyHelper.loadMemory(2, 2, 0);
        s += AssemblyHelper.compare(1, 2);
        s += AssemblyHelper.branch("NE", caseEndAddress);

        s += body.compile(variableList, 0, "", "");

        s += AssemblyHelper.jump(switchEnd);
        s += caseEndAddress;

        return s;
    }
}