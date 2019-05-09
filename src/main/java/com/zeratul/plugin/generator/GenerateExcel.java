package com.zeratul.plugin.generator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.testng.Reporter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GenerateExcel {

    public GenerateExcel(List<GenerateMethod> mList, File outFile) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        for (GenerateMethod gm : mList) {
            HSSFSheet sheet = workbook.createSheet("testCase");
            sheet.setDefaultColumnWidth(15);
            int i = 0;
            //**预留
            writeCell(sheet, i, 0, "caseName");
            writeCell(sheet, i++, 1, "用例标题", false);
            writeCell(sheet, i, 0, "assertResult");
            writeCell(sheet, i++, 1, "", false);
            writeCell(sheet, i, 0, "autotest");
            writeCell(sheet, i++, 1, "", false);
            writeObjectToSheet(i, gm.getRequestMap(), sheet, null, gm.getMethod());
        }
        FileOutputStream fo;
        workbook.createSheet("exceptResult");
        try {
            fo = new FileOutputStream(outFile);
            workbook.write(fo);
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private int writeObjectToSheet(int startIndex, Map<String, Object> maps, HSSFSheet sheet, String prefix, Method method) {
        Iterator<String> it = maps.keySet().iterator();
        int i = startIndex;
        while (it.hasNext()) {
            String param = it.next();
            Object obj = maps.get(param);
            if (obj instanceof Map) {
                i = writeObjectToSheet(i, (Map) obj, sheet, param, method);
            } else if (obj == null) {
                if (prefix == null) {
                    writeCell(sheet, i++, 0, param);
                } else {
                    writeCell(sheet, i++, 0, prefix + "." + param);
                }
            } else {
                Reporter.log("--------------" + method.getName() + "方法：生成excel行字段名称异常！------------", true);
            }
        }
        return i;
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
