package com.atomic.plugin.generator;

import com.atomic.plugin.generator.dubbo.DubboYamlCase;
import com.atomic.plugin.generator.http.HttpYamlCase;

import java.io.File;

public class YmalCaseFactory {
    public static void GenerateDubboYamlCase(File outFile) {
        DubboYamlCase.getInstance().CreateYamlCase(outFile);
    }

    public static void GenerateHttpYamlCase(File outFile) {
        HttpYamlCase.getInstance().CreateYamlCase(outFile);
    }

    public static void main(String[] args) {
        //
        File outFile = new File("testCase" + ".yaml");
        GenerateDubboYamlCase(outFile);

        File outFileHttp = new File("testCaseHttp" + ".yaml");
        GenerateHttpYamlCase(outFileHttp);
    }
}
