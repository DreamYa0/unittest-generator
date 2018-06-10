package com.zeratul.plugin.generator.http;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class HttpExcelGenerator {

    private File outFile;

    public HttpExcelGenerator(File outFile) {
        this.outFile = outFile;
    }

    public void generate() {
        HSSFWorkbook workbook = new HSSFWorkbook();
        String name = "testMethod";
        int index = workbook.getSheetIndex(name);
        HSSFSheet sheet;
        if (index == -1)
            sheet = workbook.createSheet(name);
        else {
            System.out.println("repeat name : " + name);
            sheet = workbook.createSheet(name + "1");
        }
        sheet.setDefaultColumnWidth(15);
        int i = 0;
        //**预留
        writeCell(sheet, i, 0, "caseName");
        writeCell(sheet, i++, 1, "用例标题", false);
        writeCell(sheet, i, 0, "httpMode");
        writeCell(sheet, i++, 1, "", false);
        writeCell(sheet, i, 0, "httpHost");
        writeCell(sheet, i++, 1, "", false);
        writeCell(sheet, i, 0, "httpMethod");
        writeCell(sheet, i++, 1, "", false);
        /*List<String> paramNameList = method.getParamNameList();
        for (String paramName : paramNameList) {
            writeCell(sheet, i, 0, paramName);
        }
        JSONObject paramJson = gm.paramJSONObject;
        writeObjectToSheet(i, paramJson, sheet);*/
        workbook.createSheet("exceptResult");
        try {
            FileOutputStream fo = new FileOutputStream(outFile);
            workbook.write(fo);
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private int writeObjectToSheet(int startIndex, JSONObject jobj, HSSFSheet sheet, String... prefix) {
        final int[] i = {startIndex};
        jobj.keySet().forEach(param -> {
            Object obj = jobj.get(param);
            if (obj != null && obj instanceof JSONObject) {
                i[0] = writeObjectToSheet(i[0], (JSONObject) obj, sheet, param);
            } else if (obj == null) {
                if (prefix.length == 0) {
                    writeCell(sheet, i[0]++, 0, param);
                } else {
                    writeCell(sheet, i[0]++, 0, prefix[0] + "." + param);
                }
            } else {
                System.out.println("sth is wrong");
            }
        });
        /*Iterator<String> it = jobj.keySet().iterator();
        while (it.hasNext()) {
            String param = it.next();
            Object obj = jobj.get(param);
            if (obj != null && obj instanceof JSONObject) {
                i = writeObjectToSheet(i, (JSONObject) obj, sheet, param);
            } else if (obj == null) {
                if (prefix.length == 0)
                    writeCell(sheet, i++, 0, param);
                else
                    writeCell(sheet, i++, 0, prefix[0] + "." + param);
            } else {
                System.out.println("sth is wrong");
            }
        }*/
        return i[0];
    }

    private void writeCell(HSSFSheet sheet, int rowNum, int cellNum, String value, boolean... isCreate) {
        HSSFRow row;
        if (isCreate.length == 0 || isCreate[0]) {
            row = sheet.createRow(rowNum);
        } else {
            row = sheet.getRow(rowNum);
        }
        HSSFCell cell = row.createCell(cellNum);
        cell.setCellValue(value);
    }
}
