package mcc.compiler;

import java.util.HashMap;
import java.util.Map;

public class VariableSubList {
    Map<String, Integer> variables;
    int newVariableCount;

    public VariableSubList() {
        this.variables = new HashMap<>();
    }

    public int getVariableCount() {
        return newVariableCount;
    }

    public void addVariable(String var, int index) {
        variables.put(var, index);
        newVariableCount++;
    }

    public Integer getValueOf(String key) {
        return variables.get(key);
    }
}
