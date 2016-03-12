package de.schmeller.schmasm.gui;

import de.schmeller.schmasm.Assembler;
import de.schmeller.schmasm.Computer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Vector;

/**
 * @author Maximilian Schmeller (mtronics_dev)
 */
public class Gui {
    private JPanel contentPane;
    private JTable memory;
    private JPanel cpuPane;
    private JTextField raxTextField;
    private JTextField rbxTextField;
    private JTextField rcxTextField;
    private JTextField rdxTextField;
    private JCheckBox zFlagCheckBox;
    private JCheckBox nFlagCheckBox;
    private JCheckBox vFlagCheckBox;
    private JTextArea assemblyView;
    private JButton stepButton;
    private JButton executeButton;
    private JTextField instructionPointerTextField;
    private JTextField instructionRegisterTextField;
    private JTextArea console;

    private JFrame window;

    private Computer computer;
    private boolean assembled = false;

    public static void main(String... args) {
        new Gui();
    }

    public Gui() {
        window = new JFrame("SchmASM");
        window.setContentPane(contentPane);
        window.setSize(1280, 720);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);

        stepButton.addActionListener((e) -> {
            if (!assembled) {
                computer.loadToRam(Assembler.assemble(assemblyView.getText()));
                assembled = true;
            } else computer.step();
            updateRegisters();
        });

        executeButton.addActionListener((e) -> {
            if (!assembled) {
                computer.loadToRam(Assembler.assemble(assemblyView.getText()));
                assembled = true;
            } else computer.execute();
            updateRegisters();
        });

        OutputStream outputStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                console.append(String.valueOf((char) b));
                console.setCaretPosition(console.getDocument().getLength());
            }
        };

        System.setOut(new PrintStream(outputStream));
        window.setVisible(true);
    }

    private void updateRegisters() {
        raxTextField.setText(Integer.toString(computer.getRax()) + "d");
        rbxTextField.setText(Integer.toString(computer.getRbx()) + "d");
        rcxTextField.setText(Integer.toString(computer.getRcx()) + "d");
        rdxTextField.setText(Integer.toString(computer.getRdx()) + "d");

        instructionPointerTextField.setText(Integer.toHexString(computer.getIp()) + "h");
        instructionRegisterTextField.setText(Integer.toString(computer.getIr()) + "d");

        nFlagCheckBox.setSelected(computer.isFn());
        zFlagCheckBox.setSelected(computer.isFz());
        vFlagCheckBox.setSelected(computer.isFv());

        TableModel model = memory.getModel();
        int[] ram = computer.getRam();

        for (int y = 0; y < model.getRowCount(); y++) {
            for (int x = 0; x < 16; x++) {
                memory.getModel().setValueAt(Integer.toString(ram[y * 16 + x]), y, x + 1);
            }
        }
    }

    private void createUIComponents() {
        computer = new Computer(512);

        Vector<String> columnNames = new Vector<>(16);
        columnNames.add("");
        columnNames.add("0");
        columnNames.add("1");
        columnNames.add("2");
        columnNames.add("3");
        columnNames.add("4");
        columnNames.add("5");
        columnNames.add("6");
        columnNames.add("7");
        columnNames.add("8");
        columnNames.add("9");
        columnNames.add("A");
        columnNames.add("B");
        columnNames.add("C");
        columnNames.add("D");
        columnNames.add("E");
        columnNames.add("F");
        TableModel model = new DefaultTableModel(columnNames, computer.getRamSize() / 16);
        memory = new JTable(model);

        //Initialize table with 0s
        for (int y = 0; y < computer.getRamSize() / 16; y++) {
            for (int x = 0; x < 16; x++) {
                if (x == 0) memory.getModel().setValueAt(Integer.toHexString(y), y, x);
                memory.getModel().setValueAt("0", y, x + 1);
            }
        }
    }
}
