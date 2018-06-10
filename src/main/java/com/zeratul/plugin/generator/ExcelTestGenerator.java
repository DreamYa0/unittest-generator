package com.zeratul.plugin.generator;

import com.zeratul.plugin.java.JavaAstModel;
import com.zeratul.plugin.java.Method;
import com.zeratul.plugin.parser.JavaParser;
import com.zeratul.plugin.util.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * @author dreamyao
 * @title
 * @date 2018/4/12 下午10:46
 * @since 1.0.0
 */
public class ExcelTestGenerator {

    public static void generatorList(List<JavaParser> javas, String path) throws IOException {
        Iterator iterator = javas.iterator();

        while (iterator.hasNext()) {
            JavaParser javaParser = (JavaParser) iterator.next();
            genExcel(javaParser.getModel(), path);
        }

    }

    public static void genExcel(JavaAstModel model, String path) throws IOException {
        int first = model.packageName.indexOf(".");
        String packageName = "test" + model.packageName.substring(first, model.packageName.length());
        String basePath = path + File.separator + "resources" + File.separator + org.apache.commons.lang.StringUtils.replace(packageName, ".", File.separator);

        Method method;
        String fileName;
        String genFilePath;
        for (Iterator iterator = model.methods.iterator(); iterator.hasNext(); genExcelMethod(fileName, genFilePath, model, method)) {
            method = (Method) iterator.next();
            fileName = "Test" + StringUtils.toUpperCaseFirstOne(method.getName());
            genFilePath = basePath + File.separator + model.className.toLowerCase() + File.separator;
            File genDir = new File(genFilePath);
            if (!genDir.exists()) {
                boolean mkdirs = genDir.mkdirs();
            }
        }
    }

    private static void genExcelMethod(String filename, String path, JavaAstModel model, Method method) throws IOException {

        String excelPath = path + File.separator + filename + ".xls";
        File file = new File(excelPath);
        if (!file.exists()) {
            FileOutputStream os = new FileOutputStream(excelPath);
            HSSFWorkbook wb = new HSSFWorkbook();
            // 生成入参 sheet 页
            HSSFSheet sheet = wb.createSheet("testCase");

            byte index = 0;
            int var13 = index + 1;
            createRow(sheet, index, "caseName");
            createRow(sheet, var13++, "assertResult");
            createRow(sheet, var13++, "autotest");

            List<String> params = JavaTestGenerator.getMethodParams(model, method);
            if (CollectionUtils.isNotEmpty(params)) {
                Iterator<String> iterator = params.iterator();

                while (iterator.hasNext()) {
                    String param = iterator.next();
                    createRow(sheet, var13++, param);
                }
            }

            // 生成断言 sheet 页
            HSSFSheet exceptResultSheet = wb.createSheet("exceptResult");

            wb.write(os);
            os.flush();
            os.close();
        }
    }

    private static void createRow(HSSFSheet sheet, int rowIndex, String cellValue) {
        HSSFRow row = sheet.createRow(rowIndex);
        HSSFCell cell20 = row.createCell(0);
        cell20.setCellValue(cellValue);
        if ("caseName".equals(cellValue)) {
            HSSFCell cell = row.createCell(1);
            cell.setCellValue("用例标题");
        }
    }
}
