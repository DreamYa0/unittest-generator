package com.atomic.plugin.generator;

import com.atomic.plugin.generator.service.YamlCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DubboYamlCase implements YamlCase, Serializable {
    private static final long serialVersionUID = -8357955180082434217L;
    /**
     * Dubbo接口名称
     *
     */
    private String interfaceName;

    /**
     * 测试用例
     *
     */
    private List<DubboCase> caseList;

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public List<DubboCase> getDubboCaseList() {
        return caseList;
    }

    public void setDubboCaseList(List<DubboCase> caseList) {
        this.caseList = caseList;
    }

    private DubboYamlCase() {
        this.interfaceName = "Dubbo接口名称";
        this.caseList = new ArrayList<>();
        this.caseList.add(DubboCase.getInstance());
    }

    private static class DubboYamlCaseClassIntance {
        private static final DubboYamlCase instance = new DubboYamlCase();
    }

    public static DubboYamlCase getInstance() {
        return DubboYamlCaseClassIntance.instance;
    }

    @Override
    public void CreateYamlCase(File outFile) {
        FileOutputStream fo = null;
        try {
            fo = new FileOutputStream(outFile);

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
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
