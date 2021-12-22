package com.company;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;

public class Export extends Thread {
    private Object mutex;
    private DefaultTableModel model;

    public Export(String name, DefaultTableModel model) {
        this.mutex = new Object();
        this.model = model;
        setName(name);
        setPriority(MIN_PRIORITY);
    }

    public static void execute(DefaultTableModel model) {
        Export export = new Export("export", model);
        export.start();
        try {
            export.join();
        } catch (InterruptedException er) {
            er.printStackTrace();
        }
    }

    public void run() {
        synchronized (mutex) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.newDocument();
                Node bookList = doc.createElement("busLst");
                doc.appendChild(bookList);
                for (int i = 0; i < model.getRowCount(); i++) {
                    Element book = doc.createElement("bus");
                    bookList.appendChild(book);
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
            System.out.println("finished");
            mutex.notify();
        }

    }
}
