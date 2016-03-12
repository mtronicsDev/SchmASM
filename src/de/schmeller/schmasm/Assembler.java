package de.schmeller.schmasm;

import java.util.*;

/**
 * @author Maximilian Schmeller (mtronics_dev)
 */
public class Assembler {
    public static int[] assemble(String assembly) {
        String[] instructions = assembly.split("[\\n \\t]+");

        LinkedList<Integer> assembled = new LinkedList<>();

        Map<String, Integer> labelLocations = new HashMap<>();
        Map<Integer, String> labelPointers = new HashMap<>();

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < instructions.length; i++) {
            String inst = instructions[i];

            if (isInteger(inst)) assembled.add(Integer.valueOf(inst));
            else if (inst.matches("[0-9]+h")) assembled.add(Integer.valueOf(inst.replace("h", ""), 16));
            else if (inst.endsWith(":")) {
                labelLocations.put(inst.replace(":", ""), assembled.size()); //Points to NEXT instruction after label
            } else {
                try {
                    Integer opCode = Computer.opCodes.entrySet()
                            .stream()
                            .filter(e -> Objects.equals(e.getValue(), inst))
                            .findAny()
                            .get()
                            .getKey();
                    assembled.add(opCode);
                } catch (NoSuchElementException e) {
                    labelPointers.put(assembled.size(), inst);
                    assembled.add(-1); //Placeholder
                }
            }
        }

        for (Map.Entry<Integer, String> labelPointer : labelPointers.entrySet()) {
            Integer labelLocation = labelLocations.get(labelPointer.getValue());
            if (labelLocation == null) throw new IllegalArgumentException("OpCode \"" + labelPointer.getValue() + "\" is invalid!");
            else assembled.set(labelPointer.getKey(), labelLocation);
        }

        int[] ram = new int[assembled.size()];

        for (int i = 0; i < ram.length; i++) {
            ram[i] = assembled.get(i);
        }

        return ram;
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
