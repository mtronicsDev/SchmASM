package de.schmeller.schmasm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @author Maximilian Schmeller (mtronics_dev)
 */
public class Assembler {
    public static int[] assemble(String assembly) {
        LinkedList<Integer> instructions = new LinkedList<>();
        Map<String, Integer> labels = new HashMap<>();
        LinkedList<Integer> labelPointers = new LinkedList<>();

        String[] opCodes = assembly.split("[\\n \\t]");

        for (String opCode : opCodes) {
            if (isInteger(opCode)) instructions.add(Integer.valueOf(opCode));
            else if (opCode.endsWith(":")) labels.put(opCode.replace(":", ""), instructions.size());
            else {
                try {
                    instructions.add(Computer.opCodes.entrySet().stream()
                            .filter((e) -> e.getValue().equals(opCode))
                            .findAny()
                            .get()
                            .getKey());
                } catch (NoSuchElementException e) {
                    int previous = instructions.get(instructions.size() - 1);
                    if (previous >= 130 && previous <= 145) {
                        labelPointers.add(instructions.size());
                        instructions.add(-1);
                    }
                    else System.err.println("[ASM] OpCode \"" + opCode + "\" does not exist!");
                }
            }
        }

        for (Integer index : labelPointers) {
            Integer instruction = labels.get(opCodes[index]);
            instructions.set(index, instruction);
        }

        int[] re = new int[instructions.size()];
        for (int i = 0; i < instructions.size(); i++) {
            re[i] = instructions.get(i);
        }

        return re;
    }

    private static boolean isInteger(String toCheck) {
        boolean isInteger = true;

        try {//noinspection ResultOfMethodCallIgnored
            Integer.valueOf(toCheck);
        } catch (NumberFormatException e) {
            isInteger = false;
        }

        return isInteger;
    }

}
