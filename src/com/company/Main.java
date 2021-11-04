package com.company;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.Charset;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;


public class Main {
    // Объявления графических компонентов
    private JFrame busList;
    private DefaultTableModel model;
    private JButton save, addNew, deleted, edit, open;
    private JToolBar toolBar;
    private JScrollPane scroll;
    private JTable bus;
    private JTextField search;
    private JButton filter;
    private TableRowSorter<TableModel> rowSorter;

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

        edit = new JButton(new ImageIcon("./img/edit.png"));
        edit.setToolTipText("Редактировать");
        edit.setBorderPainted(false);
        toolBar.add(edit);

        open = new JButton(new ImageIcon("./img/open.png"));
        open.setToolTipText("Редактировать");
        open.setBorderPainted(false);
        toolBar.add(open);

        String [] columns = {"Имя водителя", "Номер маршрута", "Интервал движения"};
        String [][] data = {{"Александр Александрович", "1", "каждые 10 минут"}, {"Алексей Алексеевич", "2", "каждые 5 минут"}};

        model= new DefaultTableModel(data, columns);
        bus = new JTable(model);
        rowSorter = new TableRowSorter<>(bus.getModel());
        bus.setRowSorter(rowSorter);
        scroll = new JScrollPane(bus);

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
                FileDialog save = new FileDialog(busList, "Сохранение данных", FileDialog.SAVE);
                save.setFile("*.txt");
                save.setVisible(true); 
                String fileName = save.getDirectory() + save.getFile();
                if(fileName == null) return;

                try {
                    BufferedWriter writer = new BufferedWriter (new FileWriter(fileName));
                    for (int i = 0; i < model.getRowCount(); i++)
                        for (int j = 0; j < model.getColumnCount(); j++) // Для всех столбцов
                            {
                                writer.write((String) model.getValueAt(i, j));
                                writer.write("\n");
                            }
                writer.close();
            }
                catch(IOException er) // Ошибка записи в файл
            {
                er.printStackTrace();
            }

        }
        });

        open.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FileDialog open = new FileDialog(busList, "Сохранение данных", FileDialog.LOAD);
                open.setFile("*.txt");
                open.setVisible(true);
                String fileName = open.getDirectory() + open.getFile();
                if(fileName == null) return; // Если пользователь нажал «отмена»

                try {
                    BufferedReader reader = new BufferedReader(new FileReader(fileName));
                    Charset.forName("UTF-8").newEncoder();
                    int rows = model.getRowCount();
                    for (int i = 0; i < rows; i++) model.removeRow(0); // Очистка таблицы
                    String author;
                    do {
                        author = reader.readLine();
                        if(author != null)
                        {
                            String title = reader.readLine();
                            String have = reader.readLine();
                            model.addRow(new String[]{author, title, have});
                        }}
                    while(author != null);
                        reader.close();
                    }
                catch (FileNotFoundException err) {err.printStackTrace();} // файл не найден
                catch (IOException err) {err.printStackTrace();
                }}
        });

        filter.addActionListener (new ActionListener() {
            public void actionPerformed (ActionEvent event) {
                String text = search.getText();
                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    try {
                        checkName(search);
                        rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                    } catch (MyException e) {
                        JOptionPane.showMessageDialog(null,e.getMessage());
                    }
                }


            }});

        addNew.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent event) {
               String inputText = JOptionPane.showInputDialog("Введите имя");
               Object[] row2 = new Object[3];
               row2[0] = inputText;
               model.addRow(row2);

            }
        });

        search.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                JTextField source = (JTextField)e.getComponent();
                source.setText("");
                source.removeFocusListener(this);
            }

        });
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
    new Main().show();
    } }