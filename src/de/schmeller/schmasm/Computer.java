package de.schmeller.schmasm;

/**
 * Created by schueler on 08.03.2016.
 */
public class Computer {
    public static final int
            HALT = 1,
            LOAD = 10, ILOAD = 11,
            STORE = 20,
            ADD = 30, IADD = 31,
            SUB = 40, ISUB = 41,
            MUL = 50, IMUL = 51,
            DIV = 60, IDIV = 61,
            JMP = 70,
            CMP = 80, ICMP = 81,
            JE = 90, JG = 91, JL = 92, JGE = 93, JLE = 94,
            MOV = 100,
            INT = 110,
            MOD = 120, IMOD = 121,
            RAX = 130, AX = 131, AH = 132, AL = 133,
            RBX = 140, BX = 141, BH = 142, BL = 143,
            RCX = 150, CX = 151, CH = 152, CL = 153,
            RDX = 160, DX = 161, DH = 162, DL = 163;

    int rax, rbx, rcx, rdx;

    int instructionPointer, instructionRegister;
    boolean z, n, v;

    int[] ram;

    public static void main(String... args) {
        Computer computer = new Computer(128);
        int[] inst = {11, 5, 20, 100, 11, 3, 20, 101};
    }

    public Computer(int ramSize) {
        rax = rbx = rcx = rdx = instructionPointer = instructionRegister = 0;
        ram = new int[ramSize];
    }

    public void loadInstructions(int[] instructions) {
        System.arraycopy(instructions, 0, ram, 0, instructions.length);
    }

    public void execute() {
        while (instructionRegister != HALT) {
            evaluateInstruction(instructionPointer);
        }
    }

    private void evaluateInstruction(int instructionPointer) {
        instructionRegister = ram[instructionPointer];
        instructionPointer++;

        switch (instructionRegister) {
            case LOAD:
                rax = ram[ram[instructionPointer]];
                instructionPointer++;
                break;
            case ILOAD:
                rax = ram[instructionPointer];
                instructionPointer++;
                break;
            case STORE:
                ram[ram[instructionPointer]] = rax;
                instructionPointer++;
                break;
            case ADD:
                rax += ram[ram[instructionPointer]];
                instructionPointer++;
                break;
            case IADD:
                rax += ram[instructionPointer];
                instructionPointer++;
                break;
            case SUB:
                rax -= ram[ram[instructionPointer]];
                instructionPointer++;
                break;
            case ISUB:
                rax -= ram[instructionPointer];
                instructionPointer++;
                break;
            case MUL:
                rax *= ram[ram[instructionPointer]];
                instructionPointer++;
                break;
            case IMUL:
                rax *= ram[instructionPointer];
                instructionPointer++;
                break;
            case DIV:
                rax /= ram[ram[instructionPointer]];
                instructionPointer++;
                break;
            case IDIV:
                rax /= ram[instructionPointer];
                instructionPointer++;
                break;
            case MOD:
                rax %= ram[ram[instructionPointer]];
                instructionPointer++;
                break;
            case IMOD:
                rax %= ram[instructionPointer];
                instructionPointer++;
                break;
            case JMP:
                instructionPointer = ram[instructionPointer];
                break;
            case CMP:
                int cmpTo = ram[ram[instructionPointer]];
                if (rax < cmpTo) n = true;
                else if (rax == cmpTo) z = true;
                break;
            case ICMP:
                int cmpToI = ram[instructionPointer];
                if (rax < cmpToI) n = true;
                else if (rax == cmpToI) z = true;
                break;
            case JE:
                if (z) instructionPointer = ram[instructionPointer];
                clearFlags();
                break;
            case JG:
                if (!n && !z) instructionPointer = ram[instructionPointer];
                clearFlags();
                break;
            case JL:
                if (n) instructionPointer = ram[instructionPointer];
                clearFlags();
                break;
            case JGE:
                if (!n) instructionPointer = ram[instructionPointer];
                clearFlags();
                break;
            case JLE:
                if (n || z) instructionPointer = ram[instructionPointer];
                clearFlags();
                break;
            case MOV:
                int reg1 = ram[instructionPointer];
                instructionPointer++;
                int reg2 = ram[instructionPointer];
                instructionPointer++;

                int value = 0;

                switch (reg1) {
                    case RAX:
                        value = rax;
                        break;
                    case RBX:
                        value = rbx;
                        break;
                    case RCX:
                        value = rcx;
                        break;
                    case RDX:
                        value = rdx;
                        break;
                }

                switch (reg2) {
                    case RAX:
                        rax = value;
                        break;
                    case RBX:
                        rbx = value;
                        break;
                    case RCX:
                        rcx = value;
                        break;
                    case RDX:
                        rdx = value;
                        break;
                }
                break;
        }
    }

    private void clearFlags() {
        z = n = v = false;
    }
}
