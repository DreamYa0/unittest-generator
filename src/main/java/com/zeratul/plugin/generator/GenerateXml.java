package com.zeratul.plugin.generator;

import com.beust.jcommander.ParameterException;
import com.google.common.io.Closer;
import com.zeratul.plugin.util.FileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author dreamyao
 * @version 1.0.0
 * @title
 * @date 2017/9/13 21:40
 */
public final class GenerateXml {

    private static final GenerateXml INSTANCE = new GenerateXml();

    private GenerateXml() {
    }

    public static GenerateXml getInstance() {
        return INSTANCE;
    }

    public void generateTestNGXml() {
        Closer closer = Closer.create();
        try {
            InputStream inputStream = FileUtils.getFileInputStream("template/testng.xml");
            if (inputStream == null) {
                throw new ParameterException("读取testng.xml模版失败！");
            }
            File outFile = new File("src/test/resources/", "testng.xml");
            if (outFile.exists()) {
                return;
            }
            BufferedReader bufferedReader = closer.register(new BufferedReader(new InputStreamReader(inputStream)));
            BufferedWriter bufferedWriter = closer.register(new BufferedWriter(new FileWriter(outFile)));
            String line;
            while (true) {
                line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                bufferedWriter.write(line);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                closer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
