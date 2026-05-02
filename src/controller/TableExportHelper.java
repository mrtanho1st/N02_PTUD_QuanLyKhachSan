package controller;

import java.awt.Component;
import java.awt.print.PrinterException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

final class TableExportHelper {

    private TableExportHelper() {
    }

    static void exportCsv(Component parent, DefaultTableModel model, String defaultFileName, String successMessage) {
        if (model == null || model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(parent, "Không có dữ liệu để xuất.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file Excel");
        fileChooser.setSelectedFile(new File(defaultFileName));

        int userSelection = fileChooser.showSaveDialog(parent);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = fileChooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getAbsolutePath() + ".csv");
        }

        try (
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))
        ) {
            writer.write('\ufeff');

            for (int i = 0; i < model.getColumnCount(); i++) {
                writer.write(csvValue(model.getColumnName(i)));
                if (i < model.getColumnCount() - 1) {
                    writer.write(",");
                }
            }
            writer.newLine();

            for (int row = 0; row < model.getRowCount(); row++) {
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Object value = model.getValueAt(row, col);
                    writer.write(csvValue(value == null ? "" : value.toString()));
                    if (col < model.getColumnCount() - 1) {
                        writer.write(",");
                    }
                }
                writer.newLine();
            }

            JOptionPane.showMessageDialog(parent, successMessage);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Lỗi khi xuất Excel.");
        }
    }

    static void printTable(Component parent, JTable table, String title, String successLabel) {
        if (table == null || table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(parent, "Không có dữ liệu để in.");
            return;
        }

        try {
            boolean complete = table.print(
                    JTable.PrintMode.FIT_WIDTH,
                    new MessageFormat(title),
                    new MessageFormat("Trang {0}"));

            if (complete) {
                JOptionPane.showMessageDialog(parent, "In " + successLabel + " thành công.");
            } else {
                JOptionPane.showMessageDialog(parent, "Đã hủy in " + successLabel + ".");
            }
        } catch (PrinterException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Lỗi khi in " + successLabel + ".");
        }
    }

    private static String csvValue(String value) {
        String safe = value.replace("\"", "\"\"");
        return "\"" + safe + "\"";
    }
}
