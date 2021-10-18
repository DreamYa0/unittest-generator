package com.atomic.plugin.generator.http;

import com.atomic.plugin.generator.service.YamlCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HttpYamlCase implements YamlCase, Serializable {
    private static final long serialVersionUID = 3186442579459262591L;
    /**
     * Http接口名称
     *
     */
    private String interfaceName;

    /**
     * 测试用例开关，运行测试用例
     *
     */
    private List<HttpCase> caseList;

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public List<HttpCase> getHttpCaseList() {
        return caseList;
    }

    public void setHttpCaseList(List<HttpCase> caseList) {
        this.caseList = caseList;
    }

    private HttpYamlCase() {
        this.interfaceName = "Http接口名称";
        this.caseList = new ArrayList<>();
        this.caseList.add(HttpCase.getInstance());
    }

    private static class HttpYamlCaseClassIntance {
        private static final HttpYamlCase instance = new HttpYamlCase();
    }

    public static HttpYamlCase getInstance() {
        return HttpYamlCase.HttpYamlCaseClassIntance.instance;
    }

    @Override
    public void CreateYamlCase(File outFile) {
        FileOutputStream fo = null;
        try {
            fo = new FileOutputStream(outFile);

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
            mapper.writeValue(outFile, HttpYamlCase.getInstance());

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
