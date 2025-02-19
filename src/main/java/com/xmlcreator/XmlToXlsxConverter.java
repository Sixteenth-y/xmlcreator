package com.xmlcreator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilderFactory;

public class XmlToXlsxConverter {
    public static void run(String[] args) {
        String xlsxFilePath = "H:\\work\\work2501\\files\\arcade.xlsx"; // 输入XLSX文件路径
        String xmlFilePath = "H:\\work\\work2501\\files\\games_test.xml";   // 输出XML文件路径
    
        try {
            FileInputStream file = new FileInputStream(new File(xmlFilePath));

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilderFactory docBuilder = docFactory.
            

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
