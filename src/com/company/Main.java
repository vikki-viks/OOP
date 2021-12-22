package com.company;
import org.w3c.dom.*;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;

public class Main {
    private JFrame busList;
    private DefaultTableModel model;
    private JButton save, addNew, deleted, edit, open, driversInfo,busIcon, violations;
    private JToolBar toolBar;
    private JScrollPane scroll;
    private JTable bus;
    private JTextField search;
    private JButton filter;
    private TableRowSorter<TableModel> rowSorter;
    private Connection connection;
    private SQL db;



    private class MyException extends Exception {
        public MyException() {
        super ("Вы не ввели название книги для поиска"); }}
    private void checkName (JTextField bName) throws MyException {
        String sName = bName.getText();
        if (sName.contains("Поиск")) throw new MyException();
    }

    public void show() {
        db = new SQL();
        db.setConnection();
        String jdbcURL = "jdbc:postgresql://localhost:5432/bus";
        String username = "postgres";
        String password = "postgres";
        try {
            this.connection = DriverManager.getConnection(jdbcURL, username, password);
        } catch (SQLException e) {
            System.out.println("Error");
            e.printStackTrace();
        }


        busList = new JFrame("Список маршрутов");
        busList.setSize(500, 300);
        busList.setLocation(100, 100);
        busList.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        toolBar = new JToolBar("Панель инструментов");
        busList.setLayout(new BorderLayout());
        busList.add(toolBar, BorderLayout.NORTH);

        driversInfo = new JButton(new ImageIcon("./img/driversInfo.png"));
        driversInfo.setToolTipText("Информация о водителях");
        driversInfo.setBorderPainted(false);
        toolBar.add(driversInfo);

        violations = new JButton(new ImageIcon("./img/violations.png"));
        violations.setToolTipText("Нарушения");
        violations.setBorderPainted(false);
        toolBar.add(violations);

        busIcon = new JButton(new ImageIcon("./img/busIcon.png"));
        busIcon.setToolTipText("Спрака о движении маршрутов");
        busIcon.setBorderPainted(false);
        toolBar.add(busIcon);

        save = new JButton(new ImageIcon("./img/save.png"));
        save.setToolTipText("Сохранить");
        save.setBorderPainted(false);
        toolBar.add(save);


        open = new JButton(new ImageIcon("./img/open.png"));
        open.setToolTipText("Редактировать");
        open.setBorderPainted(false);
        toolBar.add(open);

        String[] columns = {"Имя водителя", "Номер маршрута", "Интервал движения"};
        String[][] data = {};

        model = new DefaultTableModel(data, columns);
        bus = new JTable(model);
        rowSorter = new TableRowSorter<>(bus.getModel());
        bus.setRowSorter(rowSorter);
        scroll = new JScrollPane(bus);

        ArrayList<Object[]> result = db.getMainInfo();

        for (int i = 0; i < result.size(); i += 1) {
            Object[] row = result.get(i);
            model.addRow(row);
        }

        busList.add(scroll, BorderLayout.CENTER);

        search = new JTextField("Поиск");
        search.setColumns(30);
        filter = new JButton("Поиск");

        JPanel filterPanel = new JPanel();
        filterPanel.add(search);
        filterPanel.add(filter);
        busList.add(filterPanel, BorderLayout.SOUTH);
        busList.setVisible(true);

        busIcon.addActionListener(e -> {
            FileDialog save = new FileDialog(busList, "Сохранение данных", FileDialog.SAVE);
            save.setFile(".txt");
            save.setVisible(true);
            String fileName = save.getDirectory() + save.getFile();
            if (fileName == null) return;
            String schedule = db.getReport();
            Report.execute(fileName, schedule);
        });


        save.addActionListener(e -> {
            Export.execute(model);
        });

        open.addActionListener(e -> {
            Import.execute(model);
        });


        filter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String text = search.getText();
                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    try {
                        checkName(search);
                        rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                    } catch (MyException e) {
                        JOptionPane.showMessageDialog(null, e.getMessage());
                    }
                }

            }
        });

        search.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                JTextField source = (JTextField) e.getComponent();
                source.setText("");
                source.removeFocusListener(this);
            }

        });

        driversInfo.addActionListener(event -> {
            this.driver();
        });

        violations.addActionListener(event -> {
            this.violations();
        });
    }

    public void driver() {
        JFrame driverList = new JFrame("Список водителей");
        driverList.setSize(500, 300);
        driverList.setLocation(100, 100);
        driverList.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        driverList.setVisible(true);

        toolBar = new JToolBar("Панель инструментов");
        driverList.setLayout(new BorderLayout());
        driverList.add(toolBar, BorderLayout.NORTH);

        addNew = new JButton(new ImageIcon("./img/addNew.png"));
        addNew.setToolTipText("Добавить");
        addNew.setBorderPainted(false);
        toolBar.add(addNew);

        deleted = new JButton(new ImageIcon("./img/delete.png"));
        deleted.setToolTipText("Удалить");
        deleted.setBorderPainted(false);
        toolBar.add(deleted);

        String[] columns = {"Имя водителя","Стаж работы", "Класс"};
        String[][] data = {};
        model = new DefaultTableModel(data, columns);
        bus = new JTable(model);
        rowSorter = new TableRowSorter<>(bus.getModel());
        bus.setRowSorter(rowSorter);
        scroll = new JScrollPane(bus);
        ArrayList<Integer> ids = new ArrayList<Integer>();

        ArrayList<Object[]> result = db.getDriversInfo();

        for (int i = 0; i < result.size(); i += 1) {
            Object[] row = result.get(i);
            model.addRow(row);
        }

        addNew.addActionListener(event -> {
            String inputText = JOptionPane.showInputDialog("Введите имя вводителя");
            Object[] row2 = new Object[3];
            row2[0] = inputText;

            String inputText2 = JOptionPane.showInputDialog("Введите стаж работы");
            row2[1] = inputText2;

            String inputText3 = JOptionPane.showInputDialog("Введите класс");
            row2[2] = inputText3;

            String inputText4 = JOptionPane.showInputDialog("Введите номер автобуса");

            try {
                String sql = "INSERT INTO drivers (\"fullName\", \"class\", \"beginningDate\", \"busId\") VALUES(?, ?, now() - INTERVAL '" + inputText2 + " year', (SELECT id FROM buses b WHERE b. \"number\" = ?));";
                PreparedStatement stmt = this.connection.prepareStatement(sql);
                stmt.setString(1,  inputText);
                stmt.setString(2, inputText3);
                stmt.setInt(3, new Integer(inputText4));
                System.out.println(stmt.toString());
                stmt.executeUpdate();
                model.addRow(row2);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        });

        deleted.addActionListener(event -> {
            int rowDeleted = bus.getSelectedRow();
            if (rowDeleted > -1) {
                Integer idToDelete = ids.get(rowDeleted);
                try {
                    DefaultTableModel busModel= (DefaultTableModel) bus.getModel();
                    busModel.removeRow(rowDeleted);
                    String sql = "DELETE FROM drivers WHERE \"id\" = ?";
                    PreparedStatement stmt = this.connection.prepareStatement(sql);
                    stmt.setInt(1, idToDelete);
                    stmt.executeUpdate();
                } catch (SQLException e) {}
            }
        });

        driverList.add(scroll, BorderLayout.CENTER);
    }
    public void violations() {
        JFrame violationList = new JFrame("Список нарушений");
        violationList.setSize(500, 300);
        violationList.setLocation(100, 100);
        violationList.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        violationList.setVisible(true);

        toolBar = new JToolBar("Панель инструментов");
        violationList.setLayout(new BorderLayout());
        violationList.add(toolBar, BorderLayout.NORTH);

        addNew = new JButton(new ImageIcon("./img/addNew.png"));
        addNew.setToolTipText("Добавить");
        addNew.setBorderPainted(false);
        toolBar.add(addNew);

        deleted = new JButton(new ImageIcon("./img/delete.png"));
        deleted.setToolTipText("Удалить");
        deleted.setBorderPainted(false);
        toolBar.add(deleted);

        String[] columns = {"Номер автобуса", "Нарушения"};
        String[][] data = {};
        model = new DefaultTableModel(data, columns);
        bus = new JTable(model);
        rowSorter = new TableRowSorter<>(bus.getModel());
        bus.setRowSorter(rowSorter);
        scroll = new JScrollPane(bus);

        ArrayList<Integer> ids = new ArrayList<Integer>();
        ArrayList<Object[]> result = db.getViolations();

        for (int i = 0; i < result.size(); i += 1) {
            Object[] row = result.get(i);
            model.addRow(row);
        }


        addNew.addActionListener(event -> {
            String inputText = JOptionPane.showInputDialog("Введите номер автобуса");
            Object[] row2 = new Object[3];
            row2[0] = inputText;

            String inputText2 = JOptionPane.showInputDialog("Введите нарушение");
            row2[1] = inputText2;

            try {
                String sql = "insert into violations(\"busId\", \"text\") values (\n" +
                        "\t(select id from buses b where b.\"number\" = ?),\n" +
                        "\t?\n" +
                        ");";
                PreparedStatement stmt = this.connection.prepareStatement(sql);
                stmt.setInt(1,  new Integer(inputText));
                stmt.setString(2, inputText2);
                stmt.executeUpdate();
            } catch (SQLException e) {}

            model.addRow(row2);

        });

        deleted.addActionListener(event -> {
            int rowDeleted = bus.getSelectedRow();
            if (rowDeleted > -1) {
                Integer idToDelete = ids.get(rowDeleted);

                try {
                    DefaultTableModel busModel = (DefaultTableModel) bus.getModel();
                    busModel.removeRow(rowDeleted);

                    String sql = "DELETE FROM violations WHERE \"id\" = ?";
                    PreparedStatement stmt = this.connection.prepareStatement(sql);
                    stmt.setInt(1, idToDelete);
                    stmt.executeUpdate();
                } catch (SQLException e) {}
            }
        });

        violationList.add(scroll, BorderLayout.CENTER);
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        new Main().show();
    }
}