package de.schmeller.schmasm;

import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * @author Maximilian Schmeller (mtronics_dev)
 */
public class Assembler {
    public static int[] assemble(String assembly) {
        LinkedList<Integer> instructions = new LinkedList<>();

        String[] opCodes = assembly.split("[\\n \\t]");

        for (String opCode : opCodes) {
            if (isInteger(opCode)) instructions.add(Integer.valueOf(opCode));
            else {
                try {
                    instructions.add(Computer.opCodes.entrySet().stream()
                            .filter((e) -> e.getValue().equals(opCode))
                            .findAny()
                            .get()
                            .getKey());
                } catch (NoSuchElementException e) {
                    System.err.println("[ASM] OpCode \"" + opCode + "\" does not exist!");
                }
            }
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
