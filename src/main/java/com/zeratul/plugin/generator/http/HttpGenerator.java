package com.zeratul.plugin.generator.http;

import com.zeratul.plugin.generator.GenerateXml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author chenliang
 * @title
 * @date 16/9/13 下午2:28
 * @since 1.0.0
 */
public class HttpGenerator {

    private static String httpServiceName = "";
    //生成文件目录
    private static final String FILE_DIR = "src/test/java/com/atomic/autotest/";
    private static final String EXCEL_DIR = "src/test/resources/com/atomic/autotest/";
    private static final String TEST_FILE_DIR = "src/test/java/com/atomic/autotest/";
    private static final String EXCEL_FILE_DIR = "src/test/resources/com/atomic/autotest/";

    public static void createHttpApiCase(String serviceName, String... classify) {
        httpServiceName = serviceName;
        if (classify != null && classify.length == 1) {
            createTestFile(false, classify[0]);
            createExcelFile(classify[0]);
            generateHttpTestBase();
            // 生成testng.xml文件
            GenerateXml.getInstance().generateTestNGXml();
            System.out.println("--------------------测试类" + serviceName + "测试类生成成功！--------------------");
        } else {
            createTestFile(false);
            createExcelFile();
            generateHttpTestBase();
            // 生成testng.xml文件
            GenerateXml.getInstance().generateTestNGXml();
            System.out.println("--------------------测试类" + serviceName + "测试类生成成功！--------------------");
        }
    }

    public static void createRestApiCase(String serviceName, String... classify) {
        httpServiceName = serviceName;
        if (classify != null && classify.length == 1) {
            createTestFile(true, classify[0]);
            createExcelFile(classify[0]);
            generateRestTestBase();
            // 生成testng.xml文件
            GenerateXml.getInstance().generateTestNGXml();
            System.out.println("--------------------测试类" + serviceName + "测试类生成成功！--------------------");
        } else {
            createTestFile(true);
            createExcelFile();
            generateRestTestBase();
            // 生成testng.xml文件
            GenerateXml.getInstance().generateTestNGXml();
            System.out.println("--------------------测试类" + serviceName + "测试类生成成功！--------------------");
        }
    }

    private static void createTestFile(boolean isRestApi, String... classify) {
        String outName = "Test" + httpServiceName + ".java";
        File outParent;
        if (classify != null && classify.length == 1) {
            outParent = new File(FILE_DIR + classify[0].toLowerCase());
        } else {
            outParent = new File(TEST_FILE_DIR);
        }
        //读取Process文件
        if (!outParent.exists())
            outParent.mkdirs();
        File outFile = new File(outParent, outName);
        if (outFile.exists()) {
            outFile.renameTo(new File(outParent, outName + ".bak"));
            // outFile = new File(outParent,outName + ".bak");  //防止覆盖
        }
        try {
            //import 写入
            String afterImportStr;
            if (isRestApi) {
                if (classify != null && classify.length == 1) {
                    afterImportStr = createRestTemplate(classify[0].toLowerCase());
                } else {
                    afterImportStr = createRestTemplate();
                }
            } else {
                if (classify != null && classify.length == 1) {
                    afterImportStr = createTemplate(classify[0].toLowerCase());
                } else {
                    afterImportStr = createTemplate();
                }
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
     * 生成测试基类
     */
    private static void generateHttpTestBase() {
        File parentFile = new File("src/test/java/com/atomic/autotest/");
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        String baseTestFile = "HttpTestBase.java";
        File f = new File(parentFile, baseTestFile);
        if (f.exists()) {
            return;
        }
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(f));
            bufferedWriter.write("package com.atomic.autotest;\n" +
                    "\n" +
                    "import com.atomic.BaseHttp;\n" +
                    "\n" +
                    "public abstract class HttpTestBase extends BaseHttp {\n" +
                    "\n" +
                    "}");
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成REST API 接口测试用例基类
     */
    private static void generateRestTestBase() {
        File parentFile = new File("src/test/java/com/atomic/autotest/");
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
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
        File parentFile;
        if (classify != null && classify.length == 1) {
            parentFile = new File(EXCEL_DIR + classify[0].toLowerCase());
        } else {
            parentFile = new File(EXCEL_FILE_DIR);
        }
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        File outFile = new File(parentFile, "Test" + httpServiceName + ".xls");
        if (outFile.exists()) {
            outFile.renameTo(new File(parentFile, "Test" + httpServiceName + "_bak..xls"));
            // outFile = new File(parentFile, serviceName + "_bak.xls"); //防止覆盖
        }
        HttpExcelGenerator hg = new HttpExcelGenerator(outFile);
        hg.generate();
        // handleHttpFile(outFile);
    }

    private static String createTemplate(String... path) {
        if (path != null && path.length == 1) {
            return "package  com.atomic.autotest." + path[0] + ";\n" +
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
                    " \t * @AutoAssert( checkMode = CheckMode.REC)注解实现智能化断言录制\n" +
                    " \t * @AutoAssert( checkMode = CheckMode.REPLAY)注解实现智能化断言回放\n" +
                    " \t * @Scenario 使用此注解来标记用例为某个场景的用例，框架会为场景用例自动注入很多场景相关的属性\n" +
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
                    " \t * @AutoAssert( checkMode = CheckMode.REC)注解实现智能化断言录制\n" +
                    " \t * @AutoAssert( checkMode = CheckMode.REPLAY)注解实现智能化断言回放\n" +
                    " \t * @Scenario 使用此注解来标记用例为某个场景的用例，框架会为场景用例自动注入很多场景相关的属性\n" +
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
            return "package  com.atomic.autotest." + path[0] + ";\n" +
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
                    " \t * @AutoAssert( checkMode = CheckMode.REC)注解实现智能化断言录制\n" +
                    " \t * @AutoAssert( checkMode = CheckMode.REPLAY)注解实现智能化断言回放\n" +
                    " \t * @Scenario 使用此注解来标记用例为某个场景的用例，框架会为场景用例自动注入很多场景相关的属性\n" +
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
                    " \t * @AutoAssert( checkMode = CheckMode.REC)注解实现智能化断言录制\n" +
                    " \t * @AutoAssert( checkMode = CheckMode.REPLAY)注解实现智能化断言回放\n" +
                    " \t * @Scenario 使用此注解来标记用例为某个场景的用例，框架会为场景用例自动注入很多场景相关的属性\n" +
                    " \t * Data.SINGLE(测试用例串行执行),Data.PARALLEL(测试用例并行执行)\n" +
                    " \t */\n" +
                    " \t@Test(dataProvider = Data.SINGLE,enabled = false)\n" +
                    " \tpublic void testMethod(Map<String, Object> param, Response result){\n" +
                    " \t\t\n" +
                    " \t}\n" +
                    "}";
        }
    }
}
