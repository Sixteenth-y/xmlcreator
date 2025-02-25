package com.xmlcreator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class VideoSeeker {
    static void seekVideo(String inputFilePath, String outputFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             FileWriter writer = new FileWriter(outputFilePath)) {

            String line;
            int lineNumber = 0;

            // 逐行读取输入文件
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim(); // 去除前后空格

                // 检查文件是否存在
                File file = new File(line);
                if (!file.exists()) {
                    // 如果文件不存在，写入输出文件
                    String result = "Line " + lineNumber + ": " + line + "\n";
                    writer.write("NOT_FOUND" + "\n");
                    System.out.println("File not found: " + line + (lineNumber + 1));
                } else {
                    writer.write(line + "\n");
                }
            }

            System.out.println("Check completed. Results written to " + outputFilePath);

        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }
}
