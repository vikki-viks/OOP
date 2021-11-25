package com.company;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.Charset;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.sql.*;

public class Main {
    // Объявления графических компонентов
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

    private class MyException extends Exception {
        public MyException() {
        super ("Вы не ввели название книги для поиска"); }}
    private void checkName (JTextField bName) throws MyException {
        String sName = bName.getText();
        if (sName.contains("Поиск")) throw new MyException();
    }
//    private class MyException2 extends Exception {
//        public MyException2() {
//            super ("Введите имя водителя"); }}
//    private void checkName2 (JTextField name) throws MyException2 {
//        String sName = name.getText();
//        if (sName.contains("Поиск")) throw new MyException2();
//    }

    public void show() {
        String jdbcURL = "jdbc:postgresql://localhost:5432/bus";
        String username = "postgres";
        String password = "postgres";
        try {
            this.connection = DriverManager.getConnection(jdbcURL, username, password);
        } catch (SQLException e) {
            System.out.println("Error");
            e.printStackTrace();
        }
// Создание окна
        busList = new JFrame("Список маршрутов");
        busList.setSize(500, 300);
        busList.setLocation(100, 100);
        busList.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

// Создание кнопок и прикрепление иконок
        toolBar = new JToolBar("Панель инструментов");
        busList.setLayout(new BorderLayout());
        busList.add(toolBar, BorderLayout.NORTH);
        save = new JButton(new ImageIcon("./img/save.png"));
        save.setToolTipText("Сохранить");
        save.setBorderPainted(false);
        toolBar.add(save);

        addNew = new JButton(new ImageIcon("./img/addNew.png"));
        addNew.setToolTipText("Добавить");
        addNew.setBorderPainted(false);
        toolBar.add(addNew);

        deleted = new JButton(new ImageIcon("./img/delete.png"));
        deleted.setToolTipText("Удалить");
        deleted.setBorderPainted(false);
        toolBar.add(deleted);

        open = new JButton(new ImageIcon("./img/open.png"));
        open.setToolTipText("Редактировать");
        open.setBorderPainted(false);
        toolBar.add(open);

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

        String[] columns = {"Имя водителя", "Номер маршрута", "Интервал движения"};
        String[][] data = {};

        model = new DefaultTableModel(data, columns);
        bus = new JTable(model);
        rowSorter = new TableRowSorter<>(bus.getModel());
        bus.setRowSorter(rowSorter);
        scroll = new JScrollPane(bus);

        try {
            Statement stmt = this.connection.createStatement();
            ResultSet rs = stmt.executeQuery("select \"fullName\", \"intervalInMinutes\", \"number\" from drivers d\n" +
                    "join buses b on d.\"busId\" = b.id");
            while (rs.next()) {
                Object[] row = new Object[3];
                row[0] = rs.getString("fullName");
                row[1] = rs.getString("number");
                row[2] = "каждые " + rs.getString("intervalInMinutes") + " минут";
                model.addRow(row);
            }
        } catch (SQLException e) {}

// Размещение таблицы с данными
        busList.add(scroll, BorderLayout.CENTER);
// Подготовка компонентов поиска
        search = new JTextField("Поиск");
        search.setColumns(30);
        filter = new JButton("Поиск");


// Добавление компонентов на панель
        JPanel filterPanel = new JPanel();
        filterPanel.add(search);
        filterPanel.add(filter);
        busList.add(filterPanel, BorderLayout.SOUTH);
        busList.setVisible(true);


        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                 FileDialog save = new FileDialog(busList, "Сохранение данных", FileDialog.SAVE);
//                  save.setFile("");
//                  save.setVisible(true);
//                  String fileName = save.getDirectory() + save.getFile();
//                  if(fileName == null) return;

//                try {
//                    BufferedWriter writer = new BufferedWriter (new FileWriter(fileName));
//                    for (int i = 0; i < model.getRowCount(); i++)
//                        for (int j = 0; j < model.getColumnCount(); j++) // Для всех столбцов
//                            {
//                                writer.write((String) model.getValueAt(i, j));
//                                writer.write("\n");
//                            }
//                writer.close();
//            }
//                catch(IOException er) // Ошибка записи в файл
//            {
//                er.printStackTrace();
//            }
//
//        }
//        });
                try {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.newDocument();
                    Node booklist = doc.createElement("busLst");
                    doc.appendChild(booklist);
                    for (int i = 0; i < model.getRowCount(); i++) {
                        Element book = doc.createElement("bus");
                        booklist.appendChild(book);
                        book.setAttribute("name", (String) model.getValueAt(i, 0));
                        book.setAttribute("number", (String) model.getValueAt(i, 1));
                        book.setAttribute("interval", (String) model.getValueAt(i, 2));
                    }
                    try {
                        Transformer trans = TransformerFactory.newInstance().newTransformer(); // Создание файла с именем books.xml для записи документа
                        java.io.FileWriter fw = new FileWriter("bus.xml");
                        trans.transform(new DOMSource(doc), new StreamResult(fw));
                    } catch (TransformerConfigurationException error) {
                        error.printStackTrace();
                    } catch (TransformerException error) {
                        error.printStackTrace();
                    } catch (IOException error) {
                        error.printStackTrace();
                    }

                } catch (ParserConfigurationException ex) {
                    ex.printStackTrace();
                }


            }
        });

        open.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                FileDialog open = new FileDialog(busList, "Сохранение данных", FileDialog.LOAD);
