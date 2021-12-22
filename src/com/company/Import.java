package com.company;

import org.w3c.dom.*;

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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Import extends Thread {
    private Object mutex;
    private DefaultTableModel model;

    public Import(String name, DefaultTableModel model) {
        this.mutex = new Object();;
        this.model = model;
        setName(name);
        setPriority(MIN_PRIORITY);
    }

    public static void execute(DefaultTableModel model) {
        Import import2 = new Import("export", model);
        import2.start();
        try {
            import2.join();
        } catch (InterruptedException er) {
            er.printStackTrace();
        }
    }

    public void run() {
        synchronized (mutex) {
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
            }
            mutex.notify();
        }

    }
}
