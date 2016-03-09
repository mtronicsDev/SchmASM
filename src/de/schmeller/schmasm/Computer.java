package de.schmeller.schmasm;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by schueler on 08.03.2016.
 */
public class Computer {
    private int rax, rbx, rcx, rdx; //32-bit registers
    private int ip, ir;             //Instruction register & pointer
    private boolean fz, fn, fv;     //Zero-, overflow- and negative-flag
    private int[] ram;              //Memory
    private boolean stopped = false;//Has the program halted?

    public static void main(String... args) {
        Computer computer = new Computer(128);
        computer.loadToRam(Assembler.assemble("ILOAD 5 JMP label label: STORE 100 ILOAD 3 ADD 100 NOT STORE 101 HALT"));
        computer.execute();
        System.out.println(computer.ram[101]);
    }

    public Computer(int ramSize) {
        rax = rbx = rcx = rdx = 0;
        ip = ir = 0;
        fz = fn = fv = false;
        ram = new int[ramSize];
    }

    public void clearRam() {
        ram = new int[ram.length];
    }

    public void loadToRam(int[] instructions) {
        System.arraycopy(instructions, 0, ram, 0, Math.min(instructions.length, ram.length));
    }

    public void execute() {
        while (!stopped) {
            step();
        }
    }

    public void step() {
        if (stopped) return;
        ir = getAddressContent(ip);
        executeInstruction(ir);
    }

    private void executeInstruction(int opCode) {
        incrementInstructionPointer();

        switch (opCode) {
            case 1:
                stopped = true;
                break;
            case 10:
                rax += getPointerContent(ip);
                break;
            case 11:
                rax += getAddressContent(ip);
                break;
            case 20:
                rax -= getPointerContent(ip);
                break;
            case 21:
                rax -= getAddressContent(ip);
                break;
            case 30:
                rax *= getPointerContent(ip);
                break;
            case 31:
                rax *= getAddressContent(ip);
                break;
            case 40:
                rax /= getPointerContent(ip);
                break;
            case 41:
                rax /= getAddressContent(ip);
                break;
            case 50:
                rax %= getPointerContent(ip);
                break;
            case 51:
                rax %= getAddressContent(ip);
                break;
            case 60:
                rax <<= getPointerContent(ip);
                break;
            case 61:
                rax <<= getAddressContent(ip);
                break;
            case 70:
                rax >>= getPointerContent(ip);
                break;
            case 71:
                rax >>= getAddressContent(ip);
                break;
            case 80:
                rax |= getPointerContent(ip);
                break;
            case 81:
                rax |= getAddressContent(ip);
                break;
            case 90:
                rax &= getPointerContent(ip);
                break;
            case 91:
                rax &= getAddressContent(ip);
                break;
            case 100:
                rax ^= getPointerContent(ip);
                break;
            case 101:
                rax ^= getAddressContent(ip);
                break;
            case 110:
                rax = ~rax;
                return;
            case 120:
                int cmp = getPointerContent(ip);
                if (rax < cmp) fn = true;
                else if (rax == cmp) fz = true;
                break;
            case 121:
                int cmpi = getAddressContent(ip);
                if (rax < cmpi) fn = true;
                else if (rax == cmpi) fz = true;
                break;
            case 130:
                ip = getAddressContent(ip);
                return;
            case 140:
                if (fz) ip = getAddressContent(ip);
                clearFlags();
                return;
            case 141:
                if (fn) ip = getAddressContent(ip);
                clearFlags();
                return;
            case 142:
                if (!fn && !fz) ip = getAddressContent(ip);
                clearFlags();
                return;
            case 143:
                if (fz || fn) ip = getAddressContent(ip);
                clearFlags();
                return;
            case 144:
                if (!fn) ip = getAddressContent(ip);
                clearFlags();
                return;
            case 145:
                if (!fz) ip = getAddressContent(ip);
                clearFlags();
                return;
            case 150:
                rax = getPointerContent(ip);
                break;
            case 151:
                rax = getAddressContent(ip);
                break;
            case 160:
                ram[getAddressContent(ip)] = rax;
                break;
            case 170:
                int content = 0;

                //noinspection Duplicates
                switch (getAddressContent(ip)) {
                    case 1000: content = rax; break;
                    case 1100: content = rax & 0x00FF; break;
                    case 1010: content = rax & 0x00F0; break;
                    case 1001: content = rax & 0x000F; break;

                    case 2000: content = rbx; break;
                    case 2100: content = rbx & 0x00FF; break;
                    case 2010: content = rbx & 0x00F0; break;
                    case 2001: content = rbx & 0x000F; break;

                    case 3000: content = rcx; break;
                    case 3100: content = rcx & 0x00FF; break;
                    case 3010: content = rcx & 0x00F0; break;
                    case 3001: content = rcx & 0x000F; break;

                    case 4000: content = rdx; break;
                    case 4100: content = rdx & 0x00FF; break;
                    case 4010: content = rdx & 0x00F0; break;
                    case 4001: content = rdx & 0x000F; break;
                }
                
                incrementInstructionPointer();

                //noinspection Duplicates
                switch (getAddressContent(ip)) {
                    case 1000: rax = content; break;
                    case 1100: rax = content & 0x00FF; break;
                    case 1010: rax = content & 0x00F0; break;
                    case 1001: rax = content & 0x000F; break;

                    case 2000: rbx = content; break;
                    case 2100: rbx = content & 0x00FF; break;
                    case 2010: rbx = content & 0x00F0; break;
                    case 2001: rbx = content & 0x000F; break;

                    case 3000: rcx = content; break;
                    case 3100: rcx = content & 0x00FF; break;
                    case 3010: rcx = content & 0x00F0; break;
                    case 3001: rcx = content & 0x000F; break;

                    case 4000: rdx = content; break;
                    case 4100: rdx = content & 0x00FF; break;
                    case 4010: rdx = content & 0x00F0; break;
                    case 4001: rdx = content & 0x000F; break;
                }
                break;
            case 180:
                System.out.println("Called interrupt INT" + getAddressContent(ip) + ".");
                break;
            default:
                throw new IllegalArgumentException("Illegal OpCode " + opCode + "!");
        }

        incrementInstructionPointer();
    }