//                open.setFile("");
//                open.setVisible(true);
//                String fileName = open.getDirectory() + open.getFile();
//                if(fileName == null) return; // Если пользователь нажал «отмена»

//                try {
//                    BufferedReader reader = new BufferedReader(new FileReader(fileName));
//                    Charset.forName("UTF-8").newEncoder();
//                    int rows = model.getRowCount();
//                    for (int i = 0; i < rows; i++) model.removeRow(0); // Очистка таблицы
//                    String author;
//                    do {
//                        author = reader.readLine();
//                        if(author != null)
//                        {
//                            String title = reader.readLine();
//                            String have = reader.readLine();
//                            model.addRow(new String[]{author, title, have});
//                        }}
//                    while(author != null);
//                        reader.close();
//                    }
//                catch (FileNotFoundException err) {err.printStackTrace();} // файл не найден
//                catch (IOException err) {err.printStackTrace();
//                }}
//        });
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

                try {
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.newDocument();
                    DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    doc = dBuilder.parse(new File("bus.xml"));
                    doc.getDocumentElement().normalize();
                    NodeList nlBooks = doc.getElementsByTagName("bus");
                    for (int temp = 0; temp < nlBooks.getLength(); temp++) {
                        Node elem = nlBooks.item(temp);
                        NamedNodeMap attrs = elem.getAttributes();
                        String name = attrs.getNamedItem("name").getNodeValue();
                        String number = attrs.getNamedItem("number").getNodeValue();
                        String interval = attrs.getNamedItem("interval").getNodeValue();
                        model.addRow(new String[]{name, number, interval});
                    }
                } catch (Exception er2) {
                    er2.printStackTrace();
                } // Обработка ошибки парсера при чтении данных из XML-файла
            }
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

        addNew.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String inputText = JOptionPane.showInputDialog("Введите имя");
                Object[] row2 = new Object[3];
                row2[0] = inputText;

                String inputText2 = JOptionPane.showInputDialog("Введите номер маршрута");
                row2[1] = inputText2;

                String inputText3 = JOptionPane.showInputDialog("Введите интервал движения");
                row2[2] = inputText3;
                model.addRow(row2);

            }
        });
        deleted.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                int rowDeleted = bus.getSelectedRow();
                if (rowDeleted > -1) {
                    DefaultTableModel busModel= (DefaultTableModel) bus.getModel();
                    busModel.removeRow(rowDeleted);
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
// Создание окна
            JFrame driverList = new JFrame("Список водителей");
            driverList.setSize(500, 300);
            driverList.setLocation(100, 100);
            driverList.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            driverList.setVisible(true);

            String[] columns = {"Имя водителя","Стаж работы", "Класс"};
            String[][] data = {};
            model = new DefaultTableModel(data, columns);
            bus = new JTable(model);
            rowSorter = new TableRowSorter<>(bus.getModel());
            bus.setRowSorter(rowSorter);
            scroll = new JScrollPane(bus);

            try {
                Statement stmt = this.connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT \"fullName\", \"class\", DATE_PART('year', AGE(now(), \"beginningDate\")) as years FROM drivers;");
                while (rs.next()) {
                    Object[] row = new Object[3];
                    row[0] = rs.getString("fullName");
                    row[2] = rs.getString("class");
                    row[1] =  rs.getString("years") ;
                    model.addRow(row);
                }
            } catch (SQLException e) {}

// Размещение таблицы с данными
            driverList.add(scroll, BorderLayout.CENTER);
    }
    public void violations() {
// Создание окна
        JFrame violationList = new JFrame("Список водителей");
        violationList.setSize(500, 300);
        violationList.setLocation(100, 100);
        violationList.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        violationList.setVisible(true);

        String[] columns = {"Номер автобуса", "Нарушения"};
        String[][] data = {};
        model = new DefaultTableModel(data, columns);
        bus = new JTable(model);
        rowSorter = new TableRowSorter<>(bus.getModel());
        bus.setRowSorter(rowSorter);
        scroll = new JScrollPane(bus);

        try {
            Statement stmt = this.connection.createStatement();
            ResultSet rs = stmt.executeQuery("select \"text\",\"number\" from violations v\n" +
                    "join buses b on v.\"busId\" = b.id;");
            while (rs.next()) {
                Object[] row = new Object[3];
                row[0] = rs.getString("number");
                row[1] = rs.getString("text");

                model.addRow(row);
            }
        } catch (SQLException e) {}








// Размещение таблицы с данными
        violationList.add(scroll, BorderLayout.CENTER);
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {


        new Main().show();
    } }