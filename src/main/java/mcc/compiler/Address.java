package mcc.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Address {
    public static Map<String, Integer> addressCount = new HashMap<>();
    public static List<String> uniqueAddresses = new ArrayList<>();

    public static String getValidAddress(String addressName) {
        if (addressCount.containsKey(addressName)) {
            String newName = "." + addressName + "_" + addressCount.get(addressName) + "\n";
            addressCount.put(addressName, addressCount.get(addressName) + 1);
            return newName;
        } else {
            addressCount.put(addressName, 1);
            return "." + addressName + "_0\n";
        }
    }

    public static boolean uniqueAlreadyExists(String addressName) {
        return uniqueAddresses.contains(addressName);
    }

    public static String tryAddUniqueAddress(String addressName) {
        if (uniqueAlreadyExists(addressName)) {
            throw new IllegalArgumentException("Error during compiling: function '" + addressName + "' already exists.");
        }
        uniqueAddresses.add(addressName);
        return "." + addressName + "\n";
    }
}
