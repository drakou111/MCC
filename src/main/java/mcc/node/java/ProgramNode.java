package mcc.node.java;

import mcc.compiler.VariableList;
import mcc.node.ASTNode;

import java.util.ArrayList;
import java.util.List;

public class ProgramNode extends ASTNode {
    public List<ASTNode> body;

    public ProgramNode(List<ASTNode> body) {
        this.body = new ArrayList<>(body);
    }

    @Override
    public String toString() {
        String s = "{\"type\": \"program\", \"body\": [";

        for (int i = 0; i < body.size(); i++) {
            s += body.get(i).toString();
            if (i < body.size() - 1) {
                s += ", ";
            }
        }

        s += "]}";
        return s;
    }

    @Override
    public String compile(VariableList variableList, int optionalRegisterOffset, String optionalAddress, String secondOptionalAddress) {

        String s = "";

        variableList.appendVariableSubList();

        for (ASTNode node : body) {
            s += node.compile(variableList, 0, optionalAddress, secondOptionalAddress);
        }

        variableList.removeLastVariableSubList();

        return s;
    }
}
