package com.zeratul.plugin.generator;

import com.google.common.collect.Lists;
import com.zeratul.plugin.generator.http.HttpGenerator;
import com.zeratul.plugin.java.JavaParseUtil;
import com.zeratul.plugin.java.JavaParser;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author dreamyao
 * @title
 * @date 2018/4/12 下午10:21
 * @since 1.0.0
 */
@Mojo(name = "generator")
public class TestCaseGenerator extends AbstractMojo {

    /**
     * 测试代码生成路径
     */
    @Parameter
    private String testDirectory;

    /**
     * Class包路径
     */
    @Parameter
    private String classDirectory;

    /**
     * 目标类文件路径
     */
    @Parameter
    private List<String> classPath;

    /**
     * HTTP接口生成信息
     * package：测试类生成包名称
     * className：生成测试类名称
     */
    @Parameter
    private Map<String, String> httpTestMap;

    /**
     * RestFul接口生成信息
     * package：测试类生成包名称
     * className：生成测试类名称
     */
    @Parameter
    private Map<String, String> restFulTestMap;

    /**
     * SpringBoot Application主类的名称
     */
    @Parameter
    private String applicationName;

    public void execute() throws MojoExecutionException, MojoFailureException {
        Assert.notNull(testDirectory, "Test Directory is not null");

        if (!StringUtils.isEmpty(classDirectory)) {
            List<JavaParser> parse = JavaParseUtil.parse(classDirectory);

            // 如果是SpringBoot项目，且填入了Application主类名
            if (Objects.nonNull(applicationName)) {
                JavaTestGenerator.setSpringBootApplicationMainClass(applicationName);
            }

            JavaTestGenerator.generatorList(parse, testDirectory);

            try {
                ExcelTestGenerator.generatorList(parse, testDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (Boolean.FALSE.equals(CollectionUtils.isEmpty(classPath))) {
            // 如果是SpringBoot项目，且填入了Application主类名
            if (Objects.nonNull(applicationName)) {
                JavaTestGenerator.setSpringBootApplicationMainClass(applicationName);
            }

            List<JavaParser> parse = Lists.newArrayList();
            for (String path : classPath) {
                try {

                    JavaParser parser = new JavaParser(path);
                    parser.parse();
                    parse.add(parser);

                } catch (Exception e) {

                }
            }

            JavaTestGenerator.generatorList(parse, testDirectory);

            try {
                ExcelTestGenerator.generatorList(parse, testDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!CollectionUtils.isEmpty(httpTestMap)) {
            HttpGenerator.createHttpApiCase( httpTestMap.get("className"),httpTestMap.get("package"));
        }

        if (!CollectionUtils.isEmpty(restFulTestMap)) {
            HttpGenerator.createRestApiCase(restFulTestMap.get("className"),restFulTestMap.get("package"));
        }

        getLog().info("testDirectory:" + testDirectory);
    }

    public static void main(String[] args) {
        // HttpGenerator.createRestApiCase("TestPages", "unittest");
        HttpGenerator.createHttpApiCase("TestPagesHttp", "unittest");
    }
}
