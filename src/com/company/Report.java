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
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalTime;

import java.util.ArrayList;


public class Report extends Thread {
    private Object mutex;
    private String fileName;
    private String schedule;

    public Report (String name, String fileName, String schedule) {
        this.mutex = new Object();;
        this.fileName = fileName;
        this.schedule = schedule;
        setName(name);
        setPriority(MIN_PRIORITY);
    }

    public static void execute(String fileName, String schedule) {
        Report report2 = new Report("export", fileName, schedule);
        report2.start();
        try {
            report2.join();
        } catch (InterruptedException er) {
            er.printStackTrace();
        }
    }

    public void run() {
        synchronized (mutex) {
            try {
                BufferedWriter writer = new BufferedWriter (new FileWriter(fileName));
                writer.write(schedule);
                writer.close();
            }
            catch(IOException er) {
                er.printStackTrace();
            }

        }}}
