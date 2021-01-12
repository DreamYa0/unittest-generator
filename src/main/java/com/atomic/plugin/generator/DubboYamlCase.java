package com.atomic.plugin.generator;

import com.atomic.plugin.generator.service.YamlCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.*;

public class DubboYamlCase implements YamlCase, Serializable {
    private static final long serialVersionUID = 7129153011669306427L;
    /**
     * 测试用例标题
     *
     */
    private String caseName;

    /**
     * 测试用例开关，自动断言
     *
     */
    private boolean assertResult;

    /**
     * 测试用例开关，自动创建测试用例
     *
     */
    private boolean autotest;

    /**
     * 测试用例开关，运行测试用例
     *
     */
    private boolean testOnly;

    /**
     * 测试用例入参数据
     *
     */
    private String data;

    /**
     * 测试用例欲求返回结果
     *
     */
    private String expectResult;

    private DubboYamlCase() {
        this.caseName = "用例标题";
        this.assertResult = true;
        this.autotest = false;
        this.testOnly = false;
        this.data = "";
        this.expectResult = "";
    }

    private static class DubboYamlCaseClassIntance {
        private static final DubboYamlCase instance = new DubboYamlCase();
    }

    public static DubboYamlCase getInstance() {
        return DubboYamlCaseClassIntance.instance;
    }

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public boolean isAssertResult() {
        return assertResult;
    }

    public void setAssertResult(boolean assertResult) {
        this.assertResult = assertResult;
    }

    public boolean isAutotest() {
        return autotest;
    }

    public void setAutotest(boolean autotest) {
        this.autotest = autotest;
    }

    public boolean isTestOnly() {
        return testOnly;
    }

    public void setTestOnly(boolean testOnly) {
        this.testOnly = testOnly;
    }

    public String  getData() {
        return data;
    }

    public void setData(String  data) {
        this.data = data;
    }

    public String  getExpectResult() {
        return expectResult;
    }

    public void setExpectResult(String  expectResult) {
        this.expectResult = expectResult;
    }

    @Override
    public void CreateYamlCase(File outFile) {
        FileOutputStream fo = null;
        try {
            fo = new FileOutputStream(outFile);

//            ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.writeValue(outFile, DubboYamlCase.getInstance());

            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
