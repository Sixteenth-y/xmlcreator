package com.xmlcreator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class XmlToXlsxConverter {
    public static void run(String[] args) {
        String xlsxFilePath = "H:\\work\\work2501\\files\\arcade.xlsx"; // 输入XLSX文件路径
        String xmlFilePath = "H:\\work\\work2501\\files\\games_test.xml";   // 输出XML文件路径
    
        try {
            FileInputStream file = new FileInputStream(new File(xmlFilePath));
            SAXReader reader = new SAXReader();
            Document doc = reader.read(file);

            Element root = doc.getRootElement();
            List<Element> elements = root.elements();
            Element firstElm = elements.get(0); 
            List<String> titles = IntStream.range(0, firstElm.nodeCount())
                .mapToObj(i -> firstElm.node(i).getName())
                .collect(Collectors.toList()); 

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet(root.getName());
            
            


        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
    }
}
