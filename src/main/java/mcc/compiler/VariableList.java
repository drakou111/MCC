package mcc.compiler;

import java.util.ArrayList;

public class VariableList {
    ArrayList<VariableSubList> variableSubLists;
    int varCount;

    public VariableList() {
        this.variableSubLists = new ArrayList<>();
        this.varCount = 0;
    }

    // Append an "environment". For example, the inside of a for loop, the inside of an if, the inside of a function, etc.
    public void appendVariableSubList() {
        variableSubLists.add(new VariableSubList());
    }

    // idem append, but removes last.
    public void removeLastVariableSubList() {
        int count = this.variableSubLists.get(this.variableSubLists.size() - 1).getVariableCount();
        varCount -= count;
        variableSubLists.remove(this.variableSubLists.size() - 1);
    }

    // Add a variable pointing to the first available memory address.
    public void tryAddVariable(String key) {
        if (!containsVariable(key)) {
            this.variableSubLists.get(this.variableSubLists.size() - 1).addVariable(key, varCount);
            varCount++;
        }
    }

    public String forceAddVariable(String key) {
        int iter = 0;
        while (containsVariable(key + "_" + iter)) {
            iter ++;
        }

        this.variableSubLists.get(this.variableSubLists.size() - 1).addVariable(key + "_" + iter, varCount);
        varCount++;

        return key + "_" + iter;
    }

    public boolean containsVariable(String key) {
        return getValueOf(key) != null;
    }

    public Integer getValueOf(String key) {
        for (VariableSubList variableSubList : variableSubLists) {
            Integer val = variableSubList.getValueOf(key);
            if (val != null)
                return val;
        }
        return null;
    }
}