    private void incrementInstructionPointer() {
        ip++;
    }
    
    private int getAddressContent(int address) {
        return ram[address];
    }
    
    private int getPointerContent(int address) {
        return ram[ram[address]];
    }
    
    private void clearFlags() {
        fz = fn = fv = false;
    }

    public static final Map<Integer, String> opCodes = new HashMap<>(36);
    
    static {
        opCodes.put(1, "HALT");
        opCodes.put(10, "ADD");     opCodes.put(11, "IADD");
        opCodes.put(20, "SUB");     opCodes.put(21, "ISUB");
        opCodes.put(30, "MUL");     opCodes.put(31, "IMUL");
        opCodes.put(40, "DIV");     opCodes.put(41, "IDIV");
        opCodes.put(50, "MOD");     opCodes.put(51, "IMOD");
        opCodes.put(60, "SHL");     opCodes.put(61, "ISHL");
        opCodes.put(70, "SHR");     opCodes.put(71, "ISHR");
        opCodes.put(80, "OR");      opCodes.put(81, "IOR");
        opCodes.put(90, "AND");     opCodes.put(91, "IAND");
        opCodes.put(100, "XOR");    opCodes.put(101, "IXOR");
        opCodes.put(110, "NOT");
        opCodes.put(120, "CMP");    opCodes.put(121, "ICMP");
        opCodes.put(130, "JMP");
        opCodes.put(140, "JE");
        opCodes.put(141, "JL");
        opCodes.put(142, "JG");
        opCodes.put(143, "JLE");
        opCodes.put(144, "JGE");
        opCodes.put(145, "JNE");
        opCodes.put(150, "LOAD");   opCodes.put(151, "ILOAD");
        opCodes.put(160, "STORE");
        opCodes.put(170, "MOV");
        opCodes.put(180, "INT");
        
        //Register codes (for MOV instruction)
        opCodes.put(1000, "RAX");   opCodes.put(1100, "AX");   opCodes.put(1010, "AH");   opCodes.put(1001, "AL");
        opCodes.put(2000, "RBX");   opCodes.put(2100, "BX");   opCodes.put(2010, "BH");   opCodes.put(2001, "BL");
        opCodes.put(3000, "RCX");   opCodes.put(3100, "CX");   opCodes.put(3010, "CH");   opCodes.put(3001, "CL");
        opCodes.put(4000, "RDX");   opCodes.put(4100, "DX");   opCodes.put(4010, "DH");   opCodes.put(4001, "DL");
    }
}
