package com.atomic.plugin.generator.http;

import com.atomic.plugin.generator.GenerateXml;
import com.atomic.plugin.generator.YmalCaseFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 生成REST请求测试类
 * 测试用例使用yaml格式文件
 * 如果测试用例文件已存在，不覆盖，文件名追加_bak创建新文件
 */
public class HttpGenerator {

    //生成文件目录
    private static final String BASE_DIR = "src/test/java/com/atomic/autotest/";
    private static final String SCRIPT_DIR = BASE_DIR + "restService/";
    private static final String CASE_DIR = "src/test/resources/com/atomic/autotest/restService/";
    private static String httpServiceName = "";

    public static void createRestApiCase(String serviceName, String... classify) {
        httpServiceName = serviceName;
        if (classify != null && classify.length == 1) {
            createTestFile(classify[0]);
            // createExcelFile(classify[0]);
            createYamlFile(classify[0]);
        } else {
            createTestFile();
            // createExcelFile();
            createYamlFile();
        }
        // 生成testng.xml文件
        generateRestTestBase();
        GenerateXml.getInstance().generateTestNGXml();
        System.out.println("--------------------测试类" + serviceName + "测试类生成成功！--------------------");
    }

    private static void createTestFile(String... classify) {
        String outName = "Test" + httpServiceName + ".java";
        File outParent = checkParentFile(SCRIPT_DIR, classify);
        File outFile = new File(outParent, outName);
        if (outFile.exists()) {
            outFile.renameTo(new File(outParent, outName + ".bak"));
            // outFile = new File(outParent,outName + ".bak");  //防止覆盖
        }
        try {
            //import 写入
            String afterImportStr;
            if (classify != null && classify.length == 1) {
                afterImportStr = createRestTemplate(classify[0].toLowerCase());
            } else {
                afterImportStr = createRestTemplate();
            }
            afterImportStr = afterImportStr.replaceAll("\\$serviceName", httpServiceName);
            //写入文件
            BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
            bw.write(afterImportStr);
            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成REST API 接口测试用例基类
     */
    private static void generateRestTestBase() {
        File parentFile = checkParentFile(BASE_DIR);
        String baseTestFile = "RestTestBase.java";
        File f = new File(parentFile, baseTestFile);
        if (f.exists()) {
            return;
        }
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(f));
            bufferedWriter.write("package com.atomic.autotest;\n" +
                    "\n" +
                    "import com.atomic.BaseRestful;\n" +
                    "\n" +
                    "public abstract class RestTestBase extends BaseRestful {\n" +
                    "\n" +
                    "}");
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建数据驱动的Excel文件
     */
    private static void createExcelFile(String... classify) {
        File parentFile = checkParentFile(CASE_DIR, classify);
        File outFile = new File(parentFile, "Test" + httpServiceName + ".xls");
        if (outFile.exists()) {
            outFile.renameTo(new File(parentFile, "Test" + httpServiceName + "_bak..xls"));
            // outFile = new File(parentFile, serviceName + "_bak.xls"); //防止覆盖
        }
        HttpExcelGenerator hg = new HttpExcelGenerator(outFile);
        hg.generate();
        // handleHttpFile(outFile);
    }

    /**
     * 创建数据驱动的Yaml文件
     */
    private static void createYamlFile(String... classify) {
        File parentFile = checkParentFile(CASE_DIR, classify);
        File outFile = new File(parentFile, "Test" + httpServiceName + ".yaml");
        if (outFile.exists()) {
            outFile.renameTo(new File(parentFile, "Test" + httpServiceName + "_bak.yaml"));
        }
        // HttpExcelGenerator hg = new HttpExcelGenerator(outFile);
        // hg.generate();
        YmalCaseFactory.GenerateHttpYamlCase(outFile);
    }

    /**
     * 检查测试脚本和测试用例目录结构，未找到则创建
     */
    private static File checkParentFile(String dir, String... classify) {
        File parentFile;
        if (classify != null && classify.length == 1) {
            parentFile = new File(dir + classify[0].toLowerCase());
        } else {
            parentFile = new File(dir);
        }
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        return parentFile;
    }

    private static String createTemplate(String... path) {
        if (path != null && path.length == 1) {
            return "package  com.atomic.autotest.RestTestBase." + path[0] + ";\n" +
                    "\n" +
                    "import com.atomic.autotest.RestTestBase;\n" +
                    "import com.atomic.enums.Data;\n" +
                    "import org.testng.annotations.Test;\n" +
                    "\n" +
                    "import java.util.Map;\n" +
                    "\n" +
                    "\n" +
                    "public class Test$serviceName extends HttpTestBase {\n" +
                    "\n" +
                    "\t/**\n" +
                    " \t * 测试前执行，例如:获取数据库中的值,可以用新获取的值替换excel中的值、以及初始化测试数据\n" +
                    " \t * @param context excel入参\n" +
                    " \t */\n" +
                    " \t@Override\n" +
                    " \tpublic void beforeTest(Map<String, Object> context) {\n" +
                    " \t\t\n" +
                    " \t}" +
                    "\n" +
                    "\n" +
                    "\t/**\n" +
                    " \t * @RollBack( dbName = \"数据库库名\",tableName={\"表名1\",\"表名2\"})注解实现单库多表数据回滚\n" +
                    " \t * @RollBackAll( dbAndTable = \"{\"库名1.表名1\",\"库名2.表名1\"}\")注解实现多库多表数据回滚\n" +
                    " \t * Data.SINGLE(测试用例串行执行),Data.PARALLEL(测试用例并行执行)\n" +
                    " \t */\n" +
                    " \t@Test(dataProvider = Data.SINGLE,enabled = false)\n" +
                    " \tpublic void testMethod(Map<String, Object> param, Object result){\n" +
                    " \t\t\n" +
                    " \t}\n" +
                    "}";
        } else {
            return "package  com.atomic.autotest;\n" +
                    "\n" +
                    "import com.atomic.autotest.HttpTestBase;\n" +
                    "import com.atomic.enums.Data;\n" +
                    "import org.testng.annotations.Test;\n" +
                    "\n" +
                    "import java.util.Map;\n" +
                    "\n" +
                    "\n" +
                    "public class Test$serviceName extends HttpTestBase {\n" +
                    "\n" +
                    "\t/**\n" +
                    " \t * 测试前执行，例如:获取数据库中的值,可以用新获取的值替换excel中的值、以及初始化测试数据\n" +
                    " \t * @param context excel入参\n" +
                    " \t */\n" +
                    " \t@Override\n" +
                    " \tpublic void beforeTest(Map<String, Object> context) {\n" +
                    " \t\t\n" +
                    " \t}" +
                    "\n" +
                    "\n" +
                    "\t/**\n" +
                    " \t * @RollBack( dbName = \"数据库库名\",tableName={\"表名1\",\"表名2\"})注解实现单库多表数据回滚\n" +
                    " \t * @RollBackAll( dbAndTable = \"{\"库名1.表名1\",\"库名2.表名1\"}\")注解实现多库多表数据回滚\n" +
                    " \t * Data.SINGLE(测试用例串行执行),Data.PARALLEL(测试用例并行执行)\n" +
                    " \t */\n" +
                    " \t@Test(dataProvider = Data.SINGLE,enabled = false)\n" +
                    " \tpublic void testMethod(Map<String, Object> param, Object result){\n" +
                    " \t\t\n" +
                    " \t}\n" +
                    "}";
        }
    }

    private static String createRestTemplate(String... path) {
        if (path != null && path.length == 1) {
            return "package  com.atomic.autotest.restService." + path[0] + ";\n" +
                    "\n" +
                    "import com.atomic.autotest.RestTestBase;\n" +
                    "import com.atomic.enums.Data;\n" +
                    "import org.testng.annotations.Test;\n" +
                    "import io.restassured.response.Response;\n" +
                    "\n" +
                    "import java.util.Map;\n" +
                    "\n" +
                    "\n" +
                    "public class Test$serviceName extends RestTestBase {\n" +
                    "\n" +
                    "\t/**\n" +
                    " \t * 测试前执行，例如:获取数据库中的值,可以用新获取的值替换excel中的值、以及初始化测试数据\n" +
                    " \t * @param context excel入参\n" +
                    " \t */\n" +
                    " \t@Override\n" +
                    " \tpublic void beforeTest(Map<String, Object> context) {\n" +
                    " \t\t\n" +
                    " \t}" +
                    "\n" +
                    "\n" +
                    "\t/**\n" +
                    " \t * @RollBack( dbName = \"数据库库名\",tableName={\"表名1\",\"表名2\"})注解实现单库多表数据回滚\n" +
                    " \t * @RollBackAll( dbAndTable = \"{\"库名1.表名1\",\"库名2.表名1\"}\")注解实现多库多表数据回滚\n" +
                    " \t * Data.SINGLE(测试用例串行执行),Data.PARALLEL(测试用例并行执行)\n" +
                    " \t */\n" +
                    " \t@Test(dataProvider = Data.SINGLE,enabled = false)\n" +
                    " \tpublic void testMethod(Map<String, Object> param, Response result){\n" +
                    " \t\t\n" +
                    " \t}\n" +
                    "}";
        } else {
            return "package  com.atomic.autotest;\n" +
                    "\n" +
                    "import com.atomic.autotest.RestTestBase;\n" +
                    "import com.atomic.enums.Data;\n" +
                    "import org.testng.annotations.Test;\n" +
                    "import io.restassured.response.Response;\n" +
                    "\n" +
                    "import java.util.Map;\n" +
                    "\n" +
                    "\n" +
                    "public class Test$serviceName extends RestTestBase {\n" +
                    "\n" +
                    "\t/**\n" +
                    " \t * 测试前执行，例如:获取数据库中的值,可以用新获取的值替换excel中的值、以及初始化测试数据\n" +
                    " \t * @param context excel入参\n" +
                    " \t */\n" +
                    " \t@Override\n" +
                    " \tpublic void beforeTest(Map<String, Object> context) {\n" +
                    " \t\t\n" +
                    " \t}" +
                    "\n" +
                    "\n" +
                    "\t/**\n" +
                    " \t * @RollBack( dbName = \"数据库库名\",tableName={\"表名1\",\"表名2\"})注解实现单库多表数据回滚\n" +
                    " \t * @RollBackAll( dbAndTable = \"{\"库名1.表名1\",\"库名2.表名1\"}\")注解实现多库多表数据回滚\n" +
                    " \t * Data.SINGLE(测试用例串行执行),Data.PARALLEL(测试用例并行执行)\n" +
                    " \t */\n" +
                    " \t@Test(dataProvider = Data.SINGLE,enabled = false)\n" +
                    " \tpublic void testMethod(Map<String, Object> param, Response result){\n" +
                    " \t\t\n" +
                    " \t}\n" +
                    "}";
        }
    }

    public static void main(String[] args) {
        // 生成rest接口测试代码
        HttpGenerator.createRestApiCase("electronicReceipt", "cashdeskTruckBroker");
    }
}
