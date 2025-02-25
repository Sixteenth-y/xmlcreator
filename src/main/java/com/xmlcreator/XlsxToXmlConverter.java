package com.xmlcreator;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class XlsxToXmlConverter {
    static String[] words = {"NOT_FOUND", "PGM", "neogeo.zip", "机皇 MV-6F "};

    @SuppressWarnings("resource")
    public static void run(String[] args) {
        String xlsxFilePath = "H:\\work\\work2501\\files\\arcade.xlsx"; // 输入XLSX文件路径
        String xmlFilePath = "H:\\work\\work2501\\files\\games_test.xml";   // 输出XML文件路径
        String romFilterFilePath = "H:\\work\\work2501\\files\\rom_filter.txt";
        HashSet<String> romNameSet = new HashSet<>();
        HashSet<String> romFilterSet = new HashSet<>();

        //加载rom过滤器
        loadRomFilter(romFilterFilePath, romFilterSet);

        try {
            // 读取XLSX文件
            FileInputStream file = new FileInputStream(new File(xlsxFilePath));
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);

            // 创建XML文档
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            // create root node
            Element rootElement = doc.createElement("gameList");
            doc.appendChild(rootElement);

            Row headRow = sheet.getRow(0);

            if (headRow == null) {
                throw new RuntimeException("缺少xlsx表头");
            }

            List<String> tilesList = IntStream.range(0, headRow.getLastCellNum())
                .mapToObj(i -> getCellValue(headRow.getCell(i)))
                .collect(Collectors.toList());

            boolean isSkip = false;
            // run each row
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; // 跳过表头
                }

                // 创建game节点
                Element gameElement = doc.createElement("game");

                //过滤重名rom
                String romName = getCellValue(row.getCell(headRow.getFirstCellNum()));
                if (romNameSet.contains(romName)) {
                    continue;
                } else if (romFilterSet.contains(romName)) {
                    continue;                    
                }
                romNameSet.add(romName);

                // read the data form cell and put into the XML node
                for (int i = 0; i < headRow.getLastCellNum(); i++) {
                    String title = tilesList.get(i);
                    Element cellElement = doc.createElement(title);

                    //过滤节点
                    if (!cellFilter(row.getCell(i))) {
                        isSkip = true;
                        break;
                    }

                    cellElement.appendChild(doc.createTextNode(getCellValue(row.getCell(i))));
                    gameElement.appendChild(cellElement);
                }

                // 将game节点添加到根节点
                if (isSkip) {
                    isSkip = false;
                    continue;
                }
                rootElement.appendChild(gameElement);
            }

            // 将XML文档写入文件
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(xmlFilePath));
            transformer.transform(source, result);

            System.out.println("XML文件生成成功: " + xmlFilePath);

            // close off workbook
            workbook.close();
            file.close();

        } catch (IOException | ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    // get value form cell
    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    private static boolean cellFilter(Cell cell) {
        if (cell == null) {
            return false;
        }

        String value;

        switch (cell.getCellType()) {
            case STRING:
                value =  cell.getStringCellValue();
                break;
            case NUMERIC:
                value = String.valueOf((int) cell.getNumericCellValue());
                break;
            case BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            default:
                value = "";
        }

        for (String word : words) {
            if (word.equals(value)) 
                return false;
        }
        
        return true;
    }

    private static void loadRomFilter(String filePath, HashSet<String> set) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                set.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
}