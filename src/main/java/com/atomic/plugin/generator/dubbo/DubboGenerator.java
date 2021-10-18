package com.atomic.plugin.generator.dubbo;

import com.atomic.plugin.generator.TestPropGenerator;
import com.atomic.plugin.generator.GenerateExcel;
import com.atomic.plugin.generator.GenerateMethod;
import com.atomic.plugin.generator.GenerateXml;
import com.atomic.plugin.generator.YmalCaseFactory;
import com.atomic.plugin.java.Services;
import com.atomic.plugin.parser.ReadApi;
import com.atomic.plugin.util.FileUtils;
import com.google.common.collect.Lists;
import com.google.common.io.Closer;
import org.testng.Reporter;

import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class DubboGenerator {

    private Class<?> clazz;
    private final List<GenerateMethod> METHODS = Lists.newArrayList();
    private String serviceName;
    private String methodName;
    private String simpleTypeName;
    private String parameterSimpleTypeName;
    private String parameterTypeSimpleTypeName;

    /**
     * 按类生成测试类
     *
     * @param clazz clazz
     */
    public void generate(Class<?> clazz) {
        this.clazz = clazz;
        init();
        serviceName = clazz.getSimpleName();
        //生成test.properties
        TestPropGenerator.generateTestProperties();
        //生成baseTest
        generateBaseNgTest();
        // 生成testng.xml文件
        GenerateXml.getInstance().generateTestNGXml();
        //按方法依次生成Test
        METHODS.forEach(gm -> {
            generateMethodTest(gm);
            // generateExcel(gm);
            generateYaml(gm);
        });
        System.out.println("测试类" + clazz.getName() + "测试类生成成功！");
    }

    /**
     * 按包路径批量生成测试类
     *
     * @param packages 包路径
     */
    public void generate(String... packages) {
        if (packages == null || packages.length == 0) {
            Reporter.log("------------------- 被测类包名不能为空！-------------------");
            throw new RuntimeException("被测类包名不能为空！");
        }
        Arrays.stream(packages).forEach(pack -> {
            ReadApi apiUtils = new ReadApi();
            List<Services> servicesList = apiUtils.getServiceList(pack);
            servicesList.forEach(services -> {
                try {
                    this.clazz = Class.forName(services.getServiceName());
                    init();
                    serviceName = clazz.getSimpleName();
                    //生成test.properties
                    TestPropGenerator.generateTestProperties();
                    //生成baseTest
                    generateBaseNgTest();
                    // 生成testng.xml文件
                    GenerateXml.getInstance().generateTestNGXml();
                    //按方法依次生成Test
                    METHODS.forEach(gm -> {
                        generateMethodTest(gm);
                        // generateExcel(gm);
                        generateYaml(gm);
                    });
                    METHODS.clear();
                } catch (ClassNotFoundException e) {
                    Reporter.log("------------------- 对应的服务名称不存在！-------------------");
                    throw new ClassCastException("对应的服务名称不存在！");
                }
                System.out.println("测试类" + services.getServiceName() + "生成测试用例成功！");
            });
        });
    }

    /**
     * 生成测试基类
     */
    private void generateBaseNgTest() {
        File parentFile = new File("src/test/java/com/atomic/autotest/");
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        String baseTestFile = "BaseNgTest.java";
        File f = new File(parentFile, baseTestFile);
        if (f.exists()) {
            return;
        }
        String resStr = "template/BaseTest.java";
        writeFileFromTemplate(resStr, f, Lists.newArrayList());
    }

    /**
     * 生成测试类
     *
     * @param m
     */
    private void generateMethodTest(GenerateMethod m) {
        File parentFile = new File("src/test/java/com/atomic/autotest/" + serviceName.toLowerCase());
        if (!parentFile.exists()) {
            boolean mkDirs = parentFile.mkdirs();
        }
        methodName = m.getMethod().getName();
        methodName = Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1);
        String baseMethodFile = "Test" + methodName + ".java";
        File outFile = new File(parentFile, baseMethodFile);
        if (outFile.exists()) {
            //不覆盖
            return;
        }
        // 返回类型
        Type returnType = m.getResult();
        List<Type> parameterTypes = Lists.newArrayList();
        getReturnParameterType(returnType, parameterTypes);
        if (parameterTypes.size() == 1) {
            Type parameterType = parameterTypes.get(0);
            String parameterTypeName = parameterType.getTypeName();
            simpleTypeName = getReturnTypeSimpleName(returnType);
            parameterSimpleTypeName = getParameterTypeSimpleName(parameterType);
            String typeName = getReturnTypeName(returnType);
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
                bw.write("package com.atomic.autotest." + serviceName.toLowerCase() + ";\n" +
                         "\n" +
                         "import com.atomic.autotest.BaseNgTest;\n" +
                         "import org.testng.annotations.Test;\n" +
                         "import com.atomic.enums.Data;\n" +
                         "import java.util.Map;\n" +
                         "import " + clazz.getName() + ";\n" +
                         "import " + typeName + ";\n" +
                         "import " + parameterTypeName + ";\n" +
                         "\n");
                String line = "public class Test$methodName extends BaseNgTest<$serviceName> {" +
                              "\n";
                bw.write(replaceLine(line));
                bw.write("\n" +
                         "\t/**\n" +
                         " \t * 测试前执行，例如:获取数据库中的值,可以用新获取的值替换excel中的值、以及初始化测试数据\n" +
                         " \t * @param context excel入参\n" +
                         " \t */\n" +
                         " \t@Override\n" +
                         " \tpublic void beforeTest(Map<String, Object> context) {\n" +
                         " \t\t\n" +
                         " \t}");
                bw.newLine();
                bw.flush();
                bw.write("\n" +
                         "\t/**\n" +
                         " \t * @RollBack( dbName = \"数据库库名\",tableName={\"表名1\",\"表名2\"})注解实现单库多表数据回滚\n" +
                         " \t * @RollBackAll( dbAndTable = \"{\"库名1.表名1\",\"库名2.表名1\"}\")注解实现多库多表数据回滚\n" +
                         " \t * @AutoTest( autoTestMode = AutoTestMode.XXXXX)注解实现自动化测试\n" +
                         " \t * @Test( dataProvider = Data.SINGLE(测试用例串行执行),Data.PARALLEL(测试用例并行执行))\n" +
                         " \t */\n" +
                         " \t@Test(dataProvider = Data.SINGLE,enabled = false)\n");
                String line1 = " \tpublic void testCase(Map<String, Object> context, $simpleTypeName<$parameterSimpleTypeName> result) {" + "\n";
                bw.write(replaceLine(line1));
                bw.write(" \t\t\n" +
                         " \t}");
                bw.newLine();
                bw.write("}");
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (parameterTypes.size() == 2) {
            String typeName = getReturnTypeName(returnType);
            Type parameterType = parameterTypes.get(0);
            Type parameterType1 = parameterTypes.get(1);
            String parameterTypeName = getReturnTypeName(parameterType);
            String parameterTypeName1 = parameterType1.getTypeName();
            simpleTypeName = getReturnTypeSimpleName(returnType);
            parameterSimpleTypeName = getReturnTypeSimpleName(parameterType);
            parameterTypeSimpleTypeName = getParameterTypeSimpleName(parameterType1);
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
                bw.write("package com.atomic.autotest." + serviceName.toLowerCase() + ";\n" +
                         "\n" +
                         "import com.atomic.autotest.BaseNgTest;\n" +
                         "import org.testng.annotations.Test;\n" +
                         "import com.atomic.enums.Data;\n" +
                         "import java.util.Map;\n" +
                         "import " + clazz.getName() + ";\n" +
                         "import " + typeName + ";\n" +
                         "import " + parameterTypeName + ";\n" +
                         "import " + parameterTypeName1 + ";\n" +
                         "\n");
                String line = "public class Test$methodName extends BaseNgTest<$serviceName> {" +
                              "\n";
                bw.write(replaceLine(line));
                bw.write("\n" +
                         "\t/**\n" +
                         " \t * 测试前执行，例如:获取数据库中的值,可以用新获取的值替换excel中的值、以及初始化测试数据\n" +
                         " \t * @param context excel入参\n" +
                         " \t */\n" +
                         " \t@Override\n" +
                         " \tpublic void beforeTest(Map<String, Object> context) {\n" +
                         " \t\t\n" +
                         " \t}");
                bw.newLine();
                bw.flush();
                bw.write("\n" +
                         "\t/**\n" +
                         " \t * @RollBack( dbName = \"数据库库名\",tableName={\"表名1\",\"表名2\"})注解实现单库多表数据回滚\n" +
                         " \t * @RollBackAll( dbAndTable = \"{\"库名1.表名1\",\"库名2.表名1\"}\")注解实现多库多表数据回滚\n" +
                         " \t * @AutoTest( autoTestMode = AutoTestMode.XXXXX)注解实现自动化测试\n" +
                         " \t * @Test( dataProvider = Data.SINGLE(测试用例串行执行),Data.PARALLEL(测试用例并行执行))\n" +
                         " \t */\n" +
                         " \t@Test(dataProvider = Data.SINGLE,enabled = false)\n");
                String line1 = " \tpublic void testCase(Map<String, Object> context, $simpleTypeName<$parameterSimpleTypeName<$parameterTypeSimpleTypeName>> result) {" + "\n";
                bw.write(replaceLine(line1));
                bw.write(" \t\t\n" +
                         " \t}");
                bw.newLine();
                bw.write("}");
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
                bw.write("package com.atomic.autotest." + serviceName.toLowerCase() + ";\n" +
                         "\n" +
                         "import com.atomic.autotest.BaseNgTest;\n" +
                         "import org.testng.annotations.Test;\n" +
                         "import com.atomic.enums.Data;\n" +
                         "import java.util.Map;\n" +
                         "import " + clazz.getName() + ";\n" +
                         "\n");
                String line = "public class Test$methodName extends BaseNgTest<$serviceName> {" +
                              "\n";
                bw.write(replaceLine(line));
                bw.write("\n" +
                         "\t/**\n" +
                         " \t * 测试前执行，例如:获取数据库中的值,可以用新获取的值替换excel中的值、以及初始化测试数据\n" +
                         " \t * @param context excel入参\n" +
                         " \t */\n" +
                         " \t@Override\n" +
                         " \tpublic void beforeTest(Map<String, Object> context) {\n" +
                         " \t\t\n" +
                         " \t}");
                bw.newLine();
                bw.flush();
                bw.write("\n" +
                         "\t/**\n" +
                         " \t * @RollBack( dbName = \"数据库库名\",tableName={\"表名1\",\"表名2\"})注解实现单库多表数据回滚\n" +
                         " \t * @RollBackAll( dbAndTable = \"{\"库名1.表名1\",\"库名2.表名1\"}\")注解实现多库多表数据回滚\n" +
                         " \t * @AutoTest( autoTestMode = AutoTestMode.XXXXX)注解实现自动化测试\n" +
                         " \t * @Test( dataProvider = Data.SINGLE(测试用例串行执行),Data.PARALLEL(测试用例并行执行))\n" +
                         " \t */\n" +
                         " \t@Test(dataProvider = Data.SINGLE,enabled = false)\n" +
                         " \tpublic void testCase(Map<String, Object> context, Object result) {\n" +
                         " \t\t\n" +
                         " \t}");
                bw.newLine();
                bw.write("}");
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void generateExcel(GenerateMethod gm) {
        File parentFile = new File("src/test/resources/com/atomic/autotest/" + serviceName.toLowerCase());
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        File outFile = new File(parentFile, "Test" + methodName + ".xls");
        // File outFile = new File(parentFile, "Test" + methodName + ".csv");
        if (outFile.exists()) {
            return; //不覆盖
        }
        new GenerateExcel(Lists.newArrayList(gm), outFile);
        // handleFile(Lists.newArrayList(gm), outFile);
    }

    private void generateYaml(GenerateMethod gm) {
        File parentFile = new File("src/test/resources/com/atomic/autotest/" + serviceName.toLowerCase());
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        //        File outFile = new File(parentFile, "Test" + methodName + ".xls");
        File outFile = new File(parentFile, "Test" + methodName + ".yaml");
        if (outFile.exists()) {
            return; //不覆盖
        }
        //        new GenerateExcel(Lists.newArrayList(gm), outFile);
        YmalCaseFactory.GenerateDubboYamlCase(outFile);
        // handleFile(Lists.newArrayList(gm), outFile);
    }

    private String replaceLine(String s) {
        s = s.replaceAll("\\$serviceName", serviceName);
        s = s.replaceAll("\\$methodName", methodName);
        s = s.replaceAll("\\$servicename", serviceName.toLowerCase());
        s = s.replaceAll("\\$simpleTypeName", simpleTypeName);
        s = s.replaceAll("\\$parameterSimpleTypeName", parameterSimpleTypeName);
        s = s.replaceAll("\\$parameterTypeSimpleTypeName", parameterTypeSimpleTypeName);
        return s;
    }

    private void writeFileFromTemplate(String templeteFilePath, File outFile, List<String> importList) {
        Closer closer = Closer.create();
        try {
            InputStream in = FileUtils.getFileInputStream(templeteFilePath);
            BufferedReader br = closer.register(new BufferedReader(new InputStreamReader(in)));
            BufferedWriter bw = closer.register(new BufferedWriter(new FileWriter(outFile)));
            String line;
            while (true) {
                line = br.readLine();
                if (line == null) {
                    break;
                }
                if (line.startsWith("//import") && importList != null) {
                    for (String s : importList) {
                        bw.write("import " + s + ";");
                        bw.newLine();
                    }
                    bw.flush();
                    continue;
                }
                String outline = replaceLine(line);
                bw.write(outline);
                bw.newLine();
                bw.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                closer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void init() {
        if (clazz != null) {
            Arrays.stream(clazz.getMethods()).forEach(method -> METHODS.add(new GenerateMethod(method)));
        }
    }

    private void getReturnParameterType(Type returnType, List<Type> types) {
        if (returnType instanceof ParameterizedType) {
            Type parameterType = ((ParameterizedType) returnType).getActualTypeArguments()[0];
            types.add(parameterType);
            getReturnParameterType(parameterType, types);
        }
    }

    private String getParameterTypeSimpleName(Type parameterType) {
        String[] names = parameterType.getTypeName().split("\\.");
        return names[names.length - 1];
    }

    private String getReturnTypeName(Type returnType) {
        int index = returnType.getTypeName().indexOf("<");
        return returnType.getTypeName().substring(0, index);
    }

    private String getReturnTypeSimpleName(Type returnType) {
        int index = returnType.getTypeName().indexOf("<");
        String typeName = returnType.getTypeName().substring(0, index);
        String[] names = typeName.split("\\.");
        return names[names.length - 1];
    }
}